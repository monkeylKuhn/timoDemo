package com.linln.admin.buss.DTO;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SupplierOrderDTO {

    private Long id;

    private String orderNo;

    private LocalDateTime submitTime;

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

    private LocalDateTime supplierTime;
}
