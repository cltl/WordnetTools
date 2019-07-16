package vu.wntools.wordnet;

import java.io.*;
import java.util.ArrayList;

public class OmwnReader {


    static public void readLexiconFile (String filepath , WordnetData wordnetData, int id, int word, String prefix) {
        System.err.println("Before reading OMWN");
        System.err.println("synset.size() = " + wordnetData.synsetToEntries.size());
        System.err.println("lexicon.size() = " + wordnetData.entryToSynsets.size());

        File file = new File(filepath);
               if (file.exists()) {
                   try {
                       FileInputStream fis = new FileInputStream(file);
                       InputStreamReader isr = new InputStreamReader(fis);
                       BufferedReader in = new BufferedReader(isr);
                       String inputLine;
                       while (in.ready() && (inputLine = in.readLine()) != null) {
                           if (inputLine.trim().length() > 0) {
                               String[] fields = inputLine.split("\t");
                               if (id>fields.length-1) {
                                   System.err.println("Wn id column:"+id+" out of range, fields.length = " + fields.length);
                                   continue;
                               }
                               if (word>fields.length-1) {
                                   System.err.println("Word column:"+word+" out of range, fields.length = " + fields.length);
                                   continue;
                               }
                               ArrayList<String> types = new ArrayList<String>();
                               String lemma = fields[word];
                               String wnid = prefix+fields[id];
                               if (wordnetData.synsetToEntries.containsKey(wnid)) {
                                   ArrayList<String> entries = wordnetData.synsetToEntries.get(wnid);
                                   entries.add(lemma);
                                   wordnetData.synsetToEntries.put(wnid, entries);
                               }
                               else {
                                   ArrayList<String> entries = new ArrayList<String>();
                                   entries.add(lemma);
                                   wordnetData.synsetToEntries.put(wnid, entries);
                               }
                               if (wordnetData.entryToSynsets.containsKey(lemma)) {
                                   ArrayList<String> synsets = wordnetData.entryToSynsets.get(lemma);
                                   synsets.add(wnid);
                                   wordnetData.entryToSynsets.put(lemma, synsets);
                               }
                               else {
                                   ArrayList<String> synsets = new ArrayList<String>();
                                   synsets.add(wnid);
                                   wordnetData.entryToSynsets.put(lemma, synsets);
                               }
                           }
                       }
                       in.close();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               } else {
                   System.err.println("Cannot find OMWN file = " + file.getAbsolutePath());
               }
               System.err.println("After reading OMWN");
               System.err.println("synset.size() = " + wordnetData.synsetToEntries.size());
               System.err.println("lexicon.size() = " + wordnetData.entryToSynsets.size());
       }


}
