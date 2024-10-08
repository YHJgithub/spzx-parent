package com.spzx.product.domain;

import com.spzx.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "商品单位")
public class ProductUnit extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Schema(description = "商品单位名称")
    @NotBlank(message = "商品单位名称不能为空")
    @Size(min = 0, max = 10, message = "商品单位名称长度不能超过10个字符")
    private String name;
}
