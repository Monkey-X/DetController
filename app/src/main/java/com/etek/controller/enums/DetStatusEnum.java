<<<<<<< HEAD
package com.etek.controller.enums;

import com.etek.controller.R;
import com.etek.controller.utils.SommerUtils;

public enum DetStatusEnum {

    NORMAL(0,"正常", R.color.mediumseagreen),       // 正常
    UN_REG(1,"黑名单",R.color.crimson),    //黑名单
    USED(2,"已使用",R.color.chat_item5_normal),                // 已使用
    UNEXIST(3,"不存在",R.color.gray_pressed),             // 不存在
    EXCEPTION(4,"异常",R.color.setting_text_on),             // 异常
    ;
    int status;
    String name;
    int color;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    DetStatusEnum(int status, String name, int color) {
        this.status = status;
        this.name = name;
        this.color = color;
    }

    DetStatusEnum() {
    }

    public static DetStatusEnum getByStatus(int code) {
        for (DetStatusEnum sendCmdEnum : values()) {
            if (sendCmdEnum.getStatus() == code) {
                return sendCmdEnum;
            }
        }
        return null;
    }


}
=======
package com.etek.controller.enums;

import com.etek.controller.R;
import com.etek.controller.utils.SommerUtils;

public enum DetStatusEnum {

    NORMAL(0,"正常", R.color.mediumseagreen),       // 正常
    UN_REG(1,"黑名单",R.color.crimson),    //黑名单
    USED(2,"已使用",R.color.chat_item5_normal),                // 已使用
    UNEXIST(3,"不存在",R.color.gray_pressed),             // 不存在
    EXCEPTION(4,"异常",R.color.setting_text_on),             // 异常
    ;
    int status;
    String name;
    int color;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    DetStatusEnum(int status, String name, int color) {
        this.status = status;
        this.name = name;
        this.color = color;
    }

    DetStatusEnum() {
    }

    public static DetStatusEnum getByStatus(int code) {
        for (DetStatusEnum sendCmdEnum : values()) {
            if (sendCmdEnum.getStatus() == code) {
                return sendCmdEnum;
            }
        }
        return null;
    }


}
>>>>>>> 806c842... 雷管组网
