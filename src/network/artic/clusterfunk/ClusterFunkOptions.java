package network.artic.clusterfunk;

import org.apache.commons.cli.Option;

/**
 * @author Andrew Rambaut
 * @version $
 */
class ClusterFunkOptions {

    public static final String DEFAULT_DELIMITER = "|";

    enum Command {
        NONE("", ""),
        ANNOTATE("annotate", "Annotate tips and nodes from a metadata table."),
        ASSIGN("assign", "Clean and assign lineage annotations."),
        CLUSTER("cluster", "label clusters by number based on node attributes."),
        COLLAPSE("collapse", "collapse branch lengths below a threshold into polytomies"),
        EXTRACT("extract", "Extract tip annotations as a metadata csv."),
        SUBCLUSTER("subcluster", "split existing clusters into subclusters."),
        CONQUER("conquer", "Join up previously divided subtrees."),
        CONTEXT("context", "Extract trees of the neighbourhoods or contexts of a set of tips."),
        CONVERT("convert", "Convert tree from one format to another."),
        DIVIDE("divide", "Divide tree into approximately equal sized subtrees."),
        INSERT("insert", "Insert tips into the tree."),
        MERGE("merge", "Merge two metadata tables"),
        PRUNE("prune", "Prune out taxa from a list or based on metadata."),
        RECONSTRUCT("reconstruct", "Reconstruct internal node annotations."),
        REORDER("reorder", "Re-order nodes in ascending or descending clade size."),
        REROOT("reroot", "Re-root the tree using an outgroup."),
        SAMPLE("sample", "Sample taxa down using metadata attributes."),
        SCALE("scale", "Scale all the branch lengths in a tree by a factor."),
        SPLIT("split", "Split out subtrees based on tip annotations."),
        STATISTICS("statistics", "Extract statistics and information from trees."),
        TMRCA("tmrca", "Extract a TMRCA for a set of taxa from a list of trees.");

        Command(final String name, final String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Command getCommand(String name) {
            for (Command command : values()) {
                if (name.equalsIgnoreCase(command.getName())) {
                    return command;
                }
            }
            throw new IllegalArgumentException("Command not found");
        }

        private final String name;
        private final String description;
    }

    final static Option INPUT = Option.builder("i")
            .longOpt("input")
            .argName("file")
            .hasArg()
            .required(true)
            .desc("input tree file")
            .type(String.class).build();

    final static Option INPUT_PATH = Option.builder("i")
            .longOpt("input")
            .argName("path")
            .hasArg()
            .required(true)
            .desc("input path")
            .type(String.class).build();

    final static Option METADATA = Option.builder("m")
            .longOpt("metadata")
            .argName("file")
            .hasArg()
            .required(true)
            .desc("input metadata file")
            .type(String.class).build();

    final static Option LINEAGE_FILE = Option.builder()
            .longOpt("lineages")
            .argName("file")
            .hasArg()
            .required(true)
            .desc("input lineage csv file")
            .type(String.class).build();

    final static Option TAXON_FILE = Option.builder()
            .longOpt("taxon-file")
            .argName("file")
            .hasArg()
            .required(false)
            .desc("file of taxa (in a CSV table or tree)")
            .type(String.class).build();

    final static Option TAXA = Option.builder("t")
            .longOpt("taxa")
            .argName("taxon-ids")
            .hasArgs()
            .required(false)
            .desc("a list of taxon ids")
            .type(String.class).build();

    final static Option INDEX_COLUMN = Option.builder("c")
            .longOpt("id-column")
            .argName("column name")
            .hasArg()
            .required(false)
            .desc("metadata column to use to match tip labels (default first column)")
            .type(String.class).build();

    final static Option INDEX_FIELD = Option.builder("n")
            .longOpt("id-field")
            .argName("field number")
            .hasArg()
            .required(false)
            .desc("tip label field to use to match metadata (default = whole label)")
            .type(Integer.class).build();

    final static Option HEADER_DELIMITER = Option.builder()
            .longOpt("field-delimiter")
            .argName("delimiter")
            .hasArg()
            .required(false)
            .desc("the delimiter used to specify fields in the tip labels (default = '" + DEFAULT_DELIMITER + "')")
            .type(String.class).build();

    final static Option OUTPUT_FILE = Option.builder("o")
            .longOpt("output")
            .argName("file")
            .hasArg()
            .required(true)
            .desc("output file")
            .type(String.class).build();

    final static Option OUTPUT_PATH = Option.builder("o")
            .longOpt("output")
            .argName("path")
            .hasArg()
            .required(false)
            .desc("output path")
            .type(String.class).build();

    final static Option OUTPUT_PREFIX = Option.builder("p")
            .longOpt("prefix")
            .argName("file_prefix")
            .hasArg()
            .required(true)
            .desc("output file prefix")
            .type(String.class).build();

    final static Option OUTPUT_FORMAT = Option.builder("f")
            .longOpt("format")
            .argName("nexus|newick")
            .hasArg()
            .required(false)
            .desc("output file format (nexus or newick)")
            .type(String.class).build();

    final static Option OUTPUT_METADATA = Option.builder("d")
            .longOpt("output-metadata")
            .argName("file")
            .hasArg()
            .required(false)
            .desc("output a metadata file to match the output tree")
            .type(String.class).build();

    final static Option OUTPUT_TAXA = Option.builder()
            .longOpt("output-taxa")
            .required(false)
            .desc("output a text file of taxon names to match each output tree")
            .type(String.class).build();

    final static Option ATTRIBUTE = Option.builder("a")
            .longOpt("attribute")
            .argName("attribute_name")
            .hasArg()
            .required(true)
            .desc("the attribute name")
            .type(String.class).build();

    final static Option VALUE = Option.builder()
            .longOpt("value")
            .argName("attribute_value")
            .hasArg()
            .required(true)
            .desc("the attribute value")
            .type(String.class).build();

    final static Option ROOT_VALUE = Option.builder()
            .longOpt("root-value")
            .argName("attribute_value")
            .hasArg()
            .required(true)
            .desc("the attribute value at the root")
            .type(String.class).build();

    final static Option ALGORITHM = Option.builder()
            .longOpt("algorithm")
            .argName("deltran/acctran")
            .required(false)
            .numberOfArgs(1)
            .desc("the algorithm for reconstruction of node values")
            .type(String.class).build();

    final static Option CLUSTER_NAME = Option.builder()
            .longOpt("cluster-name")
            .argName("name")
            .hasArg()
            .required(true)
            .desc("the cluster name")
            .type(String.class).build();

    final static Option CLUSTER_PREFIX = Option.builder()
            .longOpt("cluster-prefix")
            .argName("prefix")
            .hasArg()
            .required(false)
            .desc("the cluster prefix (default = just a number)")
            .type(String.class).build();

    final static Option OUT_ATTRIBUTE = Option.builder()
            .longOpt("out-attribute")
            .argName("name")
            .hasArg()
            .required(false)
            .desc("the new attribute name in output")
            .type(String.class).build();

    final static Option LABEL_FIELDS = Option.builder("l")
            .longOpt("label-fields")
            .argName("columns")
            .hasArgs()
            .required(false)
            .desc("a list of metadata columns to add as tip label fields")
            .type(String.class).build();

    final static Option TIP_ATTRIBUTES = Option.builder()
            .longOpt("tip-attributes")
            .argName("columns")
            .hasArgs()
            .required(false)
            .desc("a list of metadata columns to add as tip attributes")
            .type(String.class).build();

    final static Option ADD_COLUMNS = Option.builder("a")
            .longOpt("add-columns")
            .argName("columns")
            .hasArgs()
            .required(false)
            .desc("a list of metadata columns to add")
            .type(String.class).build();

    final static Option MRCA = Option.builder()
            .longOpt("mrca")
            .required(false)
            .desc("include the entire clade from the MRCA of the target taxa")
            .type(String.class).build();

    final static Option MAX_PARENT_LEVEL = Option.builder()
            .longOpt("max-parent")
            .argName("level")
            .hasArg()
            .required(false)
            .desc("maximum parent level to include in context trees (default = 1)")
            .type(Integer.class).build();

    final static Option MAX_CHILD_LEVEL = Option.builder()
            .longOpt("max-child")
            .argName("level")
            .hasArg()
            .required(false)
            .desc("maximum level of children to include in subtrees (default = unlimited)")
            .type(Integer.class).build();

    final static Option MAX_SIBLING = Option.builder()
            .longOpt("max-siblings")
            .argName("level")
            .hasArg()
            .required(false)
            .desc("maximum number of siblings to include in subtrees (default = unlimited)")
            .type(Integer.class).build();

    final static Option COLLAPSE_BY = Option.builder()
            .longOpt("collapse-by")
            .argName("attribute_name")
            .hasArg()
            .required(false)
            .desc("an attribute to collapse homogenous subtrees by")
            .type(String.class).build();

    final static Option CLUMP_BY = Option.builder()
            .longOpt("clump-by")
            .argName("attribute_name")
            .hasArg()
            .required(false)
            .desc("an attribute to clump homogenous children by")
            .type(String.class).build();

    final static Option MIN_COLLAPSED_SIZE = Option.builder()
            .longOpt("min-collapsed")
            .argName("size")
            .hasArg()
            .required(false)
            .desc("minimum number of tips in a collapsed subtree")
            .type(Integer.class).build();

    final static Option MIN_CLUMPED_SIZE = Option.builder()
            .longOpt("min-clumped")
            .argName("size")
            .hasArg()
            .required(false)
            .desc("minimum number of tips in a clump")
            .type(Integer.class).build();

    final static Option MAX_SOFT = Option.builder()
            .longOpt("max-soft")
            .argName("size")
            .hasArg()
            .required(false)
            .desc("maximum number of tips in a soft collapsed node")
            .type(Integer.class).build();

    final static Option MIN_SUBTREE_SIZE = Option.builder()
            .longOpt("min-size")
            .argName("size")
            .hasArg()
            .required(false)
            .desc("minimum number of tips in a subtree")
            .type(Integer.class).build();

    final static Option MAX_SUBTREE_COUNT = Option.builder()
            .longOpt("max-count")
            .argName("count")
            .hasArg()
            .required(false)
            .desc("maximum number of subtrees")
            .type(Integer.class).build();

    final static Option MIDPOINT = Option.builder()
            .longOpt("midpoint")
            .required(false)
            .desc("midpoint root the tree")
            .type(String.class).build();

    final static Option OUTGROUPS = Option.builder()
            .longOpt("outgroups")
            .argName("tips")
            .hasArgs()
            .required(false)
            .desc("a list of tips to use as an outgroup for re-rooting")
            .type(String.class).build();

    final static Option ROOT_LOCATION = Option.builder()
            .longOpt("root-location")
            .argName("fraction")
            .required(false)
            .numberOfArgs(1)
            .desc("location on the root branch for the root as a fraction of the branch length from the ingroup")
            .type(Double.class).build();

    final static Option BRANCH_THRESHOLD = Option.builder("t")
            .longOpt("threshold")
            .argName("length")
            .required(true)
            .numberOfArgs(1)
            .desc("the threshold for branch lengths to be collapsed into polytomies")
            .type(Double.class).build();

    final static Option SCALE_FACTOR = Option.builder("s")
            .longOpt("factor")
            .argName("value")
            .required(true)
            .numberOfArgs(1)
            .desc("the factor to scale all branches by")
            .type(Double.class).build();

    final static Option ROOT_HEIGHT = Option.builder()
            .longOpt("height")
            .argName("value")
            .required(true)
            .numberOfArgs(1)
            .desc("the height of the root to scale to")
            .type(Double.class).build();

    final static Option INCREASING = Option.builder()
            .longOpt("increasing")
            .desc("order nodes by increasing clade size")
            .type(String.class).build();

    final static Option DECREASING = Option.builder()
            .longOpt("decreasing")
            .desc("order nodes by decreasing clade size")
            .type(String.class).build();

    final static Option SORT_COLUMNS = Option.builder()
            .longOpt("sort-by")
            .argName("columns")
            .hasArgs()
            .required(false)
            .desc("a list of metadata columns to sort by (prefix by ^ to reverse order)")
            .type(String.class).build();

    final static Option DESTINATION_COLUMN = Option.builder()
            .longOpt("destination-column")
            .argName("column name")
            .hasArg()
            .required(false)
            .desc("metadata column for destination to insert tips")
            .type(String.class).build();

    final static Option REPLACE = Option.builder("r")
            .longOpt("replace")
            .required(false)
            .desc("replace the annotations or tip label headers rather than appending (default false)")
            .type(String.class).build();

    final static Option OVERWRITE = Option.builder()
            .longOpt("overwrite")
            .required(false)
            .desc("overwrite existing values (default false)")
            .type(String.class).build();

    final static Option EXTRACT = Option.builder()
            .longOpt("extract")
            .required(false)
            .desc("extract only the matching rows (default false)")
            .type(String.class).build();

    final static Option STATISTICS = Option.builder()
            .longOpt("stats")
            .required(true)
            .desc("a list of statistics to include in the output (see docs for details)")
            .type(String.class).build();

    final static Option IGNORE_MISSING = Option.builder()
            .longOpt("ignore-missing")
            .required(false)
            .desc("ignore any missing matches in annotations table (default false)")
            .type(String.class).build();

    final static Option UNIQUE_ONLY = Option.builder()
            .longOpt("unique-only")
            .required(false)
            .desc("only place tips that have an unique position (default false)")
            .type(String.class).build();

    final static Option KEEP_TAXA = Option.builder("k")
            .longOpt("keep-taxa")
            .required(false)
            .desc("keep only the taxa specifed (default false)")
            .type(String.class).build();

    final static Option REQUIRE_OUTGROUP = Option.builder()
            .longOpt("require-outgroup")
            .required(false)
            .desc("only divide subtrees where the representative is an outgroup (default false)")
            .type(String.class).build();

    final static Option STEM = Option.builder()
            .longOpt("stem")
            .required(false)
            .desc("find the time of the stem above the MRCA (default false)")
            .type(String.class).build();

    final static Option MIN_CLUSTER_SIZE = Option.builder()
            .longOpt("min-size")
            .argName("size")
            .hasArg()
            .required(false)
            .desc("minimum number of tips in a subcluster (default = 10)")
            .type(Integer.class).build();

}

