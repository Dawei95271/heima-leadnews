package com.heima.common.constants;

/**
 * @description:
 * @author: 16420
 * @time: 2023/2/8 13:10
 */
public class AdminConstants {

    /**
     * 用户审核枚举
     */
    public enum AuthStatus{

        CREATING(0, "创建中"),
        WAIT_PASS(1, "待审核"),
        FAIL_PASS(2, "审核失败"),
        SUCCESS_PASS(9, "审核成功");

        private Short code;
        private String messsage;

        AuthStatus(Integer code, String message){
            this.code = (short) code.intValue();
            this.messsage = message;
        }

        public Short getCode(){
            return this.code;
        }

        public String getMesssage(){
            return this.messsage;
        }


    }
}
