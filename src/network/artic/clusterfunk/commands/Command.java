package network.artic.clusterfunk.commands;

import jebl.evolution.graphs.Node;
import jebl.evolution.io.*;
import jebl.evolution.taxa.Taxon;
import jebl.evolution.trees.RootedTree;
import network.artic.clusterfunk.FormatType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.*;

/**
 * Base class for clusterfunk commands. Provides some static utility functions.
 */
abstract class Command {
    final boolean isVerbose;
    final static PrintStream errorStream = System.err;
    final static PrintStream outStream = System.out;

    final String indexColumn;
    final int indexHeader;
    final String headerDelimiter;

    Map<String, CSVRecord> metadata = null;
    CSVRecord firstRecord = null;
    Set<String> taxa = null;

    /**
     * Simple constructor
     * @param isVerbose
     */
    Command(String metadataFileName, String taxaFileName, String indexColumn, int indexHeader, String headerDelimiter, boolean isVerbose) {
        this.indexColumn = indexColumn;
        this.indexHeader = indexHeader;
        this.headerDelimiter = headerDelimiter;
        this.isVerbose = isVerbose;

        if (metadataFileName != null) {
            readMetadataTable(metadataFileName, indexColumn);
        }

        if (taxaFileName != null) {
            readTaxa(taxaFileName, indexColumn);
        }
    }

    private void readMetadataTable(String metadataFileName, String indexColumn) {
        metadata = readCSV(metadataFileName, indexColumn);
        taxa = metadata.keySet();

        firstRecord =  metadata.get(taxa.iterator().next());

        if (isVerbose) {
            outStream.println("Read metadata table: " + metadataFileName);
            outStream.println("               Rows: " + metadata.size());
            outStream.println("       Index column: " + (indexColumn == null ? firstRecord.getParser().getHeaderNames().get(0) : indexColumn));
            outStream.println();
        }
    }

    private void readTaxa(String taxaFileName, String indexColumn) {

        try {
            TreeImporter importer = null;

            FormatType format = getTreeFileType(new FileReader(taxaFileName));

            if (format == FormatType.NEXUS) {
                importer = new NexusImporter(new FileReader(taxaFileName));
            } else if (format == FormatType.NEWICK) {
                importer = new NewickImporter(new FileReader(taxaFileName), false);
            } else {
                // not a tree file - do nothing...
            }

            if (importer != null) {
                RootedTree tree = (RootedTree) importer.importNextTree();
                taxa = new HashSet<>(getTaxonMap(tree).values());
            } else {
                taxa = readCSV(taxaFileName, indexColumn).keySet();
            }

        } catch (IOException ioe) {
            errorStream.println("Error reading taxon file, " + taxaFileName + ": " + ioe.getMessage());
            System.exit(1);
        } catch (ImportException ie) {
            errorStream.println("Error parsing taxon file, " + taxaFileName + ": " + ie.getMessage());
            System.exit(1);
        }


        if (isVerbose) {
            outStream.println("Read taxa list: " + taxaFileName);
            outStream.println("        Number: " + taxa.size());
            outStream.println();
        }
    }

    final RootedTree readTree(String treeFileName) {
        return readTrees(treeFileName).get(0);
    }

    final List<RootedTree> readTrees(String treeFileName) {
        List<RootedTree> trees = new ArrayList<>();

        try {
            TreeImporter importer = null;

            FormatType format = getTreeFileType(new FileReader(treeFileName));

            if (format == FormatType.NEXUS) {
                importer = new NexusImporter(new FileReader(treeFileName));
            } else if (format == FormatType.NEWICK) {
                importer = new NewickImporter(new FileReader(treeFileName), false);
            } else {
                errorStream.println("Unrecognised tree format in file, " + treeFileName);
                System.exit(1);
            }

            while (importer.hasTree()) {
                trees.add((RootedTree) importer.importNextTree());
            }

        } catch (IOException ioe) {
            errorStream.println("Error reading tree file, " + treeFileName + ": " + ioe.getMessage());
            System.exit(1);
        } catch (ImportException ie) {
            errorStream.println("Error parsing tree file, " + treeFileName + ": " + ie.getMessage());
            System.exit(1);
        }

        if (isVerbose) {
            outStream.println("  Read treefile: " + treeFileName);
            if (trees.size() > 1) {
                outStream.println("Number of trees: " + trees.size());
            }
            outStream.println(" Number of tips: " + trees.get(0).getExternalNodes().size());
            outStream.println();
        }

        return trees;
    }

    private FormatType getTreeFileType(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        while (line != null && line.length() == 0) {
            line = bufferedReader.readLine();
        }

        if (line != null && line.trim().toUpperCase().startsWith("#NEXUS")) {
            return FormatType.NEXUS;
        }
        if (line != null && line.trim().toUpperCase().startsWith("(")) {
            return FormatType.NEWICK;
        }

        return null;
    }

    final Map<Taxon, String> getTaxonMap(RootedTree tree) {
        Map<Taxon, String> taxonMap = new HashMap<>();

        for (Node tip : tree.getExternalNodes()) {
            Taxon taxon = tree.getTaxon(tip);
            String index = taxon.getName();
            if (indexHeader > 0) { // index header indexed from 1
                // if an index header field has been specified then split it out (otherwise use the entire name)
                String[] headers = taxon.getName().split(headerDelimiter);
                if (indexHeader > headers.length) {
                    errorStream.println("Tip name, " + taxon.getName() + ", doesn't have enough fields (index-header = " + indexHeader + ")");
                    System.exit(1);
                }
                index = headers[indexHeader - 1];
            }
            taxonMap.put(taxon, index);
        }

        return taxonMap;
    }

    static void clearExternalAttributes(RootedTree tree) {
        for (Node node : tree.getExternalNodes()) {
            Set<String> attributeNames = new HashSet<>(node.getAttributeNames());
            for (String attributeName : attributeNames) {
                node.removeAttribute(attributeName);
            }
        }
    }
    static void clearInternalAttributes(RootedTree tree) {
        for (Node node : tree.getInternalNodes()) {
            Set<String> attributeNames = new HashSet<>(node.getAttributeNames());
            for (String attributeName : attributeNames) {
                node.removeAttribute(attributeName);
            }
        }
    }

    /**
     * Takes a set of objects and creates a set of strings using the toString() method
     * @param objectSet
     * @return
     */
    static Set<String> toString(Set<Object> objectSet)
    {
        Set<String> strings = new TreeSet<>();
        for (Object o : objectSet) {
            if (o != null) {
                strings.add(o.toString());
            }
        }
        return strings;
    }

    private static Map<String, CSVRecord> readCSV(String fileName, String indexColumn) {
        Map<String, CSVRecord> csv = new HashMap<>();
        try {
            Reader in = new FileReader(fileName);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            if (indexColumn != null) {
                // a particular column is used to index - check it is there for the first record
                // and use it to key the records

                boolean first = true;
                for (CSVRecord record : records) {
                    if (first) {
                        if (record.get(indexColumn) == null) {
                            errorStream.println("Index column, " + indexColumn + " not found in metadata table");
                            System.exit(1);
                        }
                        first = false;
                    }
                    csv.put(record.get(indexColumn), record);
                }
            } else {
                // key the records against the first column
                for (CSVRecord record : records) {
                    csv.put(record.get(0), record);
                }
            }
        } catch (IOException e) {
            errorStream.println("Error reading metadata file: " + e.getMessage());
            System.exit(1);
        }
        return csv;
    }

    /**
     * Writes a tree
     * @param tree
     * @param fileName
     */
    static void writeTreeFile(RootedTree tree, String fileName, FormatType format) {
        writeTreeFile(Collections.singletonList(tree), fileName, format);
    }

    /**
     * Writes a tree file with a list of trees
     * @param trees
     * @param fileName
     */
    static void writeTreeFile(List<RootedTree> trees, String fileName, FormatType format) {
        try {
            FileWriter writer = new FileWriter(fileName);

            TreeExporter exporter;

            switch (format) {
                case NEXUS:
                    exporter = new NexusExporter(writer);
                    break;
                case NEWICK:
                    exporter = new NewickExporter(writer);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown format: " + format);
            }

            exporter.exportTrees(trees);
            writer.close();
        } catch (IOException e) {
            errorStream.println("Error writing tree file: " + e.getMessage());
            System.exit(1);
        }
    }
}
