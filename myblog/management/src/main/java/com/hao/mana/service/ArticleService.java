package com.hao.mana.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hao.mana.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hao.mana.entity.query.ArticleVo;
import com.hao.mana.entity.query.keywordVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author bentou
 * @since 2021-01-13
 */
public interface ArticleService extends IService<Article> {
    Page<ArticleVo> getArticleDetail(Page<ArticleVo> page);

    List<Article> getKeywords();

    Page<ArticleVo> getArticleByKeyword(Page<ArticleVo> page, String keyword);

    int countArticles();

    boolean updateKeyword(String oldkeyword,String newkeyword);

    keywordVo[] getEchartsByKeyword();

    Page<ArticleVo> getArticleBySkill(Page<ArticleVo> page, String keyword);
}
