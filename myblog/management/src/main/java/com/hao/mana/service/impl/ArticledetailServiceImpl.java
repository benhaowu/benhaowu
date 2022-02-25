package com.hao.mana.service.impl;

import com.hao.mana.entity.Articledetail;
import com.hao.mana.mapper.ArticledetailMapper;
import com.hao.mana.service.ArticledetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author bentou
 * @since 2021-01-19
 */
@Service
public class ArticledetailServiceImpl extends ServiceImpl<ArticledetailMapper, Articledetail> implements ArticledetailService {


    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public void upview(String id) {
        this.baseMapper.upview(id);
    }

    @Override
    public int updateAllIndex() {
        return this.baseMapper.updateAllIndex();
    }

    @Override
    public void updateThumbUp(String id) {
        this.baseMapper.updateThumbUp(id);
    }

    @Override
    public int updateAllThumbUp() {
        return this.baseMapper.updateAllThumbUp();
    }

    @Override
    public List<Articledetail> getHotList() {
        return this.baseMapper.getHotList();
    }
}
