
package com.etek.controller.model;
import java.util.List;
import java.util.Date;


public class ApkInfo {

    private String type;
    private List<String> splits;
    private int versionCode;
    private String versionName;
    private boolean enabled;
    private String outputFile;
    private String fullName;
    private String baseName;
    public void setType(String type) {
         this.type = type;
     }
     public String getType() {
         return type;
     }

    public void setSplits(List<String> splits) {
         this.splits = splits;
     }
     public List<String> getSplits() {
         return splits;
     }

    public void setVersionCode(int versionCode) {
         this.versionCode = versionCode;
     }
     public int getVersionCode() {
         return versionCode;
     }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
         this.enabled = enabled;
     }
     public boolean getEnabled() {
         return enabled;
     }

    public void setOutputFile(String outputFile) {
         this.outputFile = outputFile;
     }
     public String getOutputFile() {
         return outputFile;
     }

    public void setFullName(String fullName) {
         this.fullName = fullName;
     }
     public String getFullName() {
         return fullName;
     }

    public void setBaseName(String baseName) {
         this.baseName = baseName;
     }
     public String getBaseName() {
         return baseName;
     }


    @Override
    public String toString() {
        return "ApkInfo{" +
                "type='" + type + '\'' +
                ", splits=" + splits +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", enabled=" + enabled +
                ", outputFile='" + outputFile + '\'' +
                ", fullName='" + fullName + '\'' +
                ", baseName='" + baseName + '\'' +
                '}';
    }
}