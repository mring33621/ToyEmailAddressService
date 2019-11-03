package com.mattring.fetchrewards.emailaddr;

import java.util.*;
import java.util.stream.Collectors;

public class EmailAddressFunctions {

    static boolean isNotBlank(String s) {
        return s != null && s.length() > 0;
    }

    static String[] splitIntoLocalAndDomainParts(String emailAddress) {
        String localPart = null;
        String domainPart = null;
        if (isNotBlank(emailAddress)) {
            final int lastAtSign = emailAddress.lastIndexOf('@');
            if (lastAtSign > 0) {
                localPart = emailAddress.substring(0, lastAtSign);
                // TODO: special handling if addr ends with '@'?
                domainPart = emailAddress.substring(lastAtSign + 1);
            }
        }
        return new String[]{localPart, domainPart};
    }

    static Optional<String> removeAllCharsAfterFirstPlusSign(String emailAddressLocalPart) {
        String revisedEmailAddressLocalPart = null;
        if (isNotBlank(emailAddressLocalPart)) {
            final int firstPlusSign = emailAddressLocalPart.indexOf('+');
            if (firstPlusSign > 0) {
                revisedEmailAddressLocalPart = emailAddressLocalPart.substring(0, firstPlusSign);
            } else {
                // TODO: special handling if addr starts with '+'?
                revisedEmailAddressLocalPart = emailAddressLocalPart;
            }
        }
        return Optional.ofNullable(revisedEmailAddressLocalPart);
    }

    static Optional<String> removeAllPeriodChars(String emailAddressLocalPart) {
        String emailAddressLocalPartNoPeriods = null;
        if (isNotBlank(emailAddressLocalPart)) {
            emailAddressLocalPartNoPeriods = emailAddressLocalPart.replace(".", "");
        }
        return Optional.ofNullable(emailAddressLocalPartNoPeriods);
    }

    static Optional<String> normalizeEmailAddressPerGoogleAccountNameRules(String emailAddress) {
        final String[] normalizedEmailAddressHolder = new String[1];
        final String[] localAndDomainParts = splitIntoLocalAndDomainParts(emailAddress);
        final String rawLocalPart = localAndDomainParts[0];
        final String rawDomainPart = localAndDomainParts[1];
        if (isNotBlank(rawLocalPart) && isNotBlank(rawDomainPart)) {
            removeAllPeriodChars(rawLocalPart)
                    .ifPresent(lpNoPeriods -> removeAllCharsAfterFirstPlusSign(lpNoPeriods)
                            .ifPresent(lpNoPlusSuffix -> normalizedEmailAddressHolder[0] = lpNoPlusSuffix + '@' + rawDomainPart));
        }
        return Optional.ofNullable(normalizedEmailAddressHolder[0]);
    }

    /**
     * Splits a delimited string into items, ignoring null/blank/whitespace only items.
     * Supported delimiters are ',', ';' or any combo of line endings
     * @param csvList
     * @return an actual list of non-blank strings
     */
    static List<String> parseAndCleanCSVList(String csvList) {
        List<String> items;
        if (isNotBlank(csvList)) {
            String[] rawItems = csvList.split(",|;|[\r\n]");
            items = Arrays.stream(rawItems).map(String::trim).filter(s -> isNotBlank(s)).collect(Collectors.toList());
        } else {
            items = Collections.emptyList();
        }
        return items;
    }

    static Set<String> findUniqueNormalizedEmailAddresses(List<String> rawEmailAddresses) {
        return rawEmailAddresses.stream()
                .map(addr -> normalizeEmailAddressPerGoogleAccountNameRules(addr))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public static Set<String> findUniqueNormalizedEmailAddresses(String csvEmailAddressList) {
        return findUniqueNormalizedEmailAddresses(parseAndCleanCSVList(csvEmailAddressList));
    }

}
