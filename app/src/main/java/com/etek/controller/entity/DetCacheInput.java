package com.etek.controller.entity;

import java.util.ArrayList;
import java.util.List;

public class DetCacheInput {
    // 缓存的合同编号
    ArrayList<String> contractCode;
    //  缓存的单位编码
    ArrayList<String> unitCode;

    public ArrayList<String> getContractCode() {
        return contractCode;
    }

    public void setContractCode(ArrayList<String> contractCode) {
        this.contractCode = contractCode;
    }

    public ArrayList<String> getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(ArrayList<String> unitCode) {
        this.unitCode = unitCode;
    }

    @Override
    public String toString() {
        return "DetCacheInput{" +
                "contractCode=" + contractCode +
                ", unitCode=" + unitCode +
                '}';
    }

    public void appendUnitCode(String uc){
        if(null==unitCode){
            unitCode = new ArrayList<String>();
            unitCode.add(uc);
            return;
        }

        if(!unitCode.contains(uc)){
            unitCode.add(uc);
        }
        return;
    }

    public void appendContractCode(String cc){
        if(null==contractCode){
            contractCode = new ArrayList<String>();
            contractCode.add(cc);
            return;
        }

        if(!contractCode.contains(cc)){
            contractCode.add(cc);
        }
        return;
    }

    public  void init(){
        if(null==contractCode) {
            contractCode = new ArrayList<String>();
        }
        if(null==unitCode) {
            unitCode = new ArrayList<String>();
        }
        return;
    }


    public void test(){
        contractCode.add("1234567890123");
        contractCode.add("a123456788123");
        contractCode.add("b123456788123");
        contractCode.add("c123456788123");
        contractCode.add("d123456788123");

        unitCode.add("1234567890123");
        unitCode.add("2123456788123");
        unitCode.add("3123456788123");
        unitCode.add("4123456788123");
        unitCode.add("5123456788123");
    }
}
