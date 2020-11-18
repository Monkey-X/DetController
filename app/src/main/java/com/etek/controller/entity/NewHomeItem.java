package com.etek.controller.entity;

public class NewHomeItem {

    private int background;
    private String title;
    private String description;

    public NewHomeItem(int background, String title, String description) {
        this.background = background;
        this.title = title;
        this.description = description;
    }

    public int getBackground() {
        return background;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "NewHomeItem{" +
                "background=" + background +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
