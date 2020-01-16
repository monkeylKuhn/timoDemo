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
     
     private boolean testMode = false;
     
     private ShippingAddress shippingAddress;
}
