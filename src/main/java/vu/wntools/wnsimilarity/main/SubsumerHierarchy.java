package vu.wntools.wnsimilarity.main;

import vu.wntools.corpus.WordData;
import vu.wntools.util.Util;
import vu.wntools.wordnet.SynsetNode;
import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 5/22/13
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubsumerHierarchy {

    static String version = "1.0";
    static String copyright = "Piek Vossen (piek.vossen@vu.nl)";
    static boolean monosemous = false;
    static int proportion = 30;
    static String pos = "";
    static boolean firsthypernym = false;
    static boolean prune = false;
    static ArrayList<SynsetNode> topNodes = new ArrayList<SynsetNode>();
    static HashMap <String, SynsetNode> hyperTree = new HashMap<String, SynsetNode>();
    static ArrayList<WordData> wordMap = new ArrayList<WordData>();
    static HashMap<String, ArrayList<ArrayList<String>>> wordHyperMap = new HashMap<String, ArrayList<ArrayList<String>>>();

    /**
     * If words are polysemous we keep the sense with most children and prune the rest
     * @param wordnetData
     * @param wordHyperMap
     */
    public static void getTreePrunedForCumulatedHypernymFrequency (WordnetData wordnetData, HashMap<String, ArrayList<ArrayList<String>>> wordHyperMap) {

            /**
             * We select those chains that have the most frequent most specific hypernym if there is a choice between different senses
             * We build a chain of the topFrequency at each level starting from the leaves.
             * If a new chain has a higher frequency than stored so far, we declare it as a new top chain and erase all top chains so far
             * If chains are equal then they are added to the top set.
             * Longer chains just add frequency to the frequency chain but this has no effect on the top chains
             */

            for (int i = 0; i < wordMap.size(); i++) {
                WordData wordData = wordMap.get(i);
                ArrayList<ArrayList<String>> topChains = new ArrayList<ArrayList<String>>();
                ArrayList<Integer> topFreqChain = new ArrayList<Integer>();
                if (wordHyperMap.containsKey(wordData.getWord())) {
                    ArrayList<ArrayList<String>> chains = wordHyperMap.get(wordData.getWord());
                    for (int j = 0; j < chains.size(); j++) {
                        ArrayList<String> hyperChain = chains.get(j);
                        for (int k = 0; k < hyperChain.size(); k++) {
                            String s = hyperChain.get(k);
                            if (hyperTree.containsKey(s)) {
                                SynsetNode synsetNode = hyperTree.get(s);
                                if (topFreqChain.size() > k) {
                                    if (topFreqChain.get(k) < synsetNode.getFreq()) {
                                        topFreqChain.set(k, synsetNode.getFreq());
                                        topChains = new ArrayList<ArrayList<String>>();
                                        topChains.add(hyperChain);
                                    }
                                    else if (topFreqChain.get(k) == synsetNode.getFreq()) {
                                        topFreqChain.set(k, synsetNode.getFreq());
                                        topChains.add(hyperChain);
                                    }
                                }
                                else {
                                    topFreqChain.add(synsetNode.getFreq());
                                }
                            }
                        }

                    }
                    wordHyperMap.put(wordData.getWord(), topChains);
                }
            }


            /// rebuild the hyperTree based on the pruned chains based on most frequent lowest subsumers

            ArrayList<String> children = new ArrayList<String>();

            topNodes = new ArrayList<SynsetNode>();
            hyperTree = new HashMap<String, SynsetNode>();
            for (int i = 0; i < wordMap.size(); i++) {
                WordData wordData = wordMap.get(i);

                if (wordHyperMap.containsKey(wordData.getWord())) {
                    ArrayList<ArrayList<String>> chains = wordHyperMap.get(wordData.getWord());
                    for (int c = 0; c < chains.size(); c++) {
                        ArrayList<String> hyperChain = chains.get(c);
                        String synsetId = hyperChain.get(0);
                        SynsetNode sNode = wordnetData.makeSynsetNode(wordData.getWord(),synsetId );
                        if (hyperTree.containsKey(synsetId)) {
                            sNode = hyperTree.get(synsetId);
                            sNode.addFreq(wordData.getFreq());
                            hyperTree.put(synsetId, sNode);
                        }
                        else {
                            sNode.setFreq(wordData.getFreq());
                            hyperTree.put(synsetId, sNode);

                            SynsetNode hNode = null;
                            int cumFreq = wordData.getFreq();
                            for (int j = 1; j < hyperChain.size(); j++) {
                                String hyperSynsetId = hyperChain.get(j);
                                if (hyperTree.containsKey(hyperSynsetId)) {
                                    hNode = hyperTree.get(hyperSynsetId);
                                    hNode.incrementDescendants();
                                    hNode.addCum(cumFreq);
                                    cumFreq = hNode.getCum();
                                    if (!Util.hasSynsetNode(hNode.getChildren(), sNode)) {
                                        hNode.addChildren(sNode);
                                    }
                                    else {
                                    }
                                    hyperTree.put(hyperSynsetId, hNode);
                                    if (!children.contains(sNode.getSynsetId())) {
                                        children.add(sNode.getSynsetId());
                                    }
                                }
                                else {
                                    hNode = wordnetData.makeSynsetNode(hyperSynsetId);
                                    hNode.incrementDescendants();
                                    hNode.addChildren(sNode);
                                    hNode.addCum(cumFreq);
                                    cumFreq = hNode.getCum();
                                    hNode.setDepth(hyperChain.size()-j);
                                    hyperTree.put(hyperSynsetId, hNode);
                                    if (!children.contains(sNode.getSynsetId())) {
                                        children.add(sNode.getSynsetId());
                                    }
                                }
                                // we have the last hyper in the the chain
                                if (j == hyperChain.size()-1) {
                                    if (!Util.hasSynsetNode(topNodes, hNode)) {
                                        topNodes.add(hNode);
                                    }
                                }
                                else {
                                    sNode = hNode;
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("pruned hyperTree = " + hyperTree.size());
    }
    /**
     * If words are polysemous we keep the sense with most children and prune the rest
     * @param wordnetData
     * @param wordMap
     */
    public static void getFullTree (WordnetData wordnetData, ArrayList<WordData> wordMap) {
            /// WE BUILD A HYPERTREE BASED ON ALL MEANINGS OF A WORD AND WE PASS ON THE FREQUENCIES FROM THE LEAVES TO THE HYPERNYMS
            for (int i = 0; i < wordMap.size(); i++) {
                WordData wordData =  wordMap.get(i);
               // System.out.println("word = " + wordData.getWord());
                if (wordnetData.entryToSynsets.containsKey(wordData.getWord())) {
                    ArrayList<String> synsetIds = wordnetData.entryToSynsets.get(wordData.getWord());
                    if (!monosemous || synsetIds.size()==1) {
                        for (int s = 0; s < synsetIds.size(); s++) {
                            String synsetId =  synsetIds.get(s);
                           // System.out.println("synsetId = " + synsetId);
                            SynsetNode sNode = wordnetData.makeSynsetNode(wordData.getWord(), synsetId);
                            if (hyperTree.containsKey(synsetId)) {
                                /// we already got it before so we just update the frequency
                                /// no need to build its tree since it should already be there
                                sNode = hyperTree.get(synsetId);
                                sNode.addFreq(wordData.getFreq());
                                hyperTree.put(synsetId, sNode);
                            }
                            else {
                                /// this is a new one so we set the frequency
                                sNode.setFreq(wordData.getFreq());
                                hyperTree.put(synsetId, sNode);
                                /// since it is new we build the tree upward, possibly multiple hierarchies upward
                                SynsetNode hNode = null;
                                ArrayList<ArrayList<String>> hypers = new ArrayList<ArrayList<String>>();
                                if (firsthypernym) {
                                    ArrayList<String> singleHypers = new ArrayList<String>();
                                    wordnetData.getSingleHyperChain(synsetId, singleHypers);
                                    hypers.add(singleHypers);
                                }
                                else {
                                    wordnetData.getMultipleHyperChain(synsetId, hypers);
                                }
                                if (hypers.size()>0) {
                                    for (int c = 0; c < hypers.size(); c++) {
                                        ArrayList<String> hyperChain = hypers.get(c);
                                        int cumFreq = sNode.getFreq();
                                        for (int j = 0; j < hyperChain.size(); j++) {
                                            String hyperSynsetId = hyperChain.get(j);
                                            if (hyperTree.containsKey(hyperSynsetId)) {
                                                hNode = hyperTree.get(hyperSynsetId);
                                                hNode.incrementDescendants();
                                                hNode.addCum(cumFreq);
                                                cumFreq = hNode.getCum();
                                                hyperTree.put(hyperSynsetId, hNode);
                                            }
                                            else {
                                                hNode = wordnetData.makeSynsetNode(hyperSynsetId);
                                                hNode.incrementDescendants();
                                                hNode.addChildren(sNode);
                                                hNode.addCum(cumFreq);
                                                cumFreq = hNode.getCum();
                                                hNode.setDepth(hypers.size()-j);
                                                hyperTree.put(hyperSynsetId, hNode);
                                            }
                                            // we have the last hyper in the the chain
                                            if (c == hyperChain.size()-1) {
                                                if (!Util.hasSynsetNode(topNodes, hNode)) {
                                                    topNodes.add(hNode);
                                                }
                                            }
                                            else {
                                                sNode = hNode;
                                            }

                                            //sNode = hNode;
                                        }
                                    }
                                    /// In the next loop we store all the chains for a word sense or synset at the word level
                                    if (wordHyperMap.containsKey(wordData.getWord())) {
                                        ArrayList<ArrayList<String>> chains = wordHyperMap.get(wordData.getWord());
                                        for (int j = 0; j < hypers.size(); j++) {
                                            ArrayList<String> hyperChain = hypers.get(j);
                                            chains.add(hyperChain);
                                        }
                                        wordHyperMap.put(wordData.getWord(), chains);
                                    }
                                    else {
                                        wordHyperMap.put(wordData.getWord(), hypers);
                                    }

                                }
                                else {
                                    /// there is nothing to do... This is a top node
                                }
                            }
                        }
                    }
                    else {
                        /// we ignore words with more than one meaning if monosemous flag is turned on
                    }
                }
            }
            System.out.println("unpruned hyperTree = " + hyperTree.size());
    }


    /**
     * Takes as input a HashMap with all synsets that have the same Tag
     * @param tagSynsetMap
     * @param fos
     */
    public static void getMostCommonSubsumersForTagMap (WordnetData wordnetData, HashMap<String, ArrayList<String>> tagSynsetMap, FileOutputStream fos) {
/*
        try {
            String str = "";
            Set keySet = tagSynsetMap.keySet();
            Iterator keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String keystr = "\nTAG:"+key+"\n";
                System.out.println(keystr);
                fos.write(keystr.getBytes());
                HashMap <String, Integer> hyperCount = new HashMap<String, Integer>();
                HashMap <String, SynsetNode> hyperTree = new HashMap<String, SynsetNode>();
                ArrayList<String> children = new ArrayList<String>();
                //// list of synset Ids related to a tagM
                ArrayList<String> synsets = tagSynsetMap.get(key);
                for (int i = 0; i < synsets.size(); i++) {
                    String synsetId = synsets.get(i);
                    keystr = synsetId+"\n";
                    fos.write(keystr.getBytes());

                    SynsetNode sNode = new SynsetNode();
                    sNode.setSynset(synsetId);
                    sNode.setSynsetId(synsetId);
                    hyperTree.put(synsetId, sNode);
                    SynsetNode hNode = null;
                    ArrayList<String> hypers = wordnetData.getHyperRelations().get(synsetId);
                    for (int j = 0; j < hypers.size(); j++) {
                        String hyperSynsetId = hypers.get(j);
                        if (hyperTree.containsKey(hyperSynsetId)) {
                            hNode = hyperTree.get(hyperSynsetId);
                            if (!hNode.getChildren().contains(sNode.getSynsetId())) {
                                hNode.addChildren(sNode);
                                hyperTree.put(hyperSynsetId, hNode);
                                if (!children.contains(sNode.getSynsetId())) {
                                    children.add(sNode.getSynsetId());
                                }
                            }
                        }
                        else {
                            hNode = new SynsetNode();
                            hNode.setSynsetId(hyperSynsetId);
                            hNode.addChildren(sNode);
                            hyperTree.put(hyperSynsetId, hNode);
                            if (!children.contains(sNode.getSynsetId())) {
                                children.add(sNode.getSynsetId());
                            }
                        }
                        sNode = hNode;
                        keystr = "["+hyperSynsetId+"]";
                        fos.write(keystr.getBytes());
                    }
                    keystr = "\n";
                    fos.write(keystr.getBytes());
                }
                keystr = "\nOVERVIEW OF HYPERNYMS\n";
                System.out.println(keystr);
                Set keyHyperSet = hyperCount.keySet();
                Iterator hpers = keyHyperSet.iterator();
                while(hpers.hasNext()) {
                    String hper = (String) hpers.next();
                    Integer cnt = hyperCount.get(hper);
                    keystr += hper+"\t"+cnt.toString()+"\n";
                }
                fos.write(keystr.getBytes());

                keystr = "\nHYPERNYM TREES\n";
                System.out.println(keystr);
                fos.write(keystr.getBytes());
                //System.out.println("children = " + children.size());
                ArrayList<SynsetNode> topNodes = getTopNodes(hyperTree, children);
                //System.out.println("topNodes.size() = " + topNodes.size());
                writeTreeString(new ArrayList<String>(), hyperTree, topNodes, 0, fos);
                System.out.println("DONE");

            }
            System.out.println("DONE ALL");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
*/
    }


    static final String usage = "" +
            "##### Create a subhierarchy of wordnet for a specific set of input words.\n" +
            "--wn-lmf\t\t<path to a wordnet file in wordn-lmf format> \n" +
            "--relations\t\t<path to a text file with the relations y=that should be used to build the hierarchy> \n" +
            "--input-file\t\t<path to the input file>\n" +
            "--method\t\t<pruned or full tree>\n"+
            "--format\t<format of the input file. Values are 'tuplemap', 'wordmap' and 'tagmap'>\n" +
            "--pos\t<part-of-speech considered for the input words>\n" +
            "--proportion\t\t<OPTIONAL: proportional frequency threshold, relative to the most frequent word, only works for 'tuplemap' format>\n" +
            "--monosemous\t\t<OPTIONAL: only consider input words that are monosemous>\n" +
            "--first-hypernym\t\t<OPTIONAL: take the first hypernym in case there are multiple hypernyms>\n" +
            "\n";

    static public void main (String [] args) {
        //String pathToWordnetLmfFile = args[0];
        //String pathToInputFile = args[1];
        topNodes = new ArrayList<SynsetNode>();
        hyperTree = new HashMap<String, SynsetNode>();
        wordMap = new ArrayList<WordData>();
        wordHyperMap = new HashMap<String, ArrayList<ArrayList<String>>>();

        if (args.length==0) {
            System.out.println("Usage = \n" + usage);
            return;
        }
        ArrayList<String> relations = new ArrayList<String>();
        relations.add("has_hyperonym");
        relations.add("HAS_HYPERONYM");
        String format = "";
        String pathToRelationsFile = "";
        String pathToWordnetLmfFile = "/Users/kyoto/Desktop/CDB/2013-MAY-18/Cornetto-LMF/cornetto.lmf";
        String pathToInputFile = "/Users/kyoto/Desktop/CDB/2013-MAY-18/Cornetto-LMF/testkaf.kaf-profiles.txt.overview.txt";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--wn-lmf") && (args.length-1>i)) {
                pathToWordnetLmfFile = args[i+1].trim();
            }
            else if (arg.equals("--input-file") && (args.length-1>i)) {
                pathToInputFile = args[i+1].trim();
            }
            else if (arg.equals("--relations") && (args.length-1>i)) {
                pathToRelationsFile = args[i+1].trim();
            }
            else if (arg.equals("--prune")) {
                prune = true;
            }
            else if (arg.equals("--format") && (args.length-1>i)) {
                format = args[i+1].trim();
            }
            else if (arg.equals("--pos") && (args.length-1>i)) {
                pos = args[i+1].trim();
            }
            else if (arg.equals("--proportion") && (args.length-1>i)) {
                try {
                    proportion = Integer.parseInt(args[i+1].trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            else if (arg.equals("--monosemous")) {
                monosemous = true;
            }
            else if (arg.equals("--first-hypernym")) {
                firsthypernym = true;
            }
        }
        WordnetLmfSaxParser wordnetLmfSaxParser = new WordnetLmfSaxParser();
        if (!pathToRelationsFile.isEmpty()) {
            relations = Util.readFileToArrayList(pathToRelationsFile);
        }
        wordnetLmfSaxParser.setRelations(relations);
        if (!pos.isEmpty()) {
            wordnetLmfSaxParser.setPos(pos);
        }
        wordnetLmfSaxParser.parseFile(pathToWordnetLmfFile);
        System.out.println("wordnetLmfSaxParser.wordnetData.getHyperRelations().size() = " + wordnetLmfSaxParser.wordnetData.getHyperRelations().size());
        System.out.println("wordnetLmfSaxParser.wordnetData.entryToSynsets.size() = " + wordnetLmfSaxParser.wordnetData.entryToSynsets.size());
        wordnetLmfSaxParser.wordnetData.buildSynsetIndex();

        if (format.equals("tagmap"))  {
            HashMap<String, ArrayList<String>> tagSynsetMap = new HashMap<String, ArrayList<String>>();
            //// transform tagMap to wordMap
        }
        else if (format.equals("tuplemap")) {
            ArrayList<WordData> wordDataMap = Util.readOverviewFileToArrayList(pathToInputFile);
            System.out.println("wordDataMap.size() = " + wordDataMap.size());
            int topFreq = 0;
            for (int i = 0; i < wordDataMap.size(); i++) {
                WordData wordData = wordDataMap.get(i);
                if (wordData.getFreq()>topFreq) {
                    topFreq = wordData.getFreq();
                }
            }
            for (int i = 0; i < wordDataMap.size(); i++) {
                WordData wordData = wordDataMap.get(i);
                if ((100*wordData.getFreq()/topFreq)>proportion) {
                    wordMap.add(wordData);
                }
            }
        }
        else if (format.equals("wordmap")) {
           wordMap = Util.readFileToWordDataList(pathToInputFile);
        }
        /// first build up the full tree data
        getFullTree(wordnetLmfSaxParser.wordnetData, wordMap);

        if (prune) {
            getTreePrunedForCumulatedHypernymFrequency(wordnetLmfSaxParser.wordnetData, wordHyperMap);
        }
        try {
            FileOutputStream fosSpreadSheet = new FileOutputStream (pathToInputFile+"."+pos+".xls");
            FileOutputStream fos = new FileOutputStream (pathToInputFile+"."+pos+".bcs");
            System.out.println("hyperTree = " + hyperTree.size());
            String keystr = "";

            Date date = new Date(System.currentTimeMillis());

            String settingsString = "WordnetTools\n";
            settingsString += "Version\t"+version+"\n";
            settingsString += "Copyright\t"+copyright+"\n";
            SimpleDateFormat ft =
                    new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

            settingsString += "Date\t" + ft.format(date)+"\n";
            settingsString += "input file\t"+pathToInputFile+"\n";
            settingsString += "wordnet file\t"+pathToWordnetLmfFile+"\n";
            settingsString += "relations\t"+pathToRelationsFile+"\n";
            settingsString += "monosemous\t"+monosemous+"\n";
            settingsString += "prune\t"+ prune+"\n";
            settingsString += "first hypernym\t"+firsthypernym+"\n";
            settingsString += "input format\t"+format+"\n";
            settingsString += "part of speech\t"+pos+"\n";
            settingsString += "proportion\t"+proportion+"\n";
            settingsString += "\n";

            keystr += settingsString;

            keystr += "\nOVERVIEW OF HYPERNYMS\n";

            keystr += SynsetNode.toCsvHeader();


            Set keyHyperSet = hyperTree.keySet();
            Iterator hpers = keyHyperSet.iterator();
            while(hpers.hasNext()) {
                String hper = (String) hpers.next();
                SynsetNode synsetNode = hyperTree.get(hper);
                //keystr += hper+"\t"+synsetNode.getnDescendants()+"\t"+synsetNode.getDepth()+"\t"+(synsetNode.getnDescendants()*synsetNode.getDepth())+"\n";
                //keystr += synsetNode.toString();
                keystr += synsetNode.toCsv();
            }
            fosSpreadSheet.write(keystr.getBytes());
            fosSpreadSheet.close();

            //  System.out.println(keystr);
            System.out.println("topNodes = " + topNodes.size());

            keystr = settingsString;
            keystr += "\nTOP NODES\n";
            for (int i = 0; i < topNodes.size(); i++) {
                SynsetNode synsetNode = topNodes.get(i);
                keystr+= synsetNode.toCsv();
            }
            System.out.println(keystr);
            fos.write(keystr.getBytes());

            keystr = "\nHYPERNYM TREES\n";
            //  System.out.println(keystr);
            fos.write(keystr.getBytes());
            //System.out.println("children = " + children.size());
            // ArrayList<SynsetNode> topNodes = getTopNodes(hyperTree, children);
            //  System.out.println("topNodes.size() = " + topNodes.size());
            Util.writeTreeString(new ArrayList<String>(), hyperTree, topNodes, 0, fos);
            //  System.out.println("DONE");
            //  System.out.println("DONE ALL");

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
