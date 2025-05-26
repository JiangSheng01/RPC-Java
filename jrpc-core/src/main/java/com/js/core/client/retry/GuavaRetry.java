package com.js.core.client.retry;

import com.github.rholder.retry.*;
import com.js.core.client.rpcclient.RpcClient;
import com.js.common.message.RpcRequest;
import com.js.common.message.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GuavaRetry {
    public RpcResponse sendServiceWithRetry(RpcRequest request, RpcClient rpcClient) {

        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfException()
                .retryIfResult(response -> Objects.equals(response.getCode(), 500))
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("RetryListener:第" + attempt.getAttemptNumber() + "次调用");
                    }
                })
                .build();
        try {
            return retryer.call(()->rpcClient.sendRequest(request));
        } catch (ExecutionException | RetryException e) {
            log.error("重试失败: 请求 {} 执行时遇到异常", request.getMethodName(), e);
        }
        return RpcResponse.fail("重试失败，所有重试尝试已结束");
    }
}
