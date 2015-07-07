package vu.wntools.util;

import vu.wntools.lmf.Gloss;
import vu.wntools.lmf.Synset;
import vu.wntools.lmf.SynsetRelation;
import vu.wntools.wordnet.WordnetLmfDataSaxParser;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by piek on 06/06/15.
 */
public class CompareGlosses {

    static final int TOP = 70;
    static final int BOTTOM = 30;
    static final int minwordLength =3;
    static final String GLOSSLANGUAGE = "en";

    static public void main (String[] args) {
        String pathToFile = "";
        String pathToNewFile = "";
        String pathToPwnFile = "";
        pathToFile = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf";
        WordnetLmfDataSaxParser parser = new WordnetLmfDataSaxParser();
        parser.parseFile(pathToFile);
        parser.wordnetData.buildParentToChildMap();
        System.out.println("synsets = " + parser.wordnetData.synsetMap.size());
        System.out.println("entries = " + parser.wordnetData.entryMap.size());
       // pathToNewFile = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf.new";
       // pathToNewFile = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/lemma-translation/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf.new.not-mapped";
        pathToNewFile = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/lemma-translation/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf.new.not-a-pwn-entry";
        ArrayList<String> newSynsetCandidates = readNewSynsets(pathToNewFile);

        pathToPwnFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.lmf.xml";
        WordnetLmfSaxParser wordnetSaxParser = new WordnetLmfSaxParser();
        wordnetSaxParser.parseFile(pathToPwnFile);
        wordnetSaxParser.wordnetData.buildLexicalUnitIndex();
        wordnetSaxParser.wordnetData.buildSynsetIndex();

        try {
            OutputStream fosTop = new FileOutputStream(pathToNewFile+".matchTop");
            OutputStream fosMiddle = new FileOutputStream(pathToNewFile+".matchMiddle");
            OutputStream fosBottom = new FileOutputStream(pathToNewFile+".matchBottom");
            OutputStream fosZero = new FileOutputStream(pathToNewFile+".matchZero");
            OutputStream fosOneWordGloss = new FileOutputStream(pathToNewFile+".matchOneWordGloss");
            OutputStream fosNoGloss = new FileOutputStream(pathToNewFile+".matchNoGloss");
            for (int i = 0; i < newSynsetCandidates.size(); i++) {
                String candidateId = newSynsetCandidates.get(i);
                //System.out.println("candidateId = " + candidateId);
/*
               if (!candidateId.equals("odwn-10-100752902-n")) {
                    continue;
                }
*/
                Synset candidateSynset = parser.wordnetData.synsetMap.get(candidateId);
                if (candidateSynset!=null) {
                    int nDefWords = 0;
                    for (int j = 0; j < candidateSynset.getDefinitions().size(); j++) {
                        Gloss gloss = candidateSynset.getDefinitions().get(j);
                        int nWords = getContentWords(gloss.getText()).size();
                        if (nWords>nDefWords) {
                            nDefWords = nWords;
                        }
                    }
                    if (nDefWords==0) {
                        String str = "NO DEF\t"+candidateSynset.toStringWithoutRelations(parser.wordnetData)+"\n";
                        fosNoGloss.write(str.getBytes());
                    }
                    else if (nDefWords==1) {
                        String str = "ONE WORD\t"+candidateSynset.toStringWithoutRelations(parser.wordnetData)+"\n";
                        fosOneWordGloss.write(str.getBytes());
                    }
                    else {
                        int bestMatch = 0;
                        String relatedStringTop = "";
                        String relatedStringMiddle = "";
                        String relatedStringBottom = "";
                        String relatedStringZero = "";
                        ArrayList<String> targetSynsets = parser.wordnetData.getCoCoHyponyms(candidateSynset);
                        for (int k = 0; k < targetSynsets.size(); k++) {
                            String targetId = targetSynsets.get(k);
                            if (targetId.startsWith("eng")) {
                                //// eng cannot match with a newCandidate identifier!!!!!
                                Synset childSynset = parser.wordnetData.synsetMap.get(targetId);
                                ArrayList<String> synonyms = wordnetSaxParser.wordnetData.getSynonyms(targetId);
                                int sim = averageGlossSimilarity(candidateSynset, childSynset);
                                if (sim > bestMatch) {
                                    bestMatch = sim;
                                }

                                if (sim >= TOP) {
                                    SynsetRelation synsetRelation = new SynsetRelation();
                                    synsetRelation.setTarget(targetId);
                                    synsetRelation.setRelType("SIMILAR-TO:" + sim);
                                    synsetRelation.setProvenance("gloss-sim");
                                    candidateSynset.addRelations(synsetRelation);
                                    relatedStringTop += "\t" + sim + "\t" + childSynset.toStringWithoutRelations(synonyms) + "\n";
                                } else if (sim > BOTTOM) {
                                    relatedStringMiddle += "\t" + sim + "\t" + childSynset.toStringWithoutRelations(synonyms) + "\n";
                                } else if (sim > 0) {
                                    relatedStringBottom += "\t" + sim + "\t" + childSynset.toStringWithoutRelations(synonyms) + "\n";
                                } else if (sim == 0) {
                                    relatedStringZero += "\t" + sim + "\t" + childSynset.toStringWithoutRelations(synonyms) + "\n";
                                }
                            }
                        }
                        if (bestMatch>=TOP) {
                            String str = bestMatch+"\t"+candidateSynset.toStringWithoutRelations(parser.wordnetData)+"\n";
                            str += relatedStringTop;
                            fosTop.write(str.getBytes());
                        }
                        else if (bestMatch>BOTTOM){
                            String str = bestMatch+"\t"+candidateSynset.toStringWithoutRelations(parser.wordnetData)+"\n";
                            str += relatedStringMiddle;
                            fosMiddle.write(str.getBytes());
                        }
                        else if (bestMatch>0){
                            String str = bestMatch+"\t"+candidateSynset.toStringWithoutRelations(parser.wordnetData)+"\n";
                            str += relatedStringBottom;
                            fosBottom.write(str.getBytes());
                        }
                        else if (bestMatch==0){
                            String str = bestMatch+"\t"+candidateSynset.toStringWithoutRelations(parser.wordnetData)+"\n";
                            //  str += relatedStringZero;
                            fosZero.write(str.getBytes());
                        }
                    }
                }
                else {
                    System.out.println("Cannot find the synset = " + candidateId);
                }
            }
            fosTop.close();
            fosMiddle.close();
            fosBottom.close();
            fosZero.close();
            fosOneWordGloss.close();
            fosNoGloss.close();
/*
            OutputStream fos2 = new FileOutputStream(pathToFile+".link");
            parser.wordnetData.serialize(fos2);
            fos2.close();
*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int averageGlossSimilarity(Synset synset1, Synset synset2) {
        int score = 0;
        int matchCounts = 0;
        for (int l = 0; l < synset1.getDefinitions().size(); l++) {
            Gloss gloss1 = synset1.getDefinitions().get(l);
            for (int i = 0; i < synset2.getDefinitions().size(); i++) {
                Gloss gloss2 =  synset2.getDefinitions().get(i);
                if (gloss1.getLanguage().equals(gloss2.getLanguage())) {
                    int thisScore = getDiceScore(gloss1.getText(), gloss2.getText());
                    score += thisScore;
                    matchCounts++;
                }
            }
        }
/*
        System.out.println("Sum score = " + score);
        System.out.println("matchCounts = " + matchCounts);
        System.out.println("synset1.getDefinitions() = " + synset1.getDefinitions().size());
        System.out.println("synset2.getDefinitions() = " + synset2.getDefinitions().size());
*/

        if (matchCounts>0) {
            score = score / matchCounts;
        }
       // System.out.println("Average score = " + score);
        return score;
    }

    static int maxGlossSimilarity(Synset synset1, Synset synset2) {
        int score = -2;
       // System.out.println("synset1 = " + synset1.getSynsetId());
        for (int l = 0; l < synset1.getDefinitions().size(); l++) {
            Gloss gloss1 = synset1.getDefinitions().get(l);
            if (gloss1.getLanguage().equals(GLOSSLANGUAGE)) {
                for (int i = 0; i < synset2.getDefinitions().size(); i++) {
                    Gloss gloss2 = synset2.getDefinitions().get(i);
                    if (gloss1.getLanguage().equals(gloss2.getLanguage())) {
                        int thisScore = getDiceScore(gloss1.getText(), gloss2.getText());
                        if (thisScore >= score) {
                            score = thisScore;
                        }
                    }
                }
            }
        }
        return score;
    }

    static int getDiceScore (String text1, String text2) {
        ArrayList<String> words1 = getContentWords(text1);
        ArrayList<String> words2 = getContentWords(text2);
        if (words1.size()>1 && words2.size()>1) {
            return getDiceScore(words1, words2);
        }
        else {
            return 0;
        }
    }


    static int getDiceScore (ArrayList<String> words1, ArrayList<String> words2) {
        int dice = 0;
        int matchCount = 0;

        for (int i = 0; i < words1.size(); i++) {
            String w1 = words1.get(i);
            for (int j = 0; j < words2.size(); j++) {
                String w2 = words2.get(j);
                if (w1.equalsIgnoreCase(w2)) {
                  matchCount++;
                    break;
                }
            }
        }
        int w1MatchScore = ((matchCount * 100) / words1.size());
        int w2MatchScore = ((matchCount * 100) / words2.size());
        dice = (w1MatchScore + w2MatchScore) / 2;
/*
        System.out.println("matchCount = " + matchCount);
        System.out.println("words1 = " + words1.size());
        System.out.println("words2 = " + words2.size());
        System.out.println("w1MatchScore = " + w1MatchScore);
        System.out.println("w2MatchScore = " + w2MatchScore);
        System.out.println("dice = " + dice);
        System.out.println("words1.toString() = " + words1.toString());
        System.out.println("words2.toString() = " + words2.toString());
*/
        return dice;
    }

    static ArrayList<String> getContentWords (String def) {
        ArrayList<String> words = new ArrayList<String>();
        String [] fields = def.split(" ");
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i].trim();
            if (field.length()>=minwordLength) {
                if (!field.equalsIgnoreCase("V.E.") &&
                    !field.equalsIgnoreCase("dat")  &&
                    !field.equalsIgnoreCase("that")  &&
                    !field.equalsIgnoreCase("which") &&
                    !field.equalsIgnoreCase("die")  &&
                    !field.equalsIgnoreCase("who")
                        )
                words.add(field);
            }
        }
        return words;
    }

    static ArrayList<String> readNewSynsets (String pathToInputFile) {
        ArrayList<String> synsetIds = new ArrayList<String>();
            try {
                FileInputStream fis = new FileInputStream(pathToInputFile);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader in = new BufferedReader(isr);
                String inputLine = "";
                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    if (inputLine.trim().length()>0) {
                        String[] fields = inputLine.split("#");
                        if (fields.length==1) {
                            fields = inputLine.split("=");
                        }
                        if (fields.length>1) {
                            synsetIds.add(fields[0].trim());
                        }
                    }
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return synsetIds;
    }
}
