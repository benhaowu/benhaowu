package com.hao.mana.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hao.mana.entity.Resources;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hao.mana.entity.query.ArticleVo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author bentou
 * @since 2021-03-29
 */
public interface ResourcesMapper extends BaseMapper<Resources> {

    @Select("SELECT * from resources order by id desc")
    List<Resources> getResourceDetail(Page<Resources> page);
}
