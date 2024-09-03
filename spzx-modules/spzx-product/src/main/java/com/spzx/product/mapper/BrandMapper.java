package com.spzx.product.mapper;

import com.spzx.product.api.domain.Brand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BrandMapper {
    
    List<Brand> selectBrandList(Brand brand);

    Brand getInfo(Integer id);

    int add(Brand brand);

    int edit(Brand brand);

    int remove(@Param("ids") Integer[] ids); 
}
