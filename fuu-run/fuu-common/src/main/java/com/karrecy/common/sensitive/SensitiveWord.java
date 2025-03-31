package com.karrecy.common.sensitive;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {SensitiveValidator.class})
public @interface SensitiveWord {
    String message() default "含有敏感词";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}