 package com.linln.admin.buss.model;

import lombok.Data;

@Data
public class SubmitOrder {

     private String channelOrderID;
     
     private String channelOrderCreated;
     
     private String productID;
     
     private String size;
     
     private Integer soldUnits;
     
     private double unitSellingPrice;
     
     //TODO  true为测试环境 正式为false  上线删除
     private boolean testMode = true;
     
     private ShippingAddress shippingAddress;
}
