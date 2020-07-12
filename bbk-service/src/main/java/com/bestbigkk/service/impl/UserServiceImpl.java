package com.bestbigkk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bestbigkk.persistence.dao.UserDao;
import com.bestbigkk.persistence.entity.User;
import com.bestbigkk.service.UserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xugongkai
 * @since 2020-04-26
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

}
