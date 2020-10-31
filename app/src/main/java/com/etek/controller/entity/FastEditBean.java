package com.etek.controller.entity;

/**
 *  延时下载批量修改
 */
public class FastEditBean {

    int startNum;// 开始序号
    int endNum;// 截止序号
    int startTime;// 起始时间
    int holeNum; // 每孔雷管
    int holeOutTime;// 孔间延时
    int holeInTime; // 孔内延时

    public int getStartNum() {
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public int getEndNum() {
        return endNum;
    }

    public void setEndNum(int endNum) {
        this.endNum = endNum;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getHoleNum() {
        return holeNum;
    }

    public void setHoleNum(int holeNum) {
        this.holeNum = holeNum;
    }

    public int getHoleOutTime() {
        return holeOutTime;
    }

    public void setHoleOutTime(int holeOutTime) {
        this.holeOutTime = holeOutTime;
    }

    public int getHoleInTime() {
        return holeInTime;
    }

    public void setHoleInTime(int holeInTime) {
        this.holeInTime = holeInTime;
    }
}
