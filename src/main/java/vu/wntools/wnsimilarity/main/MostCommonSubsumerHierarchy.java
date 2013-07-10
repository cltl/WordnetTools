package vu.wntools.wnsimilarity.main;

import vu.wntools.corpus.WordData;
import vu.wntools.wordnet.SynsetNode;
import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 5/22/13
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class MostCommonSubsumerHierarchy {

    static boolean monosemous = false;
    static int proportion = 30;
    static String pos = "";
    static boolean firsthypernym = false;

    static void addToMap(HashMap<String, ArrayList<String>> tagSynsetMap, String tagValue, String synsetId) {
        if (tagSynsetMap.containsKey(tagValue)) {
            ArrayList<String> synsets = tagSynsetMap.get(tagValue);
            if (!synsets.contains(synsetId)) {
                synsets.add(synsetId);
                tagSynsetMap.put(tagValue, synsets);
            }
        }
        else {
            ArrayList<String> synsets = new ArrayList<String>();
            synsets.add(synsetId);
            tagSynsetMap.put(tagValue, synsets);
        }
    }
/*

    static TreeSet sortSynsetsOnFrequency (ArrayList<Triple> Triples) {
        TreeSet sorter = new TreeSet(
                new Comparator() {
                    public int compare(Object a, Object b) {
                        Triple itemA = (Triple) a;
                        Triple itemB = (Triple) b;
                        if (itemA.getRelation().compareTo(itemB.getRelation())>0) {
                            return -1;
                        }
                        else if (itemA.getRelation().equals(itemB.getRelation())) {
                            return -1;
                        }
                        else {
                            return 1;
                        }
                    }
                }
        );
        for (int i = 0; i < Triples.size(); i++) {
            Triple trp = Triples.get(i);
            sorter.add(trp);
        }
        return sorter;
    }
*/

    static public boolean hasSynsetNode (ArrayList<SynsetNode> list, SynsetNode synsetNode) {
        boolean hasIt = false;
        for (int i = 0; i < list.size(); i++) {
            SynsetNode node = list.get(i);
            if (node.getSynsetId().equals(synsetNode.getSynsetId())) {
                hasIt = true;
                break;
            }
        }
        return hasIt;
    }

    static ArrayList<SynsetNode> getTopNodes (HashMap<String, SynsetNode> hyperTree, ArrayList<String> childNodes) {
        ArrayList<SynsetNode> topNodes = new ArrayList<SynsetNode>();
        Set keyHyperSet = hyperTree.keySet();
        Iterator hpers = keyHyperSet.iterator();
        while(hpers.hasNext()) {
            String hperId = (String) hpers.next();
            SynsetNode hper = hyperTree.get(hperId);
            if (!childNodes.contains(hper.getSynsetId())) {
                System.out.println("top node hper = " + hper.getSynset());
                if (!hasSynsetNode(topNodes, hper)) {
                    topNodes.add(hper);
                }
            }
        }
        return topNodes;
    }


    /**
     * @deprecated
     * Assumes single hypernym tree or cuts it off
     * @param wordnetData
     * @param wordMap
     * @param fos
     */
    public static void getMostCommonWordSubsumerOld (WordnetData wordnetData, ArrayList<WordData> wordMap, FileOutputStream fos) {
        try {
            String keystr = "";
            SynsetNode orphanNode = new SynsetNode();
            orphanNode.setSynset("orphan");
            HashMap <String, SynsetNode> hyperTree = new HashMap<String, SynsetNode>();
            ArrayList<String> children = new ArrayList<String>();
            ArrayList<SynsetNode> topNodes = new ArrayList<SynsetNode>();
            for (int i = 0; i < wordMap.size(); i++) {
                WordData wordData =  wordMap.get(i);
                //System.out.println("word = " + word);
                if (wordnetData.entryToSynsets.containsKey(wordData.getWord())) {
                    ArrayList<String> synsetIds = wordnetData.entryToSynsets.get(wordData.getWord());
                    if (!monosemous || synsetIds.size()==1) {
                        for (int s = 0; s < synsetIds.size(); s++) {
                            String synsetId =  synsetIds.get(s);
                            //keystr = wordData.getWord()+"#"+synsetId+"\n";
                            //fos.write(keystr.getBytes());
                            SynsetNode sNode = wordnetData.makeSynsetNode(wordData.getWord(), synsetId);
                            if (hyperTree.containsKey(synsetId)) {
                                sNode = hyperTree.get(synsetId);
                                sNode.addFreq(wordData.getFreq());
                                hyperTree.put(synsetId, sNode);
                            }
                            else {
                                sNode.setFreq(wordData.getFreq());
                                hyperTree.put(synsetId, sNode);
                                SynsetNode hNode = null;
                                ArrayList<String> hypers = new ArrayList<String>();
                                if (firsthypernym) {
                                    wordnetData.getSingleHyperChain(synsetId, hypers);
                                }
                                else {
                                    wordnetData.getPlainHyperChain(synsetId, hypers);
                                }
                                if (hypers.size()==0) {
                                    topNodes.add(sNode);
                                }
                                else {
                                    int cumFreq = sNode.getFreq();
                                    for (int j = 0; j < hypers.size(); j++) {
                                        String hyperSynsetId = hypers.get(j);
                                        if (hyperTree.containsKey(hyperSynsetId)) {
                                            hNode = hyperTree.get(hyperSynsetId);
                                            hNode.incrementDescendants();
                                            hNode.addCum(cumFreq);
                                            cumFreq = hNode.getCum();
                                            if (!hasSynsetNode(hNode.getChildren(),sNode)) {
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
                                            hNode.setDepth(hypers.size()-j);
                                            hyperTree.put(hyperSynsetId, hNode);
                                            if (!children.contains(sNode.getSynsetId())) {
                                                children.add(sNode.getSynsetId());
                                            }
                                        }
                                        if (s == hypers.size()-1) {
                                            if (!hasSynsetNode(topNodes,hNode)) {
                                                topNodes.add(hNode);
                                            }
                                        }
                                        else {
                                            sNode = hNode;
                                        }
                                       // keystr = "["+hyperSynsetId+"]";
                                       // fos.write(keystr.getBytes());
                                    }
                                }
                            }
                          //  keystr = "\n";
                          //  fos.write(keystr.getBytes());
                        }
                    }
                }
                else {
                   // System.out.println("Cannot find the word = " + word);
                }
            }
            keystr = "\nOVERVIEW OF HYPERNYMS\n";
            keystr += "synset id\tsynset\tdescendants\tdepth\tvalue\tchildren\n";
            Set keyHyperSet = hyperTree.keySet();
            Iterator hpers = keyHyperSet.iterator();
            while(hpers.hasNext()) {
                String hper = (String) hpers.next();
                SynsetNode synsetNode = hyperTree.get(hper);
                //keystr += hper+"\t"+synsetNode.getnDescendants()+"\t"+synsetNode.getDepth()+"\t"+(synsetNode.getnDescendants()*synsetNode.getDepth())+"\n";
                //keystr += synsetNode.toString();
                keystr += synsetNode.toCsv();
            }
            fos.write(keystr.getBytes());
          //  System.out.println(keystr);

            keystr = "\nTOP NODES\n";
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
            writeTreeString(new ArrayList<String>(), hyperTree, topNodes, 0, fos);
          //  System.out.println("DONE");
          //  System.out.println("DONE ALL");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void getMostCommonWordSubsumer (WordnetData wordnetData, ArrayList<WordData> wordMap, FileOutputStream fos) {
        try {
            String keystr = "";
            SynsetNode orphanNode = new SynsetNode();
            orphanNode.setSynset("orphan");
            HashMap <String, SynsetNode> hyperTree = new HashMap<String, SynsetNode>();
            ArrayList<String> children = new ArrayList<String>();
            ArrayList<SynsetNode> topNodes = new ArrayList<SynsetNode>();
            for (int i = 0; i < wordMap.size(); i++) {
                WordData wordData =  wordMap.get(i);
                //System.out.println("word = " + word);
                if (wordnetData.entryToSynsets.containsKey(wordData.getWord())) {
                    ArrayList<String> synsetIds = wordnetData.entryToSynsets.get(wordData.getWord());
                    if (!monosemous || synsetIds.size()==1) {
                        for (int s = 0; s < synsetIds.size(); s++) {
                            String synsetId =  synsetIds.get(s);
                            SynsetNode sNode = wordnetData.makeSynsetNode(wordData.getWord(), synsetId);
                            if (hyperTree.containsKey(synsetId)) {
                                sNode = hyperTree.get(synsetId);
                                sNode.addFreq(wordData.getFreq());
                                hyperTree.put(synsetId, sNode);
                            }
                            else {
                                keystr = wordData.getWord()+"#"+synsetId+"\n";
                                fos.write(keystr.getBytes());
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
                                if (hypers.size()==0) {
                                  //  topNodes.add(sNode);
                                  // we should not add any orphan synsets
                                  keystr = "=> NO HYPERNYMS\n";
                                  fos.write(keystr.getBytes());
                                }
                                else {
                                    sNode.setFreq(wordData.getFreq());
                                    hyperTree.put(synsetId, sNode);
                                    for (int c = 0; c < hypers.size(); c++) {
                                        keystr = "=> ";
                                        fos.write(keystr.getBytes());
                                        ArrayList<String> hyperChain = hypers.get(c);
                                        int cumFreq = sNode.getFreq();
                                        for (int j = 0; j < hyperChain.size(); j++) {
                                            String hyperSynsetId = hyperChain.get(j);
                                            if (hyperTree.containsKey(hyperSynsetId)) {
                                                hNode = hyperTree.get(hyperSynsetId);
                                                hNode.incrementDescendants();
                                                hNode.addCum(cumFreq);
                                                cumFreq = hNode.getCum();
                                                if (!hasSynsetNode(hNode.getChildren(),sNode)) {
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
                                                hNode.setDepth(hypers.size()-j);
                                                hyperTree.put(hyperSynsetId, hNode);
                                                if (!children.contains(sNode.getSynsetId())) {
                                                    children.add(sNode.getSynsetId());
                                                }
                                            }
                                            // we have the last hyper in the the chain
                                            if (j == hyperChain.size()-1) {
                                                if (!hasSynsetNode(topNodes,hNode)) {
                                                    topNodes.add(hNode);
                                                }
                                            }
                                            else {
                                                sNode = hNode;
                                            }
                                            keystr = "["+hyperSynsetId+":"+hNode.getSynset()+"]";
                                            fos.write(keystr.getBytes());
                                        }
                                        keystr = "\n";
                                        fos.write(keystr.getBytes());
                                    }
                                }
                                keystr = "\n";
                                fos.write(keystr.getBytes());
                            }
                        }
                    }
                }
                else {
                   // System.out.println("Cannot find the word = " + word);
                }
            }
            keystr = "\nOVERVIEW OF HYPERNYMS\n";
            keystr += "synset id\tsynset\tdescendants\tdepth\tvalue\tchildren\n";
            Set keyHyperSet = hyperTree.keySet();
            Iterator hpers = keyHyperSet.iterator();
            while(hpers.hasNext()) {
                String hper = (String) hpers.next();
                SynsetNode synsetNode = hyperTree.get(hper);
                //keystr += hper+"\t"+synsetNode.getnDescendants()+"\t"+synsetNode.getDepth()+"\t"+(synsetNode.getnDescendants()*synsetNode.getDepth())+"\n";
                //keystr += synsetNode.toString();
                keystr += synsetNode.toCsv();
            }
            fos.write(keystr.getBytes());
          //  System.out.println(keystr);

            keystr = "\nTOP NODES\n";
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
            writeTreeString(new ArrayList<String>(), hyperTree, topNodes, 0, fos);
          //  System.out.println("DONE");
          //  System.out.println("DONE ALL");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    static void writeTreeString (ArrayList<String> covered, HashMap<String, SynsetNode> hyperTree, ArrayList<SynsetNode> topNodes, final int level, FileOutputStream fos) {
        String str = "";
        for (int i = 0; i < topNodes.size(); i++) {
            SynsetNode hper = topNodes.get(i);
           // System.out.println("hper = " + hper.getSynsetId());
            if (covered.contains(hper.getSynsetId())) {
                ///circular
            //    System.out.println("CIRCULAR");
            }
            else {
                str = "";
                if (hyperTree.containsKey(hper.getSynsetId())) {
                    SynsetNode node = hyperTree.get(hper.getSynsetId());
/*
                    if (node.getSynsetId().equals("nld-21-d_v-2645-v")) {
                        System.out.println("node.toString() = " + node.toString());
                        System.out.println("level = " + level);
                    }
                    if (node.getSynsetId().equals("nld-21-d_v-1370-v")) {
                        System.out.println("node.toString() = " + node.toString());
                        System.out.println("level = " + level);
                        for (int j = 0; j < node.getChildren().size(); j++) {
                            SynsetNode synsetNode = node.getChildren().get(j);
                            System.out.println("hasChild synsetNode.getSynset() = " + synsetNode.getSynset());
                        }
                    }
*/
                    for (int j = 0; j < level; j++) {
                        str += "  ";

                    }
                    str += node.getSynsetId()+":"+node.getSynset()+":"+node.getFreq()+":"+node.getCum()+"\n";
                    covered.add(hper.getSynsetId());
                    //  System.out.println("str = " + str);
                    try {
                        fos.write(str.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    ArrayList<SynsetNode> children = node.getChildren();
                    if (children.size()>0) {
                        int nextLevel = level+1;
                        writeTreeString(covered, hyperTree, children, nextLevel, fos);
                    }
                    else {
                        //     System.out.println("leaf node.getSynset() = " + node.getSynset());
                    }
                }
                else {
                    System.out.println("cannot find this hper = " + hper);
                }
            }
        }
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

    static public ArrayList<WordData> readFileToWordDataList (String file) {
        ArrayList<WordData> words = new ArrayList<WordData>();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                    WordData wordData = new WordData();
                    wordData.setWord(inputLine.trim());
                    words.add(wordData);
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;

    }

    static public ArrayList<String> readFileToArrayList (String file) {
        ArrayList<String> words = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                    words.add(inputLine.trim());
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;

    }

    static public ArrayList<WordData> readOverviewFileToArrayList (String file) {
        ArrayList<WordData> words = new ArrayList<WordData>();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                    String [] fields = inputLine.split("\t");
                    if (fields.length>1) {
                        String key = fields[0];
                        String [] keyFields = key.split(":");
                        key = keyFields[1];
                        if (!isStopWord(key)) {
                            int freq = Integer.parseInt(fields[1]);
                            int dispersion = 0;
                            if (fields.length>2) {
                                dispersion = (fields.length-2/2);
                            }
                            WordData wordData = new WordData();
                            wordData.setWord(key);
                            wordData.setFreq(freq);
                            wordData.setDispersion(dispersion);
                            words.add(wordData);
                        }
                    }
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;

    }

    static public ArrayList<String> makeFlatFileList(String inputPath, String filter) {
        ArrayList<String> acceptedFileList = new ArrayList<String>();
        File[] theFileList = null;
        File lF = new File(inputPath);
        if ((lF.canRead()) && lF.isDirectory()) {
            theFileList = lF.listFiles();
            for (int i = 0; i < theFileList.length; i++) {
                String newFilePath = theFileList[i].getAbsolutePath();
                if (theFileList[i].isFile()) {
                    if (newFilePath.endsWith(filter)) {
                        acceptedFileList.add(newFilePath);
                    }
                }
            }
        }
        return acceptedFileList;
    }

    static final String usage = "" +
            "##### Create a subhierarchy of wordnet for a specific set of input words.\n" +
            "--wn-lmf\t\t<path to a wordnet file in wordn-lmf format> \n" +
            "--relations\t\t<path to a text file with the relations y=that should be used to build the hierarchy> \n" +
            "--input-file\t\t<path to the input file>\n" +
            "--format\t<format of the input file. Values are 'tuplemap', 'wordmap' and 'tagmap'>\n" +
            "--pos\t<part-of-speech considered for the input words>\n" +
            "--proportion\t\t<OPTIONAL: proportional frequency threshold, relative to the most frequent word, only works for 'tuplemap' format>\n" +
            "--monosemous\t\t<OPTIONAL: only consider input words that are monosemous>\n" +
            "--first-hypernym\t\t<OPTIONAL: take the first hypernym in case there are multiple hypernyms>\n" +
            "\n";

    static public void main (String [] args) {
        //String pathToWordnetLmfFile = args[0];
        //String pathToInputFile = args[1];
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
            relations = readFileToArrayList(pathToRelationsFile);
        }
        wordnetLmfSaxParser.setRelations(relations);
        if (!pos.isEmpty()) {
            wordnetLmfSaxParser.setPos(pos);
        }
        wordnetLmfSaxParser.parseFile(pathToWordnetLmfFile);
        System.out.println("wordnetLmfSaxParser.wordnetData.getHyperRelations().size() = " + wordnetLmfSaxParser.wordnetData.getHyperRelations().size());
        System.out.println("wordnetLmfSaxParser.wordnetData.entryToSynsets.size() = " + wordnetLmfSaxParser.wordnetData.entryToSynsets.size());
        wordnetLmfSaxParser.wordnetData.buildSynsetIndex();
        try {
            FileOutputStream fos = new FileOutputStream (pathToInputFile+"."+pos+".mcs");

            if (format.equals("tagmap"))  {
                HashMap<String, ArrayList<String>> tagSynsetMap = new HashMap<String, ArrayList<String>>();
                getMostCommonSubsumersForTagMap(wordnetLmfSaxParser.wordnetData, tagSynsetMap, fos);
                fos.close();

            }
            else if (format.equals("tuplemap")) {
                ArrayList<WordData> wordMap = new ArrayList<WordData>();
                ArrayList<WordData> wordDataMap = readOverviewFileToArrayList(pathToInputFile);
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
                getMostCommonWordSubsumer(wordnetLmfSaxParser.wordnetData, wordMap, fos);
                fos.close();
            }
            else if (format.equals("wordmap")) {
                ArrayList<WordData> wordMap = readFileToWordDataList(pathToInputFile);
                getMostCommonWordSubsumer(wordnetLmfSaxParser.wordnetData, wordMap, fos);
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    static public boolean isStopWord (String word) {
        if (word.equals("zijn")) {
            return true;
        }
        if (word.equals("worden")) {
            return true;
        }
        if (word.equals("kunnen")) {
            return true;
        }
        if (word.equals("zaak")) {
            return true;
        }
        if (word.equals("pagina")) {
            return true;
        }
        if (word.equals("komen")) {
            return true;
        }
        if (word.equals("moeten")) {
            return true;
        }
        if (word.equals("zien")) {
            return true;
        }
        if (word.endsWith("gaan")) {
            return true;
        }
        if (word.endsWith("zitten")) {
            return true;
        }
        return false;
    }
}
