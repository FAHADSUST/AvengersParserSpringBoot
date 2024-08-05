package com.avengers.avengers_fe.service;

import com.avengers.avengers_fe.model.ContactTable;
import com.avengers.avengers_fe.model.CountryHQ;
import com.avengers.avengers_fe.utils.Utils;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;



@Service
public class DataLoaderService {

    // Loads and parses country_hq.txt
    public List<CountryHQ> loadCountryHQData(String filePath) throws IOException {
        List<CountryHQ> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String header = br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length < 5) continue;

                CountryHQ country = new CountryHQ(parts[0].trim(), parts[1].trim());
                country.getInvaderGroupToHQ().put("Aliens", parts[2].trim());
                country.getInvaderGroupToHQ().put("Predators", parts[3].trim());
                country.getInvaderGroupToHQ().put("D&D Monsters", parts[4].trim());
                list.add(country);
            }
        }
        return list;
    }

    // Loads all contact files
    public Map<String, ContactTable> loadContacts(String contactsDirPath) throws IOException {
        Map<String, ContactTable> contacts = new HashMap<>();
        Path dirPath = Paths.get(contactsDirPath);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.txt")) {
            for (Path entry : stream) {
                ContactTable ct = readContactTable(entry.toString());
                if (ct != null) {
                    contacts.put(ct.getHqName(), ct);
                }
            }
        }
        return contacts;
    }

    private ContactTable readContactTable(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String headerLine = br.readLine();
            if (headerLine == null) return null;

            String[] headerParts = headerLine.split("\t");
            if (headerParts.length < 2) return null;

            String hqName = headerParts[0].trim();
            String[] roleHeaders = new String[headerParts.length - 1];
            for (int i = 1; i < headerParts.length; i++) {
                roleHeaders[i - 1] = headerParts[i].trim();
            }

            ContactTable ct = new ContactTable(hqName, roleHeaders);
            String line;
            while ((line = br.readLine()) != null) {

                String[] parts = line.split("\t", -1);
                if (parts.length < 1) continue;

                String species = parts[0].trim().toLowerCase();
                String[] heroes = new String[roleHeaders.length];
                int limit = Math.min(parts.length - 1, roleHeaders.length);
                for (int i = 0; i < limit; i++) {
                    heroes[i] = parts[i + 1].trim();
                }
                ct.getSpeciesToRoles().put(species, heroes);
            }
            return ct;
        }
    }

    // Precomputes the lookup table
    public List<Map<String, String>> generateLookupTable(List<CountryHQ> countryHQList, Map<String, ContactTable> contacts) {
        List<Map<String, String>> lookupResults = new ArrayList<>();
        for (CountryHQ chq : countryHQList) {
            for (String group : Arrays.asList("Aliens", "Predators", "D&D Monsters")) {
                String hq = chq.getInvaderGroupToHQ().get(group);
                if (hq != null && !hq.isEmpty() && contacts.containsKey(hq)) {
                    ContactTable ct = contacts.get(hq);
                    if ("D&D Monsters".equals(group)) {
                        for (Map.Entry<String, String[]> entry : ct.getSpeciesToRoles().entrySet()) {
                            String sp = entry.getKey();
                            if (!sp.startsWith("d&d_")) continue;

                            String[] heroes = entry.getValue();
                            for (int i = 0; i < heroes.length; i++) {
                                String heroName = heroes[i];
                                if (heroName != null && !heroName.isEmpty()) {
                                    Map<String, String> row = new HashMap<>();
                                    row.put("countryCode", chq.getCountryCode());
                                    row.put("species", sp);
                                    row.put("role", ct.getRoleHeaders()[i]);
                                    row.put("email", Utils.generateEmail(heroName));
                                    lookupResults.add(row);
                                }
                            }
                        }
                    } else {
                        String sp = group.toLowerCase();
                        String[] heroes = ct.getSpeciesToRoles().get(sp);
                        if (heroes != null) {
                            for (int i = 0; i < heroes.length; i++) {
                                String heroName = heroes[i];
                                if (heroName != null && !heroName.isEmpty()) {
                                    Map<String, String> row = new HashMap<>();
                                    row.put("countryCode", chq.getCountryCode());
                                    row.put("species", sp);
                                    row.put("role", ct.getRoleHeaders()[i]);
                                    row.put("email", Utils.generateEmail(heroName));
                                    lookupResults.add(row);
                                }
                            }
                        }
                    }
                }
            }
        }
        return lookupResults;
    }

    // Generates superhero tables
    public Map<String, List<String[]>> generateSuperheroTables(Map<String, ContactTable> contacts) {
        final List<String> fixedCols = Arrays.asList("aliens", "predators", "d&d_beholder", "d&d_devil", "d&d_lich",
                "d&d_mind_flayer", "d&d_vampire", "d&d_red_dragon", "d&d_hill_giant", "d&d_treant",
                "d&d_werewolf", "d&d_yuan-ti");

        Map<String, Map<String, Map<String, BitSet>>> heroMap = new HashMap<>();
        Map<String, Integer> roleIndex = new HashMap<>();
        roleIndex.put("attack_role", 0);
        roleIndex.put("defense_role", 1);
        roleIndex.put("healing_role", 2);

        for (ContactTable ct : contacts.values()) {
            String hq = ct.getHqName();
            for (Map.Entry<String, String[]> entry : ct.getSpeciesToRoles().entrySet()) {
                String species = entry.getKey();
                String[] heroArray = entry.getValue();
                for (int j = 0; j < heroArray.length; j++) {
                    String h = heroArray[j];
                    if (h == null || h.trim().isEmpty()) continue;

                    String roleHeader = ct.getRoleHeaders()[j];
                    Integer idx = roleIndex.get(roleHeader);
                    if (idx == null) continue;

                    heroMap.computeIfAbsent(h.trim(), k -> new HashMap<>())
                            .computeIfAbsent(hq, k -> new HashMap<>())
                            .computeIfAbsent(species, k -> new BitSet(3))
                            .set(idx);
                }
            }
        }

        Map<String, List<String[]>> tables = new HashMap<>();
        for (Map.Entry<String, Map<String, Map<String, BitSet>>> heroEntry : heroMap.entrySet()) {
            String hero = heroEntry.getKey();
            List<String[]> table = new ArrayList<>();

            List<String> header = new ArrayList<>();
            header.add("HQ");
            header.addAll(fixedCols);
            table.add(header.toArray(new String[0]));

            for (Map.Entry<String, Map<String, BitSet>> hqEntry : heroEntry.getValue().entrySet()) {
                String hq = hqEntry.getKey();
                Map<String, BitSet> speciesMap = hqEntry.getValue();
                List<String> row = new ArrayList<>();
                row.add(hq);
                for (String col : fixedCols) {
                    BitSet bs = speciesMap.get(col);
                    if (bs == null || bs.isEmpty()) {
                        row.add("");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        if (bs.get(0)) sb.append("A");
                        if (bs.get(1)) sb.append("D");
                        if (bs.get(2)) sb.append("H");
                        row.add(sb.toString());
                    }
                }
                table.add(row.toArray(new String[0]));
            }
            tables.put(hero, table);
        }
        return tables;
    }
}