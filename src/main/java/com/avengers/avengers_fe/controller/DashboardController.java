package com.avengers.avengers_fe.controller;


import com.avengers.avengers_fe.model.ContactTable;
import com.avengers.avengers_fe.model.CountryHQ;
import com.avengers.avengers_fe.service.DataLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;


@Controller
public class DashboardController {

    private final DataLoaderService dataLoaderService;
    private List<CountryHQ> countryHQList;
    private Map<String, ContactTable> contacts;

    private List<Map<String, String>> lookupTable;
    private Map<String, List<String[]>> superheroTables;

    @Autowired
    public DashboardController(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
        try {

            this.countryHQList = dataLoaderService.loadCountryHQData("data/country_hq.txt");
            this.contacts = dataLoaderService.loadContacts("data/contacts");

            this.lookupTable = dataLoaderService.generateLookupTable(countryHQList, contacts);  // Precompute lookup
            this.superheroTables = dataLoaderService.generateSuperheroTables(this.contacts);    // Precompute superheroTables
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading data files", e);
        }
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("countryCount", countryHQList != null ? countryHQList.size() : 0);
        model.addAttribute("contactsCount", contacts != null ? contacts.size() : 0);
        return "index";
    }

    @GetMapping("/lookup")
    public String lookup(@RequestParam(required = false) String country,
                         @RequestParam(required = false) String species,
                         @RequestParam(required = false) String role,
                         Model model) {
        List<Map<String, String>> results = new ArrayList<>(lookupTable);
        if (country != null && !country.isEmpty()) {
            results.removeIf(row -> !row.get("countryCode").equalsIgnoreCase(country));
        }
        if (species != null && !species.isEmpty()) {
            results.removeIf(row -> !row.get("species").equalsIgnoreCase(species));
        }
        if (role != null && !role.isEmpty()) {
            results.removeIf(row -> !row.get("role").equalsIgnoreCase(role));
        }
        model.addAttribute("results", results);
        return "lookup";
    }

    @GetMapping("/superhero")
    public String superhero(@RequestParam(required = false) String hero, Model model) {
        model.addAttribute("heroes", superheroTables.keySet());
        if (hero != null && !hero.trim().isEmpty()) {
            List<String[]> table = superheroTables.get(hero.trim());
            model.addAttribute("selectedHero", hero.trim());
            model.addAttribute("table", table);
        }
        return "superhero";
    }
}