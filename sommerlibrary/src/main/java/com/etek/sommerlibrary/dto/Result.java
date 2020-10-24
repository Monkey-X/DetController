<<<<<<< HEAD
package com.etek.sommerlibrary.dto;


import java.io.Serializable;

/**
 * @author sommer
 *
 */
public class Result implements Serializable {

    private static final long serialVersionUID = -881620051415202089L;
    /**
     * 执行结果
     */
    private boolean success;

    /**
     * 结果集
     */
    private Object data;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回状态码
     */
    private String code;

    public Result() {

        this.success = true;
    }

    public Result(boolean success) {

        this.success = success;
    }

    public Result(boolean success, Object data) {

        this.success = success;
        this.data = data;
    }

    public Result(boolean success, Object data, String message) {

        this.success = success;
        this.data = data;
        this.message = message;
    }

    public Result(boolean success, Object data, String message, String code) {

        this.success = success;
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public Result(boolean success, String message) {

        this.success = success;
        this.message = message;
    }

    public Result(String code, String message) {

        this.code = code;
        this.message = message;
    }

    public Result(ResultCode rc) {

        this.code = rc.getCode();
        this.message = rc.getMessage();
    }

    public String getCode() {

        return code;
    }

    public void setCode(String code) {

        this.code = code;
    }

    public boolean isSuccess() {

        return success;
    }

    public void setSuccess(boolean success) {

        this.success = success;
    }

    public Object getData() {

        return data;
    }

    public void setData(Object data) {

        this.data = data;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }


//    public static Result getServieResult(ServiceResult serviceResult) {
//        return new Result(serviceResult.isSuccess(),serviceResult.getResult(), serviceResult.getMessage());
//    }
    public static Result errorOfMsg(String message) {
        return new Result(false, message);
    }

    public static Result errorMsg(String message) {
        return new Result(false, message);
    }
    public static Result success() {
        return new Result(true);
    }

    public static Result successOf(Object data) {
        Result result = new Result(true);
        result.setData(data);
        return result;
    }

    public static Result notFound() {
        return new Result(false,"Not Found");
    }

 

    public static Result error(String s) {
        return new Result(false,s);
    }

    public static Result ok() {
        return new Result(true);
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
=======
package com.etek.sommerlibrary.dto;


import java.io.Serializable;

/**
 * @author sommer
 *
 */
public class Result implements Serializable {

    private static final long serialVersionUID = -881620051415202089L;
    /**
     * 执行结果
     */
    private boolean success;

    /**
     * 结果集
     */
    private Object data;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回状态码
     */
    private String code;

    public Result() {

        this.success = true;
    }

    public Result(boolean success) {

        this.success = success;
    }

    public Result(boolean success, Object data) {

        this.success = success;
        this.data = data;
    }

    public Result(boolean success, Object data, String message) {

        this.success = success;
        this.data = data;
        this.message = message;
    }

    public Result(boolean success, Object data, String message, String code) {

        this.success = success;
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public Result(boolean success, String message) {

        this.success = success;
        this.message = message;
    }

    public Result(String code, String message) {

        this.code = code;
        this.message = message;
    }

    public Result(ResultCode rc) {

        this.code = rc.getCode();
        this.message = rc.getMessage();
    }

    public String getCode() {

        return code;
    }

    public void setCode(String code) {

        this.code = code;
    }

    public boolean isSuccess() {

        return success;
    }

    public void setSuccess(boolean success) {

        this.success = success;
    }

    public Object getData() {

        return data;
    }

    public void setData(Object data) {

        this.data = data;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {

        this.message = message;
    }


//    public static Result getServieResult(ServiceResult serviceResult) {
//        return new Result(serviceResult.isSuccess(),serviceResult.getResult(), serviceResult.getMessage());
//    }
    public static Result errorOfMsg(String message) {
        return new Result(false, message);
    }

    public static Result errorMsg(String message) {
        return new Result(false, message);
    }
    public static Result success() {
        return new Result(true);
    }

    public static Result successOf(Object data) {
        Result result = new Result(true);
        result.setData(data);
        return result;
    }

    public static Result notFound() {
        return new Result(false,"Not Found");
    }

 

    public static Result error(String s) {
        return new Result(false,s);
    }

    public static Result ok() {
        return new Result(true);
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", data=" + data +
                ", message='" + message + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
>>>>>>> 806c842... 雷管组网
