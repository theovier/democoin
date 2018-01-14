package com.theovier.democoin.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void generateAddress() {
        String publicKey = "0450863AD64A87AE8A2FE83C1AF1A8403CB53F53E486D8511DAD8A04887E5B23522CD470243453A299FA9E77237716103ABC11A1DF38855ED6F2EE187E9C582BA6";
        Address address = Address.generateAddress(publicKey);

        Address expectation = new Address("16UwLL9Risc3QfPqBUvKofHmBQ7wMtjvM");
        assertTrue(expectation.equals(address));
    }
}