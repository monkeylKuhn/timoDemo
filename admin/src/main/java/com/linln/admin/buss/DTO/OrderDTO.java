package com.linln.admin.buss.DTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 
 * @author mjl
 * @date 2020/01/07
 */
@Data
public class OrderDTO {

    private Long id;

    private String orderNo;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime submitTime;

    private String supplierName;

    private String sku;

    private String artNo;

    private String brand;

    private String color;

    private String size;

    private String season;

    private String price;

    private String retailPrice;

    private Integer orderStatus;
    
    private Integer supplierDeliveryStatus;
    
    private String supplierDeliveryNo;
    
    private String supplierBarCode;
    
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime supplierTime;
    
    private String warehouseBarCode;
    
    private String quality;
    
    private Integer warehouseStatus;
    
    private String warehouseDeliveryNo;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime warehouseTime;
}
