package com.ecom.common.valid.validator;

import com.ecom.common.valid.anno.ListValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {

    private final Set<Integer> intSet = new HashSet<>();

    @Override
    public void initialize(ListValue constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        int[] vals = constraintAnnotation.vals();
        for(int val : vals){
            intSet.add(val);
        }

    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        return intSet.contains(value);
    }
}
