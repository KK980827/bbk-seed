package com.bestbigkk.web.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bestbigkk.common.annotation.RequestIgnore;
import com.bestbigkk.common.entity.ListResponse;
import com.bestbigkk.common.entity.Pagination;
import com.bestbigkk.common.exception.BusinessException;
import com.bestbigkk.common.utils.QueryWrapperUtils;
import com.bestbigkk.persistence.entity.User;
import com.bestbigkk.service.UserService;
import com.bestbigkk.web.response.annotation.RW;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author xugongkai
 * @data 2020-04-26
 * @describe: API接口
 */
@RW
@Slf4j
@Api(tags = {"API接口"})
@RequestMapping(value = "/dev/user", produces = {"application/json;charset=UTF-8"})
public class UserController {

    /** 单次最大操作数量*/
    private final Integer OPERATE_MAX_BATCH = 1000;

    @Autowired
    private UserService userService;
    @Autowired
    private QueryWrapperUtils queryWrapperUtils;

    @PostMapping
    @ApiOperation(value = "新增一个对象")
    public User add( @RequestIgnore(ignoreProperties = {"name"}) User user){
        user.setId(null);
        boolean registered = userService.save( user);
        if (registered) {
            return  user;
        }
        throw new BusinessException("新增失败");
    }

    @PostMapping("/batch")
    @ApiOperation(value = "新增一批对象，最多允许：1000个对象/次")
    public Integer adds(String jsonList) {
        List<User> userList = parseJson2Obj(jsonList, User.class);
        List<User> collects =  userList.stream().peek(obj -> obj.setId(null)).collect(Collectors.toList());
        boolean addBatch = userService.saveBatch(collects);
        if (addBatch) {
            return collects.size();
        }
        throw new BusinessException("批量插入失败");
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "按照ID删除一个对象")
    public Boolean delete(@PathVariable("id") Long id) {
        idsCheck(Collections.singletonList(id));
        final boolean remove = userService.removeById(id);
        if (remove) {
            return true;
        }
        throw new BusinessException("删除失败，请检查对象 [ID = "+ id +"]");
    }

    @DeleteMapping("/batch")
    @ApiOperation(value = "批量删除对象")
    public Boolean deletes(Long[] ids) {
        idsCheck(ids);
        boolean removeBatch = userService.removeByIds(Arrays.asList(ids));
        if (removeBatch) {
            return true;
        }
        throw new BusinessException("批量删除失败");
    }

    @PutMapping
    @ApiOperation(value = "按照ID更新对象信息，以ID确定被更新对象")
    public  User update( User user){
        idsCheck(Collections.singletonList(user.getId()));
        final boolean update = userService.updateById(user);
        if (update) {
            return query(user.getId());
        }
        throw new BusinessException("更新失败");
    }

    @PutMapping("/batch")
    @ApiOperation(value = "批量更新对象")
    public Boolean updates(String jsonList) {
        List< User> userList = parseJson2Obj(jsonList,  User.class);
        List<Long> ids = userList.stream().filter(u->Objects.nonNull(u.getId())).map(User::getId).collect(Collectors.toList());
        idsCheck(ids);
        boolean updateBatch = userService.updateBatchById(userList);
        if (updateBatch) {
            return true;
        }
        throw new BusinessException("批量更新失败");
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "按照ID查询一个对象")
    public  User query(@PathVariable("id") Long id) {
       idsCheck(Collections.singletonList(id));
       return  userService.getById(id);
    }

    @GetMapping("/list")
    @ApiOperation(value = "按照条件查询对象列表")
    public ListResponse<User> list(User user, Pagination<User> page) {
        final QueryWrapper<User> query = queryWrapperUtils.buildNotNullEqualsWrapper(user);
        final IPage<User> record =  userService.page(page.toPage("create_time"), query);
        return new ListResponse<>(record.getRecords(), new Pagination<User>().toPagination(record));
    }

   /**
    * 判断一批id在数据库是否都存在。
    * @param ids 被判断id集合
    * @return 是否都存在
    */
   private <T> void idsCheck(List<T> ids) {
       if (Objects.isNull(ids) || ids.size() == 0) {
           throw new BusinessException("未指定对象ID");
       }
       int dbCount = userService.count(new QueryWrapper<User>().lambda().in(User::getId, ids));
       if (dbCount != ids.size()) {
           throw new BusinessException("请求操作的ID数量："+ids.size()+"，系统得到的实际可操作ID数量："+dbCount+", 数量不对等，部分ID可能不存在，请确认！");
       }
   }

   private <T> void idsCheck(T[] ids) {
       if (Objects.isNull(ids) || ids.length == 0) {
           throw new BusinessException("未指定对象ID");
       }
       idsCheck(Arrays.asList(ids));
   }

   /**
    * 解析JSON字符串为对应的对象
    * @param jsonString Json字符串
    * @param clazz 对象
    * @param <T> 类型
    * @return 数组
    */
   private <T> List<T> parseJson2Obj(String jsonString, Class<T> clazz) {
       List<T> list;
       try {
           list = JSON.parseArray(jsonString, clazz);
       } catch (Exception e) {
           throw new BusinessException("Json解析失败：" + e.getMessage());
       }
       if (list.size() > OPERATE_MAX_BATCH ) {
           throw new BusinessException("超出限制，批量插入最多允许：1000个对象/次");
       }
       return list;
   }

}
