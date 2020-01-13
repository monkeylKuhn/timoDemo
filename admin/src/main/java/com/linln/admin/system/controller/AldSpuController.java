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
import com.linln.admin.buss.mapper.AldSpuExportMapper;
import com.linln.admin.buss.util.EasyPoiUtils;
import com.linln.admin.system.validator.AldSpuValid;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.modules.system.domain.AldSpu;
import com.linln.modules.system.service.AldSpuService;

/**
 * @author 小懒虫
 * @date 2020/01/13
 */
@Controller
@RequestMapping("/system/aldSpu")
public class AldSpuController {

    @Autowired
    private AldSpuService aldSpuService;
    @Autowired
    AldSpuExportMapper aldSpuExportMapper;
    /**
     * 列表页面
     */
    @GetMapping("/index")
    @RequiresPermissions("system:aldSpu:index")
    public String index(Model model, AldSpu aldSpu) {

        // 创建匹配器，进行动态查询匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("sku", match -> match.contains());

        // 获取数据列表
        Example<AldSpu> example = Example.of(aldSpu, matcher);
        Page<AldSpu> list = aldSpuService.getPageList(example);

        // 封装数据
        model.addAttribute("list", list.getContent());
        model.addAttribute("page", list);
        return "/system/aldSpu/index";
    }

    /**
     * 跳转到添加页面
     */
    @GetMapping("/add")
    @RequiresPermissions("system:aldSpu:add")
    public String toAdd() {
        return "/system/aldSpu/add";
    }

    /**
     * 跳转到编辑页面
     */
    @GetMapping("/edit/{id}")
    @RequiresPermissions("system:aldSpu:edit")
    public String toEdit(@PathVariable("id") AldSpu aldSpu, Model model) {
        model.addAttribute("aldSpu", aldSpu);
        return "/system/aldSpu/add";
    }

    /**
     * 保存添加/修改的数据
     * @param valid 验证对象
     */
    @PostMapping("/save")
    @RequiresPermissions({"system:aldSpu:add", "system:aldSpu:edit"})
    @ResponseBody
    public ResultVo save(@Validated AldSpuValid valid, AldSpu aldSpu) {
        // 复制保留无需修改的数据
        if (aldSpu.getId() != null) {
            AldSpu beAldSpu = aldSpuService.getById(aldSpu.getId());
            EntityBeanUtil.copyProperties(beAldSpu, aldSpu);
        }

        // 保存数据
        aldSpuService.save(aldSpu);
        return ResultVoUtil.SAVE_SUCCESS;
    }

    /**
     * 跳转到详细页面
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("system:aldSpu:detail")
    public String toDetail(@PathVariable("id") AldSpu aldSpu, Model model) {
        model.addAttribute("aldSpu",aldSpu);
        return "/system/aldSpu/detail";
    }

    /**
     * 设置一条或者多条数据的状态
     */
    @RequestMapping("/status/{param}")
    @RequiresPermissions("system:aldSpu:status")
    @ResponseBody
    public ResultVo status(
            @PathVariable("param") String param,
            @RequestParam(value = "ids", required = false) List<Long> ids) {
        // 更新状态
        StatusEnum statusEnum = StatusUtil.getStatusEnum(param);
        if (aldSpuService.updateStatus(statusEnum, ids)) {
            return ResultVoUtil.success(statusEnum.getMessage() + "成功");
        } else {
            return ResultVoUtil.error(statusEnum.getMessage() + "失败，请重新操作");
        }
    }
    
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        List<ExportDTO> skuExport = aldSpuExportMapper.export();
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