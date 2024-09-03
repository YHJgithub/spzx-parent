package com.spzx.product.service.Impl;

import com.spzx.product.mapper.BrandMapper;
import com.spzx.product.api.domain.Brand;
import com.spzx.product.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements IBrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> selectBrandList(Brand brand) {
        return brandMapper.selectBrandList(brand);
    }

    @Override
    public Brand getInfo(Integer id) {
        return brandMapper.getInfo(id);
    }

    @Override
    public int add(Brand brand) {
        return brandMapper.add(brand);
    }

    @Override
    public int edit(Brand brand) {
        return brandMapper.edit(brand);
    }

    @Override
    public int remove(Integer[] ids) {
        return brandMapper.remove(ids);
    }

    @Override
    public List<Brand> getBrandAll() {
        return brandMapper.selectBrandList(null);
    }
}
