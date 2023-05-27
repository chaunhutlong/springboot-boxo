package com.springboot.boxo.service;

import com.springboot.boxo.entity.Discount;

public interface DiscountService {
    Discount getAvailableDiscount(String code);
}
