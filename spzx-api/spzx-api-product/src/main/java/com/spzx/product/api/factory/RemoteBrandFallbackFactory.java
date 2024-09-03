package com.spzx.product.api.factory;

import com.spzx.common.core.domain.R;
import com.spzx.product.api.RemoteBrandService;
import com.spzx.product.api.domain.Brand;
import org.slf4j.Logger;    
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

public class RemoteBrandFallbackFactory implements FallbackFactory<RemoteBrandService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteBrandService.class);

    @Override
    public RemoteBrandService create(Throwable cause) {
        
        log.error("品牌服务调用失败:{}", cause.getMessage());
        return new RemoteBrandService() {
            @Override
            public R<List<Brand>> getBrandAllList(String source) {
                return R.fail("查询所有品牌失败: " + cause.getMessage());
            }
        };
        
    }
}
