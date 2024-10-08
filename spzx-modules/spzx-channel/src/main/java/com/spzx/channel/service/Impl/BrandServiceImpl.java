package com.spzx.channel.service.Impl;

import com.spzx.channel.service.IBrandService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.product.api.RemoteBrandService;
import com.spzx.product.api.domain.Brand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BrandServiceImpl implements IBrandService {

    @Autowired
    private RemoteBrandService remoteBrandService;

    @Override
    public List<Brand> getBrandAll() {
        R<List<Brand>> brandAllList = remoteBrandService.getBrandAllList(SecurityConstants.INNER);
        if (R.FAIL == brandAllList.getCode()) {
            throw new ServiceException(brandAllList.getMsg());
        }
        return brandAllList.getData();
    }
}
