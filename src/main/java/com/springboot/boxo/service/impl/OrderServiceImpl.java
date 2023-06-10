package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.*;
import com.springboot.boxo.enums.NotificationType;
import com.springboot.boxo.enums.OrderStatus;
import com.springboot.boxo.enums.PaymentType;
import com.springboot.boxo.enums.ShippingStatus;
import com.springboot.boxo.exception.CustomException;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.OrderDTO;
import com.springboot.boxo.payload.dto.PaymentDTO;
import com.springboot.boxo.payload.dto.ShippingDTO;
import com.springboot.boxo.payload.dto.ShortBookDTO;
import com.springboot.boxo.payload.request.NotificationRequest;
import com.springboot.boxo.repository.*;
import com.springboot.boxo.service.*;
import com.springboot.boxo.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.AbstractMap.SimpleEntry;
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
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(
            UserRepository userRepository, BookRepository bookRepository,
            OrderRepository orderRepository, CartRepository cartRepository,
            DiscountRepository discountRepository, ShippingRepository shippingRepository,
            PaymentRepository paymentRepository, DiscountService discountService,
            AddressService addressService, CartService cartService, NotificationService notificationService, ModelMapper modelMapper
    ) {
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
        this.notificationService = notificationService;
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

        SimpleEntry<Order, Shipping> result = createOrderAndShippingAndPayment(
                userId, totalPaymentWithShippingCost, discount, carts,
                address, shippingCost, paymentType);

        updateBookQuantity(carts);
        cartService.clearCart(userId);

        var bodyNotification = "Your order with tracking number " + result.getValue().getTrackingNumber() + " has been placed";
        NotificationRequest notificationRequest = new NotificationRequest(
                userId, result.getKey().getId(), "Order Placed", bodyNotification, NotificationType.ORDER.toString()
        );

        notificationService.createNotification(notificationRequest);

        return mapToOrderDTO(result.getKey());
    }

    @Override
    public HttpStatus checkoutOrder(Long userId, Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus().equals(String.valueOf(OrderStatus.PAID))) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Order is already paid");
        }

        validateUserAuthorization(order, userId);
        validatePaymentNotProcessed(orderId);

        processPayment(order);
        updateOrderAndShippingStatus(order);

        return HttpStatus.OK;
    }

    @Override
    public HttpStatus cancelOrder(Long userId, Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus().equals(String.valueOf(OrderStatus.CANCELLED))) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Order is already cancelled");
        }

        validateUserAuthorization(order, userId);

        order.setStatus(String.valueOf(OrderStatus.CANCELLED));
        orderRepository.save(order);

        // create notification
        var shipping = shippingRepository.findByOrderId(orderId);
        var bodyNotification = "Your order with tracking number " + shipping.getTrackingNumber() + " has been cancelled";

        NotificationRequest notificationRequest = new NotificationRequest(
                userId, orderId, "Order Cancelled", bodyNotification, NotificationType.ORDER.toString()
        );
        notificationService.createNotification(notificationRequest);

        return HttpStatus.OK;
    }

    @Override
    public OrderDTO getOrder(Long orderId) {
        Order order = getOrderById(orderId);
        return mapToOrderDTO(order);
    }

    @Override
    public PaginationResponse<OrderDTO> getOrdersByUserId(Long userId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir);
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);

        List<OrderDTO> orderDTOs = new ArrayList<>();
        orders.forEach(order -> orderDTOs.add(mapToOrderDTO(order)));

        return PaginationUtils.createPaginationResponse(orderDTOs, orders);
    }

    @Override
    public PaginationResponse<OrderDTO> getAllOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir);
        Page<Order> orders = orderRepository.findAll(pageable);

        List<OrderDTO> orderDTOs = new ArrayList<>();
        orders.forEach(order -> orderDTOs.add(mapToOrderDTO(order)));

        return PaginationUtils.createPaginationResponse(orderDTOs, orders);
    }

    @Override
    public ShippingDTO getShippingByOrderId(Long orderId) {
        Shipping shipping = shippingRepository.findByOrderId(orderId);
        return mapToShippingDTO(shipping);
    }
    @Override
    public HttpStatus updateShippingStatus(Long orderId, ShippingStatus status) {
        Shipping shipping = shippingRepository.findByOrderId(orderId);
        shipping.setStatus(status);
        shippingRepository.save(shipping);
        return HttpStatus.OK;
    }

    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    private void validateUserAuthorization(Order order, Long userId) {
        if (!order.getUser().getId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "You are not authorized to access this order");
        }
    }

    private void validatePaymentNotProcessed(Long orderId) {
        Payment payment = paymentRepository.findByOrderIdAndPaid(orderId, true);
        if (payment != null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Payment is already processed");
        }
    }

    private void processPayment(Order order) {
        Shipping shipping = shippingRepository.findByOrderId(order.getId());
        Payment payment = createPayment(order, shipping.getAddress());
        payment.setPaid(true);
        paymentRepository.save(payment);
    }

    private Payment createPayment(Order order, Address address) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAddress(address);
        payment.setPaid(false);
        payment.setValue(order.getTotalPayment());
        return paymentRepository.save(payment);
    }

    private void updateOrderAndShippingStatus(Order order) {
        Shipping shipping = shippingRepository.findByOrderId(order.getId());
        order.setStatus(String.valueOf(OrderStatus.PAID));
        shipping.setStatus(ShippingStatus.SHIPPING);
        orderRepository.save(order);
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

    private SimpleEntry<Order, Shipping> createOrderAndShippingAndPayment(
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
        payment.setPaid(false);
        payment.setValue(totalPayment);
        payment.setAddress(address);
        paymentRepository.save(payment);

        order.setDiscount(discount);

        Set<OrderDetail> orderDetails = createOrderDetails(order, carts);
        order.setOrderDetails(orderDetails);

        return new SimpleEntry<>(order, shipping);
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
            if (orderDetail.getBook().getImages() != null && !orderDetail.getBook().getImages().isEmpty()) {
                shortBookDTO.setImageUrl(orderDetail.getBook().getImages().get(0).getUrl());
            }
            else {
                shortBookDTO.setImageUrl("");
            }
            items.add(shortBookDTO);
        }
        orderDTO.setBooks(items);
        var shipping = shippingRepository.findByOrderId(order.getId());
        if (shipping != null) {
            orderDTO.setShipping(mapToShippingDTO(shipping));
        }
        var payment = paymentRepository.findByOrderId(order.getId());
        if (payment != null) {
            orderDTO.setPayment(mapToPaymentDTO(payment));
        }
        return orderDTO;
    }

    private ShippingDTO mapToShippingDTO(Shipping shipping) {
        return modelMapper.map(shipping, ShippingDTO.class);
    }

    private PaymentDTO mapToPaymentDTO(Payment payment) {
        return modelMapper.map(payment, PaymentDTO.class);
    }
}

