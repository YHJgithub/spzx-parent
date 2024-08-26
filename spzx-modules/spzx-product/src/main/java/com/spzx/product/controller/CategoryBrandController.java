package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.CategoryBrand;
import com.spzx.product.service.ICategoryBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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

    /**
     * 查询分类品牌列表
     */
    @Operation(summary = "查询分类品牌列表")
    @GetMapping("/list")
    public TableDataInfo list(CategoryBrand categoryBrand) {
        startPage();
        List<CategoryBrand> categoryBrandList = categoryBrandService.list(categoryBrand);
        return getDataTable(categoryBrandList);
    }

    @Operation(summary = "获取分类品牌详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Integer id) {
        return success(categoryBrandService.getInfo(id));
    }

    @Operation(summary = "新增分类品牌")
    @PostMapping
    public AjaxResult add(@RequestBody @Validated CategoryBrand categoryBrand) {
        categoryBrand.setCreateBy(SecurityUtils.getUsername());
        return toAjax(categoryBrandService.add(categoryBrand));
    }

    @Operation(summary = "修改分类品牌")
    @PutMapping
    public AjaxResult update(@RequestBody @Validated CategoryBrand categoryBrand) {
        categoryBrand.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(categoryBrandService.updateCategoryBrand(categoryBrand));
    }

    @Operation(summary = "删除分类品牌")
    @DeleteMapping("/{ids}")
    public AjaxResult delete(@PathVariable Integer[] ids) {
        return toAjax(categoryBrandService.removeBatchByIds(Arrays.asList(ids)));
    }
}