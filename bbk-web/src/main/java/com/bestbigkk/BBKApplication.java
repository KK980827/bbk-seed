package com.bestbigkk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: 开
 * @date: 2020-03-23 15:23:53
 * @describe: bestbigkk
 */
@SpringBootApplication
@EnableAsync
@MapperScan(basePackages = "com.bestbigkk.persistence.dao")
public class BBKApplication {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(BBKApplication.class, args);

        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");

        String[] beanDefinitionNames = application.getBeanDefinitionNames();
        for (String beanName : beanDefinitionNames) {
            if (!beanName.contains("OperationLog")) {
                continue;
            }
            System.out.println("beanName: " + beanName);
        }

        System.out.println("------------");
        System.out.println("IP : " + ip);
        System.out.println("Port: " + port);
        System.out.println("Path: " + path);
        System.out.println("------------");
    }
}
