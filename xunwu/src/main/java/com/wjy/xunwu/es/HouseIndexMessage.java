package com.wjy.xunwu.es;

import lombok.Data;

/**
 * Created by 瓦力.
 */
@Data
public class HouseIndexMessage {

    public static final String INDEX = "index";
    public static final String REMOVE = "remove";

    public static final int MAX_RETRY = 3;

    private Long houseId;
    private String operation;
    private int retry = 0;

    /**
     * 默认构造器 防止jackson序列化失败
     */
    public HouseIndexMessage() {
    }

    public HouseIndexMessage(Long houseId, String operation) {
        this.houseId = houseId;
        this.operation = operation;
    }

    public HouseIndexMessage(Long houseId) {
        this.houseId = houseId;
    }
}
