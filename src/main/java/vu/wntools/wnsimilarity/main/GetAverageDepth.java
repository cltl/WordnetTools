package vu.wntools.wnsimilarity.main;

import vu.wntools.wordnet.CdbSynSaxParser;
import vu.wntools.wordnet.PwnSaxParser;
import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 10/2/12
 * Time: 10:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetAverageDepth {

    static public void main (String[] args) {
            WordnetData wordnetData = new WordnetData();
            String pathToWordnetFile = "";
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
            }
            int aDpeth = wordnetData.getAverageDepthByWord();
            System.out.println(aDpeth);
    }
}
