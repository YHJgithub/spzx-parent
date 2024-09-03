package com.spzx.product.api.domain;

import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 品牌对象 brand
 */
@Data
@Schema(description = "品牌")
public class Brand extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "品牌名称")
    @Size(min = 0, max = 64, message = "品牌名称长度不能超过64个字符")
    private String name;

    @Schema(description = "品牌图标")
    @Size(min = 0, max = 200, message = "品牌图标长度不能超过200个字符")
    private String logo;

}