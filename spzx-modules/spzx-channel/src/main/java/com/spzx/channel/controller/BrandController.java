package com.spzx.channel.controller;

import com.spzx.channel.service.IBrandService;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.product.api.domain.Brand;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController extends BaseController {
    
    @Autowired
    IBrandService brandService;

    @Operation(summary = "查询所有品牌")
    @GetMapping("getBrandAll")
    public R<List<Brand>> getBrandAllList() {
        return R.ok(brandService.getBrandAll());
    }
    
}
