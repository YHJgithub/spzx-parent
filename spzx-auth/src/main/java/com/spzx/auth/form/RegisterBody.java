package com.spzx.auth.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户注册对象
 *
 * @author spzx
 */
@Data
public class RegisterBody extends LoginBody {
    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "验证码")
    private String code;
}
