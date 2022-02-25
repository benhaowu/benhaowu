package com.hao.mana.service;

import com.hao.mana.entity.Articledetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author bentou
 * @since 2021-01-19
 */
public interface ArticledetailService extends IService<Articledetail> {
    void upview(String id);

    int updateAllIndex();

    void updateThumbUp(String id);

    int updateAllThumbUp();

    List<Articledetail> getHotList();
}
