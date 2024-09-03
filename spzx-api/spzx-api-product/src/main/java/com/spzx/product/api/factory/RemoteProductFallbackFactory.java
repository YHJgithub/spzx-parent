package com.spzx.product.api.factory;

import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

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
        };
    }
}
