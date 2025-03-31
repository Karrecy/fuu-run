package com.karrecy.common.sensitive;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义敏感词校验注解实现
 */
public class SensitiveValidator implements ConstraintValidator<SensitiveWord, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (SensitiveWordHelper.contains(value)) {
            String first = SensitiveWordHelper.findFirst(value);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("内容包含敏感词-"+first)
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

}
