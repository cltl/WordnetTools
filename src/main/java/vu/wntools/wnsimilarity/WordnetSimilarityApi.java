package vu.wntools.wnsimilarity;

import vu.wntools.wnsimilarity.corpus.SubsumersFrequencies;
import vu.wntools.wnsimilarity.measures.*;
import vu.wntools.wordnet.WordnetData;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 8/14/12
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 *
 *
 * Main Interface Class
 *
 */
public class WordnetSimilarityApi {

    /**
     * This function takes an ArrayList of SimilarityPairs and returns the topscoring SimilarityPair
     * @param similarityPairArrayList
     * @return  SimilarityPair
     */
    static public SimilarityPair getTopScoringSimilarityPair (ArrayList<SimilarityPair> similarityPairArrayList) {
        SimilarityPair topScore = new SimilarityPair();
        for (int i = 0; i < similarityPairArrayList.size(); i++) {
            SimilarityPair similarityPair = similarityPairArrayList.get(i);
            if (similarityPair.getScore()>topScore.getScore()) {
                topScore = similarityPair;
            }
        }
        return topScore;
    }

    /**
     *  This function normalizes the scores of an ArrayList of SimilarityPairs relative to the highest score
     *  The normalized score is a proportion of the highest score
     * @param similarityPairArrayList
     */
    static public void normalizeSimilarityPairs (ArrayList<SimilarityPair> similarityPairArrayList) {
        SimilarityPair topScore = getTopScoringSimilarityPair(similarityPairArrayList);
        for (int i = 0; i < similarityPairArrayList.size(); i++) {
            SimilarityPair similarityPair = similarityPairArrayList.get(i);
            similarityPair.setNormalizedScore(topScore.getScore());
        }
    }

    /**
     * Takes a WordnetData, a file with the frequencies of subsumers, and a pair of words.
     * It returns an ArrayList with the cumulative similarity pairs for all the meanings of the word.
     * The method used is Resnik
     * @param wordnetData
     * @param subsumersFrequencies
     * @param word1
     * @param word2
     * @return ArrayList<SimilarityPair>
     */
    static public ArrayList<SimilarityPair> wordResnikSimilarity (WordnetData wordnetData,
                                                                            SubsumersFrequencies subsumersFrequencies,
                                                                            String word1,
                                                                            String word2) {

        ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();
        boolean knownWords = true;
        if (!wordnetData.entryToSynsets.containsKey(word1)) {
            System.out.println("Unknown word = " + word1);
            knownWords= false;
        }
        if (!wordnetData.entryToSynsets.containsKey(word2)) {
            System.out.println("Unknown word = " + word2);
            knownWords= false;
        }
        if (knownWords) {
            ArrayList<String> sources = wordnetData.entryToSynsets.get(word1);
            ArrayList<String> targets = wordnetData.entryToSynsets.get(word2);
            for (int i = 0; i < sources.size(); i++) {
                String sourceId = sources.get(i);
                for (int j = 0; j < targets.size(); j++) {
                    String targetId =  targets.get(j);
                    SimilarityPair similarityPair = new SimilarityPair();
                    similarityPair.setSourceId(sourceId);
                    similarityPair.setTargetId(targetId);
                    ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
                    ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
                    wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
                    wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
                    for (int k = 0; k < hyperChainsSource.size(); k++) {
                        ArrayList<String> hyperSource = hyperChainsSource.get(k);
                        for (int l = 0; l < hyperChainsTarget.size(); l++) {
                            ArrayList<String> hyperTarget = hyperChainsTarget.get(l);
                            double score = vu.wntools.wnsimilarity.measures.Resnik.GetDistance(subsumersFrequencies.data, subsumersFrequencies.nWords, hyperSource, hyperTarget);
                            //System.out.println("score = " + score);
                            //System.out.println("similarityPair = " + similarityPair.getScore());
                            if (score>similarityPair.getScore()) {
                                similarityPair.setScore(score);
                            }
                        }
                    }
                    similarityPairArrayList.add(similarityPair);
                }
            }
        }
        return similarityPairArrayList;
    }

    /**
     * Takes a path to a CDB Synset File (Cornetto XML export dump format), a file with the frequencies of subsumers, the number of words and a pair of synset IDs.
     * It returns the similarity pair for the synset pairs
     * The method used is Resnik
     * @param wordnetData
     * @param subsumersFrequencies
     * @param sourceId
     * @param targetId
     * @return SimilarityPair
     */
    static public SimilarityPair synsetResnikSimilarity (WordnetData wordnetData,
                                                         SubsumersFrequencies subsumersFrequencies,
                                                         String sourceId,String targetId) {

        SimilarityPair similarityPair = new SimilarityPair();
        similarityPair.setSourceId(sourceId);
        similarityPair.setTargetId(targetId);

        ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
        wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
        wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
        for (int i = 0; i < hyperChainsSource.size(); i++) {
            ArrayList<String> hyperSource = hyperChainsSource.get(i);
            for (int j = 0; j < hyperChainsTarget.size(); j++) {
                ArrayList<String> hyperTarget = hyperChainsTarget.get(j);
                double score = vu.wntools.wnsimilarity.measures.Resnik.GetDistance(subsumersFrequencies.data, subsumersFrequencies.nWords, hyperSource, hyperTarget);
                if (score>similarityPair.getScore()) {
                    similarityPair.setScore(score);
                }
            }
        }
        return similarityPair;
    }

    /////////////////////////////////////////

    /**
     * Takes a WordnetData, a file with the frequencies of subsumers, and a pair of words.
     * It returns an ArrayList with the cumulative similarity pairs for all the meanings of the word.
     * The method used is Jiang and Conrath
     * @param wordnetData
     * @param subsumersFrequencies
     * @param word1
     * @param word2
     * @return ArrayList<SimilarityPair>
     */
    static public ArrayList<SimilarityPair> wordJiangConrathSimilarity (WordnetData wordnetData,
                                                                            SubsumersFrequencies subsumersFrequencies,
                                                                            String word1,
                                                                            String word2) {

        ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();
        boolean knownWords = true;
        if (!wordnetData.entryToSynsets.containsKey(word1)) {
            System.out.println("Unknown word = " + word1);
            knownWords= false;
        }
        if (!wordnetData.entryToSynsets.containsKey(word2)) {
            System.out.println("Unknown word = " + word2);
            knownWords= false;
        }
        if (knownWords) {
            ArrayList<String> sources = wordnetData.entryToSynsets.get(word1);
            ArrayList<String> targets = wordnetData.entryToSynsets.get(word2);
            for (int i = 0; i < sources.size(); i++) {
                String sourceId = sources.get(i);
                for (int j = 0; j < targets.size(); j++) {
                    String targetId =  targets.get(j);
                    SimilarityPair similarityPair = new SimilarityPair();
                    similarityPair.setSourceId(sourceId);
                    similarityPair.setTargetId(targetId);

                    ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
                    ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
                    wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
                    wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
                    for (int k = 0; k < hyperChainsSource.size(); k++) {
                        ArrayList<String> hyperSource = hyperChainsSource.get(k);
                        for (int l = 0; l < hyperChainsTarget.size(); l++) {
                            ArrayList<String> hyperTarget = hyperChainsTarget.get(l);
                            double score = JiangConrath.GetDistance(subsumersFrequencies.data, subsumersFrequencies.nWords, hyperSource, hyperTarget);
                            if (score>similarityPair.getScore()) {
                                similarityPair.setScore(score);
                            }
                        }
                    }
                    similarityPairArrayList.add(similarityPair);
                }
            }
        }
        return similarityPairArrayList;
    }

    /**
     * Takes a path to a CDB Synset File (Cornetto XML export dump format), a file with the frequencies of subsumers, the number of words and a pair of synset IDs.
     * It returns the similarity pair for the synset pairs
     * The method used is Jiang and Conrath
     * @param wordnetData
     * @param subsumersFrequencies
     * @param sourceId
     * @param targetId
     * @return SimilarityPair
     */
    static public SimilarityPair synsetJiangConrathSimilarity (WordnetData wordnetData,
                                                         SubsumersFrequencies subsumersFrequencies,
                                                         String sourceId,String targetId) {

        SimilarityPair similarityPair = new SimilarityPair();
        similarityPair.setSourceId(sourceId);
        similarityPair.setTargetId(targetId);
        ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
        wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
        wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
        for (int i = 0; i < hyperChainsSource.size(); i++) {
            ArrayList<String> hyperSource = hyperChainsSource.get(i);
            for (int j = 0; j < hyperChainsTarget.size(); j++) {
                ArrayList<String> hyperTarget = hyperChainsTarget.get(j);
                double score = JiangConrath.GetDistance(subsumersFrequencies.data, subsumersFrequencies.nWords, hyperSource, hyperTarget);
                if (score>similarityPair.getScore()) {
                    similarityPair.setScore(score);
                }
            }
        }
        return similarityPair;
    }


    /**
     * Takes a WordnetData, a file with the frequencies of subsumers, and a pair of words.
     * It returns an ArrayList with the cumulative similarity pairs for all the meanings of the word.
     * The method used is Lin
     * @param wordnetData
     * @param subsumersFrequencies
     * @param word1
     * @param word2
     * @return ArrayList<SimilarityPair>
     */
    static public ArrayList<SimilarityPair> wordLinSimilarity (WordnetData wordnetData,
                                                                            SubsumersFrequencies subsumersFrequencies,
                                                                            String word1,
                                                                            String word2) {

        ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();
        boolean knownWords = true;
        if (!wordnetData.entryToSynsets.containsKey(word1)) {
            System.out.println("Unknown word = " + word1);
            knownWords= false;
        }
        if (!wordnetData.entryToSynsets.containsKey(word2)) {
            System.out.println("Unknown word = " + word2);
            knownWords= false;
        }
        if (knownWords) {
            ArrayList<String> sources = wordnetData.entryToSynsets.get(word1);
            ArrayList<String> targets = wordnetData.entryToSynsets.get(word2);
            for (int i = 0; i < sources.size(); i++) {
                String sourceId = sources.get(i);
                for (int j = 0; j < targets.size(); j++) {
                    String targetId =  targets.get(j);
                    SimilarityPair similarityPair = new SimilarityPair();
                    similarityPair.setSourceId(sourceId);
                    similarityPair.setTargetId(targetId);

                    ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
                    ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
                    wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
                    wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
                    for (int k = 0; k < hyperChainsSource.size(); k++) {
                        ArrayList<String> hyperSource = hyperChainsSource.get(k);
                        for (int l = 0; l < hyperChainsTarget.size(); l++) {
                            ArrayList<String> hyperTarget = hyperChainsTarget.get(l);
                            double score = Lin.GetDistance(subsumersFrequencies.data, subsumersFrequencies.nWords, hyperSource, hyperTarget);
                            if (score>similarityPair.getScore()) {
                                similarityPair.setScore(score);
                            }
                        }
                    }
                    similarityPairArrayList.add(similarityPair);
                }
            }
        }
        return similarityPairArrayList;
    }

    /**
     * Takes a path to a CDB Synset File (Cornetto XML export dump format), a file with the frequencies of subsumers, the number of words and a pair of synset IDs.
     * It returns the similarity pair for the synset pairs
     * The method used is Lin
     * @param wordnetData
     * @param subsumersFrequencies
     * @param sourceId
     * @param targetId
     * @return SimilarityPair
     */
    static public SimilarityPair synsetLinSimilarity (WordnetData wordnetData,
                                                         SubsumersFrequencies subsumersFrequencies,
                                                         String sourceId,String targetId) {

        SimilarityPair similarityPair = new SimilarityPair();
        similarityPair.setSourceId(sourceId);
        similarityPair.setTargetId(targetId);

        ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
        wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
        wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
        for (int i = 0; i < hyperChainsSource.size(); i++) {
            ArrayList<String> hyperSource = hyperChainsSource.get(i);
            for (int j = 0; j < hyperChainsTarget.size(); j++) {
                ArrayList<String> hyperTarget = hyperChainsTarget.get(j);
                double score = Lin.GetDistance(subsumersFrequencies.data, subsumersFrequencies.nWords, hyperSource, hyperTarget);
                if (score>similarityPair.getScore()) {
                    similarityPair.setScore(score);
                }
            }
        }
        return similarityPair;
    }



    /**
     * Takes a path to a CDB Synset File (Cornetto XML export dump format) and a pair of words.
     * It returns an ArrayList with the cumulative similarity pairs for all the meanings of the word.
     * The method used is LeacockChodorow
     * @param wordnetData
     * @param word1
     * @param word2
     * @return ArrayList<SimilarityPair>
     */
    static public ArrayList<SimilarityPair> wordLeacockChodorowSimilarity (WordnetData wordnetData,
                                                                                     int averageDepth,
                                                                                     String word1,
                                                                                     String word2) {

        ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();
        boolean knownWords = true;
        if (!wordnetData.entryToSynsets.containsKey(word1)) {
            System.out.println("Unknown word = " + word1);
            knownWords= false;
        }
        if (!wordnetData.entryToSynsets.containsKey(word2)) {
            System.out.println("Unknown word = " + word2);
            knownWords= false;
        }
        if (knownWords) {
            ArrayList<String> sources = wordnetData.entryToSynsets.get(word1);
            ArrayList<String> targets = wordnetData.entryToSynsets.get(word2);
            for (int i = 0; i < sources.size(); i++) {
                String sourceId = sources.get(i);
                for (int j = 0; j < targets.size(); j++) {
                    String targetId =  targets.get(j);
                    SimilarityPair similarityPair = new SimilarityPair();
                    similarityPair.setSourceId(sourceId);
                    similarityPair.setTargetId(targetId);

                    ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
                    ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
                    wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
                    wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
                    for (int k = 0; k < hyperChainsSource.size(); k++) {
                        ArrayList<String> hyperSource = hyperChainsSource.get(k);
                        for (int l = 0; l < hyperChainsTarget.size(); l++) {
                            ArrayList<String> hyperTarget = hyperChainsTarget.get(l);
                            double score = LeacockChodorow.GetDistance(averageDepth, hyperSource, hyperTarget);
                            if (score>similarityPair.getScore()) {
                                similarityPair.setScore(score);
                            }
                        }
                    }
                    similarityPairArrayList.add(similarityPair);
                }
            }
        }
        return similarityPairArrayList;
    }


    /**
     * Takes a path to a CDB Synset File (Cornetto XML export dump format) and a pair of words.
     * It returns an ArrayList with the cumulative similarity pairs for all the meanings of the word.
     * The method used is LeacockChodorow. Average depth is taken from the pair that is compared
     * @param wordnetData
     * @param word1
     * @param word2
     * @return
     */
    static public ArrayList<SimilarityPair> wordLeacockChodorowSimilarity (WordnetData wordnetData,
                                                                                     String word1,
                                                                                     String word2) {

      //  System.out.println("word1 = " + word1);
      //  System.out.println("word2 = " + word2);
        ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();
        boolean knownWords = true;
        if (!wordnetData.entryToSynsets.containsKey(word1)) {
            System.out.println("Unknown word = " + word1);
            knownWords= false;
        }
        if (!wordnetData.entryToSynsets.containsKey(word2)) {
            System.out.println("Unknown word = " + word2);
            knownWords= false;
        }
        if (knownWords) {
            ArrayList<String> sources = wordnetData.entryToSynsets.get(word1);
            ArrayList<String> targets = wordnetData.entryToSynsets.get(word2);
            for (int i = 0; i < sources.size(); i++) {
                String sourceId = sources.get(i);
                for (int j = 0; j < targets.size(); j++) {
                    String targetId =  targets.get(j);
                    SimilarityPair similarityPair = new SimilarityPair();
                    similarityPair.setSourceId(sourceId);
                    similarityPair.setTargetId(targetId);

                    ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
                    ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
                    //System.out.println("\n\nWord:"+word1);
                    wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
                    //System.out.println("\n\nWord:"+word2);
                    wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
                    for (int k = 0; k < hyperChainsSource.size(); k++) {
                        ArrayList<String> hyperSource = hyperChainsSource.get(k);
                        for (int l = 0; l < hyperChainsTarget.size(); l++) {
                            ArrayList<String> hyperTarget = hyperChainsTarget.get(l);
                            int D = (hyperSource.size()+hyperTarget.size())/2;
                            double score = LeacockChodorow.GetDistance(D, hyperSource, hyperTarget);
                            //System.out.println("L & C score = " + score);
                           // System.out.println("similarityPair = " + similarityPair.getScore());
                            if (score>similarityPair.getScore()) {
                                similarityPair.setScore(score);
                            }
                        }
                    }
                    similarityPairArrayList.add(similarityPair);
                }
            }
        }
        return similarityPairArrayList;
    }

    /**
     * Takes a path to a CDB Synset File (Cornetto XML export dump format)and a pair of synset IDs.
     * It returns the similarity pair for the synset pairs
     * The method used is Leacock and Chodorow
     * @param wordnetData
     * @param sourceId
     * @param targetId
     * @return SimilarityPair
     */
    static public SimilarityPair synsetLeacockChodorowSimilarity (WordnetData wordnetData,
                                                                              int averageDepth,
                                                                              String sourceId,
                                                                              String targetId) {

        SimilarityPair similarityPair = new SimilarityPair();
        similarityPair.setSourceId(sourceId);
        similarityPair.setTargetId(targetId);

        ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
        wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
        wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
        for (int k = 0; k < hyperChainsSource.size(); k++) {
            ArrayList<String> hyperSource = hyperChainsSource.get(k);
            for (int l = 0; l < hyperChainsTarget.size(); l++) {
                ArrayList<String> hyperTarget = hyperChainsTarget.get(l);
                double score = vu.wntools.wnsimilarity.measures.LeacockChodorow.GetDistance(averageDepth, hyperSource, hyperTarget);
                if (score>similarityPair.getScore()) {
                    similarityPair.setScore(score);
                }
            }
        }
        return similarityPair;
    }

    /**
     * Takes a path to a CDB Synset File (Cornetto XML export dump format)and a pair of synset IDs.
     * It returns the similarity pair for the synset pairs
     * The method used is Leacock and Chodorow
     * @param wordnetData
     * @param sourceId
     * @param targetId
     * @return SimilarityPair
     */
    static public SimilarityPair synsetLeacockChodorowSimilarity (WordnetData wordnetData,
                                                                              String sourceId,
                                                                              String targetId) {

        SimilarityPair similarityPair = new SimilarityPair();
        similarityPair.setSourceId(sourceId);
        similarityPair.setTargetId(targetId);

        ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
        wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
        wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
        for (int i = 0; i < hyperChainsSource.size(); i++) {
            ArrayList<String> hyperSource = hyperChainsSource.get(i);
            for (int j = 0; j < hyperChainsTarget.size(); j++) {
                ArrayList<String> hyperTarget = hyperChainsTarget.get(j);
                int D = 1+(hyperSource.size()+hyperTarget.size())/2;
                double score = vu.wntools.wnsimilarity.measures.LeacockChodorow.GetDistance(D, hyperSource, hyperTarget);
                if (score>similarityPair.getScore()) {
                    similarityPair.setScore(score);
                }
            }
        }

        return similarityPair;
    }

    /**
     * Takes a path to a CDB Synset File (Cornetto XML export dump format) and a pair of words.
     * It returns an ArrayList with the cumulative similarity pairs for all the meanings of the word.
     * The method used is Wu and Palmer.
     * @param wordnetData
     * @param word1
     * @param word2
     * @return
     */
    static public ArrayList<SimilarityPair> wordWuPalmerSimilarity (WordnetData wordnetData,
                                                                           String word1,
                                                                           String word2) {

        ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();
        boolean knownWords = true;
        if (!wordnetData.entryToSynsets.containsKey(word1)) {
            System.out.println("Unknown word = " + word1);
            knownWords= false;
        }
        if (!wordnetData.entryToSynsets.containsKey(word2)) {
            System.out.println("Unknown word = " + word2);
            knownWords= false;
        }
        if (knownWords) {
            ArrayList<String> sources = wordnetData.entryToSynsets.get(word1);
            ArrayList<String> targets = wordnetData.entryToSynsets.get(word2);
            for (int i = 0; i < sources.size(); i++) {
                String sourceId = sources.get(i);
                for (int j = 0; j < targets.size(); j++) {
                    String targetId =  targets.get(j);
                    SimilarityPair similarityPair = new SimilarityPair();
                    similarityPair.setSourceId(sourceId);
                    similarityPair.setTargetId(targetId);

                    ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
                    ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
                    wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
                    wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
                    for (int k = 0; k < hyperChainsSource.size(); k++) {
                        ArrayList<String> hyperSource = hyperChainsSource.get(k);
                        for (int l = 0; l < hyperChainsTarget.size(); l++) {
                            ArrayList<String> hyperTarget = hyperChainsTarget.get(l);
                            double score = WuPalmer.GetDistance(hyperSource, hyperTarget);
                            if (score>similarityPair.getScore()) {
                                similarityPair.setScore(score);
                            }
                        }
                    }
                //    System.out.println("final similarityPair = " + similarityPair.getScore());
                    similarityPairArrayList.add(similarityPair);
                }
            }
        }
        return similarityPairArrayList;
    }

    /**
     * Takes a path to a CDB Synset File (Cornetto XML export dump format)and a pair of synset IDs.
     * It returns the similarity pair for the synset pairs
     * The method used is Wu and Palmer
     * @param wordnetData
     * @param sourceId
     * @param targetId
     * @return SimilarityPair
     */
    static public SimilarityPair synsetWuPalmerSimilarity (WordnetData wordnetData,
                                                                              String sourceId,
                                                                              String targetId) {

        SimilarityPair similarityPair = new SimilarityPair();
        similarityPair.setSourceId(sourceId);
        similarityPair.setTargetId(targetId);

        ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
        wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
        wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
        for (int i = 0; i < hyperChainsSource.size(); i++) {
            ArrayList<String> hyperSource = hyperChainsSource.get(i);
            for (int j = 0; j < hyperChainsTarget.size(); j++) {
                ArrayList<String> hyperTarget = hyperChainsTarget.get(j);
                double score = WuPalmer.GetDistance(hyperSource, hyperTarget);
                if (score>similarityPair.getScore()) {
                    similarityPair.setScore(score);
                }
            }
        }
        return similarityPair;
    }

    /**
     * Takes a path to a CDB Synset File (Cornetto XML export dump format) and a pair of words.
     * It returns an ArrayList with the cumulative similarity pairs for all the meanings of the word.
     * The method used is Path.
     * @param wordnetData
     * @param word1
     * @param word2
     * @return
     */
    static public ArrayList<SimilarityPair> wordPathSimilarity (WordnetData wordnetData,
                                                                           String word1,
                                                                           String word2) {

        ArrayList<SimilarityPair> similarityPairArrayList = new ArrayList<SimilarityPair>();
        boolean knownWords = true;
        if (!wordnetData.entryToSynsets.containsKey(word1)) {
            System.out.println("Unknown word = " + word1);
            knownWords= false;
        }
        if (!wordnetData.entryToSynsets.containsKey(word2)) {
            System.out.println("Unknown word = " + word2);
            knownWords= false;
        }
        if (knownWords) {
            ArrayList<String> sources = wordnetData.entryToSynsets.get(word1);
            ArrayList<String> targets = wordnetData.entryToSynsets.get(word2);
            for (int i = 0; i < sources.size(); i++) {
                String sourceId = sources.get(i);
                for (int j = 0; j < targets.size(); j++) {
                    String targetId =  targets.get(j);
                    SimilarityPair similarityPair = new SimilarityPair();
                    similarityPair.setSourceId(sourceId);
                    similarityPair.setTargetId(targetId);

                    ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
                    ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
                    wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
                    wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
                    for (int k = 0; k < hyperChainsSource.size(); k++) {
                        ArrayList<String> hyperSource = hyperChainsSource.get(k);
                        for (int l = 0; l < hyperChainsTarget.size(); l++) {
                            ArrayList<String> hyperTarget = hyperChainsTarget.get(l);
                            double score = BaseLines.GetPath(hyperSource, hyperTarget);
                            if (score>similarityPair.getScore()) {
                                similarityPair.setScore(score);
                            }
                        }
                    }
                    similarityPairArrayList.add(similarityPair);
                }
            }
        }
        return similarityPairArrayList;
    }

    /**
     * Takes a path to a CDB Synset File (Cornetto XML export dump format)and a pair of synset IDs.
     * It returns the similarity pair for the synset pairs
     * The method used is Path
     * @param wordnetData
     * @param sourceId
     * @param targetId
     * @return SimilarityPair
     */
    static public SimilarityPair synsetPathSimilarity (WordnetData wordnetData,
                                                                              String sourceId,
                                                                              String targetId) {

        SimilarityPair similarityPair = new SimilarityPair();
        similarityPair.setSourceId(sourceId);
        similarityPair.setTargetId(targetId);

        ArrayList<ArrayList<String>> hyperChainsSource = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> hyperChainsTarget = new ArrayList<ArrayList<String>>();
        wordnetData.getMultipleHyperChain(sourceId, hyperChainsSource);
        wordnetData.getMultipleHyperChain(targetId, hyperChainsTarget);
        for (int i = 0; i < hyperChainsSource.size(); i++) {
            ArrayList<String> hyperSource = hyperChainsSource.get(i);
            for (int j = 0; j < hyperChainsTarget.size(); j++) {
                ArrayList<String> hyperTarget = hyperChainsTarget.get(j);
                double score = BaseLines.GetPath(hyperSource, hyperTarget);
                if (score>similarityPair.getScore()) {
                    similarityPair.setScore(score);
                }
            }
        }
        return similarityPair;
    }


}
