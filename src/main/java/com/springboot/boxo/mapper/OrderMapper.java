//package com.springboot.boxo.mapper;
//
//import com.springboot.boxo.entity.Order;
//import com.springboot.boxo.entity.Payment;
//import com.springboot.boxo.entity.Shipping;
//import com.springboot.boxo.payload.dto.OrderDTO;
//import com.springboot.boxo.payload.dto.PaymentDTO;
//import com.springboot.boxo.payload.dto.ShippingDTO;
//import org.modelmapper.ModelMapper;
//import org.modelmapper.PropertyMap;
//
//public class OrderMapper {
//    private final ModelMapper modelMapper;
//    public OrderMapper(ModelMapper modelMapper) {
//        this.modelMapper = modelMapper;
//        ModelMapper modelMapper = new ModelMapper();
//        PropertyMap<Shipping, ShippingDTO> shippingMap = new PropertyMap<>() {
//            @Override
//            protected void configure() {
//                map().setCreateDate(String.valueOf(source.getCreatedDate()));
//                map().setUpdateDate(String.valueOf(source.getLastModifiedDate()));
//                map().setCreateBy(source.getCreatedBy());
//                map().setUpdateBy(source.getLastModifiedBy());
//            }
//        };
//
//        PropertyMap<Payment, PaymentDTO> paymentMap = new PropertyMap<>() {
//            @Override
//            protected void configure() {
//                map().setCreateDate(String.valueOf(source.getCreatedDate()));
//                map().setUpdateDate(String.valueOf(source.getLastModifiedDate()));
//                map().setCreateBy(source.getCreatedBy());
//                map().setUpdateBy(source.getLastModifiedBy());
//            }
//        };
//
//        PropertyMap<Order, OrderDTO> orderMap = new PropertyMap<>() {
//            @Override
//            protected void configure() {
//                map().setCreateDate(String.valueOf(source.getCreatedDate()));
//                map().setUpdateDate(String.valueOf(source.getLastModifiedDate()));
//                map().setCreateBy(source.getCreatedBy());
//                map().setUpdateBy(source.getLastModifiedBy());
//            }
//        };
//
//        modelMapper.addMappings(shippingMap);
//        modelMapper.addMappings(paymentMap);
//        modelMapper.addMappings(orderMap);
//    }
//}
