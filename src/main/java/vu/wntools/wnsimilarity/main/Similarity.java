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
            "   --average-depth <optional: a fixed value for average depth can be given, >\n"+
            "   --subsumers     <path to a file with subsumer frequencies, required for resnik, lin, jiang-conrath or all>\n"+
            "   --separator     token for separating input and output fields, default is <TAB>\n";

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
        int averageDepth = 0;
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
            else if ((arg.equalsIgnoreCase("--average-depth")) && args.length>i) {
                try {
                    averageDepth = Integer.parseInt(args[i+1]);
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
                str += "averageDepth = " + averageDepth+"\n";
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
                    str += "Distance by "+method+"\tLCS\n";
                }
                else {
                    str += "Distance by path\tLCS path\tDistance by L&C\tLCS L&C\tDistance by W&P\tLCS W&P\tDistance by R\tLCS R\tDistance by L\tLCS L\tDistance by J&C\tLCS J&C\n";
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
                        if (pairs.equalsIgnoreCase("words")) {
                            if (!method.equals("all")) {
                                String match = "";
                                ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();
                                if (method.equalsIgnoreCase("leacock-chodorow")) {
                                    LeacockChodorow.match = "";
                                    if (averageDepth>0) {
                                        similarityPairArrayList = WordnetSimilarityApi.wordLeacockChodorowSimilarity(wordnetData, averageDepth, source, target);
                                    }
                                    else {
                                        similarityPairArrayList = WordnetSimilarityApi.wordLeacockChodorowSimilarity(wordnetData, source, target);
                                    }
                                    match = vu.wntools.wnsimilarity.measures.LeacockChodorow.match;
                                }
                                else if (method.equalsIgnoreCase("path")) {
                                    BaseLines.match = "";
                                    similarityPairArrayList = WordnetSimilarityApi.wordPathSimilarity(wordnetData, source, target);
                                    match = BaseLines.match;
                                }
                                else if (method.equalsIgnoreCase("wu-palmer")) {
                                    WuPalmer.match = "";
                                    similarityPairArrayList = WordnetSimilarityApi.wordWuPalmerSimilarity(wordnetData, source, target);
                                    match = WuPalmer.match;
                                }
                                else if (method.equalsIgnoreCase("resnik")) {
                                    Resnik.match = "";
                                    Resnik.value = 0;
                                    similarityPairArrayList = WordnetSimilarityApi.wordResnikSimilarity(wordnetData, subsumersFrequencies, source, target);
                                    match = Resnik.match;
                                    logString += "Resnik value = "+Resnik.value+"\n";
                                }
                                else if (method.equalsIgnoreCase("lin")) {
                                    vu.wntools.wnsimilarity.measures.Lin.match = "";
                                    Lin.valueIc1 = 0;
                                    Lin.valueIc2 = 0;
                                    Lin.valueIcLcs = 0;
                                    similarityPairArrayList = WordnetSimilarityApi.wordLinSimilarity(wordnetData, subsumersFrequencies, source, target);
                                    match = Lin.match;
                                    logString += "Lin value Ic1 = "+Lin.valueIc1+"\n";
                                    logString += "Lin value Ic2 = "+Lin.valueIc2+"\n";
                                    logString += "Lin value IcLcs = "+Lin.valueIcLcs+"\n";
                                }
                                else if (method.equalsIgnoreCase("jiang-conrath")) {
                                    JiangConrath.match = "";
                                    JiangConrath.valueIc1 = 0;
                                    JiangConrath.valueIc2 = 0;
                                    JiangConrath.valueIcLcs = 0;
                                    similarityPairArrayList = WordnetSimilarityApi.wordJiangConrathSimilarity(wordnetData, subsumersFrequencies, source, target);
                                    match = JiangConrath.match;
                                    logString += "JiangConrath value Ic1 = "+JiangConrath.valueIc1+"\n";
                                    logString += "JiangConrath value Ic2 = "+JiangConrath.valueIc2+"\n";
                                    logString += "JiangConrath value IcLcs = "+JiangConrath.valueIcLcs+"\n";
                                }
                                logString += "\n";
                                SimilarityPair topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                inputLine+=separator+topPair.getScore()+separator+match +"\n";
                                //System.out.println("inputLine = " + inputLine);
                                fos.write(inputLine.getBytes());
                                log.write(inputLine.getBytes());
                                str = source+wordnetData.toHyperString(topPair.getSourceTree())+"\n";
                                str += target+wordnetData.toHyperString(topPair.getTargetTree())+"\n";
                                log.write(str.getBytes());
                                log.write(logString.getBytes());
                            }
                            else {
                                SimilarityPair topPair = new SimilarityPair();
                                ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();
                                LeacockChodorow.match = "";

                                similarityPairArrayList = WordnetSimilarityApi.wordPathSimilarity(wordnetData, source, target);
                                topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                inputLine+=separator+topPair.getScore()+separator+BaseLines.match;


                                if (averageDepth>0) {
                                    similarityPairArrayList = WordnetSimilarityApi.wordLeacockChodorowSimilarity(wordnetData, averageDepth, source, target);
                                }
                                else {
                                    similarityPairArrayList = WordnetSimilarityApi.wordLeacockChodorowSimilarity(wordnetData, source, target);
                                }
                                topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                inputLine+=separator+topPair.getScore()+separator+LeacockChodorow.match;

                                WuPalmer.match = "";
                                similarityPairArrayList = WordnetSimilarityApi.wordWuPalmerSimilarity(wordnetData, source, target);
                                topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                inputLine+=separator+topPair.getScore()+separator+WuPalmer.match;

                                Resnik.match = "";
                                Resnik.value = 0;
                                similarityPairArrayList = WordnetSimilarityApi.wordResnikSimilarity(wordnetData, subsumersFrequencies, source, target);
                                topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                inputLine+=separator+topPair.getScore()+separator+Resnik.match;
                                logString += "Resnik value = "+Resnik.value+"\n";

                                vu.wntools.wnsimilarity.measures.Lin.match = "";
                                Lin.valueIc1 = 0;
                                Lin.valueIc2 = 0;
                                Lin.valueIcLcs = 0;
                                similarityPairArrayList = WordnetSimilarityApi.wordLinSimilarity(wordnetData, subsumersFrequencies, source, target);
                                topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                inputLine+=separator+topPair.getScore()+separator+Lin.match;
                                logString += "Lin value Ic1 = "+Lin.valueIc1+"\n";
                                logString += "Lin value Ic2 = "+Lin.valueIc2+"\n";
                                logString += "Lin value IcLcs = "+Lin.valueIcLcs+"\n";


                                JiangConrath.match = "";

                                JiangConrath.match = "";
                                JiangConrath.valueIc1 = 0;
                                JiangConrath.valueIc2 = 0;
                                JiangConrath.valueIcLcs = 0;

                                similarityPairArrayList = WordnetSimilarityApi.wordJiangConrathSimilarity(wordnetData, subsumersFrequencies, source, target);

                                logString += "JiangConrath value Ic1 = "+JiangConrath.valueIc1+"\n";
                                logString += "JiangConrath value Ic2 = "+JiangConrath.valueIc2+"\n";
                                logString += "JiangConrath value IcLcs = "+JiangConrath.valueIcLcs+"\n";
                                logString += "\n";

                                topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
                                inputLine+=separator+topPair.getScore()+separator+JiangConrath.match;
                                inputLine+= "\n";
                                fos.write(inputLine.getBytes());
                                log.write(inputLine.getBytes());
                                str = source+wordnetData.toHyperString(topPair.getSourceTree())+"\n";
                                str += target+wordnetData.toHyperString(topPair.getTargetTree())+"\n";
                                log.write(str.getBytes());
                                log.write(logString.getBytes());
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
                                String match = "";
                                SimilarityPair similarityPair = new SimilarityPair();
                                if (method.equalsIgnoreCase("leacock-chodorow")) {
                                    vu.wntools.wnsimilarity.measures.LeacockChodorow.match = "";
                                    if (averageDepth>0) {
                                        similarityPair = WordnetSimilarityApi.synsetLeacockChodorowSimilarity(wordnetData, averageDepth, source, target);
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
                                    logString += "Resnik value = "+Resnik.value+"\n";
                                }
                                else if (method.equalsIgnoreCase("lin")) {
                                    Lin.match = "";
                                    Lin.valueIc1 = 0;
                                    Lin.valueIc2 = 0;
                                    Lin.valueIcLcs = 0;
                                    similarityPair = WordnetSimilarityApi.synsetLinSimilarity(wordnetData, subsumersFrequencies, source, target);
                                    match = Lin.match;
                                    logString += "Lin value Ic1 = "+Lin.valueIc1+"\n";
                                    logString += "Lin value Ic2 = "+Lin.valueIc2+"\n";
                                    logString += "Lin value IcLcs = "+Lin.valueIcLcs+"\n";
                                }
                                else if (method.equalsIgnoreCase("jiang-conrath")) {
                                    JiangConrath.match = "";
                                    JiangConrath.valueIc1 = 0;
                                    JiangConrath.valueIc2 = 0;
                                    JiangConrath.valueIcLcs = 0;
                                    similarityPair = WordnetSimilarityApi.synsetJiangConrathSimilarity(wordnetData, subsumersFrequencies, source, target);
                                    match = JiangConrath.match;
                                    logString += "JiangConrath value Ic1 = "+JiangConrath.valueIc1+"\n";
                                    logString += "JiangConrath value Ic2 = "+JiangConrath.valueIc2+"\n";
                                    logString += "JiangConrath value IcLcs = "+JiangConrath.valueIcLcs+"\n";
                                }
                                logString += "\n";

                                inputLine+=separator+similarityPair.getScore()+separator+match +"\n";
                                fos.write(inputLine.getBytes());
                                log.write(inputLine.getBytes());
                                str = word1+"#"+source+wordnetData.toHyperString(similarityPair.getSourceTree())+"\n";
                                str += word2+"#"+target+wordnetData.toHyperString(similarityPair.getTargetTree())+"\n";
                                log.write(str.getBytes());
                                log.write(logString.getBytes());
                            }
                            else {
                                SimilarityPair similarityPair = new SimilarityPair();

                                BaseLines.match = "";
                                similarityPair = WordnetSimilarityApi.synsetPathSimilarity(wordnetData, source, target);
                                inputLine+=separator+similarityPair.getScore()+separator+BaseLines.match;


                                LeacockChodorow.match = "";
                                if (averageDepth>0) {
                                    similarityPair = WordnetSimilarityApi.synsetLeacockChodorowSimilarity(wordnetData, averageDepth, source, target);
                                }
                                else {
                                    similarityPair = WordnetSimilarityApi.synsetLeacockChodorowSimilarity(wordnetData, source, target);
                                }
                                inputLine+=separator+similarityPair.getScore()+separator+LeacockChodorow.match;

                                WuPalmer.match = "";
                                similarityPair = WordnetSimilarityApi.synsetWuPalmerSimilarity(wordnetData, source, target);
                                inputLine+=separator+similarityPair.getScore()+separator+WuPalmer.match;

                                vu.wntools.wnsimilarity.measures.Resnik.match = "";
                                Resnik.value = 0;
                                similarityPair = WordnetSimilarityApi.synsetResnikSimilarity(wordnetData, subsumersFrequencies, source, target);
                                inputLine+=separator+similarityPair.getScore()+separator+Resnik.match;
                                logString += "Resnik value = "+Resnik.value+"\n";

                                Lin.match = "";
                                Lin.valueIc1 = 0;
                                Lin.valueIc2 = 0;
                                Lin.valueIcLcs = 0;
                                similarityPair = WordnetSimilarityApi.synsetLinSimilarity(wordnetData, subsumersFrequencies, source, target);
                                inputLine+=separator+similarityPair.getScore()+separator+Lin.match;
                                logString += "Lin value Ic1 = "+Lin.valueIc1+"\n";
                                logString += "Lin value Ic2 = "+Lin.valueIc2+"\n";
                                logString += "Lin value IcLcs = "+Lin.valueIcLcs+"\n";

                                JiangConrath.match = "";
                                JiangConrath.valueIc1 = 0;
                                JiangConrath.valueIc2 = 0;
                                JiangConrath.valueIcLcs = 0;
                                similarityPair = WordnetSimilarityApi.synsetJiangConrathSimilarity(wordnetData, subsumersFrequencies, source, target);
                                inputLine+=separator+similarityPair.getScore()+separator+JiangConrath.match;
                                logString += "JiangConrath value Ic1 = "+JiangConrath.valueIc1+"\n";
                                logString += "JiangConrath value Ic2 = "+JiangConrath.valueIc2+"\n";
                                logString += "JiangConrath value IcLcs = "+JiangConrath.valueIcLcs+"\n";
                                logString += "\n";

                                inputLine+= "\n";
                                fos.write(inputLine.getBytes());
                                log.write(inputLine.getBytes());
                                str = word1+"#"+source+wordnetData.toHyperString(similarityPair.getSourceTree())+"\n";
                                str += word2+"#"+target+wordnetData.toHyperString(similarityPair.getTargetTree())+"\n";
                                log.write(str.getBytes());
                                log.write(logString.getBytes());
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
