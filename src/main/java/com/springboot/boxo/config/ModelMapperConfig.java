package com.springboot.boxo.config;

import com.springboot.boxo.entity.Book;
import com.springboot.boxo.entity.Order;
import com.springboot.boxo.entity.Payment;
import com.springboot.boxo.entity.Shipping;
import com.springboot.boxo.payload.dto.OrderDTO;
import com.springboot.boxo.payload.dto.PaymentDTO;
import com.springboot.boxo.payload.dto.ShippingDTO;
import com.springboot.boxo.payload.dto.ShortBookDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.addMappings(new PropertyMap<Shipping, ShippingDTO>() {
            @Override
            protected void configure() {
                map().setCreateDate(String.valueOf(source.getCreatedDate()));
                map().setUpdateDate(String.valueOf(source.getLastModifiedDate()));
                map().setCreateBy(source.getCreatedBy());
                map().setUpdateBy(source.getLastModifiedBy());
            }
        });

        modelMapper.addMappings(new PropertyMap<Payment, PaymentDTO>() {
            @Override
            protected void configure() {
                map().setCreateDate(String.valueOf(source.getCreatedDate()));
                map().setUpdateDate(String.valueOf(source.getLastModifiedDate()));
                map().setCreateBy(source.getCreatedBy());
                map().setUpdateBy(source.getLastModifiedBy());
            }
        });
        modelMapper.addMappings(new PropertyMap<Order, OrderDTO>() {
            @Override
            protected void configure() {
                map().setCreateDate(String.valueOf(source.getCreatedDate()));
                map().setUpdateDate(String.valueOf(source.getLastModifiedDate()));
                map().setCreateBy(source.getCreatedBy());
                map().setUpdateBy(source.getLastModifiedBy());
            }
        });

        modelMapper.addMappings(new PropertyMap<Book, ShortBookDTO>() {
            @Override
            protected void configure() {
                map().setCreateDate(String.valueOf(source.getCreatedDate()));
                map().setUpdateDate(String.valueOf(source.getLastModifiedDate()));
                map().setCreateBy(source.getCreatedBy());
                map().setUpdateBy(source.getLastModifiedBy());
            }
        });

        return modelMapper;
    }
}

