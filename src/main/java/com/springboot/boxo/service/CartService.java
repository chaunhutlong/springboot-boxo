package com.springboot.boxo.service;

import com.springboot.boxo.payload.dto.CartDTO;
import org.springframework.http.HttpStatus;

public interface CartService {
    CartDTO getCartByUserId(Long userId);
    HttpStatus addToCart(Long userId, Long bookId, int quantity);
    HttpStatus updateCart(Long userId, Long bookId, int quantity);
    HttpStatus removeItemFromCart(Long userId, Long bookId);
    HttpStatus clearCart(Long userId);
    HttpStatus updateCartCheckStatus(Long userId, Long bookId, boolean checkStatus);
    HttpStatus updateAllCartCheckStatus(Long userId, boolean checkStatus);
}
