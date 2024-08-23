package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.service.ICategoryBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类品牌Controller
 */
@Tag(name = "分类品牌接口管理")
@RestController
@RequestMapping("/categoryBrand")
public class CategoryBrandController extends BaseController {

    @Autowired
    private ICategoryBrandService categoryBrandService;


}