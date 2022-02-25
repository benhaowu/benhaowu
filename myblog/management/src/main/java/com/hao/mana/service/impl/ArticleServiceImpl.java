package com.hao.mana.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hao.mana.entity.Article;
import com.hao.mana.entity.query.ArticleVo;
import com.hao.mana.entity.query.keywordVo;
import com.hao.mana.mapper.ArticleMapper;
import com.hao.mana.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author bentou
 * @since 2021-01-13
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public Page<ArticleVo> getArticleDetail(Page<ArticleVo> page) {
        return page.setRecords(this.baseMapper.getArticleDetail(page));
    }

    @Override
    public List<Article> getKeywords() {
        return this.baseMapper.getKeywords();
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public Page<ArticleVo> getArticleByKeyword(Page<ArticleVo> page, String keyword) {
        return page.setRecords(this.baseMapper.getArticleByKeyword(page,keyword));
    }

    @Override
    public int countArticles() {
        return this.baseMapper.countArticles();
    }

    @Override
    public boolean updateKeyword(String oldkeyword,String newkeyword) {
        return this.baseMapper.updateKeyword(oldkeyword,newkeyword);
    }

    @Override
    public keywordVo[] getEchartsByKeyword() {
        return this.baseMapper.getEchartsByKeyword();
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public Page<ArticleVo> getArticleBySkill(Page<ArticleVo> page, String keyword) {
        return page.setRecords(this.baseMapper.getArticleBySkill(page,keyword));
    }

}
