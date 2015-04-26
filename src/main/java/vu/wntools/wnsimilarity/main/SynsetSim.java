package vu.wntools.wnsimilarity.main;

import vu.wntools.wnsimilarity.WordnetSimilarityApi;
import vu.wntools.wnsimilarity.measures.SimilarityPair;
import vu.wntools.wordnet.WordnetData;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 7/29/13
 * Time: 5:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class SynsetSim {

    static public String match = "";

    static public double getSynsetSimLC(WordnetData wordnetData, String source, String target) {

        SimilarityPair topPair = WordnetSimilarityApi.synsetLeacockChodorowSimilarity(wordnetData, source, target);
        match = topPair.getMatch();

        return topPair.getScore();
    }

}
