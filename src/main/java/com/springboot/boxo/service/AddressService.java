package com.springboot.boxo.service;

import com.springboot.boxo.payload.dto.AddressDTO;
import com.springboot.boxo.payload.request.AddressRequest;
import com.springboot.boxo.payload.PaginationResponse;

public interface AddressService {
    AddressDTO createAddress(Long userId, AddressRequest addressRequest);
    AddressDTO getAddressById(Long id);
    PaginationResponse<AddressDTO> getAddressByUserId(Long userId, int pageNumber, int pageSize, String sortBy, String sortDir);
    AddressDTO updateAddress(Long id, AddressRequest addressRequest);
    void deleteAddress(Long id);
    double calculateShippingCost(Long userId);
}
