package com.hao.mana.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hao.mana.entity.Article;
import com.hao.mana.entity.Articledetail;
import com.hao.mana.entity.query.ArticleVo;
import com.hao.mana.service.ArticleService;
import com.hao.mana.service.ArticledetailService;
import com.hao.mana.utils.R;
import com.hao.mana.utils.RedisUtil;
import com.hao.mana.utils.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author bentou
 * @since 2021-01-19
 */
@RestController
@RequestMapping("/mana/articledetail")
@CrossOrigin
@Slf4j
public class ArticledetailController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticledetailService articledetailService;

    private Articledetail articledetail;

    private Page<ArticleVo> ArticleKeywordPage;

    private List<Articledetail> hotList;

    private long total;

    private List<ArticleVo> records;

    @Autowired
    private RedisUtil redisUtil;

    private Jedis jedis;

    //private String key;

    //创建等待队列
    private final BlockingQueue<Runnable> bqueue = new ArrayBlockingQueue<>(20);
    private final ExecutorService exec = new ThreadPoolExecutor(2,4,60, TimeUnit.SECONDS,bqueue,new ThreadPoolExecutor.DiscardPolicy());


//    QueryWrapper<Articledetail> wrapper = new QueryWrapper<>();

    //文章内容
    @GetMapping("getArticleDetail/{id}")
    public R getArticleDetail(@PathVariable String id){
        try{
            //多线程异步执行
            exec.execute(() -> articledetailService.upview(id));
        }catch (Exception e){
            log.info("getArticleDetail exception:",e);
        }
        Article article = articleService.getById(id);
        articledetail = articledetailService.getById(id);
        return R.ok().data("article",article).data("articledetail",articledetail);
    }

    //点赞
    @PostMapping("addThumbUp/{id}")
    public R addThumbUp(@PathVariable String id){
        try{
            //多线程异步执行
            exec.execute(() -> articledetailService.updateThumbUp(id));
        }catch (Exception e){
            log.info("addThumbUp exception:",e);
        }
        return R.ok();
    }

    //热门文章
    @GetMapping("getHotList")
    public R getHotList(){
        jedis = redisUtil.getJedis();
        jedis.auth("benhaowu");
        //jedis.flushDB();    //清空数据库 jedis.del("redisList");
        //key = "hotList";
        //存在缓存则读取，否则读数据库在写入缓存
        if(jedis.exists("hotList".getBytes())){
            hotList = SerializeUtil.unserializeForList(jedis.get("hotList".getBytes()));
            jedis.close();
        }else{
            hotList = articledetailService.getHotList();
            try{
                //多线程异步执行
                exec.execute(() -> {
                    jedis.set("hotList".getBytes(),SerializeUtil.serialize(hotList));
                    jedis.expire("hotList".getBytes(),60*60*12);
                    log.info("生成 hotList by redis");
                    jedis.close();
                });
            }catch (Exception e){
                log.info("addThumbUp exception:",e);
            }
        }
        return R.ok().data("hotList",hotList);
    }


    //主页分页条件查询列表
    @GetMapping("pageListArticleByKeyword/{current}/{limit}/{keyword}")
    public R pageListArticle(@PathVariable long current,
                             @PathVariable long limit,
                             @PathVariable String keyword) {
        if(keyword.equals("技术文章")){
            ArticleKeywordPage = articleService.getArticleBySkill(new Page<>(current, limit),keyword);
        }else {
            ArticleKeywordPage = articleService.getArticleByKeyword(new Page<>(current, limit),keyword);
        }
        total = ArticleKeywordPage.getTotal();//总记录数
        records = ArticleKeywordPage.getRecords(); //数据list集合
        return R.ok().data("total", total).data("rows", records);
    }
}

