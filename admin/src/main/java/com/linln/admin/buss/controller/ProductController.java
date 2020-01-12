package com.linln.admin.buss.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linln.admin.buss.DTO.ExportDTO;
import com.linln.admin.buss.DTO.OrderDTO;
import com.linln.admin.buss.DTO.SupplierOrderDTO;
import com.linln.admin.buss.DTO.WarehouseOrderDTO;
import com.linln.admin.buss.controller.param.CreateOrderParam;
import com.linln.admin.buss.controller.param.FindOrderListParam;
import com.linln.admin.buss.controller.param.FindSupplierOrderListParam;
import com.linln.admin.buss.controller.param.SupplierUpdateParam;
import com.linln.admin.buss.controller.param.WarehouseUpdateParam;
import com.linln.admin.buss.mapper.DressProductMapper;
import com.linln.admin.buss.mapper.OrderMapper;
import com.linln.admin.buss.model.DressProduct;
import com.linln.admin.buss.model.DressResult;
import com.linln.admin.buss.model.DressSkuSize;
import com.linln.admin.buss.model.OrderReturn;
import com.linln.admin.buss.model.ShippingAddress;
import com.linln.admin.buss.model.SubmitOrder;
import com.linln.admin.buss.task.HttpClientUtil;
import com.linln.admin.buss.task.TestStockTask;
import com.linln.admin.buss.task.TestTask;
import com.linln.admin.buss.util.EasyPoiUtils;
import com.linln.admin.buss.util.PageInfo;
import com.linln.admin.buss.util.UserUtil;
import com.linln.modules.system.domain.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private DressProductMapper dressProductMapper;

    @Autowired
    TestTask testTask;
    @Autowired
    TestStockTask testStockTask;
    
    @ApiOperation("spu")
    @GetMapping("/spu")
    public Object fetchSpu() {
        testTask.fetchProduct();
        return true;
    }

    @GetMapping("/sku")
    public Object fetchSku() {
    	User userInfo = UserUtil.getUserInfo();
//        testStockTask.fetchStock();
        return true;
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        List<ExportDTO> skuExport = dressProductMapper.export();
        for (ExportDTO exportDTO : skuExport) {
            if (exportDTO.getPhotos() != null && !exportDTO.getPhotos().isEmpty()) {
                String[] urls = exportDTO.getPhotos().split("\\^");
                if (urls.length >= 1) {
                    exportDTO.setUrl1(urls[0]);
                    if (urls.length >= 2) {
                        exportDTO.setUrl2(urls[1]);
                        if (urls.length >= 3) {
                            exportDTO.setUrl4(urls[2]);
                            if (urls.length >= 4) {
                                exportDTO.setUrl3(urls[3]);
                                if (urls.length >= 5) {
                                    exportDTO.setUrl5(urls[4]);
                                }
                            }
                        }
                    }
                }
            }
        }
        EasyPoiUtils.exportExcel(skuExport, "商品信息", "商品信息", ExportDTO.class, "商品信息.xls", response);
    }

    @PostMapping("/order")
    public String createOrder(@RequestBody CreateOrderParam createOrderParam) {
        // 获取商品信息 验证库存 价格是否发生改变
        DressProduct dressProduct = getDressProductBySku(createOrderParam.getSku());
        if (chechSku(dressProduct, createOrderParam)) {
            return "商品信息需要更新";
        }
        // 调用api接口锁商品
        String str = submitOrder(createOrderParam);
        if (!str.isEmpty()) {
            return str;
        }
        Integer flag = orderMapper.insert(createOrderParam);
        return flag.toString();
    }
    

    @PostMapping("/order/list")
    public PageInfo<OrderDTO> findOrderList(@RequestBody FindOrderListParam findOrderListParam) {
        PageInfo<OrderDTO> pageInfo = new PageInfo<>(findOrderListParam.getPageNum(), findOrderListParam.getPageSize());

        // 检索条件 排序
        pageInfo.setTotal(orderMapper.count());
        if (pageInfo.getTotal() > 0) {
            List<OrderDTO> orders = orderMapper.findList(pageInfo.getOffset(), pageInfo.getPageSize());
            pageInfo.setItems(orders);
        }
        return pageInfo;
    }

    // 供应商
    @PostMapping("/supplier/order/list")
    public PageInfo<SupplierOrderDTO>
        findSupplierOrderList(@RequestBody FindSupplierOrderListParam findSupplierOrderListParam) {
        PageInfo<SupplierOrderDTO> pageInfo =
            new PageInfo<>(findSupplierOrderListParam.getPageNum(), findSupplierOrderListParam.getPageSize());
        pageInfo.setTotal(orderMapper.supplierCount(findSupplierOrderListParam.getSupplierName()));
        if (pageInfo.getTotal() > 0) {
            List<SupplierOrderDTO> orders =
                orderMapper.findListBySupplierName(findSupplierOrderListParam.getSupplierName(),
                    findSupplierOrderListParam.getPageNum(), findSupplierOrderListParam.getPageSize());
            pageInfo.setItems(orders);
        }
        return pageInfo;
    }

    // 仓库
    @PostMapping("/warehouse/order/list")
    public PageInfo<WarehouseOrderDTO> findWarehouserOrderList(@RequestBody FindOrderListParam findOrderListParam) {
        PageInfo<WarehouseOrderDTO> pageInfo =
            new PageInfo<>(findOrderListParam.getPageNum(), findOrderListParam.getPageSize());
        pageInfo.setTotal(orderMapper.count());
        if (pageInfo.getTotal() > 0) {
            List<WarehouseOrderDTO> orders =
                orderMapper.findWarehouseList(pageInfo.getOffset(), pageInfo.getPageSize());
            pageInfo.setItems(orders);
        }
        return pageInfo;
    }

    @PostMapping("/supplier")
    public Boolean supplierUpdate(@RequestBody SupplierUpdateParam param) {
        OrderDTO order = new OrderDTO();
        order.setId(param.getId());
        order.setSupplierDeliveryStatus(param.getStatus());
        order.setSupplierDeliveryNo(param.getDeliveryNo());
        if (param.getStatus() == 1) {
            order.setSupplierTime(LocalDateTime.now().plusHours(7L));
        }
        return orderMapper.UpdateOrder(order) > 0;
    }

    @PostMapping("/warehouse")
    public Boolean warehouseUpdate(@RequestBody WarehouseUpdateParam param) {
        OrderDTO order = new OrderDTO();
        order.setId(param.getId());
        order.setWarehouseStatus(param.getStatus());
        order.setWarehouseDeliveryNo(param.getDeliveryNo());
        order.setQuality(param.getQuality());
        if (param.getStatus() == 1) {
            order.setWarehouseTime(LocalDateTime.now().plusHours(7L));
        }
        return orderMapper.UpdateOrder(order) > 0;
    }

    private String submitOrder(CreateOrderParam param) {
        SubmitOrder submit = new SubmitOrder();
        submit.setChannelOrderID(param.getOrderNo());
        submit.setChannelOrderCreated(LocalDateTime.now().toString());
        submit.setProductID(param.getSku());
        submit.setSize(param.getSize());
        submit.setSoldUnits(1);
        // 提交的价格 TODO
        submit.setUnitSellingPrice(Double.parseDouble(param.getPrice()));
        ShippingAddress shippingAddress = new ShippingAddress();
        BeanUtils.copyProperties(param, shippingAddress);
        submit.setShippingAddress(shippingAddress);

        HttpClient httpclient = HttpClients.createDefault();
        try {
            URIBuilder builder = new URIBuilder(
                "https://api.dresscode.cloud/channels/v2/api/feeds/en/clients/llf/orders/items?channelKey=0198873e-1fde-4783-8719-4f1d0790eb6e");
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", "107b04efec074c6f8f8abed90d224802");
            // Request body
            StringEntity reqEntity = new StringEntity(JSON.toJSONString(submit));
            request.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                OrderReturn returnDetail = JSONObject.parseObject(EntityUtils.toString(entity), OrderReturn.class);
                if (returnDetail.getError() != null)
                    return returnDetail.getError().getTitel();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    /**
     * 校验商品
     * 
     * @param dressProduct
     * @param dressProduct2
     * @return
     */
    private boolean chechSku(DressProduct dressProduct, CreateOrderParam createOrderParam) {
        if (!dressProduct.getPrice().equals(createOrderParam.getPrice())) {
            return true;
        }
        DressSkuSize dressSkuSize = new DressSkuSize();
        for (DressSkuSize size : dressProduct.getSizes()) {
            if (size.getSize().equals(createOrderParam.getSize()))
                dressSkuSize = size;
        }
        if (dressSkuSize == null || Integer.parseInt(dressSkuSize.getStock()) == 0) {
            return true;
        }
        return false;
    }


    /**
     * 获取对应商品校验
     * 
     * @param productId
     * @return
     */
    private DressProduct getDressProductBySku(String productId) {
        String url = "https://api.dresscode.cloud/channels/v2/api/feeds/en/clients/llf/products/" + productId
            + "?channelKey=0198873e-1fde-4783-8719-4f1d0790eb6e";
        HashMap<String, String> head = new HashMap<String, String>();
        head.put("Ocp-Apim-Subscription-Key", "107b04efec074c6f8f8abed90d224802");
        try {
            String sendGetRequest = HttpClientUtil.sendGetRequest(url, 25000, head);
            DressResult result = JSONObject.parseObject(sendGetRequest, DressResult.class);
            return result.getData().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
