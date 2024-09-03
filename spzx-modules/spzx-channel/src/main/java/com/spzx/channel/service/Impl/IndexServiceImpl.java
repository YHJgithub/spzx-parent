package com.spzx.channel.service.Impl;

import com.spzx.channel.service.IIndexService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteCategoryService;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.ProductSku;
import com.spzx.product.api.domain.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IndexServiceImpl implements IIndexService {

    @Autowired
    private RemoteProductService remoteProductService;

    @Autowired
    private RemoteCategoryService remoteCategoryService;

    @Override
    public Map<String, Object> getIndexData() {
        R<List<CategoryVo>> categoryListResult = remoteCategoryService.getOneCategory(SecurityConstants.INNER);
        if (R.FAIL == categoryListResult.getCode()) {
            throw new ServiceException(categoryListResult.getMsg());
        }
        R<List<ProductSku>> productSkuListResult = remoteProductService.getTopSale(SecurityConstants.INNER);
        if (R.FAIL == productSkuListResult.getCode()) {
            throw new ServiceException(productSkuListResult.getMsg());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("categoryList", categoryListResult.getData());
        map.put("productSkuList", productSkuListResult.getData());
        return map;
    }
}
