package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.DressSpu;
import com.linln.modules.system.repository.DressSpuRepository;
import com.linln.modules.system.service.DressSpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/01/11
 */
@Service
public class DressSpuServiceImpl implements DressSpuService {

    @Autowired
    private DressSpuRepository dressSpuRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public DressSpu getById(Long id) {
        return dressSpuRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<DressSpu> getPageList(Example<DressSpu> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return dressSpuRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param dressSpu 实体对象
     */
    @Override
    public DressSpu save(DressSpu dressSpu) {
        return dressSpuRepository.save(dressSpu);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return dressSpuRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}