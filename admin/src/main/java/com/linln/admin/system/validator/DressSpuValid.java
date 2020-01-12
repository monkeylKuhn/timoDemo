package com.linln.admin.system.validator;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;

/**
 * @author 小懒虫
 * @date 2020/01/11
 */
@Data
public class DressSpuValid implements Serializable {
    @NotEmpty(message = "spuid不能为空")
    private String productID;
}