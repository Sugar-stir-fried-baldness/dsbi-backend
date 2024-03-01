package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import io.lettuce.core.RedisClient;
import org.redisson.Redisson;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author:tzy
 * @Description : 专门提供 RedisLimiter 限流基础服务的（提供了一个通用 的能力）
 * @Date:2024/1/1519:22
 */

@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     *限流操作
     *
     * @param key 用来区分不同的限流器，比如不同的用户id应该分别统计
     */
    public void doRateLimit(String key){

        // 创建 Redisson 客户端


        // 创建一个每秒最多允许 5 次请求的限流器
        //通过getRateLimiter这个方式创建一个限流器，取名叫key，每个限流器单独统计
        // OVERALL : 无论有多少台服务器，都是放在一起统计
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

        //每当一个用户来了后，请求一个令牌 , 参数含义：每个操作占用几个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        //如果没有令牌还想执行操作，就抛出异常
        if(!canOp){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST );
        }

    }
}
