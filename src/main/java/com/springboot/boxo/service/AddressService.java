package com.springboot.boxo.service;

import com.springboot.boxo.entity.Address;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.AddressDTO;
import com.springboot.boxo.payload.request.AddressRequest;

public interface AddressService {
    AddressDTO createAddress(Long userId, AddressRequest addressRequest);

    AddressDTO getAddressById(Long id);

    PaginationResponse<AddressDTO> getAddressByUserId(Long userId, int pageNumber, int pageSize, String sortBy, String sortDir);

    AddressDTO updateAddress(Long id, AddressRequest addressRequest);

    void deleteAddress(Long id);

    double calculateShippingCost(Address address);
    Address getDefaultAddress(Long userId);
}
