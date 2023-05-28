package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Book;
import com.springboot.boxo.entity.Cart;
import com.springboot.boxo.entity.User;
import com.springboot.boxo.exception.CustomException;
import com.springboot.boxo.payload.dto.CartDTO;
import com.springboot.boxo.payload.dto.ItemCartDTO;
import com.springboot.boxo.payload.dto.UserDTO;
import com.springboot.boxo.repository.BookRepository;
import com.springboot.boxo.repository.CartRepository;
import com.springboot.boxo.repository.UserRepository;
import com.springboot.boxo.service.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    private static final String BOOK_NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Book with id {0} not found";
    private static final String BOOK_CART_NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Book with id {0} not found in cart";
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public CartServiceImpl(CartRepository cartRepository, UserRepository userRepository, BookRepository bookRepository, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartDTO getCartByUserId(Long userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);

        carts.forEach(cart -> {
            Book book = cart.getBook();
            double cartPrice = cart.getTotalPrice() / cart.getQuantity();

            if ((book.getPriceDiscount() != cartPrice && book.getPriceDiscount() > 0)
                || (book.getPrice() != cartPrice))
            {
                var totalPrice = getBookTotalPrice(book, cart.getQuantity());
                cartRepository.updateTotalPrice(cart.getUser().getId(), cart.getBook().getId(), totalPrice);
            }
        });

        return convertToDto(carts, userId);
    }

    @Override
    public HttpStatus addToCart(Long userId, Long bookId, int quantity) {
        User user = userRepository.findById(userId).orElseThrow();
        Book book = bookRepository.findById(bookId).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, MessageFormat.format(BOOK_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, bookId))
        );
        Optional<Cart> cartOptional = cartRepository.findByUserIdAndBookId(userId, bookId);

        Cart cart;
        if (cartOptional.isPresent()) {
            cart = cartOptional.get();
            updateCartDetails(cart, book, quantity + cart.getQuantity());
        } else {
            cart = createCart(user, book);
            updateCartDetails(cart, book, quantity);
        }

        cartRepository.save(cart);
        return HttpStatus.OK;
    }
    @Override
    public HttpStatus updateCart(Long userId, Long bookId, int quantity) {
        Optional<Cart> cartOptional = cartRepository.findByUserIdAndBookId(userId, bookId);
        if (cartOptional.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND, MessageFormat.format(BOOK_CART_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, bookId));
        }

        Cart cart = cartOptional.get();
        updateCartDetails(cart, cart.getBook(), quantity);
        cartRepository.save(cart);

        return HttpStatus.OK;
    }

    private Cart createCart(User user, Book book) {
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setChecked(false);
        cart.setBook(book);
        return cart;
    }

    private void updateCartDetails(Cart cart, Book book, int quantity) {
        if (book.getAvailableQuantity() < quantity) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Book quantity is not enough");
        }

        cart.setQuantity(quantity);
        cart.setBook(book);

        double bookTotalPrice = getBookTotalPrice(book, quantity);
        cart.setTotalPrice(bookTotalPrice);
    }

    private double getBookTotalPrice(Book book, int quantity) {
        double bookPrice = book.getPriceDiscount() != 0 ? book.getPriceDiscount() : book.getPrice();
        return bookPrice * quantity;
    }

    @Override
    public HttpStatus removeItemFromCart(Long userId, Long bookId) {
        Cart cart = cartRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, MessageFormat.format(BOOK_CART_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, bookId)));

        cartRepository.delete(cart);
        return HttpStatus.OK;
    }

    @Override
    public HttpStatus clearCart(Long userId) {
        List<Cart> cart = cartRepository.findByUserId(userId);

        if (!cart.isEmpty()) {
            cartRepository.deleteAll(cart);
        }

        return HttpStatus.OK;
    }

    @Override
    public HttpStatus updateCartCheckStatus(Long userId, Long bookId, boolean checkStatus) {
        Cart cart = cartRepository.findByUserIdAndBookId(userId, bookId).orElse(null);

        if (cart == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, MessageFormat.format(BOOK_CART_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, bookId));
        }

        cart.setChecked(checkStatus);
        cartRepository.save(cart);
        return HttpStatus.OK;
    }

    @Override
    public HttpStatus updateAllCartCheckStatus(Long userId, boolean checkStatus) {
        List<Cart> cart = cartRepository.findByUserId(userId);

        if (cart.isEmpty()) {
            return HttpStatus.NOT_FOUND;
        }

        cart.forEach(c -> c.setChecked(checkStatus));

        cartRepository.saveAll(cart);
        return HttpStatus.OK;
    }

    private CartDTO convertToDto(List<Cart> carts, Long userId) {
        if (carts.isEmpty()) {
            // return empty cart with user info
            CartDTO cartDTO = new CartDTO();
            cartDTO.setUser(modelMapper.map(userRepository.findById(userId).orElseThrow(), UserDTO.class));
            cartDTO.setItems(new ArrayList<>());
            return cartDTO;
        }

        CartDTO cartDTO = new CartDTO();
        cartDTO.setUser(modelMapper.map(carts.get(0).getUser(), UserDTO.class));

        List<ItemCartDTO> itemCartDTOList = carts.stream().map(this::convertToItemCartDTO).toList();
        cartDTO.setItems(itemCartDTOList);

        double totalPrice = itemCartDTOList.stream().mapToDouble(ItemCartDTO::getTotalPrice).sum();
        cartDTO.setTotalPriceInCart(totalPrice);

        return cartDTO;
    }

    private ItemCartDTO convertToItemCartDTO(Cart cart) {
        Book book = cart.getBook();
        ItemCartDTO itemCartDTO = modelMapper.map(cart, ItemCartDTO.class);
        itemCartDTO.setBookId(book.getId());
        itemCartDTO.setName(book.getName());
        itemCartDTO.setPrice(book.getPrice());
        itemCartDTO.setPriceDiscount(book.getPriceDiscount());
        if (book.getImages() != null && !book.getImages().isEmpty()) {
            itemCartDTO.setImageUrl(book.getImages().get(0).getUrl());
        }
        else {
            itemCartDTO.setImageUrl("");
        }
        itemCartDTO.setTotalPrice(getBookTotalPrice(book, cart.getQuantity()));
        return itemCartDTO;
    }
}

