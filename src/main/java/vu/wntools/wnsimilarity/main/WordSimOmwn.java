package vu.wntools.wnsimilarity.main;

import vu.wntools.wnsimilarity.WordnetSimilarityApi;
import vu.wntools.wnsimilarity.measures.SimilarityPair;
import vu.wntools.wordnet.OmwnReader;
import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.util.ArrayList;

public class WordSimOmwn {

    static public String match = "";
    static String testparameters = "--wnlmf /Code/vu/WordnetTools/resources/pwn/wneng-30.lmf.xml --omwn /Code/vu/WordnetTools/resources/cow/wn-data-cmn.tab " +
            "--wncol 0 --lemmacol 2 --target 龙葵属植物 --source 龙虎草 --prefix eng-30-";

    static public double getWordSimLC(WordnetData wordnetData, String source, String target) {

        SimilarityPair topPair = new SimilarityPair();
        ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();
        similarityPairArrayList = WordnetSimilarityApi.wordLeacockChodorowSimilarity(wordnetData, source, target);
        topPair = WordnetSimilarityApi.getTopScoringSimilarityPair(similarityPairArrayList);
        match = topPair.getMatch();

        return topPair.getScore();
    }

    static public void main (String[] args) {
        String wnLmfFilePath = "";
        String omwnFilePath = "";
        String source = "";
        String target = "";
        Integer wnid = -1;
        Integer lemma = -1;
        String synsetPrefix ="";

        if (args.length==0) {
            args = testparameters.split(" ");
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equalsIgnoreCase("--wnlmf") && args.length>(i+1)) {
                wnLmfFilePath = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--omwn") && args.length>(i+1)) {
                omwnFilePath = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--source") && args.length>(i+1)) {
                source = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--target") && args.length>(i+1)) {
                target = args[i+1];
            }
            else if (arg.equalsIgnoreCase("--wncol") && args.length>(i+1)) {
                wnid = Integer.parseInt(args[i+1]);
            }
            else if (arg.equalsIgnoreCase("--lemmacol") && args.length>(i+1)) {
                lemma = Integer.parseInt(args[i+1]);
            }
            else if (arg.equalsIgnoreCase("--prefix") && args.length>(i+1)) {
                synsetPrefix = args[i+1];
            }
        }
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            String value = args[i+1];
            System.out.println("arg = " + arg+ "; value = "+value);
            i++;
        }
        WordnetLmfSaxParser wordnetLmfDataSaxParser = new WordnetLmfSaxParser();
        wordnetLmfDataSaxParser.parseFile(wnLmfFilePath);
        wordnetLmfDataSaxParser.wordnetData.buildSynsetIndex();
        OmwnReader.readLexiconFile (omwnFilePath, wordnetLmfDataSaxParser.wordnetData, wnid.intValue(), lemma.intValue(), synsetPrefix);
        double score = getWordSimLC(wordnetLmfDataSaxParser.wordnetData, source, target);
        System.out.println(source + "\t" + target +"\t"+score);
    }

}
