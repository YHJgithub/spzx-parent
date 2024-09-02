package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.common.security.annotation.RequiresPermissions;
import com.spzx.common.security.utils.SecurityUtils;
import com.spzx.product.domain.Brand;
import com.spzx.product.service.IBrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "品牌接口管理")
@RestController
@RequestMapping("/brand")
public class BrandController extends BaseController {

    @Autowired
    private IBrandService brandService;

    /**
     * @param brand 品牌
     * @return 全部分页品牌
     */
    @RequiresPermissions("product:brand:list")
    @Operation(summary = "分页")
    @GetMapping("/list")
    public TableDataInfo list(@Validated Brand brand) {
        startPage();
        List<Brand> list = brandService.selectBrandList(brand);
        return getDataTable(list);
    }

    /**
     * @param id 品牌主键
     * @return Brand
     */
    @RequiresPermissions("product:brand:query")
    @Operation(summary = "根据id获取品牌详细信息")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Integer id) {
        return success(brandService.getInfo(id));
    }

    @RequiresPermissions("product:brand:add")
    @Operation(summary = "新增品牌")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Brand brand) {
        brand.setCreateBy(SecurityUtils.getUsername());
        return toAjax(brandService.add(brand));
    }

    @RequiresPermissions("product:brand:edit")
    @Operation(summary = "修改品牌")
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody Brand brand) {
        brand.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(brandService.edit(brand));
    }

    @RequiresPermissions("product:brand:remove")
    @Operation(summary = "删除品牌")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids) {
        return toAjax(brandService.remove(ids));
    }

    @RequiresPermissions("product:brand:query")
    @Operation(summary = "获取全部品牌")
    @GetMapping("getBrandAll")
    public AjaxResult getBrandAll() {
        return success(brandService.getBrandAll());
    }
}
