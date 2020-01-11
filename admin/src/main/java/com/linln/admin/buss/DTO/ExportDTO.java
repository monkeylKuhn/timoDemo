package com.linln.admin.buss.DTO;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

@Data
public class ExportDTO {

    @Excel(name = "供应商spu编码")
    private String productID;

    @Excel(name = "货号")
    private String sku;

    @Excel(name = "商品品牌")
    private String brand;

    @Excel(name = "商品名称")
    private String name;

    @Excel(name = "商品描述")
    private String description;

    @Excel(name = "性别")
    private String genre;

    @Excel(name = "skuId")
    private String skuId;

    @Excel(name = "一级分类")
    private String fristCategory;

    @Excel(name = "商品分类")
    private String skuCategory;

    @Excel(name = "产地")
    private String madeIn;

    @Excel(name = "材质")
    private String composition;

    @Excel(name = "季节")
    private String season;

    @Excel(name = "是否经典款")
    private String isCarryOver;

    @Excel(name = "颜色")
    private String color;

    @Excel(name = "欧洲市场价")
    private String retailPrice;

    @Excel(name = "结算价")
    private String price;

    @Excel(name = "是否含税")
    private String pricesIncludeVat;

    @Excel(name = "最近更新时间")
    private String productLastUpdated;

    @Excel(name = "尺寸")
    private String size;

    @Excel(name = "可售库存")
    private String stock;
    
    @Excel(name = "url1")
    private String url1;
    
    @Excel(name = "url2")
    private String url2;
    
    @Excel(name = "url3")
    private String url3;
    
    @Excel(name = "url4")
    private String url4;

    @Excel(name = "url5")
    private String url5;

    private String photos;


}
