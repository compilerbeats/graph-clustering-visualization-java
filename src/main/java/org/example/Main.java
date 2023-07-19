package org.example;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> file = new ArrayList<>();
        String line;

        // Read METIS output file
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/ig_communities_output.metis"))) {
            while ((line = br.readLine()) != null) {
                file.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        file.sort(Comparator.comparing(community -> community.replace("\n", "").split(" ").length));
        Collections.reverse(file);

        System.out.println("Read input file, now onto finding the biggest overlaps...");

        // Create 3-tuple for every community
        Map<Integer, List<Object>> communities = new HashMap<>();
        for (int k = 0; k < file.size(); k++) {
            String[] members = file.get(k).split(" ");
            int memberCount = members.length;
            Set<String> memberSet = new HashSet<>(Arrays.asList(members));
            communities.put(k, Arrays.asList(memberCount, 0, memberSet));
        }

        // Find overlaps in communities
        int numberOfOverlaps;
        for (int i = 0; i < communities.size(); i++) {
            for (int j = i + 1; j < communities.size(); j++) {
                // check if currently checked community already has a bigger overlap
                // then the community to be compared has members
                if ((int) communities.get(i).get(1) > (int) communities.get(j).get(0)) {
                    continue;
                }
                if (i != j) {
                    Set<String> intersection = new HashSet<>((Set<String>) communities.get(i).get(2));
                    intersection.retainAll((Set<String>) communities.get(j).get(2));
                    numberOfOverlaps = intersection.size();

                    if (numberOfOverlaps > (int) communities.get(i).get(1)) {
                        communities.get(i).set(1, numberOfOverlaps);
                    }

                    if (numberOfOverlaps > (int) communities.get(j).get(1)) {
                        communities.get(j).set(1, numberOfOverlaps);
                    }
                }
            }
            if ((i + 1) % 100 == 0) {
                System.out.println(new Timestamp(System.currentTimeMillis()).toString()
                        + ": Checked another 100 communities");
            }
        }

        System.out.println("Community 1 has " + communities.get(0).get(1) + " overlaps at max");
    }
}
