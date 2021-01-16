package com.etek.controller.hardware.test;

import java.io.IOException;

import okhttp3.Response;

public interface HttpCallback {

    void onFaile(IOException e);

    void onSuccess(Response response);
}
