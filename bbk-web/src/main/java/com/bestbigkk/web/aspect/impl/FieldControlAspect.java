package com.bestbigkk.web.aspect.impl;


import com.bestbigkk.common.FieldIgnore;
import com.bestbigkk.common.annotation.RequestIgnore;
import com.bestbigkk.web.aspect.AbstractAspect;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @ClassName: FieldControlAdvice.java
 * @author: xugongkai
 * @createTime: 2020.04.22  17:32
 * @describe: 字段控制前置处理。
 */
@Slf4j
@Aspect
@Component
public class FieldControlAspect extends AbstractAspect {

    @Override
    public Object doHandlerAspect(JoinPoint joinPoint, Method method, Throwable throwable) {
        return execute(joinPoint, method, throwable);
    }

    @Override
    public Object execute(JoinPoint joinPoint, Method method, Throwable throwable) {
        fieldControl(joinPoint, method);
        return null;
    }


    private void fieldControl(JoinPoint joinpoint, Method method) {

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        LinkedList<Object[]> params = getParams(joinpoint);

        //遍历每一个参数，找寻被 RequestValueIgnore 或者RequestIgnoreControl 注解修饰的参数
        for (int i = 0; i < params.size(); i++) {

            final Object[] param = params.get(i);
            String name = (String) param[0];
            Object obj = param[1];

            //参数为String，Integer，等类型对象。或者不是FieldIgnore实例。
            if (Objects.isNull(obj) || !(obj instanceof FieldIgnore)) {
                continue;
            }

            //未主动在方法惨设置RequestIgnore，并且这个对象的类上也不存在RequestIgnore修饰
            RequestIgnore classRequestIgnore = obj.getClass().getAnnotation(RequestIgnore.class);
            if (parameterAnnotations[i].length == 0 && Objects.isNull(classRequestIgnore)) {
                continue;
            }

            //寻找这个参数对应的 RequestIgnore，如果方法参数未指定，则为null,
            RequestIgnore requestIgnore = null;
            for (int j = 0; j < parameterAnnotations[i].length; j++) {
                if (parameterAnnotations[i][j] instanceof RequestIgnore) {
                    requestIgnore = (RequestIgnore) parameterAnnotations[i][j];
                    break;
                }
            }
            handler(method.getName(), name, obj, requestIgnore);
        }
    }

    /**
     *  给出一个obj对象，
     *  将按照该对象类上添加的RequestIgnore注解，或者请求参数前添加的RequestIgnore注解。（满足其一或者同时具有二者都可）
     *  按这两个注解中所设置的忽略字段，将这些从前端传递的字段值，在obj中重新设置为null。
     * @param methodName 当前执行的方法名称
     * @param obj 要进行处理的参数对象。
     * @param requestIgnore 针对该对象，在请求参数上所设置的请求参数忽略注解对象。
     */
    private void handler(String methodName, String name, Object obj, RequestIgnore requestIgnore) {

        List<String> list = Objects.nonNull(requestIgnore) ?  Arrays.asList(requestIgnore.ignoreProperties()) : Collections.emptyList();
        HashSet<String> ignoreProperties = new HashSet<>(list);

        RequestIgnore classRequestIgnore = obj.getClass().getAnnotation(RequestIgnore.class);
        HashSet<String> classIgnoreProperties = new HashSet<>();
        if (Objects.nonNull(classRequestIgnore)) {
            classIgnoreProperties.addAll(Arrays.asList(classRequestIgnore.ignoreProperties()));
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        Arrays.asList(fields).forEach(f->{
            f.setAccessible(true);
            String fieldName = f.getName();
            try {
                boolean isBeIgnore = ignoreProperties.contains(fieldName) || classIgnoreProperties.contains(fieldName);
                if (isBeIgnore && Objects.nonNull(f.get(obj))) {
                    log.info("Rest object [{}'s] inner field [{}] to null in method [{}]",name,  fieldName, methodName);
                    f.set(obj, null);
                }
            }catch (Exception e){
                e.printStackTrace();
                log.error("Rest object [{}'s] inner field [{}] to null in method [{}] failed",name,  fieldName, methodName, e);
            }
        });
    }


    /**
     * 按顺序获取参数列表：
     * @param joinPoint
     * @return list的参数列表顺序与方法中出现次数一致。
     * 针对一个索引位置下的数组，长度恒为2，第一个可以强转为String，表示方法名称，第二个为Object，为方法参数对象。
     */
    private LinkedList<Object[]> getParams(JoinPoint joinPoint) {
        LinkedList<Object[]> linkedList = new LinkedList<>();
        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            Object[] o = new Object[2];
            Map<String, Object> param = new HashMap<>(2);
            o[0] = paramNames[i];
            o[1] = paramValues[i];
            linkedList.add(o);
        }
        return linkedList;
    }
}
