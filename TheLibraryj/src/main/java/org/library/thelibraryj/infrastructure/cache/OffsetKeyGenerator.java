package org.library.thelibraryj.infrastructure.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

@Component("offsetKeyGenerator")
public class OffsetKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return StringUtils.arrayToDelimitedString(params, "_");
    }
}
