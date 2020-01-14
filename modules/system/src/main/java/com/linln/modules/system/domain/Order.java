package com.linln.modules.system.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.StatusUtil;

import lombok.Data;

/**
 * @author 小懒虫
 * @date 2020/01/12
 */
@Data
@Entity
@Table(name="or_order")
@EntityListeners(AuditingEntityListener.class)
@Where(clause = StatusUtil.NOT_DELETE)
public class Order implements Serializable {
    // 主键ID
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    // 订单号
    private String orderNo;
    // 下单时间
    private String submitTime;
    // 供应商名称
    private String supplierName;
    // 商品sku
    private String sku;
    // 货号
    private String artNo;
    // 品牌
    private String brand;
    // 尺码
    private String size;
    // 颜色
    private String color;
    // 季节
    private String season;
    // 价格
    private String price;
    // 市场价
    private String retailPrice;
    // 订单状态
    private String orderStatus;
    // 供应商发货物流公司
    private String supplierDelivery;
    // 供应商发货状态
    private String supplierDeliveryStatus;
    // 供应商物流号
    private String supplierDeliveryNo;
    // 供应商barcode
    private String supplierBarCode;
    // 供应商发货时间
    private String supplierTime;
    // 仓库barcode
    private String warehouseBarCode;
    // 质检
    private String quality;
    // 仓库发货状态
    private String warehouseStatus;
    // 仓库物流公司
    private String warehouseDelivery;
    // 仓库物流号
    private String warehouseDeliveryNo;
    // 仓库发货时间
    private String warehouseTime;
    // 数据状态
    private Byte status = StatusEnum.OK.getCode();
    // 更新者
    @LastModifiedBy
    @ManyToOne(fetch=FetchType.LAZY)
    @NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name="update_by")
    @JsonIgnore
    private User updateBy;
    // 创建者
    @CreatedBy
    @ManyToOne(fetch=FetchType.LAZY)
    @NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name="create_by")
    @JsonIgnore
    private User createBy;
    // 更新时间
    @LastModifiedDate
    private Date updateDate;
    // 创建时间
    @CreatedDate
    private Date createDate;
    
}