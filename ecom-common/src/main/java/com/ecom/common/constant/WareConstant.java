package com.ecom.common.constant;

import lombok.Getter;

public class WareConstant {
    @Getter
    public enum PurchaseStatusEnum {
        CREATED(0, "create"),
        ASSIGNED(1, "assign"),
        RECEIVED(2, "received"),
        FINISHED(3, "finish"),
        HAS_ERROR(4, "has error");

        private final int code;
        private final String msg;

        PurchaseStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    @Getter
    public enum PurchaseDetailStatusEnum {
        CREATED(0, "create"),
        ASSIGNED(1, "assign"),
        BUYING(2, "buying"),
        FINISHED(3, "finish"),
        HAS_ERROR(4, "has error");

        private final int code;
        private final String msg;

        PurchaseDetailStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }
}
