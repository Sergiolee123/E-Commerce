package com.ecom.common.exception;

public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000, "unkonw system com.ecom.common.exception"),
    VALID_EXCEPTION(10001, "data format invalid"),
    RESULT_NOT_FOUND_EXCEPTION(10002, "data not found"),;


    private final int code;
    private final String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
