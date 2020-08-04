package com.github.firelcw.annotation;



import com.github.firelcw.model.ContentType;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Put {
    String value() default "";
    String contentType() default ContentType.APPLICATION_JSON;
}