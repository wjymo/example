package com.wjy.xunwu.response;

/**
 * 2019-07-09 17:32
 *
 * @author wjy
 * @version 1.0
 */
public enum ResultCode {
    SUCCESS(0, "SUCCESS"),
    ERROR(-1, "ERROR"),
    SAVE_FAILD(-2, "保存失败"),
    CLUSTER_ONLY_ONE(-3,"不能查询多个集群！"),
    EXEC_FAILED(-4,"命令执行失败！"),
    POLICY_USER_OVER_RATELIMIT(-5,"请求过频繁！"),
    CREATE_FILE_FAIL(-6,"生成临时文件失败"),
    NOT_VALID_SUBWAY_LINE(-7,"Not valid subway line!"),
    NOT_VALID_SUBWAY_STATION(-8,"Not valid subway station!"),




    AUTH_ERROR(-100, "认证失败"),
    ACCESS_ERROR(-101, "授权失败"),
    CACHE_MANAGER_NOT_FOUND(-102, "缓存管理器不存在"),
    ;

    public final Integer code;
    public final String message;

    ResultCode(int code, String msg){
        this.code = code;
        this.message = msg;
    }
}
