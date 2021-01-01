package com.etek.controller.entity;

import java.io.Serializable;

public class AppUpdateBean {

    /**
     * code : 40000
     * message : 查询成功
     * timestamp : 1609073942185
     * result : {"app":{"version":"1.0.1","versionType":0,"versionNote":"更新了新的安卓APP，版本为1.0.1","downloadUrl":"http://116.239.33.45/etk-resource/app-1.0.1.apk"},"mainBoard":{"version":"1.0.1","versionType":0,"versionNote":"更新了新的主控板程序，版本为1.0.1","downloadUrl":"http://116.239.33.45/etk-resource/CoreBrd1768_Std_v1.0.13.bin"}}
     */

    private int code;
    private String message;
    private long timestamp;
    private ResultBean result;

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean implements Serializable {
        /**
         * app : {"version":"1.0.1","versionType":0,"versionNote":"更新了新的安卓APP，版本为1.0.1","downloadUrl":"http://116.239.33.45/etk-resource/app-1.0.1.apk"}
         * mainBoard : {"version":"1.0.1","versionType":0,"versionNote":"更新了新的主控板程序，版本为1.0.1","downloadUrl":"http://116.239.33.45/etk-resource/CoreBrd1768_Std_v1.0.13.bin"}
         */

        private AppBean app;
        private MainBoardBean mainBoard;

        public AppBean getApp() {
            return app;
        }

        public void setApp(AppBean app) {
            this.app = app;
        }

        public MainBoardBean getMainBoard() {
            return mainBoard;
        }

        public void setMainBoard(MainBoardBean mainBoard) {
            this.mainBoard = mainBoard;
        }

        public static class AppBean implements Serializable {
            /**
             * version : 1.0.1
             * versionType : 0
             * versionNote : 更新了新的安卓APP，版本为1.0.1
             * downloadUrl : http://116.239.33.45/etk-resource/app-1.0.1.apk
             */

            private int versionCode;
            private String versionName;
            private int versionType;
            private String versionNote;
            private String downloadUrl;

            public String getVersionName() {
                return versionName;
            }

            public void setVersionName(String versionName) {
                this.versionName = versionName;
            }

            public int getVersionCode() {
                return versionCode;
            }

            public void setVersionCode(int versionCode) {
                this.versionCode = versionCode;
            }

            public int getVersionType() {
                return versionType;
            }

            public void setVersionType(int versionType) {
                this.versionType = versionType;
            }

            public String getVersionNote() {
                return versionNote;
            }

            public void setVersionNote(String versionNote) {
                this.versionNote = versionNote;
            }

            public String getDownloadUrl() {
                return downloadUrl;
            }

            public void setDownloadUrl(String downloadUrl) {
                this.downloadUrl = downloadUrl;
            }
        }

        public static class MainBoardBean implements Serializable {
            /**
             * version : 1.0.1
             * versionType : 0
             * versionNote : 更新了新的主控板程序，版本为1.0.1
             * downloadUrl : http://116.239.33.45/etk-resource/CoreBrd1768_Std_v1.0.13.bin
             */
            private int versionCode;
            private String versionName;
            private int versionType;
            private String versionNote;
            private String downloadUrl;

            public int getVersionCode() {
                return versionCode;
            }

            public void setVersionCode(int versionCode) {
                this.versionCode = versionCode;
            }

            public String getVersionName() {
                return versionName;
            }

            public void setVersionName(String versionName) {
                this.versionName = versionName;
            }

            public int getVersionType() {
                return versionType;
            }

            public void setVersionType(int versionType) {
                this.versionType = versionType;
            }

            public String getVersionNote() {
                return versionNote;
            }

            public void setVersionNote(String versionNote) {
                this.versionNote = versionNote;
            }

            public String getDownloadUrl() {
                return downloadUrl;
            }

            public void setDownloadUrl(String downloadUrl) {
                this.downloadUrl = downloadUrl;
            }
        }
    }
}
