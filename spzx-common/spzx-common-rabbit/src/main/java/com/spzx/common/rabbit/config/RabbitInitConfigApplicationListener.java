package com.spzx.common.rabbit.config;

import com.alibaba.fastjson2.JSON;
import com.spzx.common.rabbit.entity.GuiguCorrelationData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RabbitInitConfigApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        this.setupCallbacks();
    }

    private void setupCallbacks() {

        /**
         * 只确认消息是否正确到达 Exchange 中,成功与否都会回调
         *
         * @param correlation 相关数据  非消息本身业务数据
         * @param ack             应答结果
         * @param reason           如果发送消息到交换器失败，错误原因
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, reason) -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (ack) {
                log.info("消息发送到Exchange成功: {}", correlationData);
            } else {
                log.error("消息发送到Exchange失败：{}", reason);
                this.retrySendMsg(correlationData);
            }
        });

        /**
         * 消息没有正确到达队列时触发回调，如果正确到达队列不执行
         */
        rabbitTemplate.setReturnsCallback(returned -> {
            log.error("Returned: " + returned.getMessage() + "\nreplyCode: " + returned.getReplyCode()
                    + "\nreplyText: " + returned.getReplyText() + "\nexchange/rk: "
                    + returned.getExchange() + "/" + returned.getRoutingKey());
            System.out.println("returned.getMessage().getMessageProperties() = " + returned.getMessage().getMessageProperties());
            String redisKey = returned.getMessage().getMessageProperties().getHeader("spring_returned_message_correlation");
            String correlationDataStr = (String) redisTemplate.opsForValue().get(redisKey);
            GuiguCorrelationData guiguCorrelationData = JSON.parseObject(correlationDataStr, GuiguCorrelationData.class);
            
            // todo 方式一:如果不考虑延迟消息重发 直接返回
            /* if (guiguCorrelationData.isDelay()) {
                return;
            }
            retrySendMsg(guiguCorrelationData); */
        });

    }

    private void retrySendMsg(CorrelationData correlationData) {
        // 获取相关数据
        GuiguCorrelationData guiguCorrelationData = (GuiguCorrelationData) correlationData;
        // 获取redis中存放重试次数
        // 先重发，在写回到redis中次数
        int retryCount = guiguCorrelationData.getRetryCount();
        if (retryCount >= 3) {
            // 超过最大重试次数
            log.error("生产者超过最大重试次数，将失败的消息存入数据库用由人工处理；给管理员发送邮件；给管理员发送短信；");
            return;
        }
        /* // 重发消息
        rabbitTemplate.convertAndSend(guiguCorrelationData.getExchange(), guiguCorrelationData.getRoutingKey(), guiguCorrelationData.getMessage(), guiguCorrelationData); */
        // 重发次数+1
        guiguCorrelationData.setRetryCount(++retryCount);
        redisTemplate.opsForValue().set(guiguCorrelationData.getId(), JSON.toJSONString(guiguCorrelationData), 10, TimeUnit.MINUTES);
        log.info("进行消息重发");

        // todo 方式二：如果是延迟消息，依然需要设置消息延迟时间
        if (guiguCorrelationData.isDelay()) {
            // 延迟消息
            rabbitTemplate.convertAndSend(guiguCorrelationData.getExchange(), guiguCorrelationData.getRoutingKey(), guiguCorrelationData.getMessage(), message -> {
                message.getMessageProperties().setDelay(guiguCorrelationData.getDelayTime() * 1000);
                return message;
            }, guiguCorrelationData);
        } else {
            // 普通消息
            rabbitTemplate.convertAndSend(guiguCorrelationData.getExchange(), guiguCorrelationData.getRoutingKey(), guiguCorrelationData.getMessage(), guiguCorrelationData);
        }
    }
}
