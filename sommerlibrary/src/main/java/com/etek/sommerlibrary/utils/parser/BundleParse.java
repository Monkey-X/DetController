package com.etek.sommerlibrary.utils.parser;

import android.os.Bundle;

import com.etek.sommerlibrary.utils.StringTool;


/**
 * @Description: Bundle解析器
 * @author: sommer 190119
 * @date: 16/12/11 11:01.
 */
public class BundleParse implements Parser<Bundle> {
    @Override
    public Class<Bundle> parseClassType() {
        return Bundle.class;
    }

    @Override
    public String parseString(Bundle bundle) {
        if (bundle != null) {
            StringBuilder builder = new StringBuilder(bundle.getClass().getName() + " [" +
                    LINE_SEPARATOR);
            for (String key : bundle.keySet()) {
                builder.append(String.format("'%s' => %s " + LINE_SEPARATOR,
                        key, StringTool.objectToString(bundle.get(key))));
            }
            builder.append("]");
            return builder.toString();
        }
        return null;
    }
}
