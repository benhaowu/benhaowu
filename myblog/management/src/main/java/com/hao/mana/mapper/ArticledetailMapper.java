package com.hao.mana.mapper;

import com.hao.mana.entity.Articledetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author bentou
 * @since 2021-01-19
 */
public interface ArticledetailMapper extends BaseMapper<Articledetail> {

    @Update("update articledetail set view = view+1 where id = #{id}")
    void upview(String id);

    @Select("select sum(view) from articledetail")
    int updateAllIndex();

    @Update("update articledetail set thumb_up = thumb_up + 1 where id = #{id}")
    void updateThumbUp(String id);

    @Select("select sum(thumb_up) from articledetail")
    int updateAllThumbUp();

    @Select("select id,view,title from articledetail order by view desc limit 5")
    List<Articledetail> getHotList();
}
