package com.theovier.democoin.common.codec;

import com.theovier.democoin.common.crypto.Sha256Hash;
import com.thoughtworks.xstream.converters.SingleValueConverter;

public class Sha256HashConverter implements SingleValueConverter {

    @Override
    public String toString(Object o) {
        return o.toString();
    }

    @Override
    public Object fromString(String hexString) {
        return new Sha256Hash(hexString);
    }

    @Override
    public boolean canConvert(Class aClass) {
        return aClass.equals(Sha256Hash.class);
    }
}
