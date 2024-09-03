package com.spzx.product.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.domain.Product;
import com.spzx.product.domain.ProductDetails;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.domain.SkuStock;
import com.spzx.product.mapper.ProductDetailsMapper;
import com.spzx.product.mapper.ProductMapper;
import com.spzx.product.mapper.ProductSkuMapper;
import com.spzx.product.mapper.SkuStockMapper;
import com.spzx.product.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品Service业务层处理
 */
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    ProductSkuMapper productSkuMapper;

    @Autowired
    SkuStockMapper skuStockMapper;

    @Autowired
    ProductDetailsMapper productDetailsMapper;


    @Override
    public List<Product> selectProductList(Product product) {
        return productMapper.selectProductList(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int insertProduct(Product product) {
        // 主键回填，未插入之前没有id
        productMapper.insert(product);
        // 插入后有id
        Long productId = product.getId();
        List<ProductSku> productSkuList = product.getProductSkuList();
        int size = productSkuList.size();

        for (int i = 0; i < size; i++) {
            // 添加sku
            ProductSku productSku = productSkuList.get(i);
            productSku.setProductId(productId);
            productSku.setSkuCode(String.valueOf(productId + i));
            productSku.setSkuName(product.getName() + productSku.getSkuSpec());
            productSku.setStatus(0);
            productSkuMapper.insert(productSku);

            // 添加库存
            SkuStock skuStock = new SkuStock();
            skuStock.setSkuId(productSku.getId());
            skuStock.setTotalNum(productSku.getStockNum());
            skuStock.setLockNum(0);
            skuStock.setAvailableNum(productSku.getStockNum());
            skuStock.setSaleNum(0);
            skuStock.setStatus(0);
            skuStockMapper.insert(skuStock);
        }
        ProductDetails productDetails = new ProductDetails();
        productDetails.setProductId(productId);
        productDetails.setImageUrls(String.join(",", product.getDetailsImageUrlList()));
        productDetailsMapper.insert(productDetails);

        return 1;
    }

    @Override
    public Product selectProductById(Integer id) {
        // 1.查询商品
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new ServiceException("数据不存在!");
        }

        // 2.商品sku
        List<ProductSku> productSkuList = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getProductId, id));

        // 2.1假设：根据6个sku可以获取6个skuId,可以获取6个SkuStock对象；
        // 获取每一个skuStock库存值，封装在sku对象中。

        List<Long> skuIdList = productSkuList.stream().map(ProductSku::getId).toList();
        // 3.查询库存
        List<SkuStock> skuStockList = skuStockMapper.selectList(new LambdaQueryWrapper<SkuStock>().in(SkuStock::getSkuId, skuIdList));
        Map<Long, Integer> skuIdToTotalNumMap = skuStockList.stream().collect(Collectors.toMap(SkuStock::getSkuId, SkuStock::getTotalNum));

        for (ProductSku productSku : productSkuList) {
            productSku.setStockNum(skuIdToTotalNumMap.get(productSku.getId()));
        }
        product.setProductSkuList(productSkuList);

        // 4.查询详情图片
        ProductDetails productDetails = productDetailsMapper
                .selectOne(new LambdaQueryWrapper<ProductDetails>().eq(ProductDetails::getProductId, id));
        product.setDetailsImageUrlList(Arrays.asList(productDetails.getImageUrls().split(",")));

        return product;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateProduct(Product product) {
        // 修改SPU
        productMapper.updateById(product);
        List<ProductSku> productSkuList = product.getProductSkuList();
        for (ProductSku productSku : productSkuList) {
            // 修改多个SKU
            productSku.setSkuName(product.getName() + productSku.getSkuSpec());
            productSkuMapper.updateById(productSku);
            // 修改多个SkuStock
            SkuStock skuStock = skuStockMapper.selectOne(new LambdaQueryWrapper<SkuStock>().eq(SkuStock::getSkuId, productSku.getId()));
            skuStock.setTotalNum(productSku.getStockNum());
            skuStock.setAvailableNum(productSku.getStockNum() - skuStock.getLockNum());
            skuStockMapper.updateById(skuStock);
        }
        // 修改详情图片信息
        ProductDetails productDetails = new ProductDetails();
        productDetails.setImageUrls(String.join(",", product.getDetailsImageUrlList()));
        productDetailsMapper.update(productDetails, new LambdaQueryWrapper<ProductDetails>().eq(ProductDetails::getProductId, product.getId()));
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteById(Integer[] ids) {
        // 删除SPU
        productMapper.deleteBatchIds(Arrays.asList(ids));
        // 删除SKU
        List<ProductSku> productSkuList = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>().in(ProductSku::getProductId, ids));
        List<Long> skuIdList = productSkuList.stream().map(ProductSku::getId).toList();
        productSkuMapper.deleteBatchIds(skuIdList);
        // 删除skuStock
        skuStockMapper.deleteBatchIds(skuIdList);
        // 删除productDetails
        productDetailsMapper.delete(new LambdaQueryWrapper<ProductDetails>().in(ProductDetails::getProductId, ids));
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAuditStatus(Long id, Integer auditStatus) {
        Product product = new Product();
        product.setId(id);
        if (auditStatus == 1) {
            product.setAuditStatus(1);
            product.setAuditMessage("审批通过");
        } else {
            product.setAuditStatus(-1);
            product.setAuditMessage("审批不通过");
        }
        productMapper.updateById(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatus(Long id, Integer status) {
        Product product = new Product();
        product.setId(id);
        if (status == 1) {
            product.setStatus(1); // 上架
        } else {
            product.setStatus(-1); // 下架
        }
        productMapper.updateById(product);
    }

    @Override
    public List<ProductSku> getTopSale() {
        return productSkuMapper.getTopSale();
    }
}