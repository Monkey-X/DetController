package com.etek.sommerlibrary.utils;



import com.etek.sommerlibrary.utils.parser.BundleParse;
import com.etek.sommerlibrary.utils.parser.CollectionParse;
import com.etek.sommerlibrary.utils.parser.IntentParse;
import com.etek.sommerlibrary.utils.parser.MapParse;
import com.etek.sommerlibrary.utils.parser.Parser;
import com.etek.sommerlibrary.utils.parser.ReferenceParse;
import com.etek.sommerlibrary.utils.parser.ThrowableParse;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 日志常量
 * @author: sommer 190119
 * @date: 16/12/11 10:55.
 */
public class LogConstant {



    public static final String STRING_OBJECT_NULL = "Object[object is null]";

    // 每行最大日志长度
    public static final int LINE_MAX = 1024 * 3;

    // 解析属性最大层级
    public static final int MAX_CHILD_LEVEL = 2;



    // 换行符
    public static final String BR = System.getProperty("line.separator");

    // 分割线方位
    public static final int DIVIDER_TOP = 1;
    public static final int DIVIDER_BOTTOM = 2;
    public static final int DIVIDER_CENTER = 4;
    public static final int DIVIDER_NORMAL = 3;

    // 默认支持解析库
    public static final Class<? extends Parser>[] DEFAULT_PARSE_CLASS = new Class[]{
            BundleParse.class, IntentParse.class, CollectionParse.class,
            MapParse.class, ThrowableParse.class, ReferenceParse.class
    };
    private static List<Parser> parseList;
    static {
        parseList = new ArrayList<>();
        for (Class<? extends Parser> cla : DEFAULT_PARSE_CLASS) {
            try {
                parseList.add( cla.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void addParserClass(Class<? extends Parser>[] DEFAULT_PARSE_CLASS) {
        parseList = new ArrayList<>();
        for (Class<? extends Parser> cla : DEFAULT_PARSE_CLASS) {
            try {
                parseList.add(0, cla.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 获取默认解析类
     *
     * @return
     */
    public static List<Parser> getParsers() {
        return parseList;
    }


}
