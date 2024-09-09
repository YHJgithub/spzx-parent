package com.spzx.order.controller;

import java.util.List;
import java.util.Arrays;

import com.spzx.common.security.annotation.RequiresLogin;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.order.domain.OrderInfo;
import com.spzx.order.service.IOrderInfoService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.spzx.common.core.web.page.TableDataInfo;

/**
 * 订单Controller
 *
 * @author atguigu
 * @date 2024-09-08
 */
@Tag(name = "订单接口管理")
@RestController
@RequestMapping("/orderInfo")
public class OrderInfoController extends BaseController {
    @Autowired
    private IOrderInfoService orderInfoService;

    /**
     * 查询订单列表
     */
    @Operation(summary = "查询订单列表")
    @RequiresPermissions("user:orderInfo:list")
    @GetMapping("/list")
    public TableDataInfo list(OrderInfo orderInfo) {
        startPage();
        List<OrderInfo> list = orderInfoService.selectOrderInfoList(orderInfo);
        return getDataTable(list);
    }

    /**
     * 导出订单列表
     */
    @Operation(summary = "导出订单列表")
    @RequiresPermissions("user:orderInfo:export")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OrderInfo orderInfo) {
        List<OrderInfo> list = orderInfoService.selectOrderInfoList(orderInfo);
        ExcelUtil<OrderInfo> util = new ExcelUtil<OrderInfo>(OrderInfo.class);
        util.exportExcel(response, list, "订单数据");
    }

    /**
     * 获取订单详细信息
     */
    @Operation(summary = "获取订单详细信息")
    @RequiresPermissions("user:orderInfo:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(orderInfoService.selectOrderInfoById(id));
    }

    @Operation(summary = "订单结算")
    @RequiresLogin
    @GetMapping("/trade")
    public AjaxResult orderTradeData() {
        return success(orderInfoService.orderTradeData());
    }

}
