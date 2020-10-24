<<<<<<< HEAD
package com.etek.sommerlibrary.utils.parser;


import com.etek.sommerlibrary.utils.StringTool;

import java.lang.ref.Reference;

/**
 * @Description: Reference解析器
 * @author: sommer 190119
 * @date: 16/12/11 11:04.
 */
public class ReferenceParse implements Parser<Reference> {
    @Override
    public Class<Reference> parseClassType() {
        return Reference.class;
    }

    @Override
    public String parseString(Reference reference) {
        Object actual = reference.get();
        StringBuilder builder = new StringBuilder(reference.getClass().getSimpleName() + "<"
                + actual.getClass().getSimpleName() + "> {");
        builder.append("→").append(StringTool.objectToString(actual));
        return builder.toString() + "}";
    }
}
=======
package com.etek.sommerlibrary.utils.parser;


import com.etek.sommerlibrary.utils.StringTool;

import java.lang.ref.Reference;

/**
 * @Description: Reference解析器
 * @author: sommer 190119
 * @date: 16/12/11 11:04.
 */
public class ReferenceParse implements Parser<Reference> {
    @Override
    public Class<Reference> parseClassType() {
        return Reference.class;
    }

    @Override
    public String parseString(Reference reference) {
        Object actual = reference.get();
        StringBuilder builder = new StringBuilder(reference.getClass().getSimpleName() + "<"
                + actual.getClass().getSimpleName() + "> {");
        builder.append("→").append(StringTool.objectToString(actual));
        return builder.toString() + "}";
    }
}
>>>>>>> 806c842... 雷管组网
