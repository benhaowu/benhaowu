package com.hao.mana.controller;

import com.hao.mana.entity.Bloginfo;
import com.hao.mana.entity.query.keywordVo;
import com.hao.mana.service.ArticleService;
import com.hao.mana.service.ArticledetailService;
import com.hao.mana.service.BloginfoService;
import com.hao.mana.utils.R;
import com.hao.mana.utils.RedisUtil;
import com.hao.mana.utils.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author bentou
 * @since 2021-01-24
 */
@RestController
@RequestMapping("/mana/bloginfo")
@EnableScheduling
@CrossOrigin
@Slf4j
public class BloginfoController {

    @Autowired
    private BloginfoService bloginfoService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticledetailService articledetailService;

    private Bloginfo bloginfo;

    private boolean flag;

    private int countArticles;

    //private int updateAllIndex;

    private int updateAllThumbUp;

    @Autowired
    private RedisUtil redisUtil;

    private Jedis jedis;

    private List<Bloginfo> bloginfos = new ArrayList<>();

    //private String key;

    //创建等待队列
    private final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10);
    private final ExecutorService exec = new ThreadPoolExecutor(2,4,60, TimeUnit.SECONDS,queue,new ThreadPoolExecutor.DiscardPolicy());


    //获取网站信息
    @GetMapping("getBlogInfo")
    public R getBlogInfo(){
        jedis = redisUtil.getJedis();
        jedis.auth("benhaowu");
        //jedis.flushDB();    //清空数据库 jedis.del("redisList");
        //key = "blogInfoList";
        //存在缓存则读取，否则读数据库在写入缓存
        if(jedis.exists("blogInfoList".getBytes())){
            bloginfos = SerializeUtil.unserializeForList(jedis.get("blogInfoList".getBytes()));
            bloginfo = bloginfos.get(0);
            jedis.close();
        }else{
            bloginfo = bloginfoService.getById(1);
            bloginfos.add(0,bloginfo);
            try{
                //多线程异步执行
                exec.execute(() -> {
                    jedis.set("blogInfoList".getBytes(),SerializeUtil.serialize(bloginfos));
                    jedis.expire("blogInfoList".getBytes(),60*5);
                    log.info("生成 blogInfoList by redis");
                    jedis.close();
                });
            }catch (Exception e){
                log.info("getBlogInfo exception:",e);
            }
        }

        return R.ok().data("bloginfo",bloginfo);
    }

    @PostMapping("updateBlogInfo")
    public R updateBlogInfo(@RequestBody Bloginfo bloginfo){
        boolean flag = bloginfoService.updateById(bloginfo);
        if(flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }
//    select count(keywords),keywords from article group by keywords;

    @GetMapping("getEchartsByKeyword")
    public R getEchartsByKeyword(){
        keywordVo[] keywordVos = articleService.getEchartsByKeyword();
        return R.ok().data("keywordVos",keywordVos);
    }

    @Scheduled(cron = "0 0 0 */1 * ?")
    public void updateBlogInfo(){
        bloginfo =  bloginfoService.getById(1);
        countArticles = articleService.countArticles();//统计文章数
        //updateAllIndex = articledetailService.updateAllIndex();//更新预览数
        updateAllThumbUp = articledetailService.updateAllThumbUp();
        bloginfo.setRuntime(bloginfo.getRuntime()+1);//更新运行天数
        bloginfo.setArticlenum(countArticles);
        bloginfo.setThumbupnum(updateAllThumbUp);
        bloginfo.setIndexview(bloginfo.getIndexview());
        bloginfoService.updateById(bloginfo);
    }
}

