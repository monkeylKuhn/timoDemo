package com.linln.admin.system.validator;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

/**
 * @author 小懒虫
 * @date 2020/01/12
 */
@Data
public class OrderValid implements Serializable {
    @NotEmpty(message = "订单号不能为空")
    private String orderNo;
    private String submitTime = LocalDateTime.now().toString();
    @NotEmpty(message = "供应商名称不能为空")
    private String supplierName;
    @NotEmpty(message = "商品sku不能为空")
    private String sku;
    @NotEmpty(message = "货号不能为空")
    private String artNo;
    @NotEmpty(message = "品牌不能为空")
    private String brand;
    @NotEmpty(message = "尺码不能为空")
    private String size;
    @NotEmpty(message = "颜色不能为空")
    private String color;
    @NotEmpty(message = "季节不能为空")
    private String season;
    @NotEmpty(message = "价格不能为空")
    private String price;
    @NotEmpty(message = "市场价不能为空")
    private String retailPrice;
    @NotEmpty(message = "订单状态不能为空")
    private String orderStatus;
}