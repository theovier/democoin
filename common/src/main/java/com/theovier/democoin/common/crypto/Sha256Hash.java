package com.theovier.democoin.common.crypto;

import com.google.common.primitives.Ints;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.util.encoders.Hex;

import java.io.Serializable;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

//https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/Sha256Hash.java

public class Sha256Hash implements Serializable, Comparable<Sha256Hash> {

    public static final int LENGTH = 32; // bytes
    private final byte[] bytes;
    public static final Sha256Hash ZERO_HASH = create(new byte[LENGTH]);

    public Sha256Hash(byte[] rawHashBytes) {
        checkArgument(rawHashBytes.length == LENGTH);
        this.bytes = rawHashBytes;
    }

    public Sha256Hash(String hexString) {
        checkArgument(hexString.length() == LENGTH * 2);
        this.bytes = Hex.decode(hexString);
    }

    public static Sha256Hash create(String input) {
        return new Sha256Hash(hash(input));
    }

    public static Sha256Hash create(byte[] input) {
        return new Sha256Hash(hash(input));
    }

    public static byte[] hash(String input) {
        return DigestUtils.sha256(input);
    }

    public static byte[] hash(byte[] input) {
        return DigestUtils.sha256(input);
    }

    /**
     * Returns the internal byte array, without defensively copying. Therefore do NOT modify the returned array.
     */
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Arrays.equals(bytes, ((Sha256Hash)o).bytes);
    }

    /**
     * Returns the last four bytes of the wrapped txId. This should be unique enough to be a suitable txId code even for
     * blocks, where the goal is to try and get the first bytes to be zeros (i.e. the value as a big integer lower
     * than the target value).
     */
    @Override
    public int hashCode() {
        return Ints.fromBytes(bytes[LENGTH - 4], bytes[LENGTH - 3], bytes[LENGTH - 2], bytes[LENGTH - 1]);
    }

    @Override
    public String toString() {
        return Hex.toHexString(bytes);
    }

    @Override
    public int compareTo(final Sha256Hash other) {
        for (int i = LENGTH - 1; i >= 0; i--) {
            final int thisByte = this.bytes[i] & 0xff;
            final int otherByte = other.bytes[i] & 0xff;
            if (thisByte > otherByte)
                return 1;
            if (thisByte < otherByte)
                return -1;
        }
        return 0;
    }
}
