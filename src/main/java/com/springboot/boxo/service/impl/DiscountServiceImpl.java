package com.springboot.boxo.service.impl;

import com.springboot.boxo.entity.Discount;
import com.springboot.boxo.repository.DiscountRepository;
import com.springboot.boxo.service.DiscountService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DiscountServiceImpl implements DiscountService {
    private final DiscountRepository discountRepository;
    public DiscountServiceImpl(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }
    @Override
    public Discount getAvailableDiscount(String code) {
        var discount = discountRepository.findByCodeAndIsActive(code, true);

        // Check expired date
        if (discount != null && (discount.getEndDate().before(new Date()) || discount.getStartDate().after(new Date()))) {
            // set discount to not active
            discount.setActive(false);
            discountRepository.save(discount);
            return null;
        }

        return discount;
    }
}

