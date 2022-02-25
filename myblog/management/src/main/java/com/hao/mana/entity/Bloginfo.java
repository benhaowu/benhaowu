package com.hao.mana.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author bentou
 * @since 2021-01-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Bloginfo对象")
public class Bloginfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "总的预览数")
    private Integer indexview;

    @ApiModelProperty(value = "运行天数")
    private Integer runtime;

    @ApiModelProperty(value = "文章总数")
    private Integer articlenum;

    @ApiModelProperty(value = "总评论数")
    private Integer thumbupnum;


}
