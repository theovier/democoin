package com.theovier.democoin.common.codec;

import com.theovier.democoin.common.crypto.KeyGenerator;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.util.encoders.Hex;

import java.security.GeneralSecurityException;
import java.security.PublicKey;


public class PublicKeyConverter implements SingleValueConverter {

    @Override
    public String toString(Object o) {
        PublicKey key = (PublicKey) o;
        return Hex.toHexString(key.getEncoded());
    }

    @Override
    public Object fromString(String hex) {
        try {
            return KeyGenerator.getPublicKey(hex);
        } catch (GeneralSecurityException e) {
            return null;
        }
    }

    @Override
    public boolean canConvert(Class aClass) {
        return aClass.equals(BCECPublicKey.class);
    }
}
