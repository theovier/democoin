package com.theovier.democoin.common.codec;

import com.theovier.democoin.common.Address;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.apache.log4j.Logger;

public class AddressConverter implements SingleValueConverter {

    @Override
    public String toString(Object o) {
        return o.toString();
    }

    @Override
    public Object fromString(String s) {
        return new Address(s);
    }

    @Override
    public boolean canConvert(Class aClass) {
        return aClass.equals(Address.class);
    }
}
