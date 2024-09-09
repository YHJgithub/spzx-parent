package com.spzx.order.service;

import java.util.List;

import com.spzx.order.domain.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.order.domain.TradeVo;

/**
 * 订单Service接口
 *
 * @author atguigu
 * @date 2024-09-08
 */
public interface IOrderInfoService extends IService<OrderInfo> {
    /**
     * 查询订单列表
     *
     * @param orderInfo 订单
     * @return 订单集合
     */
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo);

    /**
     * 查询订单
     *
     * @param id 订单主键
     * @return 订单
     */
    public OrderInfo selectOrderInfoById(Long id);

    TradeVo orderTradeData();
}
