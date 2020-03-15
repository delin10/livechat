package nil.ed.livechat.common.aop;


import java.text.MessageFormat;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import nil.ed.livechat.common.util.AspectJUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author lidelin
 */
@Slf4j
@Aspect
@Component
@Order(value = 0)
public class MethodInvocationLogAop {

    @Pointcut("@annotation(nil.ed.livechat.common.aop.annotation.MethodInvokeLog)")
    public void pointcut() {

    }

    @Before("pointcut()")
    public void beforeMethod(JoinPoint joinPoint) {

        Signature signature = joinPoint.getSignature();
        String paramInfos = AspectJUtils.getParametersAndValuesMap(joinPoint).entrySet().stream()
                .map(entry -> MessageFormat.format("{0} = {1}", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(","));
        // 获取签名参数名
        log.info("===> Start to invoke {}, entry params: {}", signature.getName(), paramInfos);
    }

    @AfterReturning(value = "pointcut()", returning = "returnResult")
    public void afterMethod(JoinPoint joinPoint, Object returnResult) {
        Signature signature = joinPoint.getSignature();
        log.info("<=== Succeed to invoke {} with result = {}", signature.getName(), returnResult);
    }


    /**
     * 捕获异常之后重复抛出
     *
     * @param joinPoint 切点
     * @param ex 异常
     */
    @AfterThrowing(value = "pointcut()", throwing = "ex")
    public void throwingMethod(JoinPoint joinPoint, Exception ex) {
        Signature signature = joinPoint.getSignature();
        log.error("<=== Failed to  invoke {}, throws Exception: ", signature.getName(), ex);
    }
}