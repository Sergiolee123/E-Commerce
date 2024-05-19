package com.ecom.common.constant;

public class ProductConstant {
    public  enum AttrEnum{
        ATTR_TYPE_BASE(1, "base attributes"),
        ATTR_TYPE_SALE(0, "sales attributes");

        private int code;
        private String msg;

        AttrEnum(int code, String msg) {
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
}
