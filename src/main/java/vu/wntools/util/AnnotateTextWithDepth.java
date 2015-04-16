package vu.wntools.util;

import eu.kyotoproject.kaf.KafWordForm;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.util.ArrayList;

/**
 * Created by piek on 16/04/15.
 */
public class AnnotateTextWithDepth {

    static public void main (String[] args) {
        int averageDepth = 0;
        String pathToWnLmfFile = "";
        String pathToTextFile = "";
//        pathToWnLmfFile = args[0];
//        pathToTextFile = args[1];
        pathToWnLmfFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.lmf.xml";
        pathToTextFile = "/Users/piek/Desktop/MasterLanguage/Materiaal_deel2/hobbit.txt";
        pathToTextFile = "/Users/piek/Desktop/MasterLanguage/Materiaal_deel2/CAT_XML_std-off_export_2015-04-16_17_19_01/hobbit.txt.xml";
        WordnetLmfSaxParser wordnetLmfSaxParser = new WordnetLmfSaxParser();
        wordnetLmfSaxParser.parseFile(pathToWnLmfFile);
        String labeledText = "";
        int nDepth = 0;
        CatParser catParser = new CatParser();
        catParser.parseFile(pathToTextFile);
        for (int i = 0; i < catParser.kafWordFormArrayList.size(); i++) {
            KafWordForm kafWordForm = catParser.kafWordFormArrayList.get(i);
            int depth = wordnetLmfSaxParser.wordnetData.getAverageDepthForWord(kafWordForm.getWf());
            if (depth>0) {
                nDepth++;
                averageDepth += depth;
                labeledText += kafWordForm.getWf() + "[" + depth + "]" + " ";
            }
            else {
                labeledText += kafWordForm.getWf() +" ";
            }

        }
/*
        ArrayList<String> words = Util.readTextToWordArray(pathToTextFile);
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i);
            int depth = wordnetLmfSaxParser.wordnetData.getAverageDepthForWord(word);
            if (depth>0) {
                nDepth++;
                averageDepth += depth;
                labeledText += word + "[" + depth + "]" + " ";
            }
            else {
                labeledText += word +" ";
            }
        }
*/
        labeledText += "\n"+"AverageDepth = "+averageDepth/nDepth+"\n";
        System.out.println("labeledText = " + labeledText);
    }


}
