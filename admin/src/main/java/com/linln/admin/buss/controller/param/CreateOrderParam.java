 package com.linln.admin.buss.controller.param;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 创建订单
 * @author mjl
 * @date 2020/01/07
 */
@Data
 public class CreateOrderParam {

    private String orderNo;
    
    private LocalDateTime submitTime;
    
    private String supplierName;
    
    private String sku;
    
    private String artNo;
    
    private String brand;
    
    private String color;
    
    private String size;
    
    private String season;
    
    private String price;
    
    private String orderStatus;
    
    //图片待定
    private String supplierBarCode;
    
    private String warehouseBarCode;
    
    //地址信息
    private String businessName;
    
    private String name;
    
    private String surname;
    
    private String email;
    
    private String streetName;
    
    private String streetNumber;
    
    private String city;
    
    private String zip;
    
    private String state;
    
    private String country;
}
