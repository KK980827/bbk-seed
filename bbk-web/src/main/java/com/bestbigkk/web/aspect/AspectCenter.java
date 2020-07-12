package com.bestbigkk.web.aspect;

import com.bestbigkk.web.aspect.impl.AccessLimitAspect;
import com.bestbigkk.web.aspect.impl.FieldControlAspect;
import com.bestbigkk.web.aspect.impl.LogRecordAspect;
import com.bestbigkk.web.validator.AccessLimit;
import com.bestbigkk.web.validator.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
* @author: 开
* @date: 2020-04-26 12:07:11
* @describe: Controller层进行AOP
*/
@Aspect
@Configuration
@Slf4j
public class AspectCenter {

    private final AccessLimitAspect accessLimitAspect;
    private final LogRecordAspect logRecordAspect;
    private final FieldControlAspect fieldControlAspect;

    public AspectCenter(AccessLimitAspect accessLimitAspect, LogRecordAspect logRecordAspect, FieldControlAspect fieldControlAspect) {
        this.accessLimitAspect = accessLimitAspect;
        this.logRecordAspect = logRecordAspect;
        this.fieldControlAspect = fieldControlAspect;
    }

    @Pointcut("execution(* com.bestbigkk.web.controller..*(..))  ")
    public void aspect() { }


    @Before(value = "aspect()")
    public void beforeHandler(JoinPoint joinPoint) {
        //字段控制
        fieldControlAspect.doHandlerAspect(joinPoint, currentMethod(joinPoint), null);
    }

    /**
     * 环绕增强切面处理i
     */
    @Around(value = "aspect()")
    public Object validationPoint(ProceedingJoinPoint pjp)throws Throwable{
        Method method = currentMethod(pjp);

        //是否需要限流
        if (method.isAnnotationPresent(AccessLimit.class)) {
            accessLimitAspect.doHandlerAspect(pjp, method, null);
        }

        return  pjp.proceed(pjp.getArgs());
    }

    /**
     * 异常处理增强
     */
    @AfterThrowing(value = "aspect()", throwing = "e")
    public Object afterThrowing(JoinPoint joinPoint, Throwable e) throws Throwable {
        Method method = currentMethod(joinPoint);

        //是否需要记录日志
        if (method.isAnnotationPresent(LogRecord.class)) {
            logRecordAspect.doHandlerAspect(joinPoint, method, e);
        }

        return null;
    }


    /**
     * 获取目标类的所有方法，找到当前要执行的方法
     */
    private Method currentMethod(JoinPoint joinPoint) {
        Method[] methods  = joinPoint.getTarget().getClass().getMethods();
        final String methodName = joinPoint.getSignature().getName();
        Method  resultMethod = null;
        for ( Method method : methods ) {
            if ( method.getName().equals( methodName ) ) {
                resultMethod = method;
                break;
            }
        }
        return resultMethod;
    }


}
