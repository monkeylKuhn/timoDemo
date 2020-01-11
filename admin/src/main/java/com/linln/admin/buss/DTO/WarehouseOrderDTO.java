 package com.linln.admin.buss.DTO;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 
 * @author mjl
 * @date 2020/01/07
 */
@Data
public class WarehouseOrderDTO {

     private Long id;

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

     private String retailPrice;

     private Integer orderStatus;
     
     private String supplierDeliveryStatus;

     private String supplierDeliveryNo;

     private LocalDateTime supplierTime;
     
     private String warehouseBarCode;
     
     private String quality;
     
     private Integer warehouseDeliveryStatus;
     
     private Integer warehouseDeliveryNo;
     
     private String warehouseTime;
     
}
