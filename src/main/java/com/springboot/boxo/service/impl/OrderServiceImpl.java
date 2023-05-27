package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.*;
import com.springboot.boxo.enums.OrderStatus;
import com.springboot.boxo.enums.PaymentType;
import com.springboot.boxo.enums.ShippingStatus;
import com.springboot.boxo.exception.CustomException;
import com.springboot.boxo.payload.dto.OrderDTO;
import com.springboot.boxo.payload.dto.ShortBookDTO;
import com.springboot.boxo.repository.*;
import com.springboot.boxo.service.AddressService;
import com.springboot.boxo.service.CartService;
import com.springboot.boxo.service.DiscountService;
import com.springboot.boxo.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.springboot.boxo.utils.TrackingNumberGenerator.generateTrackingNumber;

@Service
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final DiscountRepository discountRepository;
    private final ShippingRepository shippingRepository;
    private final PaymentRepository paymentRepository;
    private final DiscountService discountService;
    private final AddressService addressService;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    public OrderServiceImpl(UserRepository userRepository, BookRepository bookRepository, OrderRepository orderRepository, CartRepository cartRepository, DiscountRepository discountRepository, ShippingRepository shippingRepository, PaymentRepository paymentRepository, DiscountService discountService, AddressService addressService, CartService cartService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.discountRepository = discountRepository;
        this.shippingRepository = shippingRepository;
        this.paymentRepository = paymentRepository;
        this.discountService = discountService;
        this.addressService = addressService;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderDTO processPaymentOrder(Long userId, String paymentType, String discountCode) {
        List<Cart> carts = cartRepository.findByUserIdAndChecked(userId, true);

        if (carts.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        Map<String, Object> totalPaymentResult = calculateTotalPayment(carts, discountCode);
        double totalPayment = (double) totalPaymentResult.get("totalPayment");
        Discount discount = (Discount) totalPaymentResult.get("discount");

        Address address = addressService.getDefaultAddress(userId);
        if (address == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "No default address found");
        }

        double shippingCost = addressService.calculateShippingCost(address);
        double totalPaymentWithShippingCost = totalPayment + shippingCost;

        Order order = createOrderAndShippingAndPayment(
                userId, totalPaymentWithShippingCost, discount, carts,
                address, shippingCost, paymentType);

        updateBookQuantity(carts);
        cartService.clearCart(userId);

        return mapToOrderDTO(order);
    }

    private Map<String, Object> calculateTotalPayment(List<Cart> carts, String discountCode) {
        double totalPayment = carts.stream().mapToDouble(Cart::getTotalPrice).sum();
        Discount discount = null;

        if (discountCode != null) {
            discount = discountService.getAvailableDiscount(discountCode);

            if (discount != null && totalPayment >= discount.getMinRequiredValue() && discount.getQuantity() > 0) {
                switch (discount.getType()) {
                    case PERCENTAGE -> totalPayment -= (totalPayment * discount.getValue()) / 100;
                    case FIXED_AMOUNT -> totalPayment -= discount.getValue();
                    default -> throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid discount type");
                }

                discount.setQuantity(discount.getQuantity() - 1);
                discountRepository.save(discount);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalPayment", totalPayment);
        result.put("discount", discount);
        return result;
    }

    private Order createOrderAndShippingAndPayment(
            Long userId, double totalPayment, Discount discount, List<Cart> carts,
            Address address, double shippingCost, String paymentType)
    {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING.toString());
        order.setTotalPayment(totalPayment);
        order.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "User not found")));
        orderRepository.save(order);

        Shipping shipping = new Shipping();
        shipping.setTrackingNumber(generateTrackingNumber(8));
        shipping.setValue(shippingCost);
        shipping.setAddress(address);
        shipping.setOrder(order);
        shipping.setStatus(ShippingStatus.PENDING);
        shippingRepository.save(shipping);

        Payment payment = new Payment();
        payment.setType(PaymentType.valueOf(paymentType));
        payment.setOrder(order);
        payment.setPaid(paymentType.equals(PaymentType.CASH_ON_DELIVERY.toString()));
        payment.setValue(totalPayment);
        payment.setAddress(address);
        paymentRepository.save(payment);

        order.setShipping(shipping); // Set the shipping reference
        order.setPayment(payment); // Set the payment reference
        order.setDiscount(discount); // Set the discount reference if applicable

        Set<OrderDetail> orderDetails = createOrderDetails(order, carts);
        order.setOrderDetails(orderDetails);

        return orderRepository.save(order);
    }


    private Set<OrderDetail> createOrderDetails(Order order, List<Cart> carts) {
        Set<OrderDetail> orderDetails = new HashSet<>();

        for (Cart cart : carts) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setQuantity(cart.getQuantity());
            // check if book has the priceDiscount value
            if (cart.getBook().getPriceDiscount() != null) {
                orderDetail.setPrice(cart.getBook().getPriceDiscount());
            } else {
                orderDetail.setPrice(cart.getBook().getPrice());
            }
            orderDetail.setOrder(order);
            orderDetail.setBook(cart.getBook());

            orderDetails.add(orderDetail);
        }

        return orderDetails;
    }
    private void updateBookQuantity(List<Cart> carts) {
        for (Cart cart : carts) {
            Book book = cart.getBook();
            book.setAvailableQuantity(book.getAvailableQuantity() - cart.getQuantity());
            bookRepository.save(book);
        }
    }
    private OrderDTO mapToOrderDTO(Order order) {
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        var orderDetails = order.getOrderDetails();
        List<ShortBookDTO> items = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            ShortBookDTO shortBookDTO = modelMapper.map(orderDetail.getBook(), ShortBookDTO.class);
            shortBookDTO.setBookId(orderDetail.getBook().getId());
            shortBookDTO.setQuantity(orderDetail.getQuantity());
            shortBookDTO.setImageUrl(orderDetail.getBook().getImages().get(0).getUrl());
            shortBookDTO.setCreateBy(orderDetail.getBook().getCreatedBy());
            shortBookDTO.setCreateDate(String.valueOf(orderDetail.getBook().getCreatedDate()));
            shortBookDTO.setUpdateBy(orderDetail.getBook().getLastModifiedBy());
            shortBookDTO.setUpdateDate(String.valueOf(orderDetail.getBook().getLastModifiedDate()));
            items.add(shortBookDTO);
        }
        orderDTO.setBooks(items);
        return orderDTO;
    }
}

