package com.hao.mana.service.impl;

import com.hao.mana.entity.Bloginfo;
import com.hao.mana.mapper.BloginfoMapper;
import com.hao.mana.service.BloginfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author bentou
 * @since 2021-01-24
 */
@Service
public class BloginfoServiceImpl extends ServiceImpl<BloginfoMapper, Bloginfo> implements BloginfoService {

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public void addIndexView() {
        this.baseMapper.addIndexView();
    }
}
