package com.springboot.boxo.controller;

import com.springboot.boxo.entity.Address;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.AddressDTO;
import com.springboot.boxo.payload.request.AddressRequest;
import com.springboot.boxo.security.CustomUserDetails;
import com.springboot.boxo.service.AddressService;
import com.springboot.boxo.utils.AppConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("${spring.data.rest.base-path}/addresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER')")
    @GetMapping
    public ResponseEntity<PaginationResponse<AddressDTO>> getAllAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(value = "limit", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        Long userId = userDetails.getUserId();
        var addresses = addressService.getAddressByUserId(userId, pageNumber, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(addresses);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_USER')")
    @GetMapping("/shipping-cost")
    public ResponseEntity<Double> getShippingCost(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        Address address = addressService.getDefaultAddress(userId);
        double shippingCost = addressService.calculateShippingCost(address);
        return ResponseEntity.ok(shippingCost);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping
    public ResponseEntity<AddressDTO> createAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddressRequest addressRequest) {

        Long userId = userDetails.getUserId();
        var address = addressService.createAddress(userId, addressRequest);
        return ResponseEntity.ok(address);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<AddressDTO> getAddressById(
            @PathVariable Long id) {

        var address = addressService.getAddressById(id);
        return ResponseEntity.ok(address);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest addressRequest) {

        var address = addressService.updateAddress(id, addressRequest);
        return ResponseEntity.ok(address);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long id) {

        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}

