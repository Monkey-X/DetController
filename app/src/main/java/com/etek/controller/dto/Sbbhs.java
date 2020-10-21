/**
  * Copyright 2019 Sommer
  */
package com.etek.controller.dto;


public class Sbbhs {

    private String sbbh;
    public void setSbbh(String sbbh) {
         this.sbbh = sbbh;
     }
     public String getSbbh() {
         return sbbh;
     }

    @Override
    public String toString() {
        return "Sbbhs{" +
                "sbbh='" + sbbh + '\'' +
                '}';
    }
}