package com.spzx.common.redis.cache;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class GuiguCacheAspect {

    @Autowired
    RedisTemplate redisTemplate;

    //@SneakyThrows
    @Around(value = "@annotation(guiguCache)")
    public Object around(ProceedingJoinPoint joinPoint, GuiguCache guiguCache) throws Throwable {
        // 1.先查询redis缓存，有，直接返回，
        String prefix = guiguCache.prefix();
        Object[] args = joinPoint.getArgs();
        String paramVal = "none";
        if (args != null && args.length > 0) { // 说明目标方法有参数
            paramVal = Arrays.asList(args).stream().map(item -> item.toString()).collect(Collectors.joining(":"));
        }
        String dataKey = prefix + paramVal;

        Object result = null;
        if (redisTemplate.hasKey(dataKey)) {
            result = redisTemplate.opsForValue().get(dataKey);
            log.info("命中缓存，直接返回，线程ID：{}，线程名称：{}", Thread.currentThread().getId(), Thread.currentThread().getName());
            return result;
        }

        // 2.没有,再查询数据库，放在缓存中，给下次访问使用。利用缓存提高效率。
        String lockKey = prefix + "lock:" + paramVal;
        String lockVal = UUID.randomUUID().toString().replaceAll("-", "");
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockVal, 5, TimeUnit.SECONDS);
        if (ifAbsent) { // 加分布式锁成功
            try {
                log.info("获取锁成功：{}，线程名称：{}", Thread.currentThread().getId(), Thread.currentThread().getName());
                result = joinPoint.proceed();
                long ttl = result == null ? 1 * 60 : 10 * 60; // 解决缓存穿透：解决方案1（null值也存储，但是时间短一些。）
                redisTemplate.opsForValue().set(dataKey, result, ttl, TimeUnit.SECONDS);
                return result;
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                // 4.业务执行完毕释放锁
                String scriptText = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                redisScript.setScriptText(scriptText);
                redisScript.setResultType(Long.class);
                redisTemplate.execute(redisScript, Arrays.asList(lockKey), lockVal);
            }
        } else { // 加分布式锁失败,等待自旋
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // log.error("获取锁失败，自旋：{}，线程名称：{}", Thread.currentThread().getId(), Thread.currentThread().getName());
            // return around(joinPoint,guiguCache);   //看情况增加自旋。
        }
        return joinPoint.proceed(); // 可以最后从数据库获取数据，兜底。
    }

}