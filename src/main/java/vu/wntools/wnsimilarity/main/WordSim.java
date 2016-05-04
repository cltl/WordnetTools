package vu.wntools.wnsimilarity.main;

import vu.wntools.wnsimilarity.WordnetSimilarityApi;
import vu.wntools.wnsimilarity.measures.SimilarityPair;
import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 7/29/13
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordSim {

    static public String match = "";

    static public double getWordSimLC(WordnetData wordnetData, String source, String target) {

        SimilarityPair topPair = new SimilarityPair();
        ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();
        similarityPairArrayList = WordnetSimilarityApi.wordLeacockChodorowSimilarity(wordnetData, source, target);
        topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
        match = topPair.getMatch();

        return topPair.getScore();
    }

    static public void main (String[] args) {
        String wnFilePath = "";
        String source = "paard";
        String target = "ezel";
        wnFilePath = "/Code/vu/newsreader/vua-resources/odwn_orbn_gwg-LMF_1.3.xml.gz";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("--wn-lmf") && args.length>(i+1)) {
                wnFilePath = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--source") && args.length>(i+1)) {
                source = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--target") && args.length>(i+1)) {
                target = args[i+1];
            }
        }
        WordnetLmfSaxParser wordnetLmfDataSaxParser = new WordnetLmfSaxParser();
        wordnetLmfDataSaxParser.parseFile(wnFilePath);
        double score = getWordSimLC(wordnetLmfDataSaxParser.wordnetData, source, target);
        System.out.println(source + "\t" + target +"\t"+score);
    }
}
