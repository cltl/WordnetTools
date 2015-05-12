package vu.wntools.framenet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 16/04/15.
 */
public class AnnotateConllWithFrameNet {

    static public void main (String[] args) {
        ArrayList<String> framedWords = new ArrayList<String>();
        HashMap<String, ArrayList<String>> frameWords = new HashMap<String, ArrayList<String>>();

        String pathToFrameNetLuFile = "";
        String pathToFrameNetFrameFile = "";
        String pathToConll = "";
//        pathToFrameNetLuFile = args[0];
//        pathToFrameNetFrameFile = args[0];
//        pathToTextFile = args[1];
        pathToFrameNetLuFile = "/Resources/FrameNet/fndata-1.5/luIndex.xml";
        pathToFrameNetFrameFile = "/Resources/FrameNet/fndata-1.5/frRelation.xml";
        pathToConll = "/Users/piek/Desktop/MasterLanguage/CAT_XML_std-off_export_2015-05-11_22_31_46/hobbit.txt.xml.csv";
        FrameNetFrameReader frameNetFrameReader = new FrameNetFrameReader();
        frameNetFrameReader.parseFile(pathToFrameNetFrameFile);
        FrameNetLuReader frameNetLuReader = new FrameNetLuReader();
        frameNetLuReader.parseFile(pathToFrameNetLuFile);
        try {
            FileInputStream fis = new FileInputStream(pathToConll);
            String outputFile = pathToConll;
            int idx = outputFile.lastIndexOf(".");
            if (idx>-1) {
                outputFile = outputFile.substring(0, idx)+".fn.csv";
            }
            else {
                outputFile += ".fn.csv";
            }
            OutputStream fos = new FileOutputStream(outputFile);
            System.out.println("outputFile = " + outputFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            if (in.ready()&&(inputLine = in.readLine()) != null) {
               inputLine += "\tFN-FRAME\tFN-RELATION"+"\n";
                fos.write(inputLine.getBytes());
            }
            ArrayList<String> lineArray = new ArrayList<String>();
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (!inputLine.trim().isEmpty()) {
                    String [] substrings = inputLine.split("\t");
                    String tokenId = substrings[0].trim();
                    String word = substrings[1].trim();
                    ArrayList<String> frames = frameNetLuReader.getFramesForWord(word);
                    if (frames.size()>0) {
                        framedWords.add(tokenId);
                        frameWords.put(tokenId, frames);
                        inputLine += "\t"+"[";
                        for (int j = 0; j < frames.size(); j++) {
                            String frame = frames.get(j);
                            inputLine += frame+";";
                        }
                        inputLine += "]";
                    }
                    // System.out.println("word = " + word);
                    else {
                        inputLine += "\t";
                    }
                    lineArray.add(inputLine);
                  //  fos.write(inputLine.getBytes());
                }
            }
            HashMap<String, ArrayList<String>> coherenceWords = getCoherenceRelations(framedWords,frameWords, frameNetFrameReader);
            for (int i = 0; i < lineArray.size(); i++) {
                String line = lineArray.get(i);
                String [] fields = line.split("\t");
                String tokenId = fields[0];
                boolean coherenceMatch = false;
                Set keySet = coherenceWords.keySet();
                Iterator<String> keys = keySet.iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    ArrayList<String> tokens = coherenceWords.get(key);
                    if (tokens.contains(tokenId)) {
                        line+="\t"+key+":"+tokens.toString();
                        coherenceMatch = true;
                        break;
                    }
                }
                if (!coherenceMatch) {
                    line+="\t";

                }
                line += "\n";
                fos.write(line.getBytes());
            }

            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            //To change body of catch statement use File | Settings | File Templates.
        }
    }


    static HashMap<String, ArrayList<String>> getCoherenceRelations (ArrayList<String> framedWords ,
                                        HashMap<String, ArrayList<String>> frameWords,
                                         FrameNetFrameReader frameNetFrameReader
    ) { HashMap<String, ArrayList<String>> coherenceWords = new HashMap<String, ArrayList<String>>();
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
        return coherenceWords;

    }

}
