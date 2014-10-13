package vu.wntools.wnsimilarity.main;

import vu.wntools.wnsimilarity.WordnetSimilarityApi;
import vu.wntools.wnsimilarity.measures.*;
import vu.wntools.wordnet.WordnetData;

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

/*    static public double getWordSimLC(WordnetData wordnetData, String source, String target) {

        SimilarityPair topPair = new SimilarityPair();
        ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();

        similarityPairArrayList = WordnetSimilarityApi.wordPathSimilarity(wordnetData, source, target);
        topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
        match = BaseLines.match;


        LeacockChodorow.match = "";
        similarityPairArrayList = WordnetSimilarityApi.wordLeacockChodorowSimilarity(wordnetData, source, target);
        topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
        match = LeacockChodorow.match;

        WuPalmer.match = "";
        similarityPairArrayList = WordnetSimilarityApi.wordWuPalmerSimilarity(wordnetData, source, target);
        topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
        match = WuPalmer.match;

        Resnik.match = "";
        similarityPairArrayList = WordnetSimilarityApi.wordResnikSimilarity(wordnetData, subsumersFrequencies, source, target);
        topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
        match = Resnik.match;

        vu.wntools.wnsimilarity.measures.Lin.match = "";
        similarityPairArrayList = WordnetSimilarityApi.wordLinSimilarity(wordnetData, subsumersFrequencies, source, target);
        topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
        match = Lin.match;


        JiangConrath.match = "";
        similarityPairArrayList = WordnetSimilarityApi.wordJiangConrathSimilarity(wordnetData, subsumersFrequencies, source, target);
        topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
        match = JiangConrath.match;
        return topPair.getScore();
    }*/
}
