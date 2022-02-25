package com.hao.mana.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hao.mana.entity.Mybook;
import com.hao.mana.entity.Resources;
import com.hao.mana.service.MybookService;
import com.hao.mana.service.ResourcesService;
import com.hao.mana.utils.R;
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
 * @since 2021-03-30
 */
@Api(description="书单管理")
@RestController
@RequestMapping("/mana/mybook")
@CrossOrigin
public class MybookController {

    @Autowired
    private MybookService mybookService;

    @ApiOperation(value = "获取资源目录")
    @GetMapping("getMybookList")
    public R getMybookList(){
        QueryWrapper<Mybook> wrapper = new QueryWrapper<>();
        wrapper.select("*");
        List<Mybook> mybooks = mybookService.list(wrapper);
        return R.ok().data("mybook",mybooks);
    }

    //根据文章id进行查询
    @GetMapping("getMybookInfo/{id}")
    public R getMybookInfo(@PathVariable String id) {
        Mybook mybook = mybookService.getById(id);
        return R.ok().data("mybook",mybook);
    }

    @ApiOperation(value = "添加资源")
    @PostMapping("addBook")
    public R addBook(@RequestBody Mybook mybook){
        boolean flag;
        flag= mybookService.save(mybook);
        if(flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @ApiOperation(value = "修改资源")
    @PostMapping("updateMybook")
    public R updateMybook(@RequestBody Mybook mybook){
        boolean flag;
        flag= mybookService.updateById(mybook);
        if(flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @ApiOperation(value = "删除资源")
    @DeleteMapping("deleteMybook/{id}")
    public R deleteMybook(@PathVariable Integer id){
        boolean flag;
        flag= mybookService.removeById(id);
        if(flag) {
            return R.ok();
        } else {
            return R.error();
        }
    }
}

