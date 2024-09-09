package com.spzx.order.service.Impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.cart.api.controllr.RemoteCartService;
import com.spzx.cart.api.domain.CartInfo;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.core.utils.DateUtils;
import com.spzx.order.domain.OrderItem;
import com.spzx.order.domain.TradeVo;
import com.spzx.order.mapper.OrderItemMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.spzx.order.mapper.OrderInfoMapper;
import com.spzx.order.domain.OrderInfo;
import com.spzx.order.service.IOrderInfoService;
import org.springframework.util.CollectionUtils;

/**
 * 订单Service业务层处理
 *
 * @author atguigu
 * @date 2024-09-08
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private RemoteCartService remoteCartService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单
     */
    @Override
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo) {
        return orderInfoMapper.selectOrderInfoList(orderInfo);
    }

    /**
     * 查询订单
     *
     * @param id 订单主键
     * @return 订单
     */
    @Override
    public OrderInfo selectOrderInfoById(Long id) {
        OrderInfo orderInfo = orderInfoMapper.selectById(id);
        List<OrderItem> orderItemList = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, id));
        orderInfo.setOrderItemList(orderItemList);
        return orderInfo;
    }

    @Override
    public TradeVo orderTradeData() {
        // 获取当前登录用户的id
        Long userId = SecurityContextHolder.getUserId();

        // 查询用户购物车列表中选中商品列表
        R<List<CartInfo>> cartInfoListResult = remoteCartService.getCartCheckedList(userId, SecurityConstants.INNER);
        if (R.FAIL == cartInfoListResult.getCode()) {
            throw new ServiceException(cartInfoListResult.getMsg());
        }
        List<CartInfo> cartInfoList = cartInfoListResult.getData();
        if (CollectionUtils.isEmpty(cartInfoList)) {
            throw new ServiceException("购物车无选中商品");
        }

        // 将集合泛型从购物车改为订单明细
        List<OrderItem> orderItemList = null;
        BigDecimal totalAmount = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            orderItemList = cartInfoList.stream().map(cartInfo -> {
                OrderItem orderItem = new OrderItem();
                BeanUtils.copyProperties(cartInfo, orderItem);
                return orderItem;
            }).collect(Collectors.toList());

            // 订单总金额
            for (OrderItem orderItem : orderItemList) {
                totalAmount = totalAmount.add(orderItem.getSkuPrice().multiply(new BigDecimal(orderItem.getSkuNum())));
            }
        }

        // 渲染订单确认页面-生成用户流水号
        String tradeNo = this.generateTradeNo(userId);

        TradeVo tradeVo = new TradeVo();
        tradeVo.setTotalAmount(totalAmount);
        tradeVo.setOrderItemList(orderItemList);
        tradeVo.setTradeNo(tradeNo);
        return tradeVo;
    }

    /**
     * 渲染订单确认页面-生成用户流水号
     *
     * @param userId
     * @return
     */
    private String generateTradeNo(Long userId) {
        // 1.构建流水号Key
        String userTradeKey = "user:tradeNo:" + userId;
        // 2.构建流水号value
        String tradeNo = UUID.randomUUID().toString().replaceAll("-", "");
        // 3.将流水号存入Redis 暂存5分钟
        redisTemplate.opsForValue().set(userTradeKey, tradeNo, 5, TimeUnit.MINUTES);
        return tradeNo;
    }
}
