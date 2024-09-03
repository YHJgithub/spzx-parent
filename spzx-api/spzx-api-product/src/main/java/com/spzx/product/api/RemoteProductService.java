package com.spzx.product.api;

import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.constant.ServiceNameConstants;
import com.spzx.common.core.domain.R;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.factory.RemoteProductFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(contextId = "remoteProductService", value = ServiceNameConstants.PRODUCT_SERVICE, fallbackFactory = RemoteProductFallbackFactory.class)
public interface RemoteProductService {

    @GetMapping("/product/getTopSale")
    public R<List<ProductSku>> getTopSale(@RequestHeader(SecurityConstants.FROM_SOURCE) String source);

}