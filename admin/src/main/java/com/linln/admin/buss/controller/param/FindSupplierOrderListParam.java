 package com.linln.admin.buss.controller.param;

import lombok.Data;

@Data
 public class FindSupplierOrderListParam {

     private Integer pageNum;
     
     private Integer pageSize;
     
     private String supplierName;
}
