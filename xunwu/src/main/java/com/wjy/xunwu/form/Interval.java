package com.wjy.xunwu.form;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Interval {
    private Integer max;
    private Integer min;
}
