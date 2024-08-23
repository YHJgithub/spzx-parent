package com.spzx.product.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.utils.StringUtils;
import com.spzx.product.domain.ProductUnit;
import com.spzx.product.mapper.ProductUnitMapper;
import com.spzx.product.service.IProductUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ProductUnitService extends ServiceImpl<ProductUnitMapper, ProductUnit> implements IProductUnitService {

    @Autowired
    ProductUnitMapper productUnitMapper;

    @Override
    public List<ProductUnit> selectProductUnitList(ProductUnit productUnit) {
        return productUnitMapper.selectList(new LambdaQueryWrapper<ProductUnit>()
                .like(StringUtils.isNotEmpty(productUnit.getName()), ProductUnit::getName, productUnit.getName()));
    }

    @Override
    public ProductUnit getInfoById(Long id) {
        return productUnitMapper.selectById(id);
    }

    @Override
    public int add(ProductUnit productUnit) {
        return productUnitMapper.insert(productUnit);
    }

    @Override
    public int edit(ProductUnit productUnit) {
        return productUnitMapper.updateById(productUnit);
    }

    @Override
    public int remove(Long[] ids) {
        return productUnitMapper.deleteBatchIds(Arrays.asList(ids));
    }
}
