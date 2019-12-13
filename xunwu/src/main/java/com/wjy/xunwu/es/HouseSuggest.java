package com.wjy.xunwu.es;

import lombok.Data;

/**
 * Created by 瓦力.
 */
@Data
public class HouseSuggest {
    private String input;
    private int weight = 10; // 默认权重

}
