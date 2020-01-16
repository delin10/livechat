package nil.ed.livechat.chatroom.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import nil.ed.livechat.chatroom.aop.annotation.CheckPageParams;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * @author delin10
 * @since 2019/10/21
 **/
@Aspect
public class ServiceAop {
    @Pointcut("*(*)")
    public void pointcut(){ }


    @Around("nil.ed.livechat.chatroom.aop.ServiceAop.pointcut()")
    public Object invokePageMethod(ProceedingJoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();

        CheckPageParams checkPageParamsAnno = signature.getMethod().getAnnotation(CheckPageParams.class);
        String[] parameterNames = signature.getParameterNames();





        return null;
    }

    public Integer[] getPageParams(String[] parameterNames, Object[] args, CheckPageParams checkPageParamsAnno){
        int pageNoIndex = -1, pageSizeIndex = -1;

        for (int i = 0; i < parameterNames.length; ++i){
            if (parameterNames[i].equals(checkPageParamsAnno.pageNoSignatureName())){
                pageNoIndex = i;
            }else if (parameterNames[i].equals(checkPageParamsAnno.pageSizeSignatureName())){
                pageSizeIndex = i;
            }
        }

        if (pageNoIndex == -1){

        }

        if (pageSizeIndex == -1){

        }

        Integer pageNo = null, pageSize = null;

        if (pageNoIndex != -1 && pageSizeIndex != -1) {
            if (args[pageNoIndex] instanceof Integer) {
                pageNo = (Integer) args[pageNoIndex];
            }

            if (args[pageSizeIndex] instanceof Integer) {
                pageSize = (Integer) args[pageSizeIndex];
            }
        }

        if (pageNo == null || pageSize == null){
            throw new RuntimeException();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("'a' == 'b' && 'a' == 'b'");
        System.out.println(expression.getValue());

        Method[] methodArr = ServiceAop.class.getDeclaredMethods();
        for (Method method : methodArr){
            if (method.getName().equals("test")){
                Parameter[] parameterArr = method.getParameters();

                for (Parameter parameter : parameterArr){
                    System.out.println(parameter.getType());
                }
            }
        }

        testObj(1);
    }

    static void test(int a){
        testObj(a);
    }

    static void testObj(Object aObj){
        System.out.println(aObj.getClass());
    }
}
