package com.etek.controller.activity.project.manager;

import java.io.File;

/**
 * 数据清理类
 */
public class DataCleanManager {

    public static void deleteFile(File file){
        if (file.exists()==false) {
            return;
        }else{
            if (file.isFile()) {
                file.delete();
                return;
            }

            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length==0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                file.delete();
            }

        }
    }
}
