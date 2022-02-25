package com.hao.mana.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hao.mana.entity.Resources;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author bentou
 * @since 2021-03-29
 */
public interface ResourcesService extends IService<Resources> {

    Page<Resources> getResourceDetail(Page<Resources> page);
}
