package com.linln.modules.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.StatusUtil;
import com.linln.modules.system.domain.User;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

/**
 * @author 小懒虫
 * @date 2020/01/11
 */
@Data
@Entity
@Table(name="s_DressSpu")
@EntityListeners(AuditingEntityListener.class)
@Where(clause = StatusUtil.NOT_DELETE)
public class DressSpu implements Serializable {
    // 主键ID
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    // spuid
    private String productID;
    // cid
    private String clientProductID;
    // spu
    private String spu;
    // 货号
    private String sku;
    // 品牌
    private String brand;
    private String name;
    // 描述
    private String description;
    // 性别
    private String genre;
    // 类型
    private String type;
    // 类目
    private String category;
    // 季节
    private String season;
    // 经典
    private String isCarryOver;
    // 颜色
    private String color;
    // 市场价
    private String retailPrice;
    // 产地
    private String madeIn;
    // 材质
    private String composition;
    // 价格
    private String price;
    // 含税
    private String pricesIncludeVat;
    // 时间
    private String productLastUpdated;
    // 图片
    private String photos;
    // 创建时间
    @CreatedDate
    private Date createDate;
    // 更新时间
    @LastModifiedDate
    private Date updateDate;
    // 创建者
    @CreatedBy
    @ManyToOne(fetch=FetchType.LAZY)
    @NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name="create_by")
    @JsonIgnore
    private User createBy;
    // 更新者
    @LastModifiedBy
    @ManyToOne(fetch=FetchType.LAZY)
    @NotFound(action=NotFoundAction.IGNORE)
    @JoinColumn(name="update_by")
    @JsonIgnore
    private User updateBy;
    // 数据状态
    private Byte status = StatusEnum.OK.getCode();
}