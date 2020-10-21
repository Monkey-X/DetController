/**
 * Copyright 2020 bejson.com
 */
package com.etek.controller.dto;

import java.util.List;


public class WhiteBlackController {

    private int success;
    private List<Hbmd> hbmd;
    public void setSuccess(int success) {
        this.success = success;
    }
    public int getSuccess() {
        return success;
    }

    public void setHbmd(List<Hbmd> hbmd) {
        this.hbmd = hbmd;
    }
    public List<Hbmd> getHbmd() {
        return hbmd;
    }


    public class Hbmd {

        private String Sbbh;
        private int Status;
        public void setSbbh(String Sbbh) {
            this.Sbbh = Sbbh;
        }
        public String getSbbh() {
            return Sbbh;
        }

        public int getStatus() {
            return Status;
        }

        public void setStatus(int status) {
            Status = status;
        }
    }
}