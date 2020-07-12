package com.bestbigkk.web.aspect;

import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
* @author: 开
* @date: 2020-04-26 12:14:05
* @describe: 抽象切面类。所有具体切面都应作为其具体子类。后续版本考虑将其移除。
 *
 */

public abstract class AbstractAspect {

    /**
     *
     * @param joinPoint
     * @param method
     * @param throwable
     * @return
     * @throws Throwable
     */
     public abstract Object doHandlerAspect(JoinPoint joinPoint, Method method, Throwable throwable) throws Throwable;

     /**
      * 执行切面实现类要执行的增强操作
      * @param joinPoint
      * @param method
      * @return
      * @throws Throwable
      */
     public abstract Object execute(JoinPoint joinPoint, Method method, Throwable throwable) throws Throwable;
}
