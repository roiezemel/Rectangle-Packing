package com.example.chipfloorplanningoptimization.gui;

import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOManager {

    /*
        This class takes care of managing the input/output files
     */

    /**
     * Extract blocks (rectangles) from an input file.
     * @param file a file
     * @return map of block names to rectangles
     * @throws FileNotFoundException in case file hasn't been found
     */
    public static Map<String, Rectangle> extractBlocks(File file) throws FileNotFoundException {
        Map<String, Rectangle> map = new HashMap<>();
        Scanner myReader = new Scanner(file);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            if (data.contains("hardrectilinear")) {
                Rectangle rect = parseRect(data);
                String name = data.substring(0, data.indexOf(' '));
                map.put(name, rect);
            }
        }
        myReader.close();
        return map;
    }

    public static List<List<String>> parseNet(File file) throws FileNotFoundException {
        Scanner myReader = new Scanner(file);
        List<List<String>> result = new LinkedList<>();
        List<String> current = null;
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine().trim();
            if (data.contains("NetDegree")) {
                if (current != null && current.size() > 1)
                    result.add(current);
                current = new LinkedList<>();
            }
            else if (current != null) {
                String[] parts = data.split(" ");
                if (!parts[0].startsWith("p")) {
                    current.add(parts[0]);
                }
            }
        }
        if (current != null)
            result.add(current);
        return result;
    }

    private static Rectangle parseRect(String blockLine) {
        String regex = "\\(\\d+, \\d+\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(blockLine);

        int[][] vertices = new int[2][2];

        int count = 0;
        while(matcher.find()) {
            if (count % 2 == 0) {
                String pointS = matcher.group();
                int[] point = Arrays.stream(pointS.substring(1, pointS.indexOf(')'))
                        .replace(" ", "")
                        .split(",")).mapToInt(Integer::parseInt).toArray();
                vertices[count > 0 ? 1 : 0] = point;
            }
            count++;
        }

        int width = vertices[1][0] - vertices[0][0];
        int height = vertices[1][1] - vertices[0][1];
        return new Rectangle(vertices[0][0], vertices[0][1], width, height);
    }

}