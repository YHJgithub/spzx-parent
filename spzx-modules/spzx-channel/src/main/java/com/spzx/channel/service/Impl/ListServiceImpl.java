package com.spzx.channel.service.Impl;

import com.spzx.channel.service.IListService;
import com.spzx.common.core.constant.SecurityConstants;
import com.spzx.common.core.domain.R;
import com.spzx.common.core.exception.ServiceException;
import com.spzx.common.core.web.page.TableDataInfo;
import com.spzx.product.api.RemoteProductService;
import com.spzx.product.api.domain.vo.SkuQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ListServiceImpl implements IListService {

    @Autowired
    RemoteProductService remoteProductService;

    @Override
    public TableDataInfo selectProductSkuList(Integer pageNum, Integer pageSize, SkuQuery skuQuery) {
        R<TableDataInfo> tableDataInfoResult = remoteProductService.skuList(pageNum, pageSize, skuQuery, SecurityConstants.INNER);
        if (R.FAIL == tableDataInfoResult.getCode()) {
            throw new ServiceException(tableDataInfoResult.getMsg());
        }
        return tableDataInfoResult.getData();
    }
}
