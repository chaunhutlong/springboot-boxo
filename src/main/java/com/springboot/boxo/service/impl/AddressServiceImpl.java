package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Address;
import com.springboot.boxo.payload.PaginationResponse;
import com.springboot.boxo.payload.dto.AddressDTO;
import com.springboot.boxo.payload.request.AddressRequest;
import com.springboot.boxo.payload.request.ProfileRequest;
import com.springboot.boxo.repository.AddressRepository;
import com.springboot.boxo.repository.UserRepository;
import com.springboot.boxo.service.AddressService;
import com.springboot.boxo.service.CityService;
import com.springboot.boxo.utils.PaginationUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

import static com.springboot.boxo.utils.ShippingCalculator.calculateShippingValue;

@Service
public class AddressServiceImpl implements AddressService {
    private static final String ADDRESS_NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Address with id {0} not found";
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CityService cityService;
    private final ModelMapper modelMapper;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository, CityService cityService, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.cityService = cityService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddressDTO createAddress(Long userId, AddressRequest addressRequest) {
        Address address = mapToEntity(addressRequest);

        if (address.isDefault()) {
            // update other addresses to not default
            addressRepository.updateDefaultAddressByUserId(userId);
        }

        double distance = cityService.calculateDistance(addressRequest.getCityId());
        address.setDistance(distance);

        address.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException(MessageFormat.format(ADDRESS_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, userId))));

        return mapToDTO(addressRepository.save(address));
    }

    @Override
    public AddressDTO getAddressById(Long id) {
        return mapToDTO(addressRepository.findById(id).orElseThrow());
    }

    @Override
    public PaginationResponse<AddressDTO> getAddressByUserId(Long userId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Pageable pageable = PaginationUtils.convertToPageable(pageNumber, pageSize, sortBy, sortDir);
        Page<Address> addresses = addressRepository.getAddressByUserId(userId, pageable);

        List<Address> addressList = addresses.getContent();
        List<AddressDTO> content = addressList.stream().map(this::mapToDTO).toList();

        return PaginationUtils.createPaginationResponse(content, addresses);
    }

    @Override
    public AddressDTO updateAddress(Long id, AddressRequest addressRequest) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new RuntimeException(MessageFormat.format(ADDRESS_NOT_FOUND_ERROR_MESSAGE_TEMPLATE, id)));
        var userId = address.getUser().getId();
        var addressId = address.getId();
        if (address.isDefault()) {
            // update other addresses to not default
            addressRepository.updateDefaultAddressByUserId(userId);
        }

        address = mapToEntity(addressRequest);
        address.setId(addressId);
        address.setUser(userRepository.findById(userId).orElseThrow());
        address.setDistance(cityService.calculateDistance(addressRequest.getCityId()));

        return mapToDTO(addressRepository.save(address));
    }

    @Override
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    @Override
    public double calculateShippingCost(Long userId) {
        Address address = addressRepository.getDefaultAddressByUserId(userId);
        return calculateShippingValue(address.getDistance());
    }

    private AddressDTO mapToDTO(Address address) {
        return modelMapper.map(address, AddressDTO.class);
    }

    private Address mapToEntity(AddressRequest addressRequest) {
        ModelMapper localModelMapper = new ModelMapper();
        localModelMapper.typeMap(ProfileRequest.class, Address.class)
                .addMappings(mapper -> mapper.skip(Address::setDistance));
        return localModelMapper.map(addressRequest, Address.class);
    }
}

