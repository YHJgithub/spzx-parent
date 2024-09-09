package com.spzx.user.controller;

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
import com.spzx.user.domain.UserAddress;
import com.spzx.user.service.IUserAddressService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.spzx.common.core.web.page.TableDataInfo;

/**
 * 用户地址Controller
 *
 * @author atguigu
 * @date 2024-09-02
 */
@Tag(name = "用户地址接口管理")
@RestController
@RequestMapping("/userAddress")
public class UserAddressController extends BaseController
{
    @Autowired
    private IUserAddressService userAddressService;

    /**
     * 查询用户地址列表
     */
    @Operation(summary = "查询用户地址列表")
    @RequiresLogin
    @GetMapping("/list")
    public AjaxResult list()
    {
        List<UserAddress> list = userAddressService.selectUserAddressList();
        return success(list);
    }

    /**
     * 新增用户地址
     */
    @Operation(summary = "新增用户地址")
    @RequiresLogin
    @PostMapping
    public AjaxResult add(@RequestBody UserAddress userAddress)
    {
        return toAjax(userAddressService.insertUserAddress(userAddress));
    }

    /**
     * 修改用户地址
     */
    @Operation(summary = "修改用户地址")
    @RequiresLogin
    @PutMapping
    public AjaxResult edit(@RequestBody UserAddress userAddress)
    {
        return toAjax(userAddressService.updateUserAddress(userAddress));
    }

    /**
     * 删除用户地址
     */
    @Operation(summary = "删除用户地址")
    @RequiresLogin
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(userAddressService.removeById(id));
    }
}
