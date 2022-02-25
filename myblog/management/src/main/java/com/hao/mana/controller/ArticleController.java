package com.hao.mana.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hao.mana.entity.Article;
import com.hao.mana.entity.Articledetail;
import com.hao.mana.entity.query.ArticleVo;
import com.hao.mana.entity.query.articleQuery;
import com.hao.mana.service.ArticleService;
import com.hao.mana.service.ArticledetailService;
import com.hao.mana.service.BloginfoService;
import com.hao.mana.service.OssService;
import com.hao.mana.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author bentou
 * @since 2021-01-13
 */

@Api(description="文章管理")
@RestController
@RequestMapping("/mana/article")
@CrossOrigin
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticledetailService articledetailService;

    @Autowired
    private BloginfoService bloginfoService;

    @Autowired
    private OssService ossService;

    @Autowired
    private BloginfoController bloginfoController;

    @Autowired
    private RedisUtil redisUtil;

    private Jedis jedis;

    private List<Article> keyword;

    private Article article;

    private Page<ArticleVo> ArticleDetailPage;

    private List<ArticleVo> records;

    private List<Article> timeline;
    //构建条件
    private QueryWrapper<Article> wrapper = new QueryWrapper<>();

    private Articledetail articledetail = new Articledetail();

    //private String key;
    //创建等待队列
    private final BlockingQueue<Runnable> bqueue = new ArrayBlockingQueue<>(20);
    private final ExecutorService exec = new ThreadPoolExecutor(2,4,60, TimeUnit.SECONDS,bqueue,new ThreadPoolExecutor.DiscardPolicy());

    @ApiOperation(value = "逻辑删除文章")
    @DeleteMapping("deletearticle/{id}")
    public R removeTeacher(@PathVariable final String id){
        boolean flag = articleService.removeById(id);
        articledetailService.removeById(id);
        jedis.del("keywordList".getBytes(),"timeLineList".getBytes(),"blogInfoList".getBytes());
        if(flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }


    @ApiOperation(value = "获取标签云")
    @GetMapping("getKeywordList")
    public R getKeywordList(){
        jedis = redisUtil.getJedis();
        jedis.auth("benhaowu");
        //jedis.flushDB();    //清空数据库 jedis.del("redisList");
        //key = "keywordList";
        //存在缓存则读取，否则读数据库在写入缓存
        if(jedis.exists("keywordList".getBytes())){
            keyword = SerializeUtil.unserializeForList(jedis.get("keywordList".getBytes()));
            bloginfoService.addIndexView();//添加访问数量
            jedis.close();
        }else{
            keyword = articleService.getKeywords();
            try{
                //多线程异步执行
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        jedis.set("keywordList".getBytes(),SerializeUtil.serialize(keyword));
                        bloginfoService.addIndexView();//添加访问数量
                        log.info("生成 keywordList by redis");
                        jedis.close();
                    }
                });
            }catch (Exception e){
                log.info("getKeywordList exception:",e);
            }
//            jedis.set("keywordList".getBytes(),SerializeUtil.serialize(keyword));
//            log.info("生成 keywordList by redis");
        }
        return R.ok().data("keyword",keyword);
    }

    @ApiOperation(value = "修改标签云")
    @PostMapping("updateKeyword/{oldkeyword}/{newkeyword}")
    public R updateKeyword(@PathVariable final String oldkeyword,@PathVariable final String newkeyword){
        boolean flag = articleService.updateKeyword(oldkeyword,newkeyword);
        jedis.del("keywordList".getBytes());
        if(flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }


    @ApiOperation(value = "获取归档")
    @GetMapping("getTimeLine")
    public R getTimeLine(){
        jedis = redisUtil.getJedis();
        jedis.auth("benhaowu");
        //jedis.flushDB();    //清空数据库 jedis.del("redisList");
        //key = "timeLineList";
        //存在缓存则读取，否则读数据库在写入缓存
        if(jedis.exists("timeLineList".getBytes())){
            timeline = SerializeUtil.unserializeForList(jedis.get("timeLineList".getBytes()));
            jedis.close();
        }else{
            wrapper.orderByDesc("update_time");
            wrapper.select("id","title","update_time");
            timeline =  articleService.list(wrapper);
            try{
                //多线程异步执行
                exec.execute(() -> {
                    jedis.set("timeLineList".getBytes(),SerializeUtil.serialize(timeline));
                    log.info("生成timeLineList by redis");
                    jedis.close();
                });
            }catch (Exception e){
                log.info("getTimeLine exception:",e);
            }
        }
        jedis.close();
        return R.ok().data("timeLine",timeline);
    }


    //根据文章id进行查询
    @GetMapping("getArticleInfo/{id}")
    public R getArticleInfo(@PathVariable final String id) {
        //-=-----------------
        article = articleService.getById(id);
        //-=-----------------
        return R.ok().data("article",article);
    }

    //添加文章接口的方法
    @PostMapping("addArticle")
    public R addArticle(@RequestBody Article article) {
        boolean save = articleService.save(article);
        articledetail.setTitle(article.getTitle());
        bloginfoController.updateBlogInfo();
        articledetailService.save(articledetail);
        try{
            exec.execute(() -> {
                jedis.del("keywordList".getBytes(),"timeLineList".getBytes(),"blogInfoList".getBytes());
                log.info("删除keywordList、timeLineList、blogInfoList表");
                jedis.close();
            });
        }catch (Exception e){
            log.info("addArticle exception:",e);
        }
        if(save) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    //文章修改功能
    @PostMapping("updateArticle")
    public R updateTeacher(@RequestBody Article article) {
        boolean flag = articleService.updateById(article);
        try{
            exec.execute(() -> {
                jedis.del("keywordList".getBytes(),"timeLineList".getBytes(),"blogInfoList".getBytes());
                log.info("删除keywordList、timeLineList、blogInfoList表");
                jedis.close();
            });
        }catch (Exception e){
            log.info("updateTeacher exception:",e);
        }
        if(flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    //上传图片到oss
    @PostMapping("/eduoss/fileoss")
    public R uploadOssFile(@RequestParam(value = "file") MultipartFile multipartFile){
        String url = ossService.uploadFileAvator(multipartFile);
        log.info(url);
        return R.ok().data("url",url);
    }


    private final Lock lock = new ReentrantLock();

    //文字识别
    @PostMapping("baiDuOcr")
    public R baiDuOcr(@RequestParam(value = "datafile") MultipartFile file) throws Exception {
        lock.lock();
        try{
            File f =  MultipartFileToFile.multipartFileToFile(file);
            String image = BaseImg64.getImageStrFromFile(f);
            String param = "image="+image;
            String words = check.post(param);
            JSONObject jo = JSONObject.parseObject(words);
            return R.ok().data("words",jo);
        }catch (Exception e){
            e.printStackTrace();
            return R.error().message("发生异常");
        }finally {
            lock.unlock();
        }
    }

    //主页分页查询列表
    //@Cacheable(cacheNames = "ArticleList", key = "#current")
    @GetMapping("pageListArticle/{current}/{limit}")
    public R pageListArticle(@PathVariable long current,
                             @PathVariable long limit) {
        ArticleDetailPage = articleService.getArticleDetail(new Page<>(current, limit));
        long total = ArticleDetailPage.getTotal();//总记录数
        records = ArticleDetailPage.getRecords(); //数据list集合
        return R.ok().data("total", total).data("rows", records);
    }

    //4 条件查询带分页的方法
    @PostMapping("pageArticleCondition/{current}/{limit}")
    public R pageTeacherCondition(@PathVariable long current,@PathVariable long limit,
                                  @RequestBody(required = false) articleQuery articleQuery) {
        //创建page对象
        Page<Article> pageTeacher = new Page<>(current,limit);
        // 多条件组合查询
        // mybatis学过 动态sql
        String title = articleQuery.getTitle();
        Integer keywords = articleQuery.getKeywords();
        String createTime = articleQuery.getCreateTime();
        String updateTime = articleQuery.getUpdateTime();
        //判断条件值是否为空，如果不为空拼接条件
        if(!StringUtils.isEmpty(title)) {
            //构建条件
            wrapper.like("title",title);
        }
        if(!StringUtils.isEmpty(keywords)) {
            wrapper.like("keywords",keywords);
        }
        if(!StringUtils.isEmpty(createTime)) {
            wrapper.ge("create_time",createTime);
        }
        if(!StringUtils.isEmpty(updateTime)) {
            wrapper.le("update_time",updateTime);
        }

        //调用方法实现条件查询分页
        articleService.page(pageTeacher,wrapper);

        long total = pageTeacher.getTotal();//总记录数
        List<Article> records = pageTeacher.getRecords(); //数据list集合
        return R.ok().data("total",total).data("rows",records);
    }
}

