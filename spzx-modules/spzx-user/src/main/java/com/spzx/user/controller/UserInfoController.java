package com.spzx.user.controller;

import java.util.List;

import com.spzx.common.core.context.SecurityContextHolder;
import com.spzx.common.core.domain.R;
import com.spzx.common.security.annotation.InnerAuth;
import com.spzx.common.security.annotation.RequiresLogin;
import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;
import com.spzx.user.domain.UserInfoVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.spzx.common.log.annotation.Log;
import com.spzx.common.log.enums.BusinessType;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.user.service.IUserInfoService;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.utils.poi.ExcelUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.spzx.common.core.web.page.TableDataInfo;

/**
 * 会员Controller
 *
 * @author atguigu
 * @date 2024-09-02
 */
@Tag(name = "会员接口管理")
@RestController
@RequestMapping("/userInfo")
public class UserInfoController extends BaseController {
    @Autowired
    private IUserInfoService userInfoService;

    /**
     * 查询会员列表
     */
    @Operation(summary = "查询会员列表")
    @RequiresPermissions("user:userInfo:list")
    @GetMapping("/list")
    public TableDataInfo list(UserInfo userInfo) {
        startPage();
        List<UserInfo> list = userInfoService.selectUserInfoList(userInfo);
        return getDataTable(list);
    }

    /**
     * 导出会员列表
     */
    @Operation(summary = "导出会员列表")
    @RequiresPermissions("user:userInfo:export")
    @Log(title = "会员", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, UserInfo userInfo) {
        List<UserInfo> list = userInfoService.selectUserInfoList(userInfo);
        ExcelUtil<UserInfo> util = new ExcelUtil<UserInfo>(UserInfo.class);
        util.exportExcel(response, list, "会员数据");
    }

    /**
     * 获取会员详细信息
     */
    @Operation(summary = "获取会员详细信息")
    @RequiresPermissions("user:userInfo:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(userInfoService.getById(id));
    }

    /**
     * 根据用户id查询地址列表
     */
    @Operation(summary = "根据用户id查询地址列表")
    @RequiresPermissions("user:userInfo:query")
    @GetMapping(value = "/getUserAddress/{id}")
    public AjaxResult getUserAddress(@PathVariable("id") Long id) {
        return success(userInfoService.getUserAddress(id));
    }

    @InnerAuth
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody UserInfo userInfo) {
        userInfoService.register(userInfo);
        return R.ok();
    }

    @Operation(summary = "根据用户名获取用户信息")
    @InnerAuth
    @GetMapping("/info/{username}")
    public R<UserInfo> getUserInfo(@PathVariable("username") String username) {
        UserInfo userInfo = userInfoService.selectUserByUserName(username);
        return R.ok(userInfo);
    }

    @Operation(summary = "更新用户登录信息")
    @InnerAuth
    @PutMapping("/updateUserLogin")
    public R<Boolean> updateUserLogin(@RequestBody UpdateUserLogin updateUserLogin) {
        return R.ok(userInfoService.updateUserLogin(updateUserLogin));
    }

    @Operation(summary = "获取当前登录用户信息")
    @RequiresLogin
    @GetMapping("/getLoginUserInfo")
    public AjaxResult getLoginUserInfo(HttpServletRequest request) {
        Long userId = SecurityContextHolder.getUserId();
        UserInfo userInfo = userInfoService.getById(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, userInfoVo);
        return success(userInfoVo);
    }
}
