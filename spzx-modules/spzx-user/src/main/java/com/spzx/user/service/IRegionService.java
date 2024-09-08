package com.spzx.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.user.domain.Region;

import java.util.List;

public interface IRegionService extends IService<Region> {

    List<Region> treeSelect(String parentCode);
    
}
