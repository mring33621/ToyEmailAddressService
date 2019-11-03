package com.mattring.fetchrewards.emailaddr;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailAddressFunctionsTest {

    @ParameterizedTest
    @CsvSource({"test.email@gmail.com,test.email,gmail.com", "test.email+spam@gmail.com,test.email+spam,gmail.com", "testemail@gmail.com,testemail,gmail.com", "b.simmons@fetchrewards.com,b.simmons,fetchrewards.com", "sam.phelps@newcastleassoc.com,sam.phelps,newcastleassoc.com", "matthew_s_ring@yahoo.com,matthew_s_ring,yahoo.com"})
    void splitIntoLocalAndDomainParts(String rawAddr, String expectedLocalPart, String expectedDomainPart) {
        String[] parts = EmailAddressFunctions.splitIntoLocalAndDomainParts(rawAddr);
        assertEquals(2, parts.length);
        assertEquals(expectedLocalPart, parts[0]);
        assertEquals(expectedDomainPart, parts[1]);
    }

    @ParameterizedTest
    @CsvSource({"test.email,test.email", "test.email+spam,test.email", "testemail,testemail", "b.simmons,b.simmons", "sam.phelps,sam.phelps", "sam.phelps+something+else,sam.phelps"})
    void removeAllCharsAfterFirstPlusSign(String rawAddrLocalPart, String expectedProcessedAddrLocalPart) {
        Optional<String> maybeLocalPartPreceedingPlusSign = EmailAddressFunctions.removeAllCharsAfterFirstPlusSign(rawAddrLocalPart);
        assertTrue(maybeLocalPartPreceedingPlusSign.isPresent());
        assertEquals(expectedProcessedAddrLocalPart, maybeLocalPartPreceedingPlusSign.get());
    }

    @ParameterizedTest
    @CsvSource({"test.email,testemail", "test.email+spam,testemail+spam", "testemail,testemail", "b.simmons,bsimmons", "sam.phelps,samphelps", "sam.phelps+something+else,samphelps+something+else"})
    void removeAllPeriodChars(String rawAddrLocalPart, String expectedProcessedAddrLocalPart) {
        Optional<String> maybeLocalPartNoPeriodChars = EmailAddressFunctions.removeAllPeriodChars(rawAddrLocalPart);
        assertTrue(maybeLocalPartNoPeriodChars.isPresent());
        assertEquals(expectedProcessedAddrLocalPart, maybeLocalPartNoPeriodChars.get());
    }

    @ParameterizedTest
    @CsvSource({"test.email@gmail.com,testemail@gmail.com", "test.email+spam@gmail.com,testemail@gmail.com", "testemail@gmail.com,testemail@gmail.com", "b.simmons@fetchrewards.com,bsimmons@fetchrewards.com", "sam.phelps@newcastleassoc.com,samphelps@newcastleassoc.com", "matthew_s_ring@yahoo.com,matthew_s_ring@yahoo.com"})
    void normalizeEmailAddressPerGoogleAccountNameRules(String rawAddr, String normalizedAddr) {
        Optional<String> maybeNormalizedAddr = EmailAddressFunctions.normalizeEmailAddressPerGoogleAccountNameRules(rawAddr);
        assertTrue(maybeNormalizedAddr.isPresent());
        assertEquals(normalizedAddr, maybeNormalizedAddr.get());
    }

    @Test
    void parseAndCleanCSVList() {
        String csvList = "";
        List<String> parsedList = EmailAddressFunctions.parseAndCleanCSVList(csvList);
        assertTrue(parsedList.isEmpty());

        csvList = ";";
        parsedList = EmailAddressFunctions.parseAndCleanCSVList(csvList);
        assertTrue(parsedList.isEmpty());

        csvList = ",1,2;3,4;";
        parsedList = EmailAddressFunctions.parseAndCleanCSVList(csvList);
        assertEquals(Arrays.asList("1", "2", "3", "4"), parsedList);

        csvList = ",1 ,2; 3,4;,5 ";
        parsedList = EmailAddressFunctions.parseAndCleanCSVList(csvList);
        assertEquals(Arrays.asList("1", "2", "3", "4", "5"), parsedList);

        csvList = ",1 ,2\r\n; 3,4\n 5 \n";
        parsedList = EmailAddressFunctions.parseAndCleanCSVList(csvList);
        assertEquals(Arrays.asList("1", "2", "3", "4", "5"), parsedList);
    }

    @Test
    public void findUniqueNormalizedEmailAddresses_List() {
        List<String> addrs = Arrays.asList("test.email@gmail.com", "test.email+spam@gmail.com", "testemail@gmail.com");
        Set<String> uniqNormalizedAddrs = EmailAddressFunctions.findUniqueNormalizedEmailAddresses(addrs);
        assertEquals(1, uniqNormalizedAddrs.size());
        assertEquals("testemail@gmail.com", uniqNormalizedAddrs.iterator().next());
    }

    @Test
    public void findUniqueNormalizedEmailAddresses_CSV() {
        String addrs = "; test.email@gmail.com ,test.email+spam@gmail.com ;testemail@gmail.com , ";
        Set<String> uniqNormalizedAddrs = EmailAddressFunctions.findUniqueNormalizedEmailAddresses(addrs);
        assertEquals(1, uniqNormalizedAddrs.size());
        assertEquals("testemail@gmail.com", uniqNormalizedAddrs.iterator().next());
    }
}