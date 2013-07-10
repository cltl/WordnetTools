package vu.wntools.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: kyoto
 * Date: 12/21/10
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordnetOverlap {

    HashMap<String, ArrayList<String>> englishWnMap;
    HashMap<String, ArrayList<String>> otherWnMap;
    HashMap<String, ArrayList<String>> synsetEnglishWnMap;

    public WordnetOverlap (){
        init();
    }

    void init() {
        englishWnMap = new HashMap<String, ArrayList<String>>();
        otherWnMap = new HashMap<String, ArrayList<String>>();
        synsetEnglishWnMap = new HashMap<String, ArrayList<String>>();
    }

    public void readWordnetLexiconFile (String filePath, HashMap<String, ArrayList<String>> hashMap) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                   String [] fields = inputLine.split(" ");
                   if (fields.length>0) {
                       String lemma = fields[0];
                       ArrayList<String> synsets = new ArrayList<String>();
                       for (int i = 1; i < fields.length; i++) {
                           String field = fields[i];
                           if (field.endsWith(":0")) {
                               field = field.substring(0, field.length()-2);
                           }
                           synsets.add(field);
                       }
                       hashMap.put(lemma, synsets);
                   }
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void readWordnetLexiconFile (String filePath, HashMap<String, ArrayList<String>> wordMap, HashMap<String, ArrayList<String>> synsetMap) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                   String [] fields = inputLine.split(" ");
                   if (fields.length>0) {
                       String lemma = fields[0];
                       ArrayList<String> synsets = new ArrayList<String>();
                       for (int i = 1; i < fields.length; i++) {
                           String field = fields[i];
                           if (field.endsWith(":0")) {
                               field = field.substring(0, field.length()-2);
                           }
                           synsets.add(field);
                           if (synsetMap.containsKey(field)) {
                               ArrayList<String> words = synsetMap.get(field);
                               words.add(lemma);
                               synsetMap.put(field, words);
                           }
                           else {
                               ArrayList<String> words = new ArrayList<String>();
                               words.add(lemma);
                               synsetMap.put(field, words);
                           }
                       }
                       wordMap.put(lemma, synsets);
                   }
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public int getEditDistance (String w1, String w2) {
        int editDistance = w1.length();
        if (w1.equals(w2)) {
            editDistance = 0;
        }
        else {
            if (w1.length()==w2.length()) {
                for (int i = 0; i < w1.length(); i++) {
                    if (w1.charAt(i)==w2.charAt(i)) {
                      editDistance--;
                    }
                }
            }
            else if (w1.length()>w2.length()) {
                for (int i = 0; i < w1.length(); i++) {
                    if (w1.charAt(i)==w2.charAt(i)) {
                      editDistance--;
                    }
                }
            }
            else {
                editDistance = w2.length();
                for (int i = 0; i < w2.length(); i++) {
                    if (w2.charAt(i)==w1.charAt(i)) {
                      editDistance--;
                    }
                }
            }
        }
        if (w1.length()==editDistance) {
            editDistance=-1;
        }
        return editDistance;
    }



    static public int getEditDistance (String form1, String form2, int maxDistance) {
        int editDistance = 0;
        int k = 0;
        int i = 0;
        if (form1.length()<=form2.length()) {
            for (i=0;i<form1.length();i++) {
                if ((editDistance>maxDistance) ||
                    (k>=form2.length())){
                    return editDistance;
                }
                if (form1.charAt(i)!=form2.charAt(k)) {
                    k = findEqualChar(form1.charAt(i),form2, i);
                    if (k!=-1) {
                       editDistance += k-i;
                    }
                    else {
                       editDistance += form2.length()-i;
                        return editDistance;
                    }
                }
                k++;
            }
            editDistance += (form2.length()-k);
        }
        else {
            for (i=0;i<form2.length();i++) {
                if ((editDistance>maxDistance) ||
                    (k>=form1.length())){
                    return editDistance;
                }
                if (form2.charAt(i)!=form1.charAt(k)) {
                    k = findEqualChar(form2.charAt(i),form1, i);
                    if (k!=-1) {
                       editDistance += k-i;
                    }
                    else {
                       editDistance += form1.length()-i;
                       return editDistance;
                    }
                }
                k++;
            }
            editDistance += (form1.length()-k);
        }
        if (form1.length()==editDistance) {
            editDistance=-1;
        }
        return editDistance;
    }

    static public int findEqualChar (char aChar, String form2, int pos) {
        for (int i=pos;i<form2.length();i++) {
            if (aChar==form2.charAt(i)) {
           //     System.out.println("Found:"+aChar+" at:"+i+ " in:"+form2);
                return i;
            }
        }
        return -1;
    }



    public void compareForms (FileOutputStream fos) throws IOException {
        String str = "";
        boolean hasMappings = false;
        Set keySet = otherWnMap.keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (key.length()<3) {
                continue;
            }
            if (englishWnMap.containsKey(key)) {
                ArrayList<String> wn1Synsets = englishWnMap.get(key);
                ArrayList<String> wn2Synsets = otherWnMap.get(key);
                for (int i = 0; i < wn2Synsets.size(); i++) {
                    String s = wn2Synsets.get(i);
                    if (wn1Synsets.contains(s)) {
                        hasMappings = true;
                        System.out.println("s = " + s);
                        break;
                    }
                }
                if (!hasMappings) {
                    str = key+"\t"+wn2Synsets.size()+"\n";
                    fos.write(str.getBytes());
                }
                else {

                }
            }
        }
    }

    public int countDutchSynsets (ArrayList<String> wn2Synsets) {
        int cnt = 0;
        for (int i = 0; i < wn2Synsets.size(); i++) {
            String s = wn2Synsets.get(i);
            if (!s.startsWith("ENG")) {
               cnt++;
            }
        }
        return cnt;
    }

    public void compareSynsets (FileOutputStream fos) throws IOException {
        String str = "";
        int n0 = 0;
        int n1 = 0;
        int n2 = 0;
        int n3 = 0;
        int nNLwords = 0;
        Set keySet = otherWnMap.keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (key.length()<3) {
                continue;
            }
            ArrayList<String> wn2Synsets = otherWnMap.get(key);
            ArrayList<String> wordResults = new ArrayList<String>();
            for (int i = 0; i < wn2Synsets.size(); i++) {
                String s = wn2Synsets.get(i);
                if ((s.startsWith("ENG") && (s.endsWith("-n")))) {
                    if (synsetEnglishWnMap.containsKey(s)) {
                        ArrayList<String> words = synsetEnglishWnMap.get(s);
                        for (int j = 0; j < words.size(); j++) {
                            String word = words.get(j);
                          //  System.out.println("word = " + word);
                            int editDistance = getEditDistance(key, word, 3);
                          //  System.out.println("key = " + key+", word = "+word+", ed = "+editDistance);
                            if (editDistance==key.length()) {
                                continue;
                            }
                            if ((editDistance>-1) && (editDistance<=3)) {
                                String result = word+"\t"+editDistance+"\t"+s;
                                boolean hasWord = false;
                                for (int k = 0; k < wordResults.size(); k++) {
                                    String wordResult = wordResults.get(k);
                                    if (wordResult.startsWith(word+"\t")) {
                                        wordResult += "\t"+s;
                                        wordResults.remove(k);
                                        wordResults.add(k,wordResult);
                                      //  System.out.println("wordResult = " + wordResult);
                                        hasWord = true;
                                        break;
                                    }
                                }
                                if (!hasWord) {
                                    wordResults.add(result);
                                    if (editDistance==0) {
                                       n0++;
                                    }
                                    else if (editDistance==1) {
                                       n1++;
                                    }
                                    else if (editDistance==2) {
                                       n2++;
                                    }
                                    else if (editDistance==3) {
                                       n3++;
                                    }
                                }
                            }
                        }
                    }
                    else {

                    }
                }
            }

            if (wordResults.size()>0) {
                nNLwords++;
                for (int i = 0; i < wordResults.size(); i++) {
                    String s = wordResults.get(i);
                    str = key+"\t"+countDutchSynsets(wn2Synsets)+"\t"+s+"\n";
                    fos.write(str.getBytes());
                }
            }
        }
        System.out.println("nNLwords = " + nNLwords);
        System.out.println("n0 = " + n0);
        System.out.println("n1 = " + n1);
        System.out.println("n2 = " + n2);
        System.out.println("n3 = " + n3);
    }

    static public void main (String[] args) {
        String wnFile1 = args[0];
        String wnFile2 = args[1];
        WordnetOverlap wnOverlap = new WordnetOverlap();
        wnOverlap.readWordnetLexiconFile(wnFile1, wnOverlap.englishWnMap, wnOverlap.synsetEnglishWnMap);
        wnOverlap.readWordnetLexiconFile(wnFile2, wnOverlap.otherWnMap);
        try {
            FileOutputStream fos = new FileOutputStream("results1.txt");
            wnOverlap.compareForms(fos);
            fos.close();
            fos = new FileOutputStream("results2.txt");
            wnOverlap.compareSynsets(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
