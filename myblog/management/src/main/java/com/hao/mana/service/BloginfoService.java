package com.hao.mana.service;

import com.hao.mana.entity.Bloginfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author bentou
 * @since 2021-01-24
 */
public interface BloginfoService extends IService<Bloginfo> {

    void addIndexView();
}
