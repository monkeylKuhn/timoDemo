 package com.linln.admin.buss.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.linln.admin.buss.model.DressSkuSize;
import com.linln.admin.buss.model.DressStock;

public interface DressSkuMapper {

     @Insert({
         "<script>",
         "INSERT INTO dressskusize(productID,size,stock,retailPrice,price,createTime)  ",
         "<foreach collection='list' item='item' open='VALUES' close='' separator=','>", 
         "(#{item.productID},#{item.size},#{item.stock},#{item.retailPrice},#{item.price},#{item.createTime})",
         "</foreach>", 
         "</script>"
     })
     int batchInsert(List<DressSkuSize> list);
     
     @Insert({
         "<script>",
         "INSERT INTO dressskusize(productID,size,stock,retailPrice,price,status)  values",
         "(#{productID},#{size},#{stock},#{retailPrice},#{price},6)",
         "</script>"
     })
     int insert(DressStock dressStock);
     
     // 将所有数据设置为更新中状态
     @Update({
         "<script>",
         "UPDATE dressskusize SET status = #{status}",
         "</script>"
     })
     int updateStatus(@Param("status")int status);
     
     @Update({
         "<script>",
         "UPDATE dressskusize SET stock = 0, status = 0 WHERE status = #{status}",
         "</script>"
     })
     int updateStatusByStock(@Param("status")int status);
     
     // 将所有数据设置为更新中状态
     @Update({
         "<script>",
         "UPDATE dressskusize SET status = #{status} WHERE id = #{id}",
         "</script>"
     })
     int updateStatusById(@Param("status")int status,@Param("id")Long id);
     
     
     @Update({
         "<script>",
         " UPDATE dressskusize ",
         "<set>",
         " <if test=\" stock != null \">", " stock = #{stock},", "</if>",
         " <if test=\" retailPrice != null \">", " retailPrice = #{retailPrice},", "</if>",
         " <if test=\" price != null \">", " price = #{price},", "</if>",
         " <if test=\" status != null \">", " status = #{status},", "</if>",
         "</set>",
         " WHERE id = #{id}",
         "</script>",
     })
     Integer update(DressSkuSize dressSkuSize);     
     
     @Select({
         "SELECT id,productID,size,stock,retailPrice,price FROM dressskusize WHERE productID = #{productId} and size = #{size}"
       })
     DressSkuSize selectByProductIDandSize(@Param("productId")String productId,@Param("size")String size);
     
     @Select({
             "<script>",
             " SELECT ",
             "productID,size,stock",
             " FROM",
             "   dressskusize",
             "<where>",
             " <if test=\" stock != null \">", " stock > #{stock}", "</if>",             
             "<if test=\"sList!=null and sList.size()>0 \">",
             " and status in",
             "<foreach item=\"item\" index=\"index\" collection=\"sList\" open=\"(\" separator=\",\" close=\")\">",
             "   #{item}",
             "</foreach>",
             "</if>",
             "</where>",
             "</script>"

     })
     List<DressSkuSize> selectByStatusAndStock(@Param("stock") String stock, @Param("sList") Set<Integer> statusList);


}