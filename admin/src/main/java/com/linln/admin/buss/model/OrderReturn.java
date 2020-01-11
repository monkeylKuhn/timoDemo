 package com.linln.admin.buss.model;

import lombok.Data;

@Data
 public class OrderReturn {

     private OrderStatus data;
     
     private OrderError error;
     
}
