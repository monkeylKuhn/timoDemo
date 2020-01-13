package com.linln.modules.system.service.impl;

import com.linln.common.data.PageSort;
import com.linln.common.enums.StatusEnum;
import com.linln.modules.system.domain.AldSpu;
import com.linln.modules.system.repository.AldSpuRepository;
import com.linln.modules.system.service.AldSpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 小懒虫
 * @date 2020/01/13
 */
@Service
public class AldSpuServiceImpl implements AldSpuService {

    @Autowired
    private AldSpuRepository aldSpuRepository;

    /**
     * 根据ID查询数据
     * @param id 主键ID
     */
    @Override
    @Transactional
    public AldSpu getById(Long id) {
        return aldSpuRepository.findById(id).orElse(null);
    }

    /**
     * 获取分页列表数据
     * @param example 查询实例
     * @return 返回分页数据
     */
    @Override
    public Page<AldSpu> getPageList(Example<AldSpu> example) {
        // 创建分页对象
        PageRequest page = PageSort.pageRequest();
        return aldSpuRepository.findAll(example, page);
    }

    /**
     * 保存数据
     * @param aldSpu 实体对象
     */
    @Override
    public AldSpu save(AldSpu aldSpu) {
        return aldSpuRepository.save(aldSpu);
    }

    /**
     * 状态(启用，冻结，删除)/批量状态处理
     */
    @Override
    @Transactional
    public Boolean updateStatus(StatusEnum statusEnum, List<Long> idList) {
        return aldSpuRepository.updateStatus(statusEnum.getCode(), idList) > 0;
    }
}