package nil.ed.livechat.chatroom.aop;

/**
 * @author delin10
 * @since 2019/10/21
 **/
public interface PageParamsCheckExceptionHandler {
    /**
     * 异常处理器
     * @param pageNo 页号
     * @param pageSize 页大小
     * @param ex 异常
     * @return 响应
     */
    Object handleException(Integer pageNo, Integer pageSize, Exception ex);
}
