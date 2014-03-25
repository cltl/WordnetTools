package vu.wntools.wnsimilarity.main;

import vu.wntools.util.Util;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 11/12/13
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetStats {


    static public void main (String[] args) {
        String pathToWnLmfFile = "";
        String pathToRelationsFile = "";
        String pos = "";
        ArrayList<String> relations = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ((arg.equalsIgnoreCase("--wn-lmf")) && args.length>(i+1)) {
                pathToWnLmfFile = args[i+1];
            }

            else if (arg.equals("--relations") && (args.length-1>i)) {
                pathToRelationsFile = args[i+1].trim();
            }
            else if (arg.equals("--pos") && (args.length-1>i)) {
                pos = args[i+1].trim();
            }
        }
        if (!pathToWnLmfFile.isEmpty()) {
            WordnetLmfSaxParser wordnetLmfSaxParser = new WordnetLmfSaxParser();

            if (!pathToRelationsFile.isEmpty()) {
                relations = Util.readFileToArrayList(pathToRelationsFile);
            }
            wordnetLmfSaxParser.setRelations(relations);
            if (!pos.isEmpty()) {
                wordnetLmfSaxParser.setPos(pos);
                System.out.println("pos = " + pos);
            }
            wordnetLmfSaxParser.parseFile(pathToWnLmfFile);
            wordnetLmfSaxParser.wordnetData.buildSynsetIndex();
            System.out.println("wordnetLmfSaxParser.wordnetData.entryToSynsets.size() = " + wordnetLmfSaxParser.wordnetData.entryToSynsets.size());
            System.out.println("wordnetLmfSaxParser.wordnetData.synsetToEntries.size() = " + wordnetLmfSaxParser.wordnetData.synsetToEntries.size());
            System.out.println("wordnetLmfSaxParser.wordnetData.hyperRelations.size() = " + wordnetLmfSaxParser.wordnetData.hyperRelations.size());
            System.out.println("wordnetLmfSaxParser.wordnetData.otherRelations.size() = " + wordnetLmfSaxParser.wordnetData.otherRelations.size());
            HashMap<Integer, Integer> depthCount = new HashMap<Integer, Integer>();
            HashMap<Integer, Integer> hyperCount = new HashMap<Integer, Integer>();

            Set synsets = wordnetLmfSaxParser.wordnetData.hyperRelations.keySet();
            //Set entrySet = wordnetLmfSaxParser.wordnetData.entryToSynsets.keySet();
            Iterator keys = synsets.iterator();
            while(keys.hasNext()) {
                String key = (String) keys.next();
                int depth = wordnetLmfSaxParser.wordnetData.getAverageDepthBySynset(key);
                if (depthCount.containsKey(depth)) {
                    Integer cnt = depthCount.get(depth);
                    cnt++;
                    depthCount.put(depth, cnt);
                }
                else {
                    depthCount.put(depth, 1);
                }
                ArrayList<String> hypers = wordnetLmfSaxParser.wordnetData.hyperRelations.get(key);
                if (hyperCount.containsKey(hypers.size())) {
                    Integer cnt = hyperCount.get(hypers.size());
                    cnt++;
                    hyperCount.put(hypers.size(), cnt);
                }
                else {
                    hyperCount.put(hypers.size(), 1);
                }
            }
            System.out.println("TOPS");
            ArrayList<String> tops = wordnetLmfSaxParser.wordnetData.getTopNodes();
            for (int i = 0; i < tops.size(); i++) {
                String s = tops.get(i);
                System.out.println(s);
            }
            System.out.println();
            System.out.println("DEPTH");
            System.out.println(0+"\t"+tops.size());
            Set keySet = depthCount.keySet();
            keys = keySet.iterator();
            while (keys.hasNext()) {
                Integer key = (Integer) keys.next();
                Integer cnt = depthCount.get(key);
                System.out.println(key+"\t"+cnt);
            }
            System.out.println();
            System.out.println("HYPERS");
            keySet = hyperCount.keySet();
            keys = keySet.iterator();
            while (keys.hasNext()) {
                Integer key = (Integer) keys.next();
                Integer cnt = hyperCount.get(key);
                System.out.println(key+"\t"+cnt);
            }

        }

    }
}
