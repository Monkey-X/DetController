
package com.etek.controller.model;


public class UpdateAppResp {

    private OutputType outputType;
    private ApkInfo apkInfo;
    private String path;
    private Properties properties;
    public void setOutputType(OutputType outputType) {
         this.outputType = outputType;
     }
     public OutputType getOutputType() {
         return outputType;
     }

    public void setApkInfo(ApkInfo apkInfo) {
         this.apkInfo = apkInfo;
     }
     public ApkInfo getApkInfo() {
         return apkInfo;
     }

    public void setPath(String path) {
         this.path = path;
     }
     public String getPath() {
         return path;
     }

    public void setProperties(Properties properties) {
         this.properties = properties;
     }
     public Properties getProperties() {
         return properties;
     }


    @Override
    public String toString() {
        return "UpdateAppResp{" +
                "outputType=" + outputType +
                ", apkInfo=" + apkInfo +
                ", path='" + path + '\'' +
                ", properties=" + properties +
                '}';
    }
}