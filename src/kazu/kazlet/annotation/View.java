package kazu.kazlet.annotation;

import com.sun.istack.internal.NotNull;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface View {

    @NotNull
    String value();

}
