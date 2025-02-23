 package com.linln.admin.buss.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.linln.admin.buss.DTO.AldExportDTO;

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
        " dp.is_carry_over  AS isCarryOver, " + 
        " dp.color , " + 
        " dp.retail_price AS retailPrice, " + 
        " dp.price , " + 
        " dp.prices_include_vat  AS pricesIncludeVat, " + 
        " dp.product_last_updated AS productLastUpdated, " + 
        " ds.size , " + 
        " ds.stock , " + 
        " dp.photos,  " + 
        " dp.size_and_fit AS sizeAndFit,  " + 
        " dp.client_productid AS clientProductId  "+
        "FROM " + 
        " s_ald_spu AS dp " + 
        "RIGHT JOIN aldskusize AS ds ON dp.productID = ds.productID "+
        "WHERE dp.id IS NOT NULL"
        
    })
    List<AldExportDTO> export() ;

     
     
}
