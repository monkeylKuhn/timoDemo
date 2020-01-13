package com.linln.admin.system.controller;

import com.linln.admin.system.validator.AldSpuValid;
import com.linln.common.enums.StatusEnum;
import com.linln.common.utils.EntityBeanUtil;
import com.linln.common.utils.ResultVoUtil;
import com.linln.common.utils.StatusUtil;
import com.linln.common.vo.ResultVo;
import com.linln.modules.system.domain.AldSpu;
import com.linln.modules.system.service.AldSpuService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/01/13
 */
@Controller
@RequestMapping("/system/aldSpu")
public class AldSpuController {

    @Autowired
    private AldSpuService aldSpuService;

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
}