package vu.wntools.util;

import vu.wntools.lmf.Synset;
import vu.wntools.wnsimilarity.WordnetSimilarityApi;
import vu.wntools.wnsimilarity.measures.SimilarityPair;
import vu.wntools.wordnet.WordnetLmfDataSaxParser;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by piek on 09/06/15.
 */
public class CompareLemmaTranslations {






    static public void main(String[] args) {
        String pathToFile = "";
        String pathToPwnFile ="";
        String lemmaTranslations = "";
        lemmaTranslations = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/lemma-translation/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf.new";
        ArrayList<String> lemmaTranslationsArray = readTranslations(lemmaTranslations);
        System.out.println("lemmaTranslationsArray.size() = " + lemmaTranslationsArray.size());
        pathToFile = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf";
        pathToPwnFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.lmf.xml";

        ArrayList<String> relations = new ArrayList<String>();
        relations.add("has_hyperonym");
        relations.add("has_hypernym");
        relations.add("hypernym");

        WordnetLmfDataSaxParser parser = new WordnetLmfDataSaxParser();
        WordnetLmfSaxParser pwnparser = new WordnetLmfSaxParser();
        //parser.setPos("v");
       // parser.setRelations(relations);
       // parser.provenanceFilter = "pwn";
        parser.parseFile(pathToFile);
        pwnparser.parseFile(pathToPwnFile);


        pwnparser.wordnetData.buildSynsetIndex();
        pwnparser.wordnetData.buildLexicalUnitIndex();
        try {
            OutputStream fosMapped = new FileOutputStream(lemmaTranslations+".mapped");
            OutputStream fosMonosemousMapped = new FileOutputStream(lemmaTranslations+".mono-mapped");
            OutputStream fosSimMapped = new FileOutputStream(lemmaTranslations+".sim-mapped");
            OutputStream fosNotMapped = new FileOutputStream(lemmaTranslations+".not-mapped");
            OutputStream fosNoPwnEntry = new FileOutputStream(lemmaTranslations+".not-a-pwn-entry");

            for (int i = 0; i < lemmaTranslationsArray.size(); i++) {
                String trans = lemmaTranslationsArray.get(i);
                String [] fields = trans.split("TRANS");
                String odwn = fields[0].split("=")[0]; /// odwn synset id
                Synset synset = parser.wordnetData.synsetMap.get(odwn);
                ArrayList<String> odwnHypers = parser.wordnetData.getHyperonyms(synset);
                String synonymstring = fields[1].split("=")[1];
                synonymstring = synonymstring.substring(1, synonymstring.length()-1);
                String [] synonyms = synonymstring.split(",");   /// synonym translations
                String singleSynset = "";
                //// try to find the translations in PWN, synsets shared by translations are preferred
                ArrayList<String> sharedSynsets = pwnparser.wordnetData.getSharedSynsets(synonyms);
                if (sharedSynsets.size()==0) {
                    /// could not find the translatations as entries in PWN
                    /*
                    odwn-10-100326505-n=[zit:3] TRANS odwn-10-100326505-n=[is]
                    odwn-10-100324629-n=[overheidssector:1] TRANS odwn-10-100324629-n=[public sector]
                    odwn-10-100323103-n=[gloria:1] TRANS odwn-10-100323103-n=[Gloria]
                    odwn-10-100321930-v=[schurken:1] TRANS odwn-10-100321930-v=[thugs]
                    odwn-10-100319553-n=[tegenkracht:1] TRANS odwn-10-100319553-n=[to force]
                    odwn-10-100318571-n=[MBO:1] TRANS odwn-10-100318571-n=[MBO]
                    odwn-10-100318143-v=[heropenen:1] TRANS odwn-10-100318143-v=[reopening]
                    odwn-10-100317175-v=[omwaaien:1] TRANS odwn-10-100317175-v=[blown over]
                     */
                    trans += "\n";
                    fosNoPwnEntry.write(trans.getBytes());
                }
                else {
                    if (sharedSynsets.size() == 1) {
                        //// monosemous target
                        singleSynset = sharedSynsets.get(0);
                    }
                    for (int j = 0; j < sharedSynsets.size(); j++) {
                        /// we add the hypernyms to the shared synsets
                        String synsetId = sharedSynsets.get(j);
                        if (pwnparser.wordnetData.hyperRelations.containsKey(synsetId)) {
                            ArrayList<String> hypers = pwnparser.wordnetData.hyperRelations.get(synsetId);
                            for (int k = 0; k < hypers.size(); k++) {
                                String hyper = hypers.get(k);
                                if (!sharedSynsets.contains(hyper)) {
                                    sharedSynsets.add(hyper);
                                }
                            }
                        }

                    }
                    boolean matched = false;
                    /// if any of the shared synsets or any hypernym of the shared synsets
                    //  matches a hypernym of the ODWN synset we set MATCH to true and we consider the
                    /// matched synset a candidate for the ODWN synset


                    for (int j = 0; j < sharedSynsets.size(); j++) {
                        String synsetId = sharedSynsets.get(j);
                        if (odwnHypers.contains(synsetId)) {
                            String str = fields[0] + " TRANS " + synsetId + "=" + synonymstring + "\n";
                            fosMapped.write(str.getBytes());
                            matched = true;
                            /**
                             * odwn-10-109232236-n=[topkwaliteit:1]  TRANS eng-30-04728068-n=quality
                             odwn-10-109229564-n=[studieboek:1]  TRANS eng-30-06410904-n=textbook
                             odwn-10-109226742-n=[ergotherapie:1]  TRANS eng-30-00658082-n=occupational therapy
                             odwn-10-109220717-n=[rookworst:1]  TRANS eng-30-07675627-n=sausage
                             odwn-10-109220717-n=[rookworst:1]  TRANS eng-30-00002684-n=sausage
                             odwn-10-109220254-n=[eindsprint:1]  TRANS eng-30-00294452-n=sprint
                             odwn-10-109210977-n=[radarinstallatie:1]  TRANS eng-30-03575240-n=radar
                             odwn-10-109210246-n=[grut:1,kinderzegen:1,kroost:1]  TRANS eng-30-10373998-n=fry, blessing of children, offspring
                             odwn-10-109208446-n=[seksist:1]  TRANS eng-30-00007846-n=sexist
                             odwn-10-109207525-n=[safe sex:1]  TRANS eng-30-00844254-n=safe sex

                             */
                        }
                    }
                    if (!matched) {
                        if (!singleSynset.isEmpty()) {
                            String str = fields[0] + " TRANS " + singleSynset + "=" + synonymstring + "\n";
                            fosMonosemousMapped.write(str.getBytes());
                            /*
                            odwn-10-100310851-n=[Noord-Ier:1]  TRANS eng-30-08887841-n=Northern Ireland
                            odwn-10-100300801-n=[slangenbezweerder:1]  TRANS eng-30-10615584-n=snake charmer
                            odwn-10-100300379-v=[wegsluipen:1]  TRANS eng-30-01917123-v=slink
                            odwn-10-100296374-n=[pinkstergemeente:1]  TRANS eng-30-10414865-n=Pentecostal
                            odwn-10-100291038-n=[vliegtuigbouwer:1]  TRANS eng-30-02686568-n=aircraft
                            odwn-10-100285397-n=[wonderdokter:1]  TRANS eng-30-10784544-n=witch doctor
                            odwn-10-100284846-n=[entreegeld:1]  TRANS eng-30-13321495-n=entrance fee
                            odwn-10-100279661-v=[afwaaien:3]  TRANS eng-30-01290945-v=blow off
                             */
                        } else {
                            SimilarityPair topSim = new SimilarityPair();
                            topSim.setScore(0.0);
                            for (int j = 0; j < odwnHypers.size(); j++) {
                                String synsetId = odwnHypers.get(j);
                                for (int k = 0; k < sharedSynsets.size(); k++) {
                                    String sharedSynset = sharedSynsets.get(k);
                                    SimilarityPair similarityPair = WordnetSimilarityApi.synsetLeacockChodorowSimilarity(pwnparser.wordnetData, synsetId, sharedSynset);
                                    if (similarityPair.getScore()>topSim.getScore()) {
                                        topSim = similarityPair;
                                    }
                                }
                            }
                            if (topSim.getScore()>2.0) {
                                String str = fields[0] + " TRANS " + topSim.getTargetId() + "=" + synonymstring + "\n";
                                fosSimMapped.write(str.getBytes());
                            }
                            else {
                                trans += "\n";
                                fosNotMapped.write(trans.getBytes());
                                //  System.out.println("odwnHypers = " + odwnHypers.toString());
                            }
                        }
                    }
                }
            }
            fosMapped.close();
            fosNotMapped.close();
            fosMonosemousMapped.close();
            fosSimMapped.close();
            fosNoPwnEntry.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ///odwn-10-108431224-v=[verschrikken:2] TRANS odwn-10-108431224-v=[fray]
    //odwn-10-108426039-n=[bulldozer:2,grondverzetmachine:1,shovel:1] TRANS odwn-10-108426039-n=[bulldozer, earthmover, shovel]
    static ArrayList<String> readTranslations (String pathToFile) {
        ArrayList<String> trans = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(pathToFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            String buffer = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                    trans.add(inputLine);
                }
            }
            /*try {
                OutputStream fos = new FileOutputStream(pathToFile+"."+fileCnt+".txt");
                fos.write(buffer.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trans;
    }




}
