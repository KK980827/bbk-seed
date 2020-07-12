package com.bestbigkk.boot;

import com.bestbigkk.BBKApplication;
import com.bestbigkk.common.utils.encryption.AESUtils;
import com.bestbigkk.web.config.WebSocketConfig;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
* @author: 开
* @date: 2020-03-24 16:03:20
* @describe: SpringBoot单元测试
*/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BBKApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BootTest {

    @Autowired
    private AESUtils aesEncryptUtil;

    @org.junit.Test
    public void test() {
        final String encrypt = aesEncryptUtil.encrypt("Hello哈哈哈！");
        System.out.println(encrypt);
        final String desEncrypt = aesEncryptUtil.decrypt(encrypt);
        System.out.println(desEncrypt);
    }

}
