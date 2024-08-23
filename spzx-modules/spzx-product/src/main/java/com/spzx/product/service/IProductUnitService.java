package com.spzx.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spzx.product.domain.ProductUnit;

import java.util.List;

public interface IProductUnitService extends IService<ProductUnit> {

    List<ProductUnit> selectProductUnitList(ProductUnit productUnit);

    ProductUnit getInfoById(Long id);

    int add(ProductUnit productUnit);

    int edit(ProductUnit productUnit);

    int remove(Long[] ids);
}
