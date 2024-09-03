package com.spzx.product.service;

import com.spzx.product.api.domain.Brand;

import java.util.List;

public interface IBrandService {

    /**
     * 品牌分页接口
     *
     * @param brand
     * @return
     */
    List<Brand> selectBrandList(Brand brand);

    /**
     * 根据id获取品牌详细信息
     * @param id 品牌主键
     * @return   Brand
     */
    Brand getInfo(Integer id);

    int add(Brand brand);

    int edit(Brand brand);

    int remove(Integer[] ids);

    List<Brand> getBrandAll();
}
