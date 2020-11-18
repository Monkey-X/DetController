package com.etek.controller.entity;

public class ProjectImplementItem {

    private int background;
    private int nuBackground;
    private String title;
    private String status;

    public ProjectImplementItem(int background, int nuBackground, String title, String status) {
        this.background = background;
        this.nuBackground = nuBackground;
        this.title = title;
        this.status = status;
    }

    public int getBackground() {
        return background;
    }

    public int getNuBackground() {
        return nuBackground;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public void setNuBackground(int nuBackground) {
        this.nuBackground = nuBackground;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ProjectImplementItem{" +
                "background=" + background +
                ", nuBackground=" + nuBackground +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
