package vu.wntools.wnsimilarity.main;

import vu.wntools.wnsimilarity.WordnetSimilarityApi;
import vu.wntools.wnsimilarity.corpus.SubsumersFrequencies;
import vu.wntools.wnsimilarity.measures.*;
import vu.wntools.wordnet.CdbSynSaxParser;
import vu.wntools.wordnet.PwnSaxParser;
import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Piek Vossen
 * Date: 4/15/12
 * Time: 9:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class Similarity {
    static final String version = "1.0";
    static String separator = "\t";

    static final String usage = "\n" +
            "   Choose one of the 3 options to load a wordnet file\n" +
            "   --gwg-file      <path to wordnet file in global wordnet grid format\n" +
            "   --lmf-file      <path to wordnet file in lmf format\n" +
            "   --cdb-file      <path to wordnet file in Cornetto export synset format\n" +
            "   --pos           <optional part-of-speech filter, values: n, v, a\n" +
            "   --relations     <optional file with relations used for the hiearchy\n"+
            "   --input         <file with pairs to be compared on single lines separate with \"/\">\n"+
            "   --pairs         <indicate the type of input values: \"words\" or \"synsets\" or \"word-synsets\">\n" +
            "   --method        <leacock-chodorow, resnik, path, wu-palmer, jiang-conrath, lin or all>\n"+
            "   --depth         <optional: a fixed value for average depth can be given, >\n"+
            "   --subsumers     <path to a file with subsumer frequencies, required for resnik, lin, jiang-conrath or all>\n"+
            "   --separator     <token for separating input and output fields, default is <TAB>>\n";

    /**
     *
     * @param args
     * --gwg-file <a wordnet file in the global wordnet grid format></a>
     * --input <a file with pairs to be compared on single lines separate with "/"></a>
     * --
     */
    static public void main (String[] args) {
        WordnetData wordnetData = new WordnetData();
        SubsumersFrequencies subsumersFrequencies = new SubsumersFrequencies();
        ArrayList<String> relations = new ArrayList<String>();
        boolean ok = true;
        String wnformat = "";
        String pathToWordnetFile = "";
        String pathToInputFile = "";
        String pathToRelFile = "";
        String pathToSubsumerFrequencies = "";
        String posFilter = "";
        int depth = 0;
        String pairs = "";
        String method = "";
        if (args.length==0) {
            System.out.println(usage);
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            System.out.println("arg = " + arg);
            if ((arg.equalsIgnoreCase("--pos")) && args.length>i) {
                posFilter = args[i+1];
            }
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ((arg.equalsIgnoreCase("--gwg-file")) && args.length>i) {
                pathToWordnetFile = args[i+1];
                wnformat = arg;
            }
            else if ((arg.equalsIgnoreCase("--lmf-file")) && args.length>i) {
                pathToWordnetFile = args[i+1];
                wnformat = arg;
            }
            else if ((arg.equalsIgnoreCase("--cdb-file")) && args.length>i) {
                pathToWordnetFile = args[i+1];
                wnformat = arg;
            }
            else if ((arg.equalsIgnoreCase("--input")) && args.length>i) {
                pathToInputFile = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--relations")) && args.length>i) {
                pathToRelFile = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--method")) && args.length>i) {
                method = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--pairs")) && args.length>i) {
                pairs = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--depth")) && args.length>i) {
                try {
                    depth = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            else if ((arg.equalsIgnoreCase("--subsumers")) && args.length>i) {
                pathToSubsumerFrequencies = args[i+1];
                subsumersFrequencies.readSubsumerFrequenciesFromFile(pathToSubsumerFrequencies);
            }
            else if ((arg.equalsIgnoreCase("--separator")) && args.length>i) {
                separator = args[i+1];
            }
        }
        if (pathToWordnetFile.isEmpty()) {
            System.out.println("Missing parameter for wordnet file \n" +
                    " --gwg-file global wordnet grid format\n" +
                    " --cdb-file cornetto export format\n" +
                    " --lmf-file wordnet lmf format\n");
            ok = false;
        }
        else if (pathToInputFile.isEmpty()) {
            System.out.println("Missing parameter --input");
            ok = false;
        }
        else if (method.isEmpty()) {
            System.out.println("Missing parameter --method");
            ok = false;
        }
        else if (pairs.isEmpty()) {
            System.out.println("Missing parameter --pairs");
            ok = false;
        }
        else if ((method.equalsIgnoreCase("resnik")) ||
                (method.equalsIgnoreCase("lin")) ||
                (method.equalsIgnoreCase("jiang-conrath"))){
            if (pathToSubsumerFrequencies.isEmpty())  {
                System.out.println("Missing parameter --subsumers");
                ok = false;
            }
        }

        if (!ok) {
            System.out.println(usage);
        }
        else {
            try {
                String str = "Wordnet Tools, version "+version+", implementation by Piek Vossen (piek.vossen@vu.nl), VU University Amsterdam\n";
                str += "pathToWordnetFile = " + pathToWordnetFile+"\n";
                str += "pathToInputFile = " + pathToInputFile+"\n";;
                str += "method = " + method+"\n";
                str += "pathToSubsumerFrequencies = " + pathToSubsumerFrequencies+"\n";
                str += "subsumersFrequencies subsumers = " + subsumersFrequencies.data.size()+"\n";
                str += "subsumersFrequencies nr of words= " + subsumersFrequencies.nWords+"\n";
                str += "posFilter = " + posFilter+"\n";
                str += "pathToRelFile = " + pathToRelFile+"\n";
                str += "depth = " + depth+"\n";
                if (pathToRelFile.isEmpty()) {
                    relations = readRelationsFile(pathToInputFile);
                    str += "relationFile = "+pathToRelFile+"\n";
                }
                if (wnformat.equalsIgnoreCase("--cdb-file")) {
                    CdbSynSaxParser parser = new CdbSynSaxParser();
                    if (relations.size()>0) parser.setRelations( relations);
                    if (!posFilter.isEmpty()) parser.setPos(posFilter);
                    parser.parseFile(pathToWordnetFile);
                    wordnetData = parser.wordnetData;
                }
                else if (wnformat.equalsIgnoreCase("--gwg-file")) {
                    PwnSaxParser parser = new PwnSaxParser();
                    if (relations.size()>0) parser.setRelations( relations);
                    if (!posFilter.isEmpty()) parser.setPos(posFilter);
                    parser.parseFile(pathToWordnetFile);
                    wordnetData = parser.wordnetData;
                }
                else if (wnformat.equalsIgnoreCase("--lmf-file")) {
                    WordnetLmfSaxParser parser = new WordnetLmfSaxParser();
                    if (relations.size()>0) parser.setRelations( relations);
                    if (!posFilter.isEmpty()) parser.setPos(posFilter);
                    parser.parseFile(pathToWordnetFile);
                    wordnetData = parser.wordnetData;
                }
                wordnetData.buildSynsetIndex();
                str += "wordnetData entries = " + wordnetData.entryToSynsets.size()+"\n";
                str += "wordnetData synsets = " + wordnetData.getHyperRelations().size()+"\n\n";
                FileOutputStream fos = new FileOutputStream(pathToInputFile+"."+method);
                FileOutputStream log = new FileOutputStream(pathToInputFile+"."+method+".log");
                log.write(str.getBytes());
                System.out.println(str);
                FileInputStream fis = new FileInputStream(pathToInputFile);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                str = "";
                if (pairs.equals("words")) {
                    str = "word-1\tword-2\t";
                }
                if (pairs.equals("synsets")) {
                    str = "synset-1\tsynset-2\t";
                }
                if (pairs.equals("word-synsets")) {
                    str = "word-synset-1\tword-synset-2\t";
                }
                if (!method.equals("all"))  {
                    str += "Similar by "+method+"\tLCS\n";
                }
                else {
                    str += "Similar by path\tLCS path\tSimilar by L&C\tLCS L&C\tSimilar by W&P\tLCS W&P\tSimilar by R\tLCS R\tSimilar by L\tLCS L\tSimilar by J&C\tLCS J&C\n";
                }
                fos.write(str.getBytes());
                log.write(str.getBytes());
                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    String [] fields = inputLine.split(separator);
                    if (fields.length==2) {
                        String source = fields[0].trim();
                        String target = fields[1].trim();
                       // System.out.println("source = " + source);
                       // System.out.println("target = " + target);
                        String logString = "";
                        String logString2 = "";
                        String match = "";
                        if (pairs.equalsIgnoreCase("words")) {
                            if (!method.equals("all")) {
                                ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();
                                SimilarityPair topPair = new SimilarityPair();
                                if (method.equalsIgnoreCase("leacock-chodorow")) {
                                    LeacockChodorow.match = "";
                                    if (depth>0) {
                                        similarityPairArrayList = WordnetSimilarityApi.wordLeacockChodorowSimilarity(wordnetData, depth, source, target);
                                    }
                                    else {
                                        similarityPairArrayList = WordnetSimilarityApi.wordLeacockChodorowSimilarity(wordnetData, source, target);
                                    }
                                    topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                    match = vu.wntools.wnsimilarity.measures.LeacockChodorow.match;

                                }
                                else if (method.equalsIgnoreCase("path")) {
                                    BaseLines.match = "";
                                    similarityPairArrayList = WordnetSimilarityApi.wordPathSimilarity(wordnetData, source, target);
                                    topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                    match = BaseLines.match;
                                }
                                else if (method.equalsIgnoreCase("wu-palmer")) {
                                    WuPalmer.match = "";
                                    similarityPairArrayList = WordnetSimilarityApi.wordWuPalmerSimilarity(wordnetData, source, target);
                                    topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                    match = WuPalmer.match;
                                }
                                else if (method.equalsIgnoreCase("resnik")) {
                                    Resnik.match = "";
                                    Resnik.value = 0;
                                    similarityPairArrayList = WordnetSimilarityApi.wordResnikSimilarity(wordnetData, subsumersFrequencies, source, target);
                                    topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                    match = Resnik.match;
                                    logString2 = "Resnik value = "+Resnik.value+"\n";
                                }
                                else if (method.equalsIgnoreCase("lin")) {
                                    vu.wntools.wnsimilarity.measures.Lin.match = "";
                                    Lin.valueIc1 = 0;
                                    Lin.valueIc2 = 0;
                                    Lin.valueIcLcs = 0;
                                    similarityPairArrayList = WordnetSimilarityApi.wordLinSimilarity(wordnetData, subsumersFrequencies, source, target);
                                    topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                    match = Lin.match;
                                    logString2 = "Lin value Ic1 = "+Lin.valueIc1+"\n";
                                    logString2 += "Lin value Ic2 = "+Lin.valueIc2+"\n";
                                    logString2 += "Lin value IcLcs = "+Lin.valueIcLcs+"\n";
                                }
                                else if (method.equalsIgnoreCase("jiang-conrath")) {
                                    JiangConrath.match = "";
                                    JiangConrath.valueIc1 = 0;
                                    JiangConrath.valueIc2 = 0;
                                    JiangConrath.valueIcLcs = 0;
                                    similarityPairArrayList = WordnetSimilarityApi.wordJiangConrathSimilarity(wordnetData, subsumersFrequencies, source, target);
                                    topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                    match = JiangConrath.match;
                                    logString2 = "JiangConrath value Ic1 = "+JiangConrath.valueIc1+"\n";
                                    logString2 += "JiangConrath value Ic2 = "+JiangConrath.valueIc2+"\n";
                                    logString2 += "JiangConrath value IcLcs = "+JiangConrath.valueIcLcs+"\n";
                                }
                                logString = "Method = "+method+"\n";
                                logString += "\tScore = "+topPair.getScore()+"\n";
                                if (!logString2.isEmpty()) {
                                    logString += logString2;
                                }
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(match)+"\n";
                                logString += "\t"+source+wordnetData.toHyperString(topPair.getSourceTree())+"\n";
                                logString += "\t"+target+wordnetData.toHyperString(topPair.getTargetTree())+"\n";
                                logString += "\n";
                                log.write(logString.getBytes());

                                inputLine+=separator+topPair.getScore()+separator+match +"\n";
                                //System.out.println("inputLine = " + inputLine);
                                fos.write(inputLine.getBytes());
                            }
                            else {
                                SimilarityPair topPair = new SimilarityPair();
                                ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();
                                LeacockChodorow.match = "";

                                //// Path
                                similarityPairArrayList = WordnetSimilarityApi.wordPathSimilarity(wordnetData, source, target);
                                topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                inputLine+=separator+topPair.getScore()+separator+BaseLines.match;

                                logString = "Method = Path\n";
                                logString += "\tScore = "+topPair.getScore()+"\n";
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(BaseLines.match)+"\n";
                                logString += "\t"+source+wordnetData.toHyperString(topPair.getSourceTree())+"\n";
                                logString += "\t"+target+wordnetData.toHyperString(topPair.getTargetTree())+"\n";
                                log.write(logString.getBytes());


                                /// L&C
                                if (depth>0) {
                                    similarityPairArrayList = WordnetSimilarityApi.wordLeacockChodorowSimilarity(wordnetData, depth, source, target);
                                }
                                else {
                                    similarityPairArrayList = WordnetSimilarityApi.wordLeacockChodorowSimilarity(wordnetData, source, target);
                                }
                                topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                inputLine+=separator+topPair.getScore()+separator+LeacockChodorow.match;

                                logString = "Method = LeacockChodorow\n";
                                logString += "\tScore = "+topPair.getScore()+"\n";
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(LeacockChodorow.match)+"\n";
                                logString += "\t"+source+wordnetData.toHyperString(topPair.getSourceTree())+"\n";
                                logString += "\t"+target+wordnetData.toHyperString(topPair.getTargetTree())+"\n";
                                log.write(logString.getBytes());

                                //// W&P
                                WuPalmer.match = "";
                                similarityPairArrayList = WordnetSimilarityApi.wordWuPalmerSimilarity(wordnetData, source, target);
                                topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                inputLine+=separator+topPair.getScore()+separator+WuPalmer.match;
                                logString = "Method = WuPalmer\n";
                                logString += "\tScore = "+topPair.getScore()+"\n";
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(WuPalmer.match)+"\n";
                                logString += "\t"+source+wordnetData.toHyperString(topPair.getSourceTree())+"\n";
                                logString += "\t"+target+wordnetData.toHyperString(topPair.getTargetTree())+"\n";
                                log.write(logString.getBytes());

                                /// Resnik
                                Resnik.match = "";
                                Resnik.value = 0;
                                similarityPairArrayList = WordnetSimilarityApi.wordResnikSimilarity(wordnetData, subsumersFrequencies, source, target);
                                topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                inputLine+=separator+topPair.getScore()+separator+Resnik.match;

                                logString = "Method = Resnik\n";
                                logString += "\tScore = "+topPair.getScore()+"\n";
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(Resnik.match)+"\n";
                                logString += "\tIcLcs = "+Resnik.value+"\n";
                                logString += "\t"+source+wordnetData.toHyperString(topPair.getSourceTree())+"\n";
                                logString += "\t"+target+wordnetData.toHyperString(topPair.getTargetTree())+"\n";
                                log.write(logString.getBytes());

                                /// Lin
                                vu.wntools.wnsimilarity.measures.Lin.match = "";
                                Lin.valueIc1 = 0;
                                Lin.valueIc2 = 0;
                                Lin.valueIcLcs = 0;
                                similarityPairArrayList = WordnetSimilarityApi.wordLinSimilarity(wordnetData, subsumersFrequencies, source, target);
                                topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                inputLine+=separator+topPair.getScore()+separator+Lin.match;

                                logString = "Method = Lin\n";
                                logString += "\tScore = "+topPair.getScore()+"\n";
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(Lin.match)+"\n";
                                logString += "Ic1 = "+Lin.valueIc1+"\n";
                                logString += "Ic2 = "+Lin.valueIc2+"\n";
                                logString += "IcLcs = "+Lin.valueIcLcs+"\n";
                                logString += "\t"+source+wordnetData.toHyperString(topPair.getSourceTree())+"\n";
                                logString += "\t"+target+wordnetData.toHyperString(topPair.getTargetTree())+"\n";
                                log.write(logString.getBytes());


                                // J&C
                                JiangConrath.match = "";
                                JiangConrath.valueIc1 = 0;
                                JiangConrath.valueIc2 = 0;
                                JiangConrath.valueIcLcs = 0;

                                similarityPairArrayList = WordnetSimilarityApi.wordJiangConrathSimilarity(wordnetData, subsumersFrequencies, source, target);
                                topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                inputLine+=separator+topPair.getScore()+separator+JiangConrath.match;

                                logString = "Method = JiangConrath\n";
                                logString += "\tScore = "+topPair.getScore()+"\n";
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(JiangConrath.match)+"\n";
                                logString += "Ic1 = "+Lin.valueIc1+"\n";
                                logString += "Ic2 = "+Lin.valueIc2+"\n";
                                logString += "IcLcs = "+Lin.valueIcLcs+"\n";
                                logString += "\t"+source+wordnetData.toHyperString(topPair.getSourceTree())+"\n";
                                logString += "\t"+target+wordnetData.toHyperString(topPair.getTargetTree())+"\n\n";
                                log.write(logString.getBytes());

                                inputLine+= "\n";
                                fos.write(inputLine.getBytes());
                            }
                        }
                        else if ((pairs.equalsIgnoreCase("synsets")) ||
                                 (pairs.equalsIgnoreCase("word-synsets"))) {
                            String word1 = "";
                            String word2 = "";
                            if ((pairs.equalsIgnoreCase("word-synsets"))) {
                                String [] inputFields = source.split("#");
                                if (inputFields.length==2) {
                                    word1 = inputFields[0];
                                    source = inputFields[1];
                                }
                                inputFields = target.split("#");
                                if (inputFields.length==2) {
                                    word2 = inputFields[0];
                                    target = inputFields[1];
                                }
                            }
                            if (!method.equals("all")) {
                                SimilarityPair similarityPair = new SimilarityPair();
                                if (method.equalsIgnoreCase("leacock-chodorow")) {
                                    vu.wntools.wnsimilarity.measures.LeacockChodorow.match = "";
                                    if (depth>0) {
                                        similarityPair = WordnetSimilarityApi.synsetLeacockChodorowSimilarity(wordnetData, depth, source, target);
                                    }
                                    else {
                                        similarityPair = WordnetSimilarityApi.synsetLeacockChodorowSimilarity(wordnetData, source, target);
                                    }
                                    match = vu.wntools.wnsimilarity.measures.LeacockChodorow.match;
                                }
                                else if (method.equalsIgnoreCase("wu-palmer")) {
                                    WuPalmer.match = "";
                                    similarityPair = WordnetSimilarityApi.synsetWuPalmerSimilarity(wordnetData, source, target);
                                    match = WuPalmer.match;
                                }
                                else if (method.equalsIgnoreCase("path")) {
                                    BaseLines.match = "";
                                    similarityPair = WordnetSimilarityApi.synsetPathSimilarity(wordnetData, source, target);
                                    match = BaseLines.match;
                                }
                                else if (method.equalsIgnoreCase("resnik")) {
                                    vu.wntools.wnsimilarity.measures.Resnik.match = "";
                                    Resnik.value = 0;
                                    similarityPair = WordnetSimilarityApi.synsetResnikSimilarity(wordnetData, subsumersFrequencies, source, target);
                                    match = Resnik.match;
                                    logString2 = "Resnik icLcs = "+Resnik.value+"\n";
                                }
                                else if (method.equalsIgnoreCase("lin")) {
                                    Lin.match = "";
                                    Lin.valueIc1 = 0;
                                    Lin.valueIc2 = 0;
                                    Lin.valueIcLcs = 0;
                                    similarityPair = WordnetSimilarityApi.synsetLinSimilarity(wordnetData, subsumersFrequencies, source, target);
                                    match = Lin.match;
                                    logString2 = "Lin value Ic1 = "+Lin.valueIc1+"\n";
                                    logString2 += "Lin value Ic2 = "+Lin.valueIc2+"\n";
                                    logString2 += "Lin value IcLcs = "+Lin.valueIcLcs+"\n";
                                }
                                else if (method.equalsIgnoreCase("jiang-conrath")) {
                                    JiangConrath.match = "";
                                    JiangConrath.valueIc1 = 0;
                                    JiangConrath.valueIc2 = 0;
                                    JiangConrath.valueIcLcs = 0;
                                    similarityPair = WordnetSimilarityApi.synsetJiangConrathSimilarity(wordnetData, subsumersFrequencies, source, target);
                                    match = JiangConrath.match;
                                    logString2 = "JiangConrath value Ic1 = "+JiangConrath.valueIc1+"\n";
                                    logString2 += "JiangConrath value Ic2 = "+JiangConrath.valueIc2+"\n";
                                    logString2 += "JiangConrath value IcLcs = "+JiangConrath.valueIcLcs+"\n";
                                }

                                logString = "Method = "+method+"\n";
                                logString += "\tScore = "+similarityPair.getScore()+"\n";
                                if (!logString2.isEmpty()) {
                                    logString += logString2;
                                }
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(match)+"\n";
                                logString += "\t"+word1+"#"+source+wordnetData.toHyperString(similarityPair.getSourceTree())+"\n";
                                logString += "\t"+word2+"#"+target+wordnetData.toHyperString(similarityPair.getTargetTree())+"\n";
                                logString += "\n";
                                log.write(logString.getBytes());

                                inputLine+=separator+similarityPair.getScore()+separator+match +"\n";
                                fos.write(inputLine.getBytes());
                            }
                            else {
                                SimilarityPair similarityPair = new SimilarityPair();

                                /// Path
                                BaseLines.match = "";
                                similarityPair = WordnetSimilarityApi.synsetPathSimilarity(wordnetData, source, target);
                                inputLine+=separator+similarityPair.getScore()+separator+BaseLines.match;

                                logString = "Method = Path\n";
                                logString += "\tScore = "+similarityPair.getScore()+"\n";
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(BaseLines.match)+"\n";
                                logString += "\t"+source+wordnetData.toHyperString(similarityPair.getSourceTree())+"\n";
                                logString += "\t"+target+wordnetData.toHyperString(similarityPair.getTargetTree())+"\n";
                                log.write(logString.getBytes());

                                /// L&C
                                LeacockChodorow.match = "";
                                if (depth>0) {
                                    similarityPair = WordnetSimilarityApi.synsetLeacockChodorowSimilarity(wordnetData, depth, source, target);
                                }
                                else {
                                    similarityPair = WordnetSimilarityApi.synsetLeacockChodorowSimilarity(wordnetData, source, target);
                                }
                                inputLine+=separator+similarityPair.getScore()+separator+LeacockChodorow.match;

                                logString = "Method = LeacockChodorow\n";
                                logString += "\tScore = "+similarityPair.getScore()+"\n";
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(LeacockChodorow.match)+"\n";
                                logString += "\t"+source+wordnetData.toHyperString(similarityPair.getSourceTree())+"\n";
                                logString += "\t"+target+wordnetData.toHyperString(similarityPair.getTargetTree())+"\n";
                                log.write(logString.getBytes());

                                //// WuPalmer
                                WuPalmer.match = "";
                                similarityPair = WordnetSimilarityApi.synsetWuPalmerSimilarity(wordnetData, source, target);
                                inputLine+=separator+similarityPair.getScore()+separator+WuPalmer.match;

                                logString = "Method = WuPalmer\n";
                                logString += "\tScore = "+similarityPair.getScore()+"\n";
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(WuPalmer.match)+"\n";
                                logString += "\t"+source+wordnetData.toHyperString(similarityPair.getSourceTree())+"\n";
                                logString += "\t"+target+wordnetData.toHyperString(similarityPair.getTargetTree())+"\n";
                                log.write(logString.getBytes());


                                //// Resnik
                                vu.wntools.wnsimilarity.measures.Resnik.match = "";
                                Resnik.value = 0;
                                similarityPair = WordnetSimilarityApi.synsetResnikSimilarity(wordnetData, subsumersFrequencies, source, target);
                                inputLine+=separator+similarityPair.getScore()+separator+Resnik.match;

                                logString = "Method = Resnik\n";
                                logString += "\tScore = "+similarityPair.getScore()+"\n";
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(Resnik.match)+"\n";
                                logString += "IcLcs = "+Resnik.value+"\n";
                                logString += "\t"+source+wordnetData.toHyperString(similarityPair.getSourceTree())+"\n";
                                logString += "\t"+target+wordnetData.toHyperString(similarityPair.getTargetTree())+"\n";
                                log.write(logString.getBytes());

                                /// Lin
                                Lin.match = "";
                                Lin.valueIc1 = 0;
                                Lin.valueIc2 = 0;
                                Lin.valueIcLcs = 0;
                                similarityPair = WordnetSimilarityApi.synsetLinSimilarity(wordnetData, subsumersFrequencies, source, target);
                                inputLine+=separator+similarityPair.getScore()+separator+Lin.match;

                                logString = "Method = Lin\n";
                                logString += "\tScore = "+similarityPair.getScore()+"\n";
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(Lin.match)+"\n";
                                logString += "Ic1 = "+Lin.valueIc1+"\n";
                                logString += "Ic2 = "+Lin.valueIc2+"\n";
                                logString += "IcLcs = "+Lin.valueIcLcs+"\n";
                                logString += "\t"+source+wordnetData.toHyperString(similarityPair.getSourceTree())+"\n";
                                logString += "\t"+target+wordnetData.toHyperString(similarityPair.getTargetTree())+"\n";
                                log.write(logString.getBytes());

                                /// J&C
                                JiangConrath.match = "";
                                JiangConrath.valueIc1 = 0;
                                JiangConrath.valueIc2 = 0;
                                JiangConrath.valueIcLcs = 0;
                                similarityPair = WordnetSimilarityApi.synsetJiangConrathSimilarity(wordnetData, subsumersFrequencies, source, target);
                                inputLine+=separator+similarityPair.getScore()+separator+JiangConrath.match;


                                logString = "Method = JiangConrath\n";
                                logString += "\tScore = "+similarityPair.getScore()+"\n";
                                logString += "\tMatch ="+wordnetData.getFirstEntryForSynset(JiangConrath.match)+"\n";
                                logString += "Ic1 = "+JiangConrath.valueIc1+"\n";
                                logString += "Ic2 = "+JiangConrath.valueIc2+"\n";
                                logString += "IcLcs = "+JiangConrath.valueIcLcs+"\n";
                                logString += "\t"+source+wordnetData.toHyperString(similarityPair.getSourceTree())+"\n";
                                logString += "\t"+target+wordnetData.toHyperString(similarityPair.getTargetTree())+"\n";
                                logString += "\n";
                                log.write(logString.getBytes());

                                inputLine+= "\n";
                                fos.write(inputLine.getBytes());
                            }

                        }
                        else {
                            System.out.println("Unknown input type pairs = " + pairs);
                        }
                    }
                }
                fos.close();
                log.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

    static ArrayList<String> readRelationsFile (String pathToRelationFile) {
        ArrayList<String> relations = new ArrayList<String>();
        if (!new File(pathToRelationFile).exists())   {
            System.out.println("Cannot find pathToRelationFile = " + pathToRelationFile);
        }
        try {
            FileInputStream fis = new FileInputStream(pathToRelationFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (!inputLine.trim().isEmpty()) {
                    if (!inputLine.startsWith("#"))
                    relations.add(inputLine.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //To change body of catch statement use File | Settings | File Templates.
        }
        return relations;
    }

}
