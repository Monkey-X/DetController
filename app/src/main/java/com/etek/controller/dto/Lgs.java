/**
  * Copyright 2019 Sommer
  */
package com.etek.controller.dto;
import java.util.List;


public class Lgs {

    private List<Lg> lg;
    public void setLg(List<Lg> lg) {
         this.lg = lg;
     }
     public List<Lg> getLg() {
         return lg;
     }

    @Override
    public String toString() {
        return "Lgs{" +
                "lg=" + lg +
                '}';
    }
}