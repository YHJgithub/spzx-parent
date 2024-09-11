package com.spzx.order.receiver;

import com.rabbitmq.client.Channel;
import com.spzx.common.rabbit.constant.MqConst;
import com.spzx.order.configure.DeadLetterMqConfig;
import com.spzx.order.configure.DelayedMqConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TestReceiver {
    
    @Autowired
    private RedisTemplate redisTemplate;

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = MqConst.EXCHANGE_TEST, durable = "true"),
            value = @Queue(value = MqConst.QUEUE_TEST, durable = "true"),
            key = MqConst.ROUTING_TEST
    ))
    public void test(String content, Message message) {
        log.info("content接收消息：{}", content);
        log.info("new String(message.getBody())接收消息：{}", new String(message.getBody()));
    }

    @SneakyThrows
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = MqConst.EXCHANGE_TEST, durable = "true"),
            value = @Queue(value = MqConst.QUEUE_CONFIRM, durable = "true"),
            key = MqConst.ROUTING_CONFIRM
    ))
    public void confirm(String content, Message message, Channel channel) {
        log.info("接收确认消息：{}", content);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 监听延迟消息
     *
     * @param msg
     * @param message
     * @param channel
     */
    @SneakyThrows
    @RabbitListener(queues = {DeadLetterMqConfig.queue_dead_2})
    public void getDeadLetterMsg(String msg, Message message, Channel channel) {
        log.info("死信消费者：{}", msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 监听延迟消息(解决幂等性)
     *
     * @param msg
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = {DelayedMqConfig.queue_delay_1})
    public void getDeadLetterMsg2(String msg, Message message, Channel channel)
            throws IOException {
        log.info("TestReceiver-消息：{}", msg);

        String lockKey = "mq:" + msg;
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(lockKey, "", 200, TimeUnit.SECONDS);
        // 没有设置锁成功，说明消息已经被处理，无需重复处理，直接确认即可。
        if (!flag) {
            log.info("TestReceiver-没有获取锁，直接确认该消息。");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            return;
        }
        // 如果设置锁成功,说明第一次处理消息。TODO 执行业务逻辑代码。
        log.info("TestReceiver-处理消息幂等性，执行业务处理。first");
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
