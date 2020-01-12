package com.linln.admin.buss.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.linln.admin.buss.mapper.DressProductMapper;
import com.linln.admin.buss.mapper.DressSkuMapper;
import com.linln.admin.buss.model.DressProduct;
import com.linln.admin.buss.model.DressResult;
import com.linln.admin.buss.model.DressSkuSize;
import com.linln.modules.system.repository.DressSpuRepository;
import com.linln.modules.system.service.DressSpuService;

@Component
public class TestTask {
	
	@Autowired
	DressProductMapper dressProductMapper;
	
	@Autowired
	DressSkuMapper dressSkuMapper;
	
//	@Scheduled(cron = "0 0/2 * * * ?")
//	@Scheduled(fixedDelay=1000*60*10)
	public void fetchProduct() {
		
		System.err.println(new Date().toLocaleString()+"商品更新开始"+System.currentTimeMillis());

		String url = "https://api.dresscode.cloud/channels/v2/api/feeds/en/clients/llf/products?channelKey=0198873e-1fde-4783-8719-4f1d0790eb6e";
		HashMap<String, String> head = new HashMap<String,String>();
		head.put("Ocp-Apim-Subscription-Key", "107b04efec074c6f8f8abed90d224802");
		try {
			String sendGetRequest = HttpClientUtil.sendGetRequest(url, 600000, head);
			DressResult result = JSONObject.parseObject(sendGetRequest, DressResult.class);
			List<DressProduct> dressProductList = result.getData();
				
			if (CollectionUtils.isEmpty(dressProductList)) {
				return;
			}
			Date date = new Date();
			for (DressProduct dressProduct : dressProductList) {
				try {
					Integer count = dressProductMapper.count(dressProduct.getProductID());
					if (count == 1) {
						// 更新数据, 此处不处理sku
						dressProductMapper.updateByProductID(dressProduct);
					}else{
						// 插入
						dressProductMapper.insert(dressProduct);
						List<DressSkuSize> sizes = dressProduct.getSizes();
						for (DressSkuSize dressSkuSize : sizes) {
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
        System.err.println(new Date().toLocaleString()+"商品更新结束"+System.currentTimeMillis());
	}
	
}