package com.wjy.response;

/**
 * 2019-07-09 17:32
 *
 * @author XL
 * @version 1.0
 */
public enum ResultCode {
    SUCCESS(0, "SUCCESS"),
    ERROR(-1, "ERROR"),
    SAVE_FAILD(-2, "保存失败"),
    THRID_PRODUCT_LIST_NULL(-3, "三级产品列表为空"),
    THRID_PRODUCT_NOT_EXIST(-4, "不存在此三级产品"),
    OLD_PASSWORD_ERROR(-5, "旧密码输入错误"),
    USERNAME_EXIST(-6, "用户名已存在"),
    EMAIL_EXIST(-7, "邮箱已存在"),
    USER_NOT_EXIST(-8, "用户不存在"),
    ARGS_MUST_NEED(-9, "参数必须要有"),
    CREATE_FILE_FAIL(-10,"生成临时文件失败"),
    ADMIN_NOT_EXIST(-11,"管理员不存在"),
    POSITION_DETAIL_NOT_EXIST(-12,"职位不存在"),
    CREATE_FILE_FAIL_fOR_UPLOAD(-13,"fail to create file used to storage picture"),
    SPRING_CONTAINER_NOT_START(-14,"spring容器未启动"),
    BASE_64_CONVERT_FAIL(-15,"base64转换失败"),



    AUTH_ERROR(-100, "认证失败"),
    ACCESS_ERROR(-101, "授权失败"),
    CACHE_MANAGER_NOT_FOUND(-102, "缓存管理器不存在"),
    ;

    public final Integer code;
    public final String message;

    ResultCode(int code,String msg){
        this.code = code;
        this.message = msg;
    }
}
