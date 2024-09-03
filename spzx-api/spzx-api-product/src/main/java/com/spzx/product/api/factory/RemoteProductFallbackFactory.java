package com.spzx.product.api.factory;

import com.spzx.common.core.domain.R;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.Product;
import com.spzx.product.api.domain.ProductDetails;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.vo.SkuPrice;
import com.spzx.product.api.domain.vo.SkuQuery;
import com.spzx.product.api.domain.vo.SkuStockVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;
import java.util.Map;

public class RemoteProductFallbackFactory implements FallbackFactory<RemoteProductService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteProductFallbackFactory.class);

    @Override
    public RemoteProductService create(Throwable cause) {
        log.error("商品服务调用失败:{}", cause.getMessage());
        return new RemoteProductService() {
            @Override
            public R<List<ProductSku>> getTopSale(String source) {
                return R.fail("获取畅销商品列表失败:" + cause.getMessage());
            }

            @Override
            public R<TableDataInfo> skuList(Integer pageNum, Integer pageSize, SkuQuery skuQuery, String source) {
                return R.fail("获取商品列表失败:" + cause.getMessage());
            }

            @Override
            public R<ProductSku> getProductSku(Long skuId, String source) {
                return R.fail("获取商品sku失败:" + cause.getMessage());
            }

            @Override
            public R<Product> getProduct(Long id, String source) {
                return R.fail("获取商品信息失败:" + cause.getMessage());
            }

            @Override
            public R<SkuPrice> getSkuPrice(Long skuId, String source) {
                return R.fail("获取商品sku价格失败:" + cause.getMessage());
            }

            @Override
            public R<ProductDetails> getProductDetails(Long id, String source) {
                return R.fail("获取商品详情失败:" + cause.getMessage());
            }

            @Override
            public R<Map<String, Long>> getSkuSpecValue(Long id, String source) {
                return R.fail("获取商品sku规格失败:" + cause.getMessage());
            }

            @Override
            public R<SkuStockVo> getSkuStock(Long skuId, String source) {
                return R.fail("获取商品sku库存失败:" + cause.getMessage());
            }

        };
    }
}
