package nil.ed.livechat.chatroom.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nil.ed.livechat.chatroom.aop.PageParamsCheckExceptionHandler;

/**
 * @author delin10
 * @since 2019/10/21
 **/
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CheckPageParams {
    String pageNoSignatureName();

    String pageSizeSignatureName();

    int minPageNo() default 0;

    int maxPageNo() default Integer.MAX_VALUE;

    int maxPageSize() default 20;

    Class<? extends PageParamsCheckExceptionHandler> handler();
}
