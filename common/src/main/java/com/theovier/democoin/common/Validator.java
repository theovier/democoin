package com.theovier.democoin.common;

public interface Validator<T> {
    boolean isValid(final T toValidate);
}
