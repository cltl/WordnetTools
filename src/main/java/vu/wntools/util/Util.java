package vu.wntools.util;

import vu.wntools.corpus.WordData;
import vu.wntools.wordnet.SynsetNode;
import vu.wntools.wordnet.WordnetData;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 9/13/13
 * Time: 7:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    static public void writeTreeString (ArrayList<String> covered, HashMap<String, SynsetNode> hyperTree, ArrayList<SynsetNode> topNodes, final int level, FileOutputStream fos) {
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


    static public void writeTreeString (WordnetData wordnetData, ArrayList<String> topNodes, int level, FileOutputStream fos) {
        String str = "";
        for (int i = 0; i < topNodes.size(); i++) {
            String hper = topNodes.get(i);
            str = "";
            if (wordnetData.childRelations.containsKey(hper)) {
                for (int j = 0; j < level; j++) {
                    str += "  ";

                }
                str += hper+"\n";
                //  System.out.println("str = " + str);
                try {
                    fos.write(str.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                ArrayList<String> children = wordnetData.childRelations.get(hper);
                if (children.size()>0) {
                    writeTreeString(wordnetData, children, level+1, fos);
                }
                else {
                    //     System.out.println("leaf node.getSynset() = " + node.getSynset());
                }
            }
        }
    }

    static public void writeTreeString (WordnetData wordnetData, String parent, int level, FileOutputStream fos, ArrayList<String> done) {
        String str = "";
        str = "";

        for (int j = 0; j < level; j++) {
            str += "  ";

        }
        str += "["+parent;
        if (wordnetData.synsetToEntries.containsKey(parent)) {
            ArrayList<String> entries = wordnetData.synsetToEntries.get(parent);
            for (int j = 0; j < entries.size(); j++) {
                str += ";"+ entries.get(j);
            }
        }

        str += "]\n";
        done.add(parent);
        //  System.out.println("str = " + str);
        try {
            fos.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (wordnetData.childRelations.containsKey(parent)) {
            ArrayList<String> children = wordnetData.childRelations.get(parent);
            if (children.size()>0) {
                for (int i = 0; i < children.size(); i++) {
                    String child =  children.get(i);
                    if (!done.contains(child)) {
                        writeTreeString(wordnetData, child, level+1, fos, done);
                    }
                }
            }
            else {
                //     System.out.println("leaf node.getSynset() = " + node.getSynset());
            }
        }
        else {
           // System.out.println("No relations found");
        }
    }



    static public ArrayList<String> readRelationsFile (String pathToRelationFile) {
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

    static public ArrayList<String> readFile (String pathToFile) {
        ArrayList<String> relations = new ArrayList<String>();
        if (!new File(pathToFile).exists())   {
            System.out.println("Cannot find pathToFile = " + pathToFile);
        }
        try {
            FileInputStream fis = new FileInputStream(pathToFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (!inputLine.trim().isEmpty()) {
                        relations.add(inputLine.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //To change body of catch statement use File | Settings | File Templates.
        }
        return relations;
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
                        // event:other:aanbieden:	3	participant:Fabrimetal:a1	1
                        // OR
                        // functie:matigen:	136	object:G:	21	object:S:	14	object:m:	14

                        key = keyFields[keyFields.length-1];
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
}
