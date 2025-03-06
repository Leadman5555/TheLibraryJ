package org.library.thelibraryj.infrastructure.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

@Component("offsetKeyGenerator")
public class OffsetKeyGenerator implements KeyGenerator {
    @Override
    @NonNull
    public Object generate(@NonNull Object target, @NonNull Method method, @NonNull Object... params) {
        return StringUtils.arrayToDelimitedString(params, "_");
    }

    public static final String OFFSET_KEY_GENERATOR = "offsetKeyGenerator";
}
