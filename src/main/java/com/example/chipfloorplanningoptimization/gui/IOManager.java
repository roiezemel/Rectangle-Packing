package com.example.chipfloorplanningoptimization.gui;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.representation.Floorplan;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOManager {

    /*
        This class takes care of managing the input/output files
     */

    public static Floorplan extractBlocksToFloorplan(File blocksFile, File netFile) throws FileNotFoundException {
        if (blocksFile == null)
            return null;

        Floorplan floorplan = new Floorplan();
        Scanner myReader = new Scanner(blocksFile);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            if (data.contains("hardrectilinear")) {
                Rectangle rect = parseRect(data);
                String name = data.substring(0, data.indexOf(' '));
                floorplan.addModule(new CModule(rect.getWidth(), rect.getHeight(), name));
            }
        }
        myReader.close();
        if (netFile != null)
            floorplan.setNet(parseNet(netFile));
        return floorplan;
    }

    /**
     * Extract blocks (rectangles) from an input file.
     * @param file a file
     * @return map of block names to rectangles
     * @throws FileNotFoundException in case file hasn't been found
     */
    @Deprecated
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

    private static List<List<String>> parseNet(File file) throws FileNotFoundException {
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

    public static <T> void saveList(String path, List<? extends T> list, Function<? super T, String> oneLineSerialize) throws IOException {
        FileWriter writer = new FileWriter(path);
        StringBuilder text = new StringBuilder();
        for (T item : list) {
            String append = oneLineSerialize.apply(item);
            if (append.contains("\n"))
                throw new IllegalArgumentException("oneLineSerialize should produce Strings " +
                        "without newlines since newlines are used as separators!");
            text.append(append).append("\n");
        }
        writer.write(text.toString());
        writer.close();
    }

    public static <T> List<T> loadList(String path, Function<String, ? extends T> deserialize) throws FileNotFoundException {
        Scanner myReader = new Scanner(new File(path));
        List<T> list = new LinkedList<>();
        while (myReader.hasNextLine()) {
            list.add(deserialize.apply(myReader.nextLine()));
        }
        return list;
    }

}
