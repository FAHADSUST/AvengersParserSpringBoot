package com.avengers.avengers_fe.model;

import java.util.HashMap;
import java.util.Map;

public class CountryHQ {
    private String countryName;
    private String countryCode;
    private Map<String, String> invaderGroupToHQ = new HashMap<>();

    public CountryHQ(String countryName, String countryCode) {
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Map<String, String> getInvaderGroupToHQ() {
        return invaderGroupToHQ;
    }
}
