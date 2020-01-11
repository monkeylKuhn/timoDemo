package com.linln.admin.buss.model;

import java.util.Date;

import lombok.Data;

@Data
public class DressSkuSize {
    
    private Long id;
	
	private String productID;
	
	private String size;
	
	private String stock;
	
	private String retailPrice;
	
	private String price;
	
	private Date createTime;
	
	private int status;
}