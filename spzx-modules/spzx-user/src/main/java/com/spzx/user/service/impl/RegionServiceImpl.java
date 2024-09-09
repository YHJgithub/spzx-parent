package com.spzx.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.user.domain.Region;
import com.spzx.user.mapper.RegionMapper;
import com.spzx.user.service.IRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements IRegionService {

    @Autowired
    private RegionMapper regionMapper;


    @Override
    public List<Region> treeSelect(String parentCode) {
        return regionMapper.selectList(new LambdaQueryWrapper<Region>().eq(Region::getParentCode, parentCode));
    }

    @Override
    public String getNameByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        Region region = regionMapper.selectOne(new LambdaQueryWrapper<Region>().eq(Region::getCode, code).select(Region::getName));
        if (null != region) {
            return region.getName();
        }
        return "";
    }
}
