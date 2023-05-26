package com.springboot.boxo.controller;

import com.springboot.boxo.payload.dto.CartDTO;
import com.springboot.boxo.payload.request.CheckItemCartRequest;
import com.springboot.boxo.payload.request.UpdateCartRequest;
import com.springboot.boxo.security.CustomUserDetails;
import com.springboot.boxo.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("${spring.data.rest.base-path}/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/get")
    public ResponseEntity<CartDTO> getCartByUserId(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        var cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/add-to-cart")
    public ResponseEntity<CartDTO> addToCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                                          @Valid @RequestBody UpdateCartRequest cartRequest) {
        Long userId = userDetails.getUserId();

        HttpStatus statusCode = cartService.addToCart(userId, cartRequest.getBookId(), cartRequest.getQuantity());
        return ResponseEntity.status(statusCode).build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PutMapping("/update")
    public ResponseEntity<HttpStatus> updateCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @Valid @RequestBody UpdateCartRequest cartRequest) {
        Long userId = userDetails.getUserId();

        HttpStatus statusCode = cartService.updateCart(userId, cartRequest.getBookId(), cartRequest.getQuantity());
        return ResponseEntity.status(statusCode).build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PutMapping("/remove")
    public ResponseEntity<HttpStatus> removeItemFromCart(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                         @RequestBody UpdateCartRequest cartRequest) {
        Long userId = userDetails.getUserId();

        HttpStatus statusCode = cartService.removeItemFromCart(userId, cartRequest.getBookId());
        return ResponseEntity.status(statusCode).build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PutMapping("/clear")
    public ResponseEntity<HttpStatus> clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        HttpStatus statusCode = cartService.clearCart(userId);
        return ResponseEntity.status(statusCode).build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PutMapping("/checked-item")
    public ResponseEntity<HttpStatus> updateCartCheckStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @RequestBody CheckItemCartRequest cartRequest) {
        Long userId = userDetails.getUserId();

        HttpStatus statusCode = cartService.updateCartCheckStatus(userId, cartRequest.getBookId(), cartRequest.isChecked());
        return ResponseEntity.status(statusCode).build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PutMapping("/checked-all-items")
    public ResponseEntity<HttpStatus> updateAllCartCheckStatus(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                               @RequestBody CheckItemCartRequest cartRequest) {
        Long userId = userDetails.getUserId();

        HttpStatus statusCode = cartService.updateAllCartCheckStatus(userId, cartRequest.isChecked());
        return ResponseEntity.status(statusCode).build();
    }
}

