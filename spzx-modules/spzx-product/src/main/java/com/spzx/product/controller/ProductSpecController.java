package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.service.IProductSpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品规格Controller
 */
@Tag(name = "商品规格接口管理")
@RestController
@RequestMapping("/productSpec")
public class ProductSpecController extends BaseController {

    @Autowired
    private IProductSpecService productSpecService;
    
}