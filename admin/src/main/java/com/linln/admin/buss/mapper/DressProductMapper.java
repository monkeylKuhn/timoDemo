 package com.linln.admin.buss.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.linln.admin.buss.DTO.ExportDTO;
import com.linln.admin.buss.model.DressProduct;

public interface DressProductMapper {


     @Select({
       "SELECT COUNT(1) FROM s_dress_spu WHERE productid = #{productId}"
     })
    Integer count(String productId);
     
     
     @Update({
       "UPDATE  s_dress_spu SET ",
       "    spu = #{spu},",
       "    sku = #{sku}, ",
       "    brand = #{brand}, ",
       "    name = #{name}, ",
       "    description = #{description}, ",
       "    season = #{season}, ",
       "    is_carry_over = #{isCarryOver}, ",
       "    retail_price = #{retailPrice}, ",
       "    price = #{price}, ",
       "    prices_include_vat = #{pricesIncludeVat}, ",
       "    product_last_updated = #{productLastUpdated}, ",
       "    photos = #{photos} ",
       "WHERE productid = #{productID}"
     })
     Integer updateByProductID(DressProduct dressProduct);     
     
     @Insert({
         "<script>",
         "INSERT INTO s_dress_spu(productid,client_productid,spu,sku,brand,name,description,genre,type,",
         "category,season,is_carry_over,color,retail_price,price,prices_include_vat,product_last_updated,photos,made_in,composition)  ",
         "values(#{productID},#{clientProductID},#{spu},#{sku},#{brand},#{name},#{description},#{genre},#{type},",
         "#{category},#{season},#{isCarryOver},#{color},#{retailPrice},#{price},#{pricesIncludeVat},#{productLastUpdated},#{photos},#{madeIn},#{composition})",
         "</script>"
     })
     @Options(useGeneratedKeys = true, keyProperty = "id")
     int insert(DressProduct dressProduct);


     
     @Select({
         "SELECT ",
         " dp.productid , ",
         " dp.sku , ",
         " dp.brand , ",
         " dp.`name` , ",
         " dp.description , ",
         " dp.genre , ",
         " CONCAT(dp.productid,'_',ds.size) AS skuId, ",
         " CONCAT(dp.genre,'+',dp.type) AS fristCategory, ",
         " CONCAT(dp.genre,'+',dp.type,'+',dp.category) AS skuCategory, ",
         " dp.made_in , ",
         " dp.composition , ",
         " dp.season , ",
         " IF(dp.is_carry_over,'是','否') AS isCarryOver, ",
         " dp.color , ",
         " dp.retail_price , ",
         " dp.price , ",
         " IF(dp.prices_include_vat,\"是\"  ,\"否\") AS pricesIncludeVat, ",
         " dp.product_last_updated , ",
         " ds.size , ",
         " ds.stock , ",
         " dp.photos  ",
         "FROM ",
         " s_dress_spu AS dp ",
         "RIGHT JOIN dressskusize AS ds ON dp.productid = ds.productID"
     })
     List<ExportDTO> export();
}
