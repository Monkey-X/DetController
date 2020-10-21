package com.etek.controller.dto;


import com.etek.sommerlibrary.dto.ResultCode;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author sommer
 *
 */
public class ActivityResult implements Serializable {

    private static final long serialVersionUID = -881620051415202089L;
    /**
     * 执行结果
     */
    private int code;

  
    /**
     * 返回信息
     */
    private String message;






    public ActivityResult( String message) {

        this.message = message;
    }

    public ActivityResult( int code) {

        this.code = code;
    }


    public ActivityResult(int code, String message) {

        this.code = code;
        this.message = message;
    }


    public int getCode() {

        return code;
    }

    public void setCode(int code) {

        this.code = code;
    }




    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }


//    public static ActivityResult getServieResult(ServiceResult serviceResult) {
//        return new ActivityResult(serviceResult.isSuccess(),serviceResult.getResult(), serviceResult.getMessage());
//    }
    public static ActivityResult errorOfMsg(String message) {
        return new ActivityResult(-1, message);
    }

    public static ActivityResult success() {
        return new ActivityResult(0);
    }

    public static ActivityResult successOf(String data) {
        ActivityResult result = new ActivityResult(0);
        result.setMessage(data);
        return result;
    }

    public static ActivityResult notFound() {
        return new ActivityResult(-2,"Not Found");
    }

 

    public static ActivityResult error(String s) {
        return new ActivityResult(-1,s);
    }

    public static ActivityResult ok() {
        return new ActivityResult(0);
    }
}
