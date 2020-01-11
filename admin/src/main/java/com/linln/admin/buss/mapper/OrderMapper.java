 package com.linln.admin.buss.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.linln.admin.buss.DTO.OrderDTO;
import com.linln.admin.buss.DTO.SupplierOrderDTO;
import com.linln.admin.buss.DTO.WarehouseOrderDTO;
import com.linln.admin.buss.controller.param.CreateOrderParam;

public interface OrderMapper {


     @Insert({
         "INSERT INTO order(orderNo,submitTime,supperName,sku,atrNo,brand,size,color,season,price,retailPrice,orderStatus)",
         "VALUES(#{orderNo},#{submitTime},#{supperName},#{sku},#{atrNo},#{brand},#{size},#{color},#{season},#{price},#{retailPrice},#{orderStatus})"
     })
     Integer insert(CreateOrderParam createOrderParam);

     @Select({
         "SELECT COUNT(1) FROM Order ",
     })
    int count();

     @Select({
         "SELECT id,orderNo,submitTime,supplierName,sku,artNo,brand,size,color,season,price,retailPrice,orderStatus,",
         "supplierDeliveryStatus,supplierDeliveryNo,supplierBarCode,supplierTime,",
         "warehouseBarCode,quality,warehouseStatus,warehouseDeliveryNo,warehouseTime",
         "FROM Order",
         "ORDER BY submitTime DESC",
         //按什么排序
         "LIMIT #{offset},#{pageSize}"
         
     })
    List<OrderDTO> findList( @Param("offset")int offset, @Param("pageSize")int pageSize);

     @Select({
         "SELECT COUNT(1) FROM Order WHERE supplierName = #{supplierName} ",
     })
    int supplierCount(@Param("supplierName")String supplierName);

     @Select({
         "SELECT id,orderNo,submitTime,sku,artNo,brand,size,color,season,price,retailPrice,orderStatus,",
         "supplierDeliveryStatus,supplierDeliveryNo,supplierBarCode,supplierTime",
         "FROM Order",
         "WHERE supplierName = #{supplierName}",
         "ORDER BY submitTime DESC",
         "LIMIT #{offset},#{pageSize}"
     })
    List<SupplierOrderDTO> findListBySupplierName(@Param("supplierName")String supplierName, @Param("offset")int pageNum, @Param("pageSize")int pageSize);

     @Select({
         "SELECT id,orderNo,submitTime,supplierName,sku,artNo,brand,size,color,season,price,retailPrice,orderStatus,",
         "supplierDeliveryStatus,supplierDeliveryNo,supplierTime,",
         "warehouseBarCode,quality,warehouseStatus,warehouseDeliveryNo,warehouseTime",
         "FROM Order",
         "ORDER BY submitTime DESC",
         "LIMIT #{offset},#{pageSize}"
         
     })
    List<WarehouseOrderDTO> findWarehouseList( @Param("offset")int offset, @Param("pageSize")int pageSize);
     
     @Update({
         "<script>",
         "UPDATE Order SET",
         "<if test = \" orderNo != null\">",
         "  orderNo = #{orderNo},",
         "</if>",
         "<if test = \" submitTime != null\">",
         "submitTime = #{submitTime},",
         "</if>",
         "<if test = \"supplierName != null\">",
         "  supplierName = #{supplierName},",
         "</if>",
         "<if test = \"sku != null\">",
         "  sku = #{sku},",
         "</if>",
         "<if test = \"artNo != null\">",
         "  artNo = #{artNo},",
         "</if>",
         "<if test = \"brand != null\">",
         "  orderNo = #{brand},",
         "</if>",
         "<if test = \"size != null\">",
         "  size = #{size},",
         "</if>",
         "<if test = \"color != null\">",
         "  color = #{color},",
         "</if>",
         "<if test = \"season != null\">",
         "  season = #{season},",
         "</if>",
         "<if test = \"price != null\">",
         "  price = #{price},",
         "</if>",
         "<if test = \"retailPrice != null\">",
         "  retailPrice = #{retailPrice},",
         "</if>",
         "<if test = \"orderStatus != null\">",
         "  orderStatus = #{orderStatus},",
         "</if>",
         "<if test = \"supplierDeliveryStatus != null\">",
         "  supplierDeliveryStatus = #{supplierDeliveryStatus},",
         "</if>",
         "<if test = \"supplierDeliveryNo != null\">",
         "  supplierDeliveryNo = #{supplierDeliveryNo},",
         "</if>",
         "<if test = \"supplierBarCode != null\">",
         "  supplierBarCode = #{supplierBarCode},",
         "</if>",
         "<if test = \"supplierTime != null\">",
         "  supplierTime = #{supplierTime},",
         "</if>",
         "<if test = \"warehouseBarCode != null\">",
         "  warehouseBarCode = #{warehouseBarCode},",
         "</if>",
         "<if test = \"quality != null\">",
         "  quality = #{quality},",
         "</if>",
         "<if test = \"warehouseStatus != null\">",
         "  warehouseStatus = #{warehouseStatus},",
         "</if>",
         "<if test = \"warehouseDeliveryNo != null\">",
         "  warehouseDeliveryNo = #{warehouseDeliveryNo},",
         "</if>",
         "<if test = \"warehouseTime != null\">",
         "  warehouseTime = #{warehouseTime},",
         "</if>",
         "WHERE id = #{id}",
         "</script>",
     })
     Integer UpdateOrder(OrderDTO order);
}
