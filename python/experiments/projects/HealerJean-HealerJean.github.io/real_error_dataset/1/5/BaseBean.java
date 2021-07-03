package com.hlj.vialidate.data;

import com.hlj.vialidate.group.ValidateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author HealerJean
 * @version 1.0v
 * @ClassName BaseBean
 * @date 2019/6/11  16:38.
 * @Description
 */
@Data
public class BaseBean {

    @NotBlank(message = "父类String 不能为空",groups = ValidateGroup.HealerJean.class)
    private String fatherString;

}
