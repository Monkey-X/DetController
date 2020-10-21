package com.etek.controller.entity;

import java.util.List;

public class ServerRespDetDetail {
    int total;
    List<DetReportDetail> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<DetReportDetail> getRows() {
        return rows;
    }

    public void setRows(List<DetReportDetail> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "ServerRespDetDetail{" +
                "total=" + total +
                ", rows=" + rows +
                '}';
    }
}
