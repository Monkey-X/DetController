<<<<<<< HEAD
package com.etek.controller.entity;


public class HomeItem {
    private String title;
    private Class<?> activity;
    private int imageResource;
    private int thumbnail;
    private int navId;

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNavId() {
        return navId;
    }

    public void setNavId(int navId) {
        this.navId = navId;
    }

    public Class<?> getActivity() {
        return activity;
    }

    public void setActivity(Class<?> activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "HomeItem{" +
                "title='" + title + '\'' +
                ", activity=" + activity +
                ", imageResource=" + imageResource +
                ", thumbnail=" + thumbnail +
                '}';
    }
=======
package com.etek.controller.entity;


public class HomeItem {
    private String title;
    private Class<?> activity;
    private int imageResource;
    private int thumbnail;
    private int navId;

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNavId() {
        return navId;
    }

    public void setNavId(int navId) {
        this.navId = navId;
    }

    public Class<?> getActivity() {
        return activity;
    }

    public void setActivity(Class<?> activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "HomeItem{" +
                "title='" + title + '\'' +
                ", activity=" + activity +
                ", imageResource=" + imageResource +
                ", thumbnail=" + thumbnail +
                '}';
    }
>>>>>>> 806c842... 雷管组网
}