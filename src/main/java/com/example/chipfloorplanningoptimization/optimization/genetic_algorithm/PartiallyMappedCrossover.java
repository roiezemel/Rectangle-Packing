package com.example.chipfloorplanningoptimization.optimization.genetic_algorithm;

import com.example.chipfloorplanningoptimization.abstract_structures.CModule;
import com.example.chipfloorplanningoptimization.representation.BTree;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PartiallyMappedCrossover implements Crossover{

    // PMX - partially-mapped crossover

    @Override
    public BTree[] crossover(BTree[] parents) {
        BTree p1 = parents[0], p2 = parents[1];
        List<NodeData> p1Data = Arrays.stream(p1
                        .serialize()
                        .replace("|", "|,")
                        .split(","))
                .map(NodeData::new)
                .collect(Collectors.toList());
        List<NodeData> p2Data = Arrays.stream(p2
                        .serialize()
                        .replace("|", "|,")
                        .split(","))
                .map(NodeData::new)
                .collect(Collectors.toList());

        int firstCut = ThreadLocalRandom.current().nextInt(0, p1Data.size() - 1);
        int secondCut = ThreadLocalRandom.current().nextInt(firstCut + 1, p1Data.size());

        List<NodeData> sub1 = new ArrayList<>(p1Data.subList(firstCut, secondCut + 1));
        List<NodeData> sub2 = new ArrayList<>(p2Data.subList(firstCut, secondCut + 1));

        p1Data.removeAll(sub1);
        p2Data.removeAll(sub2);
        p1Data.addAll(sub2.stream().map(NodeData::clone).collect(Collectors.toList()));
        p2Data.addAll(sub1.stream().map(NodeData::clone).collect(Collectors.toList()));

        for (int i = 0; i < sub1.size(); i++) {
            int index = i;
            p1Data.stream()
                    .filter(nodeData -> nodeData.name.equals(sub2.get(index).name))
                    .findFirst()
                    .get().name = sub1.get(i).name;
            p2Data.stream()

                    .filter(nodeData -> nodeData.name.equals(sub1.get(index).name))
                    .findFirst()
                    .get().name = sub2.get(i).name;
        }

        for (int i = 0; i < sub1.size(); i++) {
            p1Data.add(i + firstCut, p1Data.remove(i + p1Data.size() - secondCut + firstCut - 1));
            p2Data.add(i + firstCut, p2Data.remove(i + p2Data.size() - secondCut + firstCut - 1));
        }

        String child1Data = p1Data.stream().map(NodeData::toString).collect(Collectors.joining(",")).replace("|,", "|");
        String child2Data = p2Data.stream().map(NodeData::toString).collect(Collectors.joining(",")).replace("|,", "|");

        HashMap<String, CModule> modulesByNamesP1 = p1.createNameModuleMap();

        HashMap<String, CModule> modulesByNamesC1 = p1.createNameModuleMap();
        HashMap<String, CModule> modulesByNamesC2 = p2.createNameModuleMap();

        sub2.forEach(nodeData -> modulesByNamesC1.replace(nodeData.name, modulesByNamesC2.get(nodeData.name)));
        sub1.forEach(nodeData -> modulesByNamesC2.replace(nodeData.name, modulesByNamesP1.get(nodeData.name)));

        return new BTree[] {BTree.deserialize(child1Data, modulesByNamesC1), BTree.deserialize(child2Data, modulesByNamesC2)};
    }

    private static class NodeData implements Cloneable {

        public String name;
        public String postfix;

        public NodeData() {}

        public NodeData(String data) {
            this.name = data.replace("|", "");
            this.postfix = data.endsWith("|") ? "|" : "";
        }

        public String toString() {
            return name + postfix;
        }

        @Override
        public NodeData clone() {
            try {
                return (NodeData) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }

    }

}
