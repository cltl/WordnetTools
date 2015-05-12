package vu.wntools.wnsimilarity.main;

import vu.wntools.wordnet.CdbSynSaxParser;
import vu.wntools.wordnet.PwnSaxParser;
import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by piek on 6/3/14.
 */
public class GetDepthForWords {

    static public int getAverageDepthForWord (WordnetData wordnetData, String word) {
        int averageDepth = -1;
        if (wordnetData.entryToSynsets.containsKey(word)) {
            ArrayList<String> synsetIds = wordnetData.entryToSynsets.get(word);
            for (int i = 0; i < synsetIds.size(); i++) {
                String s = synsetIds.get(i);
                int depth = wordnetData.getAverageDepthBySynset(s);
                averageDepth+= depth;
            }
            if (synsetIds.size()>0) {
                averageDepth = averageDepth / synsetIds.size();
            }
        }
        return averageDepth;
    }

    static public void main (String[] args) {
        WordnetData wordnetData = new WordnetData();
        String pathToWordnetFile = "/Tools/wordnet-tools.0.1/resources/cornetto2.1.lmf.xml";
        String posFilter = "";
        String pathToWordFile = "paard";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ((arg.equalsIgnoreCase("--pos")) && args.length>i) {
                posFilter = args[i+1];
                break;
            }
            else if ((arg.equalsIgnoreCase("--words")) && args.length>i) {
                pathToWordFile = args[i+1];
                break;
            }
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            //System.out.println("arg = " + arg);
            if ((arg.equalsIgnoreCase("--cdb-file")) && args.length>i) {
                pathToWordnetFile = args[i+1];
                CdbSynSaxParser parser = new CdbSynSaxParser();
                parser.parseFile(pathToWordnetFile);
                wordnetData = parser.wordnetData;
            }
            else if ((arg.equalsIgnoreCase("--lmf-file")) && args.length>i) {
                pathToWordnetFile = args[i+1];
                WordnetLmfSaxParser parser = new WordnetLmfSaxParser();
                if (!posFilter.isEmpty()) {
                    System.out.println("posFilter = " + posFilter);
                    parser.setPos(posFilter);
                }
                parser.parseFile(pathToWordnetFile);
                wordnetData = parser.wordnetData;
               // System.out.println("wordnetData.getHyperRelations().size() = " + wordnetData.getHyperRelations().size());
               // System.out.println("wordnetData.entryToSynsets.size() = " + wordnetData.entryToSynsets.size());
            }
            else if ((arg.equalsIgnoreCase("--gwg-file")) && args.length>i) {
                pathToWordnetFile = args[i+1];
                PwnSaxParser parser = new PwnSaxParser();
                parser.parseFile(pathToWordnetFile);
                wordnetData = parser.wordnetData;
            }
        }
        /*
        test
         */

      /*  WordnetLmfSaxParser parser = new WordnetLmfSaxParser();
        if (!posFilter.isEmpty()) {
            System.out.println("posFilter = " + posFilter);
            parser.setPos(posFilter);
        }
        parser.parseFile(pathToWordnetFile);
        wordnetData = parser.wordnetData;
        System.out.println("wordnetData.getHyperRelations().size() = " + wordnetData.getHyperRelations().size());
        System.out.println("wordnetData.entryToSynsets.size() = " + wordnetData.entryToSynsets.size());

        String str = "";

        if (wordnetData.entryToSynsets.containsKey(pathToWordFile)) {
            str = pathToWordFile;
            String synsetString = "";
            int averageDepth = 0;
            ArrayList<String> synsetIds = wordnetData.entryToSynsets.get(pathToWordFile);
            for (int i = 0; i < synsetIds.size(); i++) {
                String s = synsetIds.get(i);
                int depth = wordnetData.getAverageDepthBySynset(s);
                synsetString += "\t"+s+"\t"+depth;
                averageDepth+= depth;
            }
            averageDepth = averageDepth/synsetIds.size();
            str += "\t"+averageDepth+synsetString;
            System.out.println(str);
        }
        else {
            System.out.println(pathToWordFile+"\t"+"unknown word");
        }*/

        try {
            FileInputStream fis = new FileInputStream(pathToWordFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            String str = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (wordnetData.entryToSynsets.containsKey(inputLine)) {
                    str = inputLine;
                    String synsetString = "";
                    int averageDepth = 0;
                    ArrayList<String> synsetIds = wordnetData.entryToSynsets.get(inputLine);
                    for (int i = 0; i < synsetIds.size(); i++) {
                        String s = synsetIds.get(i);
                        int depth = wordnetData.getAverageDepthBySynset(s);
                        synsetString += "\t"+s+"\t"+depth;
                        averageDepth+= depth;
                    }
                    averageDepth = averageDepth/synsetIds.size();
                    str += "\t"+averageDepth+synsetString;
                    System.out.println(str);
                }
                else {
                    System.out.println(inputLine+"\t"+"unknown word");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
