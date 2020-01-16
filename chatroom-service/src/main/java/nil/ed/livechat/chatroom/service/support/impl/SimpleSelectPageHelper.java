package nil.ed.livechat.chatroom.service.support.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import nil.ed.livechat.chatroom.common.PageResult;
import nil.ed.livechat.chatroom.common.PageResultResponseBuilder;
import nil.ed.livechat.chatroom.common.Response;
import nil.ed.livechat.chatroom.common.ResponseCodeEnum;
import nil.ed.livechat.chatroom.service.support.SelectPageHelper;
import nil.ed.livechat.chatroom.util.PageUtils;

public class SimpleSelectPageHelper<T> implements SelectPageHelper<T> {
    private Supplier<Integer> counter;

    private Integer pageNo, pageSize;

    private Executor executor;

    private long timeout = 3;

    public SimpleSelectPageHelper(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Response<PageResult<T>> operate(Supplier<List<T>> supplier) {
        PageUtils.checkPageParam(pageNo, pageSize);

        CompletableFuture<List<T>> listCompletableFuture = CompletableFuture.supplyAsync(supplier, executor);
        CompletableFuture<Integer> counterCompletableFuture = CompletableFuture.supplyAsync(counter, executor);

        try {
            return new PageResultResponseBuilder<T>()
                    .setPageNo(pageNo)
                    .setPageSize(pageSize)
                    .setTotal(counterCompletableFuture.get(timeout, TimeUnit.SECONDS))
                    .setPageData(listCompletableFuture.get(timeout, TimeUnit.SECONDS))
                    .build();
        } catch (TimeoutException e){
            return new PageResultResponseBuilder<T>()
                    .setCodeEnum(ResponseCodeEnum.TIMEOUT)
                    .build();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public SelectPageHelper<T> setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    @Override
    public SelectPageHelper<T> setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public SelectPageHelper<T> setCounter(Supplier<Integer> counter) {
        this.counter = counter;
        return this;
    }
}
