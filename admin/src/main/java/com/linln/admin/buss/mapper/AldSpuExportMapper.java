 package com.linln.admin.buss.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.linln.admin.buss.DTO.ExportDTO;

public interface AldSpuExportMapper {

    
    @Select({
        "SELECT " + 
        " dp.productid AS productID, " + 
        " dp.sku , " + 
        " dp.brand , " + 
        " dp.`name` , " + 
        " dp.description , " + 
        " dp.genre , " + 
        " CONCAT(dp.productid,'_',ds.size) AS skuId, " + 
        " CONCAT(dp.genre,'+',dp.type) AS fristCategory, " + 
        " CONCAT(dp.genre,'+',dp.type,'+',dp.category) AS skuCategory, " + 
        " dp.made_in as madeIn, " + 
        " dp.composition , " + 
        " dp.season , " + 
        " IF(dp.is_carry_over,\"是\",\"否\") AS isCarryOver, " + 
        " dp.color , " + 
        " dp.retail_price AS retailPrice, " + 
        " dp.price , " + 
        " IF(dp.prices_include_vat,\"是\",\"否\") AS pricesIncludeVat, " + 
        " dp.product_last_updated AS productLastUpdated, " + 
        " ds.size , " + 
        " ds.stock , " + 
        " dp.photos  " + 
        "FROM " + 
        " s_ald_spu AS dp " + 
        "RIGHT JOIN aldskusize AS ds ON dp.productID = ds.productID;"
    })
    List<ExportDTO> export() ;

     
     
}
