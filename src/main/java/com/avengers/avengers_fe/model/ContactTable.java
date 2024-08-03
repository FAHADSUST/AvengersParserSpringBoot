package com.avengers.avengers_fe.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ContactTable {
    private String hqName;
    private String[] roleHeaders;
    private Map<String, String[]> speciesToRoles = new HashMap<>();

    public ContactTable(String hqName, String[] roleHeaders) {
        this.hqName = hqName;
        this.roleHeaders = roleHeaders;
    }

    public String getHqName() {
        return hqName;
    }

    public String[] getRoleHeaders() {
        return roleHeaders;
    }

    public Map<String, String[]> getSpeciesToRoles() {
        return speciesToRoles;
    }
}
