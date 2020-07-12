package com.bestbigkk.persistence.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.bestbigkk.common.FieldIgnore;
import com.bestbigkk.common.annotation.RequestIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 
 * @author xugongkai
 * @since 2020-04-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user")
@ApiModel(value="User对象", description="")
@RequestIgnore(ignoreProperties = {"id"})
public class User implements FieldIgnore {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


}
