package com.hao.mana.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hao.mana.entity.Article;
import com.hao.mana.entity.Resources;
import com.hao.mana.service.ResourcesService;
import com.hao.mana.utils.R;
import com.hao.mana.utils.SerializeUtil;
import com.sun.org.apache.regexp.internal.RE;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author bentou
 * @since 2021-03-29
 */
@Api(description="资源管理")
@RestController
@RequestMapping("/mana/resources")
@CrossOrigin
public class ResourcesController {

    @Autowired
    private ResourcesService resourcesService;

    //private QueryWrapper<Resources> wrapper = new QueryWrapper<>();

    @ApiOperation(value = "获取资源目录")
    @GetMapping("getResoucesByFrom/{from}")
    public R getResoucesByFrom(@PathVariable String from){
        //构建条件
        QueryWrapper<Resources> wrapper = new QueryWrapper<>();
        wrapper.like("keywords",from);
        final List<Resources> resources = resourcesService.list(wrapper);
        return R.ok().data("resources",resources);
    }

    @ApiOperation(value = "获取资源目录条件版本")
    @GetMapping("getResoucesByFrom/{from}/{version}")
    public R getResoucesByVersion(@PathVariable String from,@PathVariable String version){
        QueryWrapper<Resources> wrapper = new QueryWrapper<>();
        wrapper.like("keywords",from);
        wrapper.like("versions",version);
        List<Resources> resources = resourcesService.list(wrapper);
        return R.ok().data("resources",resources);
    }

    //根据文章id进行查询
    @GetMapping("getResourceInfo/{id}")
    public R getResourceInfo(@PathVariable String id) {
        Resources resources = resourcesService.getById(id);
        return R.ok().data("resources",resources);
    }

    //主页分页查询列表
    @GetMapping("pageListResource/{current}/{limit}")
    public R pageListResource(@PathVariable long current,
                             @PathVariable long limit) {
        Page<Resources> ResourcesDetailPage = resourcesService.getResourceDetail(new Page<>(current, limit));
        long total = ResourcesDetailPage.getTotal();//总记录数
        List<Resources> records = ResourcesDetailPage.getRecords(); //数据list集合
        return R.ok().data("total", total).data("rows", records);
    }

    @ApiOperation(value = "添加资源")
    @PostMapping("addResouces")
    public R addResouces(@RequestBody Resources resources){
        boolean flag;
        flag= resourcesService.save(resources);
        if(flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @ApiOperation(value = "修改资源")
    @PostMapping("updateResouces")
    public R updateResouces(@RequestBody Resources resources){
        boolean flag;
        flag= resourcesService.updateById(resources);
        if(flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @ApiOperation(value = "删除资源")
    @DeleteMapping("deleteResouces/{id}")
        public R deleteResouces(@PathVariable Integer id){
        boolean flag;
        flag= resourcesService.removeById(id);
        if(flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }
}

