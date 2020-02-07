package com.linln.admin.buss.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.linln.admin.buss.mapper.AldProductMapper;
import com.linln.admin.buss.mapper.AldSkuMapper;
import com.linln.admin.buss.model.AldProduct;
import com.linln.admin.buss.model.AldResult;
import com.linln.admin.buss.model.AldSkuSize;

@Component
public class AldTask {
	
	@Autowired
	AldProductMapper dressProductMapper;
	
	@Autowired
	AldSkuMapper dressSkuMapper;
	
//	@Scheduled(cron = "0 0/2 * * * ?")
	@Scheduled(fixedDelay=1000*60*40)
	public void fetchProduct() {
		
		System.err.println(new Date().toLocaleString()+"adda商品更新开始"+System.currentTimeMillis());

		String url = "https://api.dresscode.cloud/channels/v2/api/feeds/en/clients/adda/products?channelKey=c05b4b60-a34e-4a06-81e1-9d57d047d017";
		HashMap<String, String> head = new HashMap<String,String>();
		head.put("Ocp-Apim-Subscription-Key", "107b04efec074c6f8f8abed90d224802");
		try {
			String sendGetRequest = HttpClientUtil.sendGetRequest(url, 600000, head);
			AldResult result = JSONObject.parseObject(sendGetRequest, AldResult.class);
			List<AldProduct> dressProductList = result.getData();
				
			if (CollectionUtils.isEmpty(dressProductList)) {
				return;
			}
			Date date = new Date();
			for (AldProduct dressProduct : dressProductList) {
				try {
					Integer count = dressProductMapper.count(dressProduct.getProductID());
					if (count == 1) {
						// 更新数据, 此处不处理sku
						dressProductMapper.updateByProductID(dressProduct);
					}else{
						// 插入
						dressProductMapper.insert(dressProduct);
						List<AldSkuSize> sizes = dressProduct.getSizes();
						for (AldSkuSize dressSkuSize : sizes) {
							dressSkuSize.setProductID(dressProduct.getProductID());
							dressSkuSize.setCreateTime(date);
						}
						dressSkuMapper.batchInsert(sizes);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        System.err.println(new Date().toLocaleString()+"adda商品更新结束"+System.currentTimeMillis());
	}
	
}