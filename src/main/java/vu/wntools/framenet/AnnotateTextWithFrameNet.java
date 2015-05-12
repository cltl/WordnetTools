package vu.wntools.framenet;

import eu.kyotoproject.kaf.KafWordForm;
import vu.wntools.util.CatParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 17/04/15.
 */
public class AnnotateTextWithFrameNet {

    static public void main (String[] args) {
        ArrayList<String> framedWords = new ArrayList<String>();
        HashMap<String, ArrayList<String>> frameWords = new HashMap<String, ArrayList<String>>();
        HashMap<String, ArrayList<String>> coherenceWords = new HashMap<String, ArrayList<String>>();
        String pathToFrameNetLuFile = "";
        String pathToFrameNetFrameFile = "";
        String pathToTextFile = "";
        pathToFrameNetLuFile = "/Resources/FrameNet/fndata-1.5/luIndex.xml";
        pathToFrameNetFrameFile = "/Resources/FrameNet/fndata-1.5/frRelation.xml";
        pathToTextFile = "/Users/piek/Desktop/MasterLanguage/Materiaal_deel2/hobbit.txt";
        pathToTextFile = "/Users/piek/Desktop/MasterLanguage/Materiaal_deel2/CAT_XML_std-off_export_2015-04-16_17_19_01/hobbit.txt.xml";
        FrameNetFrameReader frameNetFrameReader = new FrameNetFrameReader();
        frameNetFrameReader.parseFile(pathToFrameNetFrameFile);
        FrameNetLuReader frameNetLuReader = new FrameNetLuReader();
        frameNetLuReader.parseFile(pathToFrameNetLuFile);
        String labeledText = "";
        CatParser catParser = new CatParser("ANNOTATION");
        catParser.parseFile(pathToTextFile);
        for (int i = 0; i < catParser.kafWordFormArrayList.size(); i++) {
            KafWordForm kafWordForm = catParser.kafWordFormArrayList.get(i);
            String word = kafWordForm.getWf();
            ArrayList<String>  frames = frameNetLuReader.getFramesForWord(word);
            labeledText += kafWordForm.getWf();
            if (frames.size()>0) {
                framedWords.add(word);
                frameWords.put(word, frames);
                labeledText += "[";
                for (int j = 0; j < frames.size(); j++) {
                    String frame = frames.get(j);
                    labeledText += frame+";";
                }
                labeledText += "]";
            }
            labeledText += " ";

        }
        System.out.println("labeledText = " + labeledText);
        for (int i = 0; i < framedWords.size(); i++) {
            String word = framedWords.get(i);
            for (int j = i+1; j < framedWords.size(); j++) {
                String otherFramedWord =  framedWords.get(j);
                if (!word.equalsIgnoreCase(otherFramedWord)) {
                    ArrayList<String> wordFrames = frameWords.get(word);
                    ArrayList<String> otherWordFrames = frameWords.get(otherFramedWord);
                    for (int k = 0; k < wordFrames.size(); k++) {
                        String frame = wordFrames.get(k);
                        for (int l = 0; l < otherWordFrames.size(); l++) {
                            String otherframe = otherWordFrames.get(l);
                            ArrayList<String> bridgingFrames = frameNetFrameReader.getBridgingFrames(frame, otherframe);
                            for (int m = 0; m < bridgingFrames.size(); m++) {
                                String bridge = bridgingFrames.get(m);
                                if (coherenceWords.containsKey(bridge)) {
                                    ArrayList<String> words = coherenceWords.get(bridge);
                                    if (!words.contains(word)) {
                                        words.add(word);
                                    }
                                    if (!words.contains(otherFramedWord)) {
                                        words.add(otherFramedWord);
                                    }
                                    coherenceWords.put(bridge, words);
                                } else {
                                    ArrayList<String> words = new ArrayList<String>();
                                    words.add(word);
                                    words.add(otherFramedWord);
                                    coherenceWords.put(bridge, words);
                                }
                            }
                        }
                    }
                }
            }
        }
        Set keySet = coherenceWords.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            ArrayList<String> words = coherenceWords.get(key);
            System.out.println("Coherence = " + key+words.toString());
        }
    }
}
