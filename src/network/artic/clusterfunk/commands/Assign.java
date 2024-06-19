package network.artic.clusterfunk.commands;

import jebl.evolution.graphs.Node;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.RootedTree;
import network.artic.clusterfunk.FormatType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Assign lineages to branches
 * @deprecated
 */
public class Assign extends Command {
    public Assign(String treeFileName,
                  String metadataFileName,
                  String outputFileName,
                  FormatType outputFormat,
                  String outputMetadataFileName,
                  String indexColumn,
                  int indexHeader,
                  String headerDelimiter,
                  String lineageName,
                  String outputLineageName,
                  boolean cleanSublineages,
                  boolean ignoreMissing,
                  boolean isVerbose) {

        super(metadataFileName, null, indexColumn, indexHeader, headerDelimiter, isVerbose);

        RootedTree tree = readTree(treeFileName);

        Map<Taxon, String> taxonMap = getTaxonMap(tree);

        annotateTips(tree, taxonMap, lineageName, ignoreMissing);

        assignNodeLineages(tree, tree.getRootNode(), lineageName);

        assignLineages(tree, tree.getRootNode(), lineageName, null, outputLineageName);
        
        writeTreeFile(tree, outputFileName, outputFormat);

        if (outputMetadataFileName != null) {
            try {
                PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(outputMetadataFileName)));

                writer.println("sequence_name," + outputLineageName);

                for (Node node : tree.getExternalNodes()) {
                    writer.print(tree.getTaxon(node).getName());
                    writer.print(",");
                    writer.print(node.getAttribute(outputLineageName));
                    writer.println();
                }

                writer.close();
            } catch (IOException e) {
                errorStream.println("Error writing metadata file: " + e.getMessage());
                System.exit(1);
            }
        }


    }

    private Map<String, Integer> assignNodeLineages(RootedTree tree, Node node, String lineageName) {
        if (tree.isExternal(node)) {
            Object value = node.getAttribute(lineageName);
            if (value != null) {
                return Collections.singletonMap((String) value, 1);
            } else {
                return Collections.singletonMap("", 1);
            }
        }

        Map<String, Integer> contentsMap = new HashMap<>();
        for (Node child : tree.getChildren(node)) {
            Map<String, Integer> contents = assignNodeLineages(tree, child, lineageName);
            for (String key: contents.keySet()) {
                contentsMap.put(key, contentsMap.getOrDefault(key, 0) + contents.get(key));
            }
        }

        Map<String, Integer> sortedMap =
                contentsMap.entrySet().stream()
                        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        String lineage = "";
        for (String key: sortedMap.keySet()) {
            if (lineage.equals("")) {
                lineage = key;
            }
        }

        node.setAttribute(lineageName, lineage);

        return contentsMap;
    }

    private void assignLineages(RootedTree tree, Node node, String lineageName, String parentLineage, String newAttributeName) {

        if (!tree.isExternal(node)) {
            String lineage = (String)node.getAttribute(lineageName);

            if (lineage != null && lineage != parentLineage && (parentLineage == null || isSublineage(lineage, parentLineage))) {
                propagateAttribute(tree, node, null, null, newAttributeName, lineage);
            }

            for (Node child : tree.getChildren(node)) {
                assignLineages(tree, child, lineageName, lineage, newAttributeName);
            }
        }

    }


    /**
     * Is lineage 1 a sub-lineage of lineage 2 in the dot notation
     * @param lineage1
     * @param lineage2
     * @return
     */
    private boolean isSublineage(String lineage1, String lineage2) {
        String[] split1 = lineage1.split("\\.");
        String[] split2 = lineage2.split("\\.");

        if (split1.length < split2.length) {
            // lineage1 is actually shorter than lineage2
            return false;
        }

        for (int i = 0; i < split2.length; i++) {
            if (!split1[i].equals(split2[i])) {
                return false;
            }
        }
        return true;
    }

    private String getParentLineage(String lineage) {
        int index = lineage.lastIndexOf(".");

        if (index == -1) {
            return null;
        }

        return lineage.substring(0, index);
    }

}

