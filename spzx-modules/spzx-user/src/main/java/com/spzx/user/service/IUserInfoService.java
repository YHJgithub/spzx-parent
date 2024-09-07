package com.spzx.user.service;

import java.util.List;

import com.spzx.user.api.domain.UpdateUserLogin;
import com.spzx.user.api.domain.UserInfo;
import com.spzx.user.domain.UserAddress;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 会员Service接口
 *
 * @author atguigu
 * @date 2024-09-02
 */
public interface IUserInfoService extends IService<UserInfo>
{

    /**
     * 查询会员列表   
     *
     * @param userInfo 会员
     * @return 会员集合
     */
    public List<UserInfo> selectUserInfoList(UserInfo userInfo);

    /**
     * 根据用户id查询地址列表
     * @param id 用户id
     * @return 地址列表
     */
    List<UserAddress> getUserAddress(Long id);

    void register(UserInfo userInfo);

    UserInfo selectUserByUserName(String username);

    Boolean updateUserLogin(UpdateUserLogin updateUserLogin);   
}
