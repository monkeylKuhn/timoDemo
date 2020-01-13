package com.linln.admin.system.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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

import com.linln.admin.buss.DTO.ExportDTO;
import com.linln.admin.buss.mapper.DressSpuMapper;
import com.linln.admin.buss.util.EasyPoiUtils;
import com.linln.admin.system.validator.DressSpuValid;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.modules.system.domain.DressSpu;
import com.linln.modules.system.service.DressSpuService;

/**
 * @author 小懒虫
 * @date 2020/01/11
 */
@Controller
@RequestMapping("/system/dressSpu")
public class DressSpuController {

    @Autowired
    private DressSpuService dressSpuService;
    
    @Autowired
    private DressSpuMapper dressSpuMapper;

    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:dressSpu:index")
    public String index(Model model, DressSpu dressSpu) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching();

        // 获取数据列表
        Example<DressSpu> example = Example.of(dressSpu, matcher);
        Page<DressSpu> list = dressSpuService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/system/dressSpu/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:dressSpu:add")
    public String toAdd() {
        return "/system/dressSpu/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:dressSpu:edit")
    public String toEdit(@PathVariable("id") DressSpu dressSpu, Model model) {
        model.addAttribute("dressSpu", dressSpu);
        return "/system/dressSpu/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:dressSpu:add", "system:dressSpu:edit"})
    @ResponseBody
    public ResultVo save(@Validated DressSpuValid valid, DressSpu dressSpu) {
        // 复制保留无需修改的数据
        if (dressSpu.getId() != null) {
            DressSpu beDressSpu = dressSpuService.getById(dressSpu.getId());
            EntityBeanUtil.copyProperties(beDressSpu, dressSpu);
        }

        // 保存数据
        dressSpuService.save(dressSpu);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:dressSpu:detail")
    public String toDetail(@PathVariable("id") DressSpu dressSpu, Model model) {
        model.addAttribute("dressSpu",dressSpu);
        return "/system/dressSpu/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:dressSpu:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (dressSpuService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
    
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        List<ExportDTO> skuExport = dressSpuMapper.export();
        for (ExportDTO exportDTO : skuExport) {
            if (exportDTO.getPhotos() != null && !exportDTO.getPhotos().isEmpty()) {
                String[] urls = exportDTO.getPhotos().split("\\^");
                if (urls.length >= 1) {
                    exportDTO.setUrl1(urls[0]);
                    if (urls.length >= 2) {
                        exportDTO.setUrl2(urls[1]);
                        if (urls.length >= 3) {
                            exportDTO.setUrl3(urls[2]);
                            if (urls.length >= 4) {
                                exportDTO.setUrl4(urls[3]);
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
}