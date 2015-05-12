package vu.wntools.wnsimilarity.main;

import vu.wntools.util.Util;
import vu.wntools.wordnet.SynsetNode;
import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 9/13/13
 * Time: 7:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExpandWord {

    static public ArrayList<SynsetNode> getWordHypernyms (WordnetData wordnetData, String word) {
        ArrayList<SynsetNode> hypers = new ArrayList<SynsetNode>();
        if (wordnetData.entryToSynsets.containsKey(word)) {
            ArrayList<String> sources = wordnetData.entryToSynsets.get(word);
            for (int j = 0; j < sources.size(); j++) {
                String s = sources.get(j);
                ArrayList<ArrayList<String>> targetChains = new ArrayList<ArrayList<String>>();
                wordnetData.getMultipleHyperChain(s, targetChains );
                for (int k = 0; k < targetChains.size(); k++) {
                    ArrayList<String> strings = targetChains.get(k);
                    if (strings.size()>1) {
                        SynsetNode synsetNode = wordnetData.makeSynsetNode(strings.get(1));
                        hypers.add(synsetNode);
                    }
                }
            }
        }
        return hypers;

    }
    static public ArrayList<SynsetNode> getWordHypernymsMinimalDepth (WordnetData wordnetData, String word, int depth) {
            ArrayList<SynsetNode> hypers = new ArrayList<SynsetNode>();
            if (wordnetData.entryToSynsets.containsKey(word)) {
                ArrayList<String> sources = wordnetData.entryToSynsets.get(word);
                for (int j = 0; j < sources.size(); j++) {
                    String s = sources.get(j);
                    ArrayList<ArrayList<String>> targetChains = new ArrayList<ArrayList<String>>();
                    wordnetData.getMultipleHyperChain(s, targetChains );
                    for (int k = 0; k < targetChains.size(); k++) {
                        ArrayList<String> strings = targetChains.get(k);
                        if (strings.size()>depth) {
                            SynsetNode synsetNode = wordnetData.makeSynsetNode(strings.get(1));
                            hypers.add(synsetNode);
                        }
                    }
                }
            }
            return hypers;
        }

    static public void main (String[] args) {
        String pathToWordnetFile = "";
        //String pathToWordnetFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.lmf.xml";
        String pathToRelFile = "";
        //String pathToInputFile = "/Tools/wordnet-tools.0.1/input/expansion/smell.txt";
        //String pathToInputFile = "/Tools/wordnet-tools.0.1/input/expansion/huis.txt";
        //String pathToInputFile = "oorzaak";
        boolean UP = false;
        String pathToInputFile = "";
        String posFilter = "";
        WordnetData wordnetData = new WordnetData();
        ArrayList<String> relations = new ArrayList<String>();
        ArrayList<String> inputwords = new ArrayList<String>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ((arg.equalsIgnoreCase("--input")) && args.length>i) {
                pathToInputFile = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--wn-lmf")) && args.length>i) {
                pathToWordnetFile = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--up"))) {
                UP = true;
            }
            else if ((arg.equalsIgnoreCase("--relations")) && args.length>i) {
                pathToRelFile = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--pos")) && args.length>i) {
                posFilter = args[i+1];
            }

        }
        if (pathToInputFile.isEmpty()) {
            return;
        }
        else {
            if (new File(pathToInputFile).exists()) {
                inputwords = Util.readRelationsFile(pathToInputFile);
            }
            else {
                inputwords.add(pathToInputFile);
            }
        }
        if (!pathToRelFile.isEmpty()) {
            relations = Util.readRelationsFile(pathToRelFile);
        }
        if (!pathToWordnetFile.isEmpty()) {
            try {
                FileOutputStream fos = new FileOutputStream(pathToInputFile+".tree");
                WordnetLmfSaxParser parser = new WordnetLmfSaxParser();
                if (relations.size()>0) parser.setRelations( relations);
                if (!posFilter.isEmpty()) parser.setPos(posFilter);
                parser.parseFile(pathToWordnetFile);
                wordnetData = parser.wordnetData;
                wordnetData.buildSynsetIndex();
                if (!UP) {
                    wordnetData.buildChildRelationsFromids();
                    System.out.println("wordnetData entryToSynsets = " + wordnetData.entryToSynsets.size());
                    System.out.println("wordnetData child relations = " + wordnetData.childRelations.size());
                    for (int i = 0; i < inputwords.size(); i++) {
                        String word =  inputwords.get(i);
                        String str ="word = " + word+"\n";
                        System.out.println(word);
                        fos.write(str.getBytes());
                        if (wordnetData.entryToSynsets.containsKey(word)) {
                            ArrayList<String> sources = wordnetData.entryToSynsets.get(word);
                            if ((sources != null) && (sources.size() > 0)) {
                                for (int j = 0; j < sources.size(); j++) {
                                    String sourceId = sources.get(j);
                                    Util.writeTreeString(wordnetData, sourceId, 0, fos, new ArrayList<String>());
                                    str = "\n"; fos.write(str.getBytes());
                                    Util.writeWordString(wordnetData, sourceId, 0, fos, new ArrayList<String>());
                                    str = "\n";
                                    str = "\n"; fos.write(str.getBytes());
                                }
                            }
                        }
                        else {
                            System.out.println("Cannot find the word:"+word);
                        }
                    }
                }
                else {
                    for (int i = 0; i < inputwords.size(); i++) {
                        String word =  inputwords.get(i);
                        System.out.println("word = " + word);
                         if (wordnetData.entryToSynsets.containsKey(word)) {
                             ArrayList<String> sources = wordnetData.entryToSynsets.get(word);
                             for (int j = 0; j < sources.size(); j++) {
                                 String s = sources.get(j);
                                 ArrayList<ArrayList<String>> targetChains = new ArrayList<ArrayList<String>>();
                                 wordnetData.getMultipleHyperChain(s, targetChains );
                                 for (int k = 0; k < targetChains.size(); k++) {
                                     ArrayList<String> strings = targetChains.get(k);
                                     for (int l = 0; l < strings.size(); l++) {
                                         String tab = "";
                                         for (int m = 0; m < l; m++) {
                                            tab+=" ";
                                         }
                                         String s1 = strings.get(l);
                                         SynsetNode synsetNode = wordnetData.makeSynsetNode(s1);
                                         System.out.println(tab+synsetNode.toString());
                                     }
                                 }
                             }

                         }
                    }
                }
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

}
