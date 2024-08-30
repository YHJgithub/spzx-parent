package com.spzx.product;

import com.alibaba.excel.EasyExcel;
import com.spzx.product.domain.vo.CategoryExcelVo;
import com.spzx.product.service.ICategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class EasyExcelTest {
    @Autowired
    ICategoryService categoryService;

    @Test
    public void writeDataToExcel() {
        List<CategoryExcelVo> list = categoryService.list().stream().map(category -> {
            CategoryExcelVo categoryExcelVo = new CategoryExcelVo();
            BeanUtils.copyProperties(category, categoryExcelVo);
            return categoryExcelVo;
        }).toList();
        EasyExcel.write("E:/分类数据-导出.xlsx", CategoryExcelVo.class).sheet("分类数据").doWrite(list);
    }

    @Test
    public void readDataFromExcel() {
        String fileName = "E:/分类数据-导出.xlsx";
        ExcelListener categoryExcelDataListener = new ExcelListener();
        EasyExcel.read(fileName)
                .head(CategoryExcelVo.class)
                .sheet()
                .registerReadListener(categoryExcelDataListener)
                .doRead();
        categoryExcelDataListener.getDatas().forEach(System.out::println);
        categoryExcelDataListener.getDatas().clear();
    }
}
