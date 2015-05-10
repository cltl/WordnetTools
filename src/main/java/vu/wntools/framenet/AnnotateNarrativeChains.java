package vu.wntools.framenet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 09/05/15.
 */
public class AnnotateNarrativeChains {



    /**
     * Created by piek on 09/05/15.
     */
    public static class NarrativeChains {
        //Events: sell buy own acquire operate purchase spin_off build plan pay merge announce

        public HashMap<String, ArrayList<Integer>> chainMap;
        public HashMap<Integer, ArrayList<String>> verbMap;

        public NarrativeChains (String chainFilePath) {
            chainMap = new HashMap<String, ArrayList<Integer>>();
            verbMap = new HashMap<Integer, ArrayList<String>>();
            if (new File(chainFilePath).exists() ) {
                try {
                    FileInputStream fis = new FileInputStream(chainFilePath);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader in = new BufferedReader(isr);
                    String inputLine;
                    Integer chainId = 0;
                    while (in.ready()&&(inputLine = in.readLine()) != null) {
                        //System.out.println(inputLine);
                        if (inputLine.trim().startsWith("Events:")) {
                            chainId++;
                            ArrayList<String> verbsArrayList = new ArrayList<String>();
                            String [] verbs = inputLine.split(" ");
                            for (int i = 1; i < verbs.length; i++) { /// skipping Events:
                                String verb = verbs[i];
                                verbsArrayList.add(verb);
                                if (chainMap.containsKey(verb)) {
                                    ArrayList<Integer> chains = chainMap.get(verb);
                                    chains.add(chainId);
                                    chainMap.put(verb, chains);
                                }
                                else {
                                    ArrayList<Integer> chains = new ArrayList<Integer>();
                                    chains.add(chainId);
                                    chainMap.put(verb, chains);
                                }
                            }
                            verbMap.put(chainId, verbsArrayList);
                        }
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    static public void main (String[] args) {
        String chainpath = "/Users/piek/Desktop/storylines/narrativechains/schemas-size12.txt";
        NarrativeChains chains = new NarrativeChains(chainpath);
        String pathToFrameNetLuFile = "/Resources/FrameNet/fndata-1.5/luIndex.xml";
        String pathToFrameNetFrameFile = "/Resources/FrameNet/fndata-1.5/frRelation.xml";
        FrameNetFrameReader frameNetFrameReader = new FrameNetFrameReader();
        frameNetFrameReader.parseFile(pathToFrameNetFrameFile);
        FrameNetLuReader frameNetLuReader = new FrameNetLuReader();
        frameNetLuReader.parseFile(pathToFrameNetLuFile);
        String str = "";
        Set keySet = chains.verbMap.keySet();
        Iterator<Integer> keys = keySet.iterator();
        while (keys.hasNext()) {
            Integer key = keys.next();
            str = key.toString()+"\n";
            ArrayList<String> verbs = chains.verbMap.get(key);
            HashMap<String, Integer> frameCount = new HashMap<String, Integer>();
            for (int i = 0; i < verbs.size(); i++) {
                String verb = verbs.get(i);
                str += "\t"+verb;
                if (frameNetLuReader.lexicalUnitFrameMap.containsKey(verb)) {
                    ArrayList<String> frames = frameNetLuReader.lexicalUnitFrameMap.get(verb);
                    for (int j = 0; j < frames.size(); j++) {
                        String frame = frames.get(j);
                        if (frameCount.containsKey(frame)) {
                            Integer cnt = frameCount.get(frame);
                            cnt++;
                            frameCount.put(frame, cnt);
                        }
                        else {
                            frameCount.put(frame, 1);
                        }
                    }
                }
            }
            str += "\n";
            str += getTopFrames(frameCount)+"\n\n";
            System.out.println(str);
        }
    }

    static String getTopFrames (HashMap<String, Integer> map) {
        String str = "";
        int topCount = 0;
        Set keySet = map.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String frame = keys.next();
            Integer cnt = map.get(frame);
            if (cnt>topCount) {
                topCount = cnt;
            }
        }
        keys = keySet.iterator();
        while (keys.hasNext()) {
            String frame = keys.next();
            Integer cnt = map.get(frame);
            if (100*cnt/topCount>30) {
                str +="\t"+frame+":"+cnt;
            }
        }

        return str;
    }

}
