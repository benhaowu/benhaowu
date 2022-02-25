package com.hao.mana.mapper;

import com.hao.mana.entity.Bloginfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author bentou
 * @since 2021-01-24
 */
public interface BloginfoMapper extends BaseMapper<Bloginfo> {

    @Update("update bloginfo set indexview = indexview+1")
    void addIndexView();

}
