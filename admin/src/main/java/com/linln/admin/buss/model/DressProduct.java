package com.linln.admin.buss.model;

import java.util.List;

import lombok.Data;

@Data
public class DressProduct {
	
	private String productID;
	
	private String clientProductID;
	
	private String spu;
	
	private String sku;
	
	private String brand;
	
	private String name;
	
	private String description;
	
	private String genre;
	
	private String type;
	
	private String category;
	
	private String season;
	
	private boolean isCarryOver;
	
	private String color;
	
	private String retailPrice;
	
	private String price;
	
	private String pricesIncludeVat;
	
	private String productLastUpdated;
	
	private String madeIn;
	
	private String composition;
	
	private List<DressSkuSize> sizes;
	
	private List<String> photos;
	public String getPhotos(){
		StringBuffer sb = new StringBuffer();
		for (String string : photos) {
			sb.append(string).append("^");
		}
		return sb.toString();
	}
		
	private Long id;
}