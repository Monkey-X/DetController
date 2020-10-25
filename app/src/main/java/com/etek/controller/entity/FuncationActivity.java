package com.etek.controller.entity;

public class FuncationActivity {

    Class<?> funcation;
    String name;
    int title;
    int image;
    int icon;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public FuncationActivity(Class<?> funcation, String name, int title, int image) {
        this.funcation = funcation;
        this.name = name;
        this.title = title;
        this.image = image;
    }

    public Class<?> getFuncation() {
        return funcation;
    }

    public void setFuncation(Class<?> funcation) {
        this.funcation = funcation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "FuncationActivity{" +
                "funcation=" + funcation +
                ", name='" + name + '\'' +
                ", title=" + title +
                ", image=" + image +
                '}';
    }
}
