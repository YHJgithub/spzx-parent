package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.ProductSpec;
import com.spzx.product.service.IProductSpecService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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

    @Operation(summary = "查询商品规格列表")
    @GetMapping("/list")
    public TableDataInfo list(ProductSpec productSpec) {
        startPage();
        List<ProductSpec> list = productSpecService.list(productSpec);
        return getDataTable(list);
    }

    @Operation(summary = "获取商品规格详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Integer id) {
        return AjaxResult.success(productSpecService.getInfo(id));
    }

    @Operation(summary = "新增商品规格")
    @PostMapping
    public AjaxResult save(@RequestBody @Validated ProductSpec productSpec) {
        productSpec.setCreateBy(SecurityUtils.getUsername());
        return toAjax(productSpecService.save(productSpec));
    }

    @Operation(summary = "修改商品规格")
    @PutMapping
    public AjaxResult update(@RequestBody @Validated ProductSpec productSpec) {
        productSpec.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(productSpecService.updateById(productSpec));
    }

    @Operation(summary = "删除商品规格")
    @DeleteMapping("/{ids}")
    public AjaxResult delete(@PathVariable Integer[] ids) {
        return toAjax(productSpecService.removeBatchByIds(Arrays.asList(ids)));
    }

    @Operation(summary = "根据分类id获取商品规格列表")
    @GetMapping("/productSpecList/{categoryId}")
    public AjaxResult selectProductSpecListByCategoryId(@PathVariable Integer categoryId) {
        return success(productSpecService.selectProductSpecListByCategoryId(categoryId));
    }
}