package com.etek.controller.common;

import java.util.HashMap;
import java.util.Map;

public class DetConstant {

    @SuppressWarnings({ "serial" })
    public final static Map<String, Integer> RANYI_CODE = new HashMap<String, Integer>() {{
        put("D", 0);
        put("2", 1);
        put("3", 2);
        put("4", 3);
        put("5", 4);
        put("6", 5);
        put("7", 6);
        put("8", 7);
        put("9", 8);

    }};
    @SuppressWarnings({ "serial" })
    public final static Map<String, Integer> QIANJIN_CODE = new HashMap<String, Integer>() {{
        put("6", 0);
        put("1", 1);
        put("2", 2);
        put("3", 3);
        put("4", 4);
        put("5", 5);
        put("7", 6);
        put("8", 7);
        put("9", 8);

    }};
    @SuppressWarnings({ "serial" })
    public final static Map<String, Integer> ETEK_CODE = new HashMap<String, Integer>() {{
        put("Z", 0);
        put("Y", 1);
        put("X", 2);
        put("1", 3);
        put("2", 4);
        put("3", 5);
        put("B", 6);

    }};

}
