<<<<<<< HEAD
package com.etek.sommerlibrary.utils.parser;


import com.etek.sommerlibrary.utils.LogConstant;

/**
 * @Description: 解析器接口
 * @author: sommer 190119
 * @date: 16/12/11 10:59.
 */
public interface Parser<T> {
    String LINE_SEPARATOR = LogConstant.BR;

    Class<T> parseClassType();

    String parseString(T t);
}
=======
package com.etek.sommerlibrary.utils.parser;


import com.etek.sommerlibrary.utils.LogConstant;

/**
 * @Description: 解析器接口
 * @author: sommer 190119
 * @date: 16/12/11 10:59.
 */
public interface Parser<T> {
    String LINE_SEPARATOR = LogConstant.BR;

    Class<T> parseClassType();

    String parseString(T t);
}
>>>>>>> 806c842... 雷管组网
