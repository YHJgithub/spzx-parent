package com.spzx.product.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.redis.cache.GuiguCache;
import com.spzx.product.api.domain.vo.SkuPrice;
import com.spzx.product.api.domain.vo.SkuQuery;
import com.spzx.product.api.domain.vo.SkuStockVo;
import com.spzx.product.api.domain.Product;
import com.spzx.product.api.domain.ProductDetails;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.domain.SkuStock;
import com.spzx.product.mapper.ProductDetailsMapper;
import com.spzx.product.mapper.ProductMapper;
import com.spzx.product.mapper.ProductSkuMapper;
import com.spzx.product.mapper.SkuStockMapper;
import com.spzx.product.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
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

    @Autowired
    RedisTemplate redisTemplate;

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

    @Override
    public List<ProductSku> selectProductSkuList(SkuQuery skuQuery) {
        return productSkuMapper.selectProductSkuList(skuQuery);
    }

    // ==================================获取商品详情START=====================================
    /* @Override
    public ProductSku getProductSku(Long skuId) {
        // 1.先查询redis缓存，有，直接返回，
        String dataKey = "product:sku:" + skuId;

        ProductSku productSku = null;
        if (redisTemplate.hasKey(dataKey)) {
            productSku = (ProductSku) redisTemplate.opsForValue().get(dataKey);
            log.info("命中缓存，直接返回，线程ID：{}，线程名称：{}", Thread.currentThread().getId(), Thread.currentThread().getName());
            return productSku;
        }

        // 2.没有,再查询数据库，放在缓存中，给下次访问使用。利用缓存提高效率。
        String lockKey = "product:sku:lock:" + skuId;
        String lockVal = UUID.randomUUID().toString().replaceAll("-", "");
        Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockVal, 5, TimeUnit.SECONDS);
        if (ifAbsent) { // 加分布式锁成功
            try {
                log.info("获取锁成功：{}，线程名称：{}", Thread.currentThread().getId(), Thread.currentThread().getName());
                productSku = productSkuMapper.selectById(skuId);
                long ttl = productSku == null ? 1 * 60 : 10 * 60; // 解决缓存穿透：解决方案1（null值也存储，但是时间短一些。）
                redisTemplate.opsForValue().set(dataKey, productSku, ttl, TimeUnit.SECONDS);
                return productSku;
            } finally {
                // 4.业务执行完毕释放锁
                String scriptText = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                        "then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                redisScript.setScriptText(scriptText);
                redisScript.setResultType(Long.class);
                redisTemplate.execute(redisScript, Arrays.asList(lockKey), lockVal);
            }
        } else { // 加分布式锁失败,等待自旋
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.error("获取锁失败，自旋：{}，线程名称：{}", Thread.currentThread().getId(), Thread.currentThread().getName());
            return getProductSku(skuId);
        }
    } */

    @GuiguCache(prefix = "product:sku:")
    @Override
    public ProductSku getProductSku(Long skuId) {
        ProductSku productSku = productSkuMapper.selectById(skuId);
        return productSku;
    }

    @GuiguCache(prefix = "product:")
    @Override
    public Product getProduct(Long id) {
        return productMapper.selectById(id);
    }

    @GuiguCache(prefix = "skuPrice:")
    @Override
    public SkuPrice getSkuPrice(Long skuId) {
        ProductSku productSku = productSkuMapper.selectOne(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getId, skuId).select(ProductSku::getSalePrice, ProductSku::getMarketPrice));
        SkuPrice skuPrice = new SkuPrice();
        BeanUtils.copyProperties(productSku, skuPrice);
        return skuPrice;
    }

    @GuiguCache(prefix = "productDetails:")
    @Override
    public ProductDetails getProductDetails(Long id) {
        return productDetailsMapper.selectOne(new LambdaQueryWrapper<ProductDetails>().eq(ProductDetails::getProductId, id));
    }

    @GuiguCache(prefix = "skuSpecValue:")
    @Override
    public Map<String, Long> getSkuSpecValue(Long id) {
        List<ProductSku> productSkuList = productSkuMapper.selectList(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getProductId, id).select(ProductSku::getId, ProductSku::getSkuSpec));
        Map<String, Long> skuSpecValueMap = new HashMap<>();
        productSkuList.forEach(item -> {
            skuSpecValueMap.put(item.getSkuSpec(), item.getId());
        });
        return skuSpecValueMap;
    }

    @GuiguCache(prefix = "skuStockVo:")
    @Override
    public SkuStockVo getSkuStock(Long skuId) {
        SkuStock skuStock = skuStockMapper.selectOne(new LambdaQueryWrapper<SkuStock>().eq(SkuStock::getSkuId, skuId));
        SkuStockVo skuStockVo = new SkuStockVo();
        BeanUtils.copyProperties(skuStock, skuStockVo);
        return skuStockVo;
    }
    // ==================================获取商品详情END=====================================
}