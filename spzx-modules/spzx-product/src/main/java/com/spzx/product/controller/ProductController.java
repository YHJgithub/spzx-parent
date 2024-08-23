package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品Controller
 */
@Tag(name = "商品管理")
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private IProductService productService;


}