package vu.wntools.wnsimilarity.main;

import vu.wntools.util.Util;
import vu.wntools.wordnet.CdbSynSaxParser;
import vu.wntools.wordnet.PwnSaxParser;
import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 10/18/13
 * Time: 5:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetAverageSynsetDepth {

    static public void main (String[] args) {
        WordnetData wordnetData = new WordnetData();
        String pathToWordnetFile = "";
        String pathToSynsetFile = "";
        String synset = "";
        String posFilter = "";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ((arg.equalsIgnoreCase("--pos")) && args.length>i) {
                posFilter = args[i+1];
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
                System.out.println("wordnetData.getHyperRelations().size() = " + wordnetData.getHyperRelations().size());
                System.out.println("wordnetData.entryToSynsets.size() = " + wordnetData.entryToSynsets.size());
            }
            else if ((arg.equalsIgnoreCase("--gwg-file")) && args.length>i) {
                pathToWordnetFile = args[i+1];
                PwnSaxParser parser = new PwnSaxParser();
                parser.parseFile(pathToWordnetFile);
                wordnetData = parser.wordnetData;
            }
            else if ((arg.equalsIgnoreCase("--synset-file")) && args.length>i) {
                pathToSynsetFile = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--synset")) && args.length>i) {
                synset = args[i+1];
            }
        }
        if (!pathToSynsetFile.isEmpty()) {
            ArrayList<String> synsets = Util.readFile(pathToSynsetFile);
            for (int i = 0; i < synsets.size(); i++) {
                String s = synsets.get(i);
                //System.out.println(s);
                int aDpeth = wordnetData.getAverageDepthBySynset(s);
                System.out.println(s+"\t"+aDpeth);
            }
        }
        else if (!synset.isEmpty()) {
                int aDpeth = wordnetData.getAverageDepthBySynset(synset);
                System.out.println(synset+"\t"+aDpeth);
        }
    }
}
