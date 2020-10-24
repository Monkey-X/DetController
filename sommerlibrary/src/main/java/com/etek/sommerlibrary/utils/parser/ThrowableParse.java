<<<<<<< HEAD
package com.etek.sommerlibrary.utils.parser;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Description: Throwable解析器
 * @author: sommer 190119
 * @date: 16/12/11 11:04.
 */
public class ThrowableParse implements Parser<Throwable> {
    @Override
    public Class<Throwable> parseClassType() {
        return Throwable.class;
    }

    @Override
    public String parseString(Throwable throwable) {
        return getStackTraceString(throwable);
    }

    private String getStackTraceString(Throwable t) {
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
=======
package com.etek.sommerlibrary.utils.parser;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Description: Throwable解析器
 * @author: sommer 190119
 * @date: 16/12/11 11:04.
 */
public class ThrowableParse implements Parser<Throwable> {
    @Override
    public Class<Throwable> parseClassType() {
        return Throwable.class;
    }

    @Override
    public String parseString(Throwable throwable) {
        return getStackTraceString(throwable);
    }

    private String getStackTraceString(Throwable t) {
        StringWriter sw = new StringWriter(256);
        PrintWriter pw = new PrintWriter(sw, false);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
>>>>>>> 806c842... 雷管组网
