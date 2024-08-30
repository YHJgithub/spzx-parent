package com.spzx.product.controller;

import com.spzx.common.core.web.controller.BaseController;
import com.spzx.common.core.web.domain.AjaxResult;
import com.spzx.product.service.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商品分类Controller
 */
@Tag(name = "商品分类接口管理")
@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {

    @Autowired
    private ICategoryService categoryService;

    @Operation(summary = "获取分类下拉树列表")
    @GetMapping(value = "/treeSelect/{id}")
    public AjaxResult treeSelect(@PathVariable Long id) {
        return success(categoryService.treeSelect(id));
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response) {
        categoryService.exportData(response);
    }
    
    @PostMapping("/import")
    public AjaxResult importData(MultipartFile file) {
        try {
            categoryService.importData(file);
            return AjaxResult.success("导入成功");
        } catch (Exception e){
            e.printStackTrace();
        }
        return AjaxResult.error("导入失败");
    }
}