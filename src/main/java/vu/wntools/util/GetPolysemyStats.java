package vu.wntools.util;

import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.util.*;

/**
 * Created by piek on 09/04/15.
 */
public class GetPolysemyStats {
    static public class Polysemy {
        private String lemma;
        private Integer pol;

        public Polysemy(String lemma, Integer pol) {
            this.lemma = lemma;
            this.pol = pol;
        }

        public String getLemma() {
            return lemma;
        }

        public void setLemma(String lemma) {
            this.lemma = lemma;
        }

        public Integer getPol() {
            return pol;
        }

        public void setPol(Integer pol) {
            this.pol = pol;
        }
    }

    static public void main (String[] args) {
        //String pathToFile = args[0];
        // String pathToFile = "/Releases/wordnetsimilarity_v.0.1/resources/cornetto2.0.lmf.xml";
        //String pathToFile = "/Tools/wordnet-tools.0.1/resources/cornetto2.1.lmf.xml";
        String pathToFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.lmf.xml";
        String pos = "-a";
        WordnetLmfSaxParser parser = new WordnetLmfSaxParser();

        parser.parseFile(pathToFile);

        parser.wordnetData.buildSynsetIndex();
        parser.wordnetData.buildChildRelationsFromids();
        parser.wordnetData.buildLemmaIndex(pos);
        parser.wordnetData.buildLexicalUnitIndex();
        getStats2(parser.wordnetData);
    }


    static public void getStats (WordnetData wordnetData) {
        ArrayList<Polysemy> polysemyList = new ArrayList<Polysemy>();
        HashMap<Integer, Integer> cntMap = new HashMap<Integer, Integer>();
        Set keySet = wordnetData.lemmaToSynsets.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            Integer cnt = wordnetData.lemmaToSynsets.get(key).size();
            if (cntMap.containsKey(cnt)) {
                Integer nr = cntMap.get(cnt);
                nr++;
                cntMap.put(cnt, nr);
            }
            else {
                cntMap.put(cnt, 1);
            }
            Polysemy pol = new Polysemy(key, cnt);
            polysemyList.add(pol);
        }
        ArrayList<Polysemy> topHundred = new ArrayList<Polysemy>();

        Collections.sort(polysemyList, new Comparator<Polysemy>() {
            @Override
            public int compare(Polysemy p1, Polysemy p2) {

                return p2.getPol().compareTo(p1.getPol());
            }
        });
        for (int i = 0; i < polysemyList.size(); i++) {
            Polysemy polysemy = polysemyList.get(i);
            if (topHundred.size()<100) {
                topHundred.add(polysemy);
            }
            else {
                break;
            }
        }

        keySet = cntMap.keySet();
        Iterator<Integer> iKeys = keySet.iterator();
        while (iKeys.hasNext()) {
            Integer key = iKeys.next();
            Integer nr = cntMap.get(key);
            System.out.println( key+"\t"+nr);
        }

        for (int i = 0; i < topHundred.size(); i++) {
            Polysemy polysemy = topHundred.get(i);
            System.out.println(polysemy.getLemma()+"\t"+polysemy.getPol());
        }
    }

    static public void getStats2 (WordnetData wordnetData) {
        HashMap<String, Integer> cntMap = new HashMap<String, Integer>();
        Set keySet = wordnetData.lexicalUnitsToSynsets.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            ArrayList<String> synsetIds = wordnetData.lexicalUnitsToSynsets.get(key);
            int cCnt = 0;
            for (int i = 0; i < synsetIds.size(); i++) {
                String id = synsetIds.get(i);
                cCnt += wordnetData.getChildCountForId(id);
            }
            String senseNr = key;
            int idx = key.lastIndexOf("_");
            if (idx>-1) senseNr = key.substring(idx+1);
          //  System.out.println("senseNr = " + senseNr);

            if (cntMap.containsKey(senseNr)) {
                Integer nr = cntMap.get(senseNr);
                nr += cCnt;
                cntMap.put(senseNr, nr);
            }
            else {
                cntMap.put(senseNr, cCnt);
            }
        }


        keySet = cntMap.keySet();
        Iterator<String> iKeys = keySet.iterator();
        while (iKeys.hasNext()) {
            String key = iKeys.next();
            Integer nr = cntMap.get(key);
            System.out.println( key+"\t"+nr);
        }


    }



}
