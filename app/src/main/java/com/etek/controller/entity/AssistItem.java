package com.etek.controller.entity;

public class AssistItem {

    private String title;
    private int imageResource;

    public AssistItem(String title, int imageResource) {
        this.title = title;
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    @Override
    public String toString() {
        return "assist{" +
                "title='" + title + '\'' +
                ", imageResource=" + imageResource +
                '}';
    }
}
