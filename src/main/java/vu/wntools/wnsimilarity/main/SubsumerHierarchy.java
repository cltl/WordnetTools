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
    static String prune = "";
    static ArrayList<SynsetNode> topNodes = new ArrayList<SynsetNode>();
    static HashMap <String, SynsetNode> hyperTree = new HashMap<String, SynsetNode>();
    static ArrayList<WordData> wordMap = new ArrayList<WordData>();
    static HashMap<String, ArrayList<ArrayList<String>>> wordHyperMap = new HashMap<String, ArrayList<ArrayList<String>>>();


    public static void buildHyperTreeFromChains (WordnetData wordnetData, WordData wordData, ArrayList<ArrayList<String>> chains) {
        for (int c = 0; c < chains.size(); c++) {
            ArrayList<String> hyperChain = chains.get(c);
            String synsetId = hyperChain.get(0); /// only taking the first
            SynsetNode sNode = wordnetData.makeSynsetNode(wordData.getWord(),synsetId );
            if (hyperTree.containsKey(synsetId)) {
                /// it is there so we assume the rest of the tree is already built
                /// just update the frequency
                sNode = hyperTree.get(synsetId);
                sNode.addFreq(wordData.getFreq());
                hyperTree.put(synsetId, sNode);
            }
            else {
                /// the first is a new synset
                sNode.setFreq(wordData.getFreq());
                hyperTree.put(synsetId, sNode);
                SynsetNode hNode = null;
                int cumFreq = wordData.getFreq();
                /// iterate over the rest of the chain
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
                            /// do nothing
                        }
                        hyperTree.put(hyperSynsetId, hNode);
                    }
                    else {
                        hNode = wordnetData.makeSynsetNode(hyperSynsetId);
                        hNode.incrementDescendants();
                        hNode.addChildren(sNode);
                        hNode.addCum(cumFreq);
                        cumFreq = hNode.getCum();
                        hNode.setDepth(hyperChain.size()-j);
                        hyperTree.put(hyperSynsetId, hNode);
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

    public static void getFullTree (WordnetData wordnetData, ArrayList<WordData> wordMap) {
        /// WE BUILD A HYPERTREE BASED ON ALL MEANINGS OF A WORD AND WE PASS ON THE FREQUENCIES FROM THE LEAVES TO THE HYPERNYMS
        for (int i = 0; i < wordMap.size(); i++) {
            WordData wordData =  wordMap.get(i);
            //System.out.println("word = " + wordData.getWord());
            ArrayList<ArrayList<String>> chains = new ArrayList<ArrayList<String>>();
            if (wordnetData.entryToSynsets.containsKey(wordData.getWord())) {
                ArrayList<String> synsetIds = wordnetData.entryToSynsets.get(wordData.getWord());
                if (!monosemous || synsetIds.size()==1) {
                    for (int s = 0; s < synsetIds.size(); s++) {
                        String synsetId =  synsetIds.get(s);
                        if (firsthypernym) {
                            ArrayList<String> singleHypers = new ArrayList<String>();
                            wordnetData.getSingleHyperChain(synsetId, singleHypers);
                            chains.add(singleHypers);
                        }
                        else {
                            wordnetData.getMultipleHyperChain(synsetId, chains);
                        }
                    }
                }
                else {
                    /// we ignore words with more than one meaning if monosemous flag is turned on
                }
            }

            buildHyperTreeFromChains(wordnetData, wordData, chains);
            /// In the next loop we store all the chains for a word sense or synset at the word level
            if (wordHyperMap.containsKey(wordData.getWord())) {
                ArrayList<ArrayList<String>> hyperchains = wordHyperMap.get(wordData.getWord());
                for (int j = 0; j < chains.size(); j++) {
                    ArrayList<String> hyperChain = chains.get(j);
                    hyperchains.add(hyperChain);
                }
                wordHyperMap.put(wordData.getWord(), hyperchains);
            }
            else {
                wordHyperMap.put(wordData.getWord(), chains);
            }

        }
        System.out.println("unpruned hyperTree = " + hyperTree.size());
    }


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
            ArrayList<Integer> topChildChain = new ArrayList<Integer>(); /// for storing the topFrequencies
            /// get all the chains stored for this word
            if (wordHyperMap.containsKey(wordData.getWord())) {
                ArrayList<ArrayList<String>> chains = wordHyperMap.get(wordData.getWord());
                for (int j = 0; j < chains.size(); j++) {
                    ArrayList<String> hyperChain = chains.get(j);
                    if (topChildChain.size()==0) {
                        /// this is the first chain so we assume it is the best so far
                        for (int l = 0; l < hyperChain.size(); l++) {
                            String s1 =  hyperChain.get(l);
                            if (hyperTree.containsKey(s1)) {
                                SynsetNode synsetNode1 = hyperTree.get(s1);
                                topChildChain.add(synsetNode1.getFreq());
                            }
                        }
                        topChains = new ArrayList<ArrayList<String>>();
                        topChains.add(hyperChain);
                    }
                    else {
                        /// now we got competition
                        boolean completed = true;
                        for (int k = 0; k < hyperChain.size(); k++) {
                            String s = hyperChain.get(k);
                            if (hyperTree.containsKey(s)) {
                                SynsetNode synsetNode = hyperTree.get(s);
                                if (topChildChain.size() > k) {
                                    if (topChildChain.get(k) < synsetNode.getFreq()) {
                                        /// we found a better chain, with a lower higher match
                                        /// recreate the chain of top counts
                                        topChildChain = new ArrayList<Integer>();
                                        for (int l = 0; l < hyperChain.size(); l++) {
                                            String s1 =  hyperChain.get(l);
                                            if (hyperTree.containsKey(s1)) {
                                                SynsetNode synsetNode1 = hyperTree.get(s1);
                                                topChildChain.add(synsetNode1.getFreq());
                                            }
                                        }
                                        topChains = new ArrayList<ArrayList<String>>();
                                        topChains.add(hyperChain);
                                        completed = false;
                                        break;
                                    }
                                    else if (topChildChain.get(k) > synsetNode.getFreq()) {
                                        /// the one we have is better so we can break and try the next chain if any
                                        completed = false;
                                        break;
                                    }
                                    else {
                                        /// we do nothing if equal and move to the next level
                                    }
                                }
                                else {
                                    //// if equal or smaller this means we do not have a winner
                                    // and the chain is longer
                                    // in this case we consider equal strength and add the top-chain
                                }
                            }
                            else {
                                //// we do nothing
                            }
                        }
                        if (completed) {
                            //// this means this chain is as good as the top chain
                            topChains.add(hyperChain);
                        }
                    }
                }
                //// replace the map with the topChains
                wordHyperMap.put(wordData.getWord(), topChains);
            }
        }
            /// rebuild the hyperTree based on the pruned chains based on most frequent lowest subsumers
            /// reinitialize the static repositories
            topNodes = new ArrayList<SynsetNode>();
            hyperTree = new HashMap<String, SynsetNode>();
            for (int i = 0; i < wordMap.size(); i++) {
                WordData wordData = wordMap.get(i);
                if (wordHyperMap.containsKey(wordData.getWord())) {
                    //// we get the topchains only and not the full set of chains
                    ArrayList<ArrayList<String>> chains = wordHyperMap.get(wordData.getWord());
                    buildHyperTreeFromChains(wordnetData, wordData, chains);
                }
            }
            System.out.println("pruned hyperTree = " + hyperTree.size());
    }
    /**
     * If words are polysemous we keep the sense with most children and prune the rest
     * @param wordnetData
     * @param wordHyperMap
     */
    public static void getTreePrunedForCumulatedHypernymCumulatedFreq (WordnetData wordnetData, HashMap<String, ArrayList<ArrayList<String>>> wordHyperMap) {

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
            ArrayList<Integer> topChildChain = new ArrayList<Integer>(); /// for storing the topFrequencies
            /// get all the chains stored for this word
            if (wordHyperMap.containsKey(wordData.getWord())) {
                ArrayList<ArrayList<String>> chains = wordHyperMap.get(wordData.getWord());
                for (int j = 0; j < chains.size(); j++) {
                    ArrayList<String> hyperChain = chains.get(j);
                    if (topChildChain.size()==0) {
                        /// this is the first chain so we assume it is the best so far
                        for (int l = 0; l < hyperChain.size(); l++) {
                            String s1 =  hyperChain.get(l);
                            if (hyperTree.containsKey(s1)) {
                                SynsetNode synsetNode1 = hyperTree.get(s1);
                                topChildChain.add(synsetNode1.getCum());
                            }
                        }
                        topChains = new ArrayList<ArrayList<String>>();
                        topChains.add(hyperChain);
                    }
                    else {
                        /// now we got competition
                        boolean completed = true;
                        for (int k = 0; k < hyperChain.size(); k++) {
                            String s = hyperChain.get(k);
                            if (hyperTree.containsKey(s)) {
                                SynsetNode synsetNode = hyperTree.get(s);
                                if (topChildChain.size() > k) {
                                    if (topChildChain.get(k) < synsetNode.getCum()) {
                                        /// we found a better chain, with a lower higher match
                                        /// recreate the chain of top counts
                                        topChildChain = new ArrayList<Integer>();
                                        for (int l = 0; l < hyperChain.size(); l++) {
                                            String s1 =  hyperChain.get(l);
                                            if (hyperTree.containsKey(s1)) {
                                                SynsetNode synsetNode1 = hyperTree.get(s1);
                                                topChildChain.add(synsetNode1.getCum());
                                            }
                                        }
                                        topChains = new ArrayList<ArrayList<String>>();
                                        topChains.add(hyperChain);
                                        completed = false;
                                        break;
                                    }
                                    else if (topChildChain.get(k) > synsetNode.getCum()) {
                                        /// the one we have is better so we can break and try the next chain if any
                                        completed = false;
                                        break;
                                    }
                                    else {
                                        /// we do nothing if equal and move to the next level
                                    }
                                }
                                else {
                                    //// if equal or smaller this means we do not have a winner
                                    // and the chain is longer
                                    // in this case we consider equal strength and add the top-chain
                                }
                            }
                            else {
                                //// we do nothing
                            }
                        }
                        if (completed) {
                            //// this means this chain is as good as the top chain
                            topChains.add(hyperChain);
                        }
                    }
                }
                //// replace the map with the topChains
                wordHyperMap.put(wordData.getWord(), topChains);
            }
        }
            /// rebuild the hyperTree based on the pruned chains based on most frequent lowest subsumers
            /// reinitialize the static repositories
            topNodes = new ArrayList<SynsetNode>();
            hyperTree = new HashMap<String, SynsetNode>();
            for (int i = 0; i < wordMap.size(); i++) {
                WordData wordData = wordMap.get(i);
                if (wordHyperMap.containsKey(wordData.getWord())) {
                    //// we get the topchains only and not the full set of chains
                    ArrayList<ArrayList<String>> chains = wordHyperMap.get(wordData.getWord());
                    buildHyperTreeFromChains(wordnetData, wordData, chains);
                }
            }
            System.out.println("pruned hyperTree = " + hyperTree.size());
    }

    /**
     * If words are polysemous we keep the sense with most children and prune the rest
     * @param wordnetData
     * @param wordHyperMap
     */
    public static void getTreePrunedForCumulatedHypernymChildren (WordnetData wordnetData, HashMap<String, ArrayList<ArrayList<String>>> wordHyperMap) {

            /**
             * We select those chains that have most specific hypernym with most children if there is a choice between different senses
             * We build a chain of the topFrequency at each level starting from the leaves.
             * If a new chain has a higher frequency than stored so far, we declare it as a new top chain and erase all top chains so far
             * If chains are equal then they are added to the top set.
             * Longer chains just add frequency to the frequency chain but this has no effect on the top chains
             */

            for (int i = 0; i < wordMap.size(); i++) {
                WordData wordData = wordMap.get(i);
                ArrayList<ArrayList<String>> topChains = new ArrayList<ArrayList<String>>();
                ArrayList<Integer> topChildChain = new ArrayList<Integer>(); /// for storing the topFrequencies
                /// get all the chains stored for this word
                if (wordHyperMap.containsKey(wordData.getWord())) {
                    ArrayList<ArrayList<String>> chains = wordHyperMap.get(wordData.getWord());
                    for (int j = 0; j < chains.size(); j++) {
                        ArrayList<String> hyperChain = chains.get(j);
                        if (topChildChain.size()==0) {
                            /// this is the first chain so we assume it is the best so far
                            for (int l = 0; l < hyperChain.size(); l++) {
                                String s1 =  hyperChain.get(l);
                                if (hyperTree.containsKey(s1)) {
                                    SynsetNode synsetNode1 = hyperTree.get(s1);
                                    topChildChain.add(synsetNode1.getChildren().size());
                                }
                            }
                            topChains = new ArrayList<ArrayList<String>>();
                            topChains.add(hyperChain);
                        }
                        else {
                            /// now we got competition
                            boolean completed = true;
                            for (int k = 0; k < hyperChain.size(); k++) {
                                String s = hyperChain.get(k);
                                if (hyperTree.containsKey(s)) {
                                    SynsetNode synsetNode = hyperTree.get(s);
                                    if (topChildChain.size() > k) {
                                        if (topChildChain.get(k) < synsetNode.getChildren().size()) {
                                            /// we found a better chain, with a lower higher match
                                            /// recreate the chain of top counts
                                            topChildChain = new ArrayList<Integer>();
                                            for (int l = 0; l < hyperChain.size(); l++) {
                                                String s1 =  hyperChain.get(l);
                                                if (hyperTree.containsKey(s1)) {
                                                    SynsetNode synsetNode1 = hyperTree.get(s1);
                                                    topChildChain.add(synsetNode1.getChildren().size());
                                                }
                                            }
                                            topChains = new ArrayList<ArrayList<String>>();
                                            topChains.add(hyperChain);
                                            completed = false;
                                            break;
                                        }
                                        else if (topChildChain.get(k) > synsetNode.getChildren().size()) {
                                            /// the one we have is better so we can break and try the next chain if any
                                            completed = false;
                                            break;
                                        }
                                        else {
                                            /// we do nothing if equal and move to the next level
                                        }
                                    }
                                    else {
                                       //// if equal or smaller this means we do not have a winner
                                       // and the chain is longer
                                       // in this case we consider equal strength and add the top-chain
                                    }
                                }
                                else {
                                    //// we do nothing
                                }
                            }
                            if (completed) {
                               //// this means this chain is as good as the top chain
                                topChains.add(hyperChain);
                            }
                        }
                    }
                    //// replace the map with the topChains
                    wordHyperMap.put(wordData.getWord(), topChains);
                }
            }

            /// rebuild the hyperTree based on the pruned chains based on most frequent lowest subsumers
            /// reinitialize the static repositories
            topNodes = new ArrayList<SynsetNode>();
            hyperTree = new HashMap<String, SynsetNode>();
            for (int i = 0; i < wordMap.size(); i++) {
                WordData wordData = wordMap.get(i);
                if (wordHyperMap.containsKey(wordData.getWord())) {
                    //// we get the topchains only and not the full set of chains
                    ArrayList<ArrayList<String>> chains = wordHyperMap.get(wordData.getWord());
                    buildHyperTreeFromChains(wordnetData, wordData, chains);
                }
            }
            System.out.println("pruned hyperTree = " + hyperTree.size());
    }

    /**
     * If words are polysemous we keep the sense with most descendants and prune the rest
     * @param wordnetData
     * @param wordHyperMap
     */
    public static void getTreePrunedForCumulatedHypernymDescendants (WordnetData wordnetData, HashMap<String, ArrayList<ArrayList<String>>> wordHyperMap) {

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
            ArrayList<Integer> topChildChain = new ArrayList<Integer>(); /// for storing the topFrequencies
            /// get all the chains stored for this word
            if (wordHyperMap.containsKey(wordData.getWord())) {
                ArrayList<ArrayList<String>> chains = wordHyperMap.get(wordData.getWord());
                for (int j = 0; j < chains.size(); j++) {
                    ArrayList<String> hyperChain = chains.get(j);
                    if (topChildChain.size()==0) {
                        /// this is the first chain so we assume it is the best so far
                        for (int l = 0; l < hyperChain.size(); l++) {
                            String s1 =  hyperChain.get(l);
                            if (hyperTree.containsKey(s1)) {
                                SynsetNode synsetNode1 = hyperTree.get(s1);
                                topChildChain.add(synsetNode1.getnDescendants());
                            }
                        }
                        topChains = new ArrayList<ArrayList<String>>();
                        topChains.add(hyperChain);
                    }
                    else {
                        /// now we got competition
                        boolean completed = true;
                        for (int k = 0; k < hyperChain.size(); k++) {
                            String s = hyperChain.get(k);
                            if (hyperTree.containsKey(s)) {
                                SynsetNode synsetNode = hyperTree.get(s);
                                if (topChildChain.size() > k) {
                                    if (topChildChain.get(k) < synsetNode.getnDescendants()) {
                                        /// we found a better chain, with a lower higher match
                                        /// recreate the chain of top counts
                                        topChildChain = new ArrayList<Integer>();
                                        for (int l = 0; l < hyperChain.size(); l++) {
                                            String s1 =  hyperChain.get(l);
                                            if (hyperTree.containsKey(s1)) {
                                                SynsetNode synsetNode1 = hyperTree.get(s1);
                                                topChildChain.add(synsetNode1.getnDescendants());
                                            }
                                        }
                                        topChains = new ArrayList<ArrayList<String>>();
                                        topChains.add(hyperChain);
                                        completed = false;
                                        break;
                                    }
                                    else if (topChildChain.get(k) > synsetNode.getnDescendants()) {
                                        /// the one we have is better so we can break and try the next chain if any
                                        completed = false;
                                        break;
                                    }
                                    else {
                                        /// we do nothing if equal and move to the next level
                                    }
                                }
                                else {
                                    //// if equal or smaller this means we do not have a winner
                                    // and the chain is longer
                                    // in this case we consider equal strength and add the top-chain
                                }
                            }
                            else {
                                //// we do nothing
                            }
                        }
                        if (completed) {
                            //// this means this chain is as good as the top chain
                            topChains.add(hyperChain);
                        }
                    }
                }
                //// replace the map with the topChains
                wordHyperMap.put(wordData.getWord(), topChains);
            }
        }

            /// rebuild the hyperTree based on the pruned chains based on most frequent lowest subsumers
            /// reinitialize the static repositories
            topNodes = new ArrayList<SynsetNode>();
            hyperTree = new HashMap<String, SynsetNode>();
            for (int i = 0; i < wordMap.size(); i++) {
                WordData wordData = wordMap.get(i);
                if (wordHyperMap.containsKey(wordData.getWord())) {
                    //// we get the topchains only and not the full set of chains
                    ArrayList<ArrayList<String>> chains = wordHyperMap.get(wordData.getWord());
                    buildHyperTreeFromChains(wordnetData, wordData, chains);
                }
            }
            System.out.println("pruned hyperTree = " + hyperTree.size());
    }



    /**
     * Takes as input a HashMap with all synsets that have the same Tag
     * @param tagSynsetMap
     * @param fos
     */
    public static void getMostCommonSubsumersForTagMap (WordnetData wordnetData, HashMap<String, ArrayList<String>> tagSynsetMap, FileOutputStream fos) {
    }


    static final String usage = "" +
            "##### Create a subhierarchy of wordnet for a specific set of input words.\n" +
            "--wn-lmf\t\t<path to a wordnet file in wordn-lmf format> \n" +
            "--relations\t\t<path to a text file with the relations y=that should be used to build the hierarchy> \n" +
            "--input-file\t\t<path to the input file>\n" +
            "--prune\t\t<prunes the tree to lowest hypernyms scored for frequency(freq), descendants (descendant), cumulated freq (cumulation), or children (child)>\n"+
            "--format\t<format of the input file. Values are 'kybotmap' (overview file generated by kybots from kaf) or 'wordmap (with or without frequencies)'>\n" +
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
            else if (arg.equals("--prune") && (args.length-1>i)) {
                prune = args[i+1].trim();
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
        System.out.println("format = " + format);
        if (format.equals("kybotmap")) {
            ArrayList<WordData> wordDataMap = Util.readOverviewFileToArrayList(pathToInputFile);
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
        System.out.println("prune = " + prune);
        /// first build up the full tree data
        getFullTree(wordnetLmfSaxParser.wordnetData, wordMap);

        if (prune.equalsIgnoreCase("freq")) {
            getTreePrunedForCumulatedHypernymFrequency(wordnetLmfSaxParser.wordnetData, wordHyperMap);
        }
        else if (prune.equalsIgnoreCase("child")) {
            getTreePrunedForCumulatedHypernymChildren(wordnetLmfSaxParser.wordnetData, wordHyperMap);
        }
        else if (prune.equalsIgnoreCase("descendant")) {
            getTreePrunedForCumulatedHypernymDescendants(wordnetLmfSaxParser.wordnetData, wordHyperMap);
        }
        else if (prune.equalsIgnoreCase("cumulation")) {
            getTreePrunedForCumulatedHypernymCumulatedFreq(wordnetLmfSaxParser.wordnetData, wordHyperMap);
        }
        try {
            FileOutputStream fosSpreadSheet = new FileOutputStream (pathToInputFile+"."+pos+".prune="+prune+".xls");
            FileOutputStream fos = new FileOutputStream (pathToInputFile+"."+pos+".prune="+prune+".bcs");
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
            settingsString += "input words\t"+wordMap.size()+"\n";
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
                keystr += synsetNode.toCsv();
            }
            fosSpreadSheet.write(keystr.getBytes());
            fosSpreadSheet.close();

            keystr = settingsString;
            keystr += "\nTOP NODES\n";
            keystr += SynsetNode.toCsvHeader();
            for (int i = 0; i < topNodes.size(); i++) {
                SynsetNode synsetNode = topNodes.get(i);
                keystr+= synsetNode.toCsv();
            }
            fos.write(keystr.getBytes());

            keystr = "\nHYPERNYM TREES\n";
            fos.write(keystr.getBytes());
            Util.writeTreeString(new ArrayList<String>(), hyperTree, topNodes, 0, fos);

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
