package com.wjy.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wjy.exception.NettyException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * @author wjy
 */
public class BeanValidator {

    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    public static <T> Map<String, String> validate(T t, Class<Object>... groups) {
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> validateResult = validator.validate(t, groups);
        if (validateResult.isEmpty()) {
            return Collections.emptyMap();
        } else {
            LinkedHashMap<String, String> errors = Maps.newLinkedHashMap();
            Iterator<ConstraintViolation<T>> iterator = validateResult.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> violation = iterator.next();
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return errors;
        }
    }

    public static Map<String, String> validateList(Collection<Object> collection) {
        Preconditions.checkNotNull(collection);
        Iterator<Object> iterator = collection.iterator();
        Map<String, String> errors;

        do {
            if (!iterator.hasNext()) {
                return Collections.emptyMap();
            }
            Object object = iterator.next();
            errors = validate(object, new Class[0]);
        } while (errors.isEmpty());

        return errors;
    }

    public static Map<String, String> validateObject(Object first, Object... objects) {
        if (objects != null && objects.length > 0) {
            return validateList(Lists.asList(first, objects));
        } else {
            return validate(first,new Class[0]);
        }
    }

    public static void check(Object param)  {
        Map<String, String> map = BeanValidator.validateObject(param);
        if (map!=null&&!map.isEmpty()) {
            for(Map.Entry entry:map.entrySet()){
                throw new NettyException(map.get(entry.getKey()));
            }
        }
    }
}