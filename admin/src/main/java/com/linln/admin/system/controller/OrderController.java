package com.linln.admin.system.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.linln.admin.buss.mapper.OrderMapper;
import com.linln.admin.buss.mapper.RoleMapper;
import com.linln.admin.buss.model.OrderReturn;
import com.linln.admin.buss.model.ShippingAddress;
import com.linln.admin.buss.model.SubmitOrder;
import com.linln.admin.system.validator.OrderValid;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.modules.system.domain.Order;
import com.linln.modules.system.domain.User;
import com.linln.modules.system.service.OrderService;

/**
 * @author 小懒虫
 * @date 2020/01/12
 */
@Controller
@RequestMapping("/system/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:order:index")
    public String index(Model model, Order order) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        Subject subject = SecurityUtils.getSubject();
        User user = (User)subject.getPrincipal();
        String type = roleMapper.getRoleNameByUserId(user.getId());
        if (type.equals("supplier")) {
            order.setSupplierName(user.getUsername());
        }
        // 获取数据列表
        Example<Order> example = Example.of(order, matcher);
        Page<Order> list = orderService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        if (order.getSupplierName() != null) {
            return "/system/order/supplier";
        }
        if (type.equals("warehouse")) {
            return "/system/order/warehouse";
        }
        return "/system/order/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:order:add")
    public String toAdd() {
        return "/system/order/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:order:edit")
    public String toEdit(@PathVariable("id") Order order, Model model) {
        model.addAttribute("order", order);
        Subject subject = SecurityUtils.getSubject();
        User user = (User)subject.getPrincipal();
        String type = roleMapper.getRoleNameByUserId(user.getId());

        if (type.equals("supplier")) {
            return "/system/order/supplier-update";
        }
        if (type.equals("warehouse")) {
            return "/system/order/warehouse-update";
        }

        return "/system/order/update";
    }
    
    /**
     * 跳转到编辑页面
     */
    @GetMapping("/update/supplier/{id}")
    @RequiresPermissions("system:order:edit")
    public String upSupplierBarCode(@PathVariable("id") Order order, Model model) {
        model.addAttribute("order", order);
        return "/system/order/update-supplierBarCode";
    }
    
    /**
     * 跳转到编辑页面
     */
    @GetMapping("/update/warehouse/{id}")
    @RequiresPermissions("system:order:edit")
    public String upWarehouseBarCode(@PathVariable("id") Order order, Model model) {
        model.addAttribute("order", order);
        return "/system/order/update-warehouseBarCode";
    }


    /**
     * 保存添加/修改的数据
     * 
     * @param valid
     *            验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:order:add", "system:order:edit"})
    @ResponseBody
    public ResultVo save(@Validated OrderValid valid, Order order) {
        // 复制保留无需修改的数据
        if (order.getId() != null) {
            System.out.println("new "+order);
            Order beOrder = orderService.getById(order.getId());
            System.out.println("old"+beOrder);
            EntityBeanUtil.copyProperties(beOrder, order);
            order.setSupplierBarCode(beOrder.getSupplierBarCode());
            order.setSupplierDeliveryNo(beOrder.getSupplierDeliveryNo());
            order.setSupplierDeliveryStatus(beOrder.getSupplierDeliveryStatus());
            order.setSupplierTime(beOrder.getSupplierTime());
            order.setWarehouseBarCode(beOrder.getWarehouseBarCode());
            order.setWarehouseDeliveryNo(beOrder.getWarehouseDeliveryNo());
            order.setWarehouseStatus(beOrder.getWarehouseStatus());
            order.setWarehouseTime(beOrder.getWarehouseTime());
        }

        System.out.println("new "+order);
        Subject subject = SecurityUtils.getSubject();
        User user = (User)subject.getPrincipal();
        String type = roleMapper.getRoleNameByUserId(user.getId());
        if (type.equals("supplier") && order.getSupplierDeliveryStatus() != null
            && order.getSupplierDeliveryStatus().equals("shipped")) {
            order.setSupplierTime(LocalDateTime.now().plusHours(7L).toString());
        }
        if (type.equals("warehouse") && order.getWarehouseStatus() != null
            && order.getWarehouseStatus().equals("已发出")) {
            order.setWarehouseTime(LocalDateTime.now().plusHours(7L).toString());
        }
        // else {
        // this.submitOrder(order);
        // }

        // 保存数据
        orderService.save(order);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:order:detail")
    public String toDetail(@PathVariable("id") Order order, Model model) {
        model.addAttribute("order", order);
        return "/system/order/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:order:status")
    @ResponseBody
    public ResultVo status(@PathVariable("param") String param,
        @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (orderService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }

    private String submitOrder(Order param) {
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
}