package com.hao.mana.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hao.mana.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hao.mana.entity.query.ArticleVo;
import com.hao.mana.entity.query.keywordVo;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author bentou
 * @since 2021-01-13
 */
public interface ArticleMapper extends BaseMapper<Article> {

    @Select("SELECT article.id,article.title,cover_image,description,create_time,view,thumb_up,commit_sum " +
            "FROM article LEFT JOIN articledetail on article.id=articledetail.id order by id desc")
    List<ArticleVo> getArticleDetail(Page<ArticleVo> page);

    @Select("SELECT article.id,article.title,cover_image,description,create_time,view,thumb_up,commit_sum " +
            "FROM article LEFT JOIN articledetail on article.id=articledetail.id where keywords not regexp '笔记|杂言杂语' order by id desc")
    List<ArticleVo> getArticleBySkill(Page<ArticleVo> page, String keyword);

    @Select("select distinct keywords from article")
    List<Article> getKeywords();

    @Select("select count(keywords) as value,keywords as name from article group by keywords")
    keywordVo[] getEchartsByKeyword();

    @Select("SELECT article.id,article.title,cover_image,description,create_time,view,thumb_up,commit_sum " +
            "FROM article LEFT JOIN articledetail on article.id=articledetail.id where (keywords like concat('%',#{keyword},'%') or article.title like concat('%',#{keyword},'%')) order by id desc")
    List<ArticleVo> getArticleByKeyword(Page<ArticleVo> Page, String keyword);

    @Select("select count(*) from article")
    int countArticles();

    @Update(" update article set keywords = #{newkeyword} where keywords = #{oldkeyword}")
    boolean updateKeyword(String oldkeyword,String newkeyword);
}
