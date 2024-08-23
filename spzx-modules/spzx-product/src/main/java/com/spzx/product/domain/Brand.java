package com.spzx.product.domain;

import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 品牌对象 brand
 */
@Data
@Schema(description = "品牌")
public class Brand extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "品牌名称")
    private String name;

    @Schema(description = "品牌图标")
    private String logo;

}