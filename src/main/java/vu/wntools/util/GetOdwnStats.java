package vu.wntools.util;

import vu.wntools.lmf.*;
import vu.wntools.wordnet.WordnetLmfData;
import vu.wntools.wordnet.WordnetLmfDataSaxParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by piek on 06/06/15.
 */
public class GetOdwnStats {

    static boolean FIX = false;
    static ArrayList<String> newConceptCandidates = new ArrayList<String>();
    static ArrayList<String> newKnownConceptCandidates = new ArrayList<String>();
    static ArrayList<String> odwnSynsetsWithoutLemma = new ArrayList<String>();
    static ArrayList<String> missingTargets = new ArrayList<String>();

    static public void main(String[] args) {
        //String pathToFile = args[0];
        String pathToFile = "";
        pathToFile = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf";
        WordnetLmfDataSaxParser parser = new WordnetLmfDataSaxParser();
        parser.parseFile(pathToFile);
        try {
            OutputStream fosFix = null;
            if (FIX)  fosFix = new FileOutputStream(pathToFile + ".fix");

            OutputStream fos = new FileOutputStream(pathToFile + ".stats.csv");
            String str = new File(pathToFile).getName()+"\n\n";
            str += parser.wordnetData.getLexiconLabel()+"\n";
            str += "Entries\t"+parser.wordnetData.getEntries().size()+"\n";
            str += "Synsets\t"+parser.wordnetData.getSynsets().size()+"\n";
            str += countEntryInfo(parser.wordnetData);
            str += "\n";
            str += countSynsetInfo(parser.wordnetData);
            fos.write(str.getBytes());
            fos.close();

            if (FIX) parser.wordnetData.serialize(fosFix);

            fos = new FileOutputStream(pathToFile+".new");
            for (int i = 0; i < newConceptCandidates.size(); i++) {
                String s = newConceptCandidates.get(i);
                if (parser.wordnetData.synsetToEntriesMap.containsKey(s)) {
                    ArrayList<String> syns = parser.wordnetData.synsetToEntriesMap.get(s);
                    s += "#[";
                    for (int j = 0; j < syns.size(); j++) {
                        String syn = syns.get(j);
                        s += syn+":";
                        if (parser.wordnetData.entryToSynsetsMap.containsKey(syn)) {
                            ArrayList<String> synsets = parser.wordnetData.entryToSynsetsMap.get(syn);
                            s+= synsets.size();
                        }
                        else {
                            s += "0";
                        }
                        if ((j+1)<syns.size())  {
                           s+=",";
                        }
                    }
                    s += "]\n";
                }
                else {
                    s += "#[]\n";
                }
                fos.write(s.getBytes());
            }
            fos.close();

            fos = new FileOutputStream(pathToFile+".new-old");
            for (int i = 0; i < newKnownConceptCandidates.size(); i++) {
                String s = newKnownConceptCandidates.get(i);
                if (parser.wordnetData.synsetToEntriesMap.containsKey(s)) {
                    ArrayList<String> syns = parser.wordnetData.synsetToEntriesMap.get(s);
                    s += "#[";
                    for (int j = 0; j < syns.size(); j++) {
                        String syn = syns.get(j);
                        s += syn+":";
                        if (parser.wordnetData.entryToSynsetsMap.containsKey(syn)) {
                            ArrayList<String> synsets = parser.wordnetData.entryToSynsetsMap.get(syn);
                            s+= synsets.size();
                            for (int k = 0; k < synsets.size(); k++) {
                                String s1 = synsets.get(k);
                                if (s1.startsWith("eng")) {
                                    s+=":"+s1;
                                }
                            }
                        }
                        else {
                            s += "0";
                        }
                        if ((j+1)<syns.size())  {
                            s+=",";
                        }
                    }
                    s += "]\n";
                }
                else {
                    s += "#[]\n";
                }
                fos.write(s.getBytes());
            }
            fos.close();

            fos = new FileOutputStream(pathToFile+".nolemma");
            for (int i = 0; i < odwnSynsetsWithoutLemma.size(); i++) {
                String s = odwnSynsetsWithoutLemma.get(i)+"\n";
                fos.write(s.getBytes());
            }
            fos.close();

            fos = new FileOutputStream(pathToFile+".missing-targets");
            for (int i = 0; i < missingTargets.size(); i++) {
                String s = missingTargets.get(i)+"\n";
                fos.write(s.getBytes());
            }
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String countSynsetInfo (WordnetLmfData wordnetLmfData) {
        String cnt = "";
        int nDefinitions = 0;
        int nDefinitionsNL = 0;
        int nDefinitionsENPWN = 0;
        int nDefinitionsENGT = 0;
        int nPwnSynsets = 0;
        int nPwnOdwnSynsets = 0;
        int nPwnOnlySynsets = 0;
        int nOdwnSynsets = 0;
        for (int i = 0; i < wordnetLmfData.getSynsets().size(); i++) {
            Synset synset = wordnetLmfData.getSynsets().get(i);
/*
            ArrayList<ArrayList<String>>  hyperChains = new ArrayList<ArrayList<String>>();
            wordnetLmfData.getHyperChains(synset.getSynsetId(),hyperChains, new ArrayList<String>());
*/

            nDefinitions += synset.getDefinitions().size();

            ArrayList<SynsetRelation> fixedRelations = new ArrayList<SynsetRelation>();
            for (int j = 0; j < synset.getRelations().size(); j++) {
                SynsetRelation synsetRelation = synset.getRelations().get(j);
                if (synsetRelation.getProvenance().isEmpty()) {
                    synsetRelation.setProvenance("odwn");
                }
                String target = synsetRelation.getTarget();
                if (!wordnetLmfData.synsetMap.containsKey(target)) {
                    if (!missingTargets.contains(target)) {
                        missingTargets.add(target);
                    }
                }
                else {
/*
                    if (FIX) {
                        if (target.startsWith("odwn") && !wordnetLmfData.synsetToEntriesMap.containsKey(target))  {
                            //// target is a synset without lemma's
                            //// so we remove it
                        }
                        else {
                           // fixedRelations.add(synsetRelation);
                        }
                    }
*/
                }
            }
            //if (FIX) synset.setRelations(fixedRelations);

            for (int j = 0; j < synset.getDefinitions().size(); j++) {
                Gloss gloss = synset.getDefinitions().get(j);
                if (gloss.getLanguage().equalsIgnoreCase("nl")) {
                    nDefinitionsNL++;
                }
                else if (gloss.getProvenance().equalsIgnoreCase("pwn")) {
                    nDefinitionsENPWN++;
                }
                else {
                    nDefinitionsENGT++;
                }
            }
            if (synset.getSynsetId().startsWith("eng")) {
                nPwnSynsets++;
                /// check for Dutch synonyms
                if (wordnetLmfData.synsetToEntriesMap.containsKey(synset.getSynsetId())) {
                    //// there is an entry in Dutch for this synset
                    nPwnOdwnSynsets++;
                }
                else {
                    nPwnOnlySynsets++;
                }
            }
            else {
                nOdwnSynsets++;
                /// check for mappings other meanings
                if (wordnetLmfData.synsetToEntriesMap.containsKey(synset.getSynsetId())) {
                    //// there is an entry in Dutch for this synset
                    //// we check all the entries (synonyms to see if they have pwn mappings in onen sense or another
                    ArrayList<String> entries = wordnetLmfData.synsetToEntriesMap.get(synset.getSynsetId());
                    for (int j = 0; j < entries.size(); j++) {
                        String entryWrittenForm = entries.get(j);
                        if (wordnetLmfData.entryToSynsetsMap.containsKey(entryWrittenForm)) {
                            ArrayList<String> synsetIds = wordnetLmfData.entryToSynsetsMap.get(entryWrittenForm);
                            boolean PWN = false;
                            for (int k = 0; k < synsetIds.size(); k++) {
                                String synsetId = synsetIds.get(k);
                                if (synsetId.startsWith("eng")) {
                                    PWN = true;
                                    break;
                                }
                            }
                            if (PWN) {
                                //// a synonym or another sense has a direct PWN mapping
                                if (!newKnownConceptCandidates.contains(synset.getSynsetId())) {
                                    newKnownConceptCandidates.add(synset.getSynsetId());
                                }
                            }
                            else {
                                //// true candidate for new concept
                                if (!newConceptCandidates.contains(synset.getSynsetId())) {
                                    newConceptCandidates.add(synset.getSynsetId());
                                }
                            }
                        }
                    }
                }
                else {
                    //// weird this is a ODWN synset without synonyms!!!!!
                    //System.out.println("synset.getSynsetId() = " + synset.getSynsetId());
                    if (!odwnSynsetsWithoutLemma.contains(synset.getSynsetId())) {
                        odwnSynsetsWithoutLemma.add(synset.getSynsetId());
                    }
                }
            }
        }
        cnt += "\tODWN\tPWN\tPWN-ODWN\tPWN-ENG\n";
        cnt += "Synsets\t"+nOdwnSynsets+"\t"+nPwnSynsets+"\t"+nPwnOdwnSynsets+"\t"+ nPwnOnlySynsets+"\n";

        cnt += "\tODWN\tODWN-PWN\tODWN-ONLY\n";
        cnt += "Synsets\t"+nOdwnSynsets+"\t"+newKnownConceptCandidates.size()+"\t"+ newConceptCandidates.size()+"\n\n";

        cnt += "\tTotal\tNL\tEN-PWN\tEN-GT\n";
        cnt += "Definitions\t"+nDefinitions+"\t"+nDefinitionsNL+"\t"+nDefinitionsENPWN+"\t"+nDefinitionsENGT+"\n";

        cnt += "ODWN without entries\t"+odwnSynsetsWithoutLemma.size()+"\n";
        return cnt;
    }

    static String countEntryInfo (WordnetLmfData wordnetLmfData) {
        String cnt = "";
        int nNoun = 0;
        int nVerb = 0;
        int nOtherPos = 0;
        int nNounSenses = 0;
        int nVerbSenses = 0;
        int nOtherSenses = 0;
        int nOdwnNounSynsets = 0;
        int nPwnNounSynsets = 0;
        int nNoNounSynsets = 0;
        int nOdwnVerbSynsets = 0;
        int nPwnVerbSynsets = 0;
        int nNoVerbSynsets = 0;
        int nOdwnOtherSynsets = 0;
        int nPwnOtherSynsets = 0;
        int nNoOtherSynsets = 0;
        for (int i = 0; i < wordnetLmfData.getEntries().size(); i++) {
            LmfEntry lmfEntry = wordnetLmfData.getEntries().get(i);
            if (lmfEntry.getPos().equalsIgnoreCase("noun")) {
                nNoun++;
                nNounSenses += lmfEntry.getSenses().size();
                for (int j = 0; j < lmfEntry.getSenses().size(); j++) {
                    LmfSense lmfSense =  lmfEntry.getSenses().get(j);
                    if (lmfSense.getSynset().startsWith("odwn")) {
                        nOdwnNounSynsets++;
                    }
                    else if (lmfSense.getSynset().startsWith("eng")) {
                        nPwnNounSynsets++;
                    }
                    else if (lmfSense.getSynset().isEmpty()) {
                        nNoNounSynsets++;
                    }
                }
            }
            else if (lmfEntry.getPos().equalsIgnoreCase("verb")) {
                nVerb++;
                nVerbSenses += lmfEntry.getSenses().size();
                for (int j = 0; j < lmfEntry.getSenses().size(); j++) {
                    LmfSense lmfSense =  lmfEntry.getSenses().get(j);
                    if (lmfSense.getSynset().startsWith("odwn")) {
                        nOdwnVerbSynsets++;
                    }
                    else if (lmfSense.getSynset().startsWith("eng")) {
                        nPwnVerbSynsets++;
                    }
                    if (lmfSense.getSynset().isEmpty()) {
                        nNoVerbSynsets++;
                    }
                }
            }
            else {
                nOtherPos++;
                //// THESE ARE ALL Multiword Units (mwe)
                //System.out.println("lmfEntry.getEntryId() = " + lmfEntry.getEntryId());
                nOtherSenses += lmfEntry.getSenses().size();
                for (int j = 0; j < lmfEntry.getSenses().size(); j++) {
                    LmfSense lmfSense =  lmfEntry.getSenses().get(j);
                    if (lmfSense.getSynset().startsWith("odwn")) {
                        nOdwnOtherSynsets++;
                    }
                    else if (lmfSense.getSynset().startsWith("eng")) {
                        nPwnOtherSynsets++;
                    }
                    if (lmfSense.getSynset().isEmpty()) {
                        nNoOtherSynsets++;
                    }
                }
            }
        }
        cnt += "\tTotal\tNouns\tVerbs\tOther\n";
        cnt += "Entries\t"+(nNoun+nVerb+nOtherPos)+"\t"+nNoun+"\t"+nVerb+"\t"+nOtherPos+"\n";
        cnt += "Sense\t"+(nNounSenses+nVerbSenses+nOtherSenses)+"\t"+nNounSenses+"\t"+nVerb+"\t"+nOtherPos+"\n";
        cnt += "Odwn Synsets\t"+(nOdwnNounSynsets+nOdwnVerbSynsets+nOdwnOtherSynsets)+"\t"+nOdwnNounSynsets+"\t"+nOdwnVerbSynsets+"\t"+nOdwnOtherSynsets+"\n";
        cnt += "Pwn Synsets\t"+(nPwnNounSynsets+nPwnVerbSynsets+nPwnOtherSynsets)+"\t"+nPwnNounSynsets+"\t"+nPwnVerbSynsets+"\t"+nPwnOtherSynsets+"\n";
        cnt += "No Synsets\t"+(nNoNounSynsets+nNoVerbSynsets+nNoOtherSynsets)+"\t"+nNoNounSynsets+"\t"+nNoVerbSynsets+"\t"+nNoOtherSynsets+"\n";
        return cnt;
    }

/*    static void getNewSynsetStats (WordnetLmfDataSaxParser parser) {
        for (int i = 0; i < parser.wordnetData.getSynsets().size(); i++) {
            Synset synset = parser.wordnetData.getSynsets().get(i);
            if (synset.getSynsetId().startsWith("odwn")) {
                    ArrayList<String> synonyms = parser.wordnetData.getSynonyms(synsetId);
                    boolean PWN = false;
                    for (int j = 0; j < synonyms.size(); j++) {
                        String synonym = synonyms.get(j);
                        ArrayList<String> synsetIds = parser.wordnetData.lemmaToSynsets.get(synonym);
                        if (synsetIds!=null) {
                            for (int k = 0; k < synsetIds.size(); k++) {
                                String id = synsetIds.get(k);
                                //  System.out.println("id = " + id);
                                if (id.startsWith("eng")) {
                                    PWN = true;
                                    break;
                                }
                            }
                        }
                        if (PWN == true) {
                            break;
                        }
                    }

                    ArrayList<String> hypers = new ArrayList<String>();
                    parser.wordnetData.getSingleHyperChain(synsetId, hypers);
                    String hyper = "";
                    for (int j = hypers.size() - 1; j >= 0; j--) {
                        String hyperId = hypers.get(j);
                        if (hyperId.startsWith("eng")) {
                            hyper += hyperId + ":" + pwnparser.wordnetData.getSynsetString(hyperId) + "\t";
                            //  break;
                        }
                    }
                    String syns = parser.wordnetData.getSynsetString(synsetId);
                    String str = hyper + "odwn = " + synsetId + ":" + syns + "\n";
                    if (!PWN) {
                        if (!pureowdnIds.contains(synsetId)) {
                            pureowdnIds.add(synsetId);
                            fos.write(str.getBytes());
                        }
                    }
                    else {
                        if (!owdnpwnIds.contains(synsetId)) {
                            owdnpwnIds.add(synsetId);
                            fos1.write(str.getBytes());
                        }
                    }

    }*/
}
