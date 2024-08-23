package com.spzx.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.domain.ProductUnit;
import com.spzx.product.service.IProductUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productUnit")
@Tag(name = "商品单位管理接口")
public class ProductUnitController extends BaseController {

    @Autowired
    IProductUnitService productUnitService;

    @Operation(summary = "分页")
    @GetMapping("/list")
    public TableDataInfo list(ProductUnit productUnit){
        startPage();
        List<ProductUnit> productUnitList =  productUnitService.selectProductUnitList(productUnit);
        return getDataTable(productUnitList);
    }

    @Operation(summary = "根据id获取商品单位详细信息")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfoById(@PathVariable("id") Long id){
        return success(productUnitService.getInfoById(id));
    }

    @Operation(summary = "新增商品单位")
    @PostMapping
    public AjaxResult add(@RequestBody ProductUnit productUnit){
        return toAjax(productUnitService.add(productUnit));
    }

    @Operation(summary = "修改商品单位")
    @PutMapping
    public AjaxResult edit(@RequestBody ProductUnit productUnit){
        return toAjax(productUnitService.edit(productUnit));
    }

    @Operation(summary = "删除商品单位")
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(productUnitService.remove(ids));
    }
}
