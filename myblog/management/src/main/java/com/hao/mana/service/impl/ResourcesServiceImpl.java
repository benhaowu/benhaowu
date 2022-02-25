package com.hao.mana.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hao.mana.entity.Resources;
import com.hao.mana.mapper.ResourcesMapper;
import com.hao.mana.service.ResourcesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author bentou
 * @since 2021-03-29
 */
@Service
public class ResourcesServiceImpl extends ServiceImpl<ResourcesMapper, Resources> implements ResourcesService {

    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.REPEATABLE_READ)
    public Page<Resources> getResourceDetail(Page<Resources> page) {
        return page.setRecords(this.baseMapper.getResourceDetail(page));
    }
}
