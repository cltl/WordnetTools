package vu.wntools.wordnet;

import eu.kyotoproject.kaf.KafSense;
import vu.wntools.lmf.Gloss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 8/14/12
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordnetData {
    public boolean DEBUG = false;
    public HashMap<String, ArrayList<Gloss>> synsetToGlosses = new HashMap<String, ArrayList<Gloss>>();
    public HashMap<String, ArrayList<String>> hyperRelations = new HashMap<String, ArrayList<String>>();
    public HashMap<String, ArrayList<String>> otherRelations = new HashMap<String, ArrayList<String>>();
    public HashMap<String, ArrayList<String>> entryToSynsets = new HashMap<String, ArrayList<String>>();
    public HashMap<String, ArrayList<String>> lemmaToSynsets = new HashMap<String, ArrayList<String>>();
    public HashMap<String, ArrayList<String>> lexicalUnitsToSynsets = new HashMap<String, ArrayList<String>>();
    public HashMap<String, String> lexicalUnitsToLemmas = new HashMap<String, String>();
    public HashMap<String, ArrayList<String>> synsetToLexicalUnits = new HashMap<String, ArrayList<String>>();
    public HashMap<String, ArrayList<String>> synsetToDirectEquiSynsets = new HashMap<String, ArrayList<String>>();
    public HashMap<String, ArrayList<String>> synsetToNearEquiSynsets = new HashMap<String, ArrayList<String>>();
    public HashMap<String, ArrayList<String>> synsetToOtherEquiSynsets = new HashMap<String, ArrayList<String>>();
    public HashMap<String, ArrayList<String>> synsetToEntries = new HashMap<String, ArrayList<String>>();
    public HashMap<String, ArrayList<String>> childRelations = new HashMap<String, ArrayList<String>>();
    public ArrayList<String> synsetArrayList = new ArrayList<String>();
    private int nAverageNounDepth = 0;
    private int nAverageVerbDepth = 0;
    private int nAverageAdjectiveDepth = 0;
    private String resource;
    private String version;


    public WordnetData() {
        init();
    }

    public void init () {
        synsetArrayList = new ArrayList<String>();
        synsetToGlosses = new HashMap<String, ArrayList<Gloss>>();
        hyperRelations = new HashMap<String, ArrayList<String>>();
        otherRelations = new HashMap<String, ArrayList<String>>();
        entryToSynsets = new HashMap<String, ArrayList<String>>();
        synsetToLexicalUnits = new HashMap<String, ArrayList<String>>();
        synsetToDirectEquiSynsets = new HashMap<String, ArrayList<String>>();
        synsetToNearEquiSynsets = new HashMap<String, ArrayList<String>>();
        synsetToOtherEquiSynsets = new HashMap<String, ArrayList<String>>();
        lexicalUnitsToSynsets = new HashMap<String, ArrayList<String>>();
        lemmaToSynsets = new HashMap<String, ArrayList<String>>();
        synsetToEntries = new HashMap<String, ArrayList<String>>();
        childRelations = new HashMap<String, ArrayList<String>>();
        lexicalUnitsToLemmas = new HashMap<String, String>();
        nAverageNounDepth = 0;
        nAverageVerbDepth = 0;
        nAverageAdjectiveDepth = 0;
        resource = "";
        version = "";
    }

    public HashMap<String, ArrayList<String>> getHyperRelations() {
        return hyperRelations;
    }

    public void setHyperRelations(HashMap<String, ArrayList<String>> hyperRelations) {
        this.hyperRelations = hyperRelations;
    }

    public void addHyperRelation(String sourceId, ArrayList<String> targetIds) {
        if (hyperRelations.containsKey(sourceId)) {
            ArrayList<String> givenHypers = hyperRelations.get(sourceId);
            for (int i = 0; i < targetIds.size(); i++) {
                String target = targetIds.get(i);
               // System.out.println("target = " + target);
                if (!givenHypers.contains(target)) {
                    givenHypers.add(target);
                }
            }
            this.hyperRelations.put(sourceId, givenHypers);
        }
        else {
            this.hyperRelations.put(sourceId, targetIds);
        }
    }


    public int getnAverageAdjectiveDepth() {
        return nAverageAdjectiveDepth;
    }

    public void setnAverageAdjectiveDepth(int nAverageAdjectiveDepth) {
        this.nAverageAdjectiveDepth = nAverageAdjectiveDepth;
    }

    public int getnAverageNounDepth() {
        return nAverageNounDepth;
    }

    public void setnAverageNounDepth(int nAverageNounDepth) {
        this.nAverageNounDepth = nAverageNounDepth;
    }

    public int getnAverageVerbDepth() {
        return nAverageVerbDepth;
    }

    public void setnAverageVerbDepth(int nAverageVerbDepth) {
        this.nAverageVerbDepth = nAverageVerbDepth;
    }

    public HashMap<String, ArrayList<String>> getOtherRelations() {
        return otherRelations;
    }

    public void setOtherRelations(HashMap<String, ArrayList<String>> otherRelations) {
        this.otherRelations = otherRelations;
    }

    public void addOtherRelations(String sourceId, ArrayList<String> targetIds) {
        if (otherRelations.containsKey(sourceId)) {
            ArrayList<String> givenHypers = otherRelations.get(sourceId);
            for (int i = 0; i < targetIds.size(); i++) {
                String target = targetIds.get(i);
                if (!givenHypers.contains(target)) {
                    givenHypers.add(target);
                }
            }
            this.otherRelations.put(sourceId, givenHypers);
        }
        else {
            this.otherRelations.put(sourceId, targetIds);
        }
    }

    public HashMap<String, ArrayList<String>> getSynsetToDirectEquiSynsets() {
        return synsetToDirectEquiSynsets;
    }

    public void addSynsetToDirectEquiSynsets(String synsetID, ArrayList<String> synsetToDirectEquiSynsets) {
        this.synsetToDirectEquiSynsets.put(synsetID, synsetToDirectEquiSynsets);
    }

    public HashMap<String, ArrayList<String>> getSynsetToNearEquiSynsets() {
        return synsetToNearEquiSynsets;
    }

    public void addSynsetToNearEquiSynsets(String synsetID, ArrayList<String> synsetToNearEquiSynsets) {
        this.synsetToNearEquiSynsets.put(synsetID, synsetToNearEquiSynsets);
    }

    public HashMap<String, ArrayList<String>> getSynsetToOtherEquiSynsets() {
        return synsetToOtherEquiSynsets;
    }

    public void addSynsetToOtherEquiSynsets(String synsetID, ArrayList<String> synsetToOtherEquiSynsets) {
        this.synsetToOtherEquiSynsets.put(synsetID, synsetToOtherEquiSynsets);
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<String> getTopNodes () {
        ArrayList<String> topNodes = new ArrayList<String>();
        Set keyHyperSet = entryToSynsets.keySet();
        Iterator entries = keyHyperSet.iterator();
        while(entries.hasNext()) {
            String entry = (String) entries.next();
            ArrayList<String> synsetIds = entryToSynsets.get(entry);
            for (int i = 0; i < synsetIds.size(); i++) {
                String synsetId = synsetIds.get(i);
                if (!hyperRelations.containsKey(synsetId)) {
                   if (!topNodes.contains(synsetId)) topNodes.add(synsetId);
                }
            }
        }
        return topNodes;
    }

    public ArrayList<String> getTopNodesFromIds () {
        ArrayList<String> topNodes = new ArrayList<String>();
        Set keyHyperSet = hyperRelations.keySet();
        Iterator entries = keyHyperSet.iterator();
        while(entries.hasNext()) {
            String entry = (String) entries.next();
            ArrayList<String> synsetIds = hyperRelations.get(entry);
            for (int i = 0; i < synsetIds.size(); i++) {
                String synsetId = synsetIds.get(i);
                if (!hyperRelations.containsKey(synsetId)) {
                   if (!topNodes.contains(synsetId)) topNodes.add(synsetId);
                }
            }
        }
        return topNodes;
    }

    public void buildChildRelations () {
        Set keyHyperSet = entryToSynsets.keySet();
        Iterator entries = keyHyperSet.iterator();
        while(entries.hasNext()) {
            String entry = (String) entries.next();
            ArrayList<String> synsetIds = entryToSynsets.get(entry);
            for (int i = 0; i < synsetIds.size(); i++) {
                String synsetId = synsetIds.get(i);
                if (!hyperRelations.containsKey(synsetId)) {
                   ArrayList<String> hypers = hyperRelations.get(synsetId);
                   if (hypers!=null) {
                        for (int j = 0; j < hypers.size(); j++) {
                            String hyper = hypers.get(j);
                            if (childRelations.containsKey(hyper)) {
                                ArrayList<String> children = childRelations.get(hyper);
                                children.add(synsetId);
                                childRelations.put(hyper, children);
                            }
                            else {
                                ArrayList<String> children = new ArrayList<String>();
                                children.add(synsetId);
                                childRelations.put(hyper, children);
                            }
                        }
                   }
                }
            }
        }
    }

    public void buildChildRelationsFromSynsets () {
        Set keyHyperSet = hyperRelations.keySet();
        Iterator entries = keyHyperSet.iterator();
        while(entries.hasNext()) {
            String child = (String) entries.next();
            ArrayList<String> synsetIds = hyperRelations.get(child);
            if (synsetIds!=null) {
                for (int j = 0; j < synsetIds.size(); j++) {
                    String hyper = synsetIds.get(j);
                    if (childRelations.containsKey(hyper)) {
                        ArrayList<String> children = childRelations.get(hyper);
                        if (!children.contains(child)) {
                            children.add(child);
                            childRelations.put(hyper, children);
                        }
                    }
                    else {
                        ArrayList<String> children = new ArrayList<String>();
                        children.add(child);
                        childRelations.put(hyper, children);
                       // System.out.println("hyper = " + hyper);
                    }
                }
            }
        }
    }

    public int getChildCountForId(String synsetId) {
        int count = 0;
        if (childRelations.containsKey(synsetId)) {
            count = childRelations.get(synsetId).size();
        }
        return count;
    }

    public void buildChildRelationsFromids () {
        Set keyHyperSet = hyperRelations.keySet();
        Iterator entries = keyHyperSet.iterator();
        while(entries.hasNext()) {
            String synsetId = (String) entries.next();
            ArrayList<String> hypers = hyperRelations.get(synsetId);
            for (int i = 0; i < hypers.size(); i++) {
                String hyper = hypers.get(i);
                if (childRelations.containsKey(hyper)) {
                    ArrayList<String> children = childRelations.get(hyper);
                    if (!children.contains(synsetId)) {
                        children.add(synsetId);
                        childRelations.put(hyper, children);
                    }
                }
                else {
                    ArrayList<String> children = new ArrayList<String>();
                    children.add(synsetId);
                    childRelations.put(hyper, children);
                }
            }
        }
    }

    public void buildSynsetIndex () {
        Set keyHyperSet = entryToSynsets.keySet();
        Iterator entries = keyHyperSet.iterator();
        while(entries.hasNext()) {
            String entry = (String) entries.next();
            ArrayList<String> synsetIds = entryToSynsets.get(entry);
            for (int i = 0; i < synsetIds.size(); i++) {
                String synsetId = synsetIds.get(i);
                if (synsetToEntries.containsKey(synsetId)) {
                   ArrayList<String> synonyms = synsetToEntries.get(synsetId);
                    if (!synonyms.contains(entry)) {
                        synonyms.add(entry);
                        synsetToEntries.put(synsetId, synonyms);
                    }
                }
                else {
                    ArrayList<String> synonyms = new ArrayList<String>();
                    synonyms.add(entry);
                    synsetToEntries.put(synsetId, synonyms);
                }
            }
        }
    }

    public void buildLexicalUnitIndex () {
        Set keyHyperSet = synsetToLexicalUnits.keySet();
        Iterator entries = keyHyperSet.iterator();
        while(entries.hasNext()) {
            String synset = (String) entries.next();
            ArrayList<String> luIds = synsetToLexicalUnits.get(synset);
            for (int i = 0; i < luIds.size(); i++) {
                String luId = luIds.get(i);
                if (lexicalUnitsToSynsets.containsKey(luId)) {
                   ArrayList<String> synsets = lexicalUnitsToSynsets.get(luId);
                    synsets.add(synset);
                    lexicalUnitsToSynsets.put(luId, synsets);
                }
                else {
                    ArrayList<String> synsets = new ArrayList<String>();
                    synsets.add(synset);
                    lexicalUnitsToSynsets.put(luId, synsets);
                }
            }
        }
    }

    public void buildLemmaIndex () {
        for (int i = 0; i < synsetArrayList.size(); i++) {
            String s = synsetArrayList.get(i);
            ArrayList<String> lemmas = getSynonyms(s);
            if (lemmas!=null) {
                for (int j = 0; j < lemmas.size(); j++) {
                    String lemma = lemmas.get(j);
                    if (lemmaToSynsets.containsKey(lemma)) {
                        ArrayList<String> synsets = lemmaToSynsets.get(lemma);
                        if (!synsets.contains(s)) {
                            synsets.add(s);
                            lemmaToSynsets.put(lemma, synsets);
                        }
                    }
                    else {
                        ArrayList<String> synsets = new ArrayList<String>();
                        synsets.add(s);
                        lemmaToSynsets.put(lemma, synsets);
                    }
                }
            }
        }
    }

    public void buildLemmaIndex (String posTag) {
        for (int i = 0; i < synsetArrayList.size(); i++) {
            String s = synsetArrayList.get(i);
            if (s.endsWith(posTag)) {
                ArrayList<String> lemmas = getSynonyms(s);
                if (lemmas != null) {
                    for (int j = 0; j < lemmas.size(); j++) {
                        String lemma = lemmas.get(j);
                        if (lemmaToSynsets.containsKey(lemma)) {
                            ArrayList<String> synsets = lemmaToSynsets.get(lemma);
                            if (!synsets.contains(s)) {
                                synsets.add(s);
                                lemmaToSynsets.put(lemma, synsets);
                            }
                        } else {
                            ArrayList<String> synsets = new ArrayList<String>();
                            synsets.add(s);
                            lemmaToSynsets.put(lemma, synsets);
                        }
                    }
                }
            }
        }
    }

    public SynsetNode makeSynsetNode (String word, String synsetId) {
        SynsetNode synsetNode = new SynsetNode();
        synsetNode.setSynsetId(synsetId);
        if (synsetToEntries.containsKey(synsetId)) {
            String synonymString = word + "#";
            ArrayList<String> synonyms = synsetToEntries.get(synsetId);
            for (int i = 0; i < synonyms.size(); i++) {
                String s = synonyms.get(i);
                synonymString += s+";";
            }
            synsetNode.setSynset(synonymString);
        }
        return synsetNode;
    }

    public SynsetNode makeSynsetNode (String synsetId) {
        SynsetNode synsetNode = new SynsetNode();
        synsetNode.setSynsetId(synsetId);
        if (synsetToEntries.containsKey(synsetId)) {
            String synonymString = "";
            ArrayList<String> synonyms = synsetToEntries.get(synsetId);
            for (int i = 0; i < synonyms.size(); i++) {
                String s = synonyms.get(i);
                synonymString += s+";";
            }
            synsetNode.setSynset(synonymString);
        }
        return synsetNode;
    }

    public void buildDirectEquivalencesFromIds (String prefix1, String prefix2) {
        for (int i = 0; i < synsetArrayList.size(); i++) {
            String synsetId = synsetArrayList.get(i);
            if (synsetId.startsWith(prefix1)) {
                String id = prefix2+synsetId.substring(prefix1.length());
               // System.out.println("id = " + id);
                if (this.synsetToDirectEquiSynsets.containsKey(synsetId)) {
                    ArrayList<String> equivalences = this.synsetToDirectEquiSynsets.get(synsetId);
                    if (!equivalences.contains(id)) {
                        equivalences.add(id);
                        this.synsetToDirectEquiSynsets.put(synsetId, equivalences);
                    }
                }
                else {
                    ArrayList<String> equivalences = new ArrayList<String>();
                    equivalences.add(id);
                    this.synsetToDirectEquiSynsets.put(synsetId, equivalences);
                }

            }
        }
    }
    public String getSynsetString (String synsetId) {
        String synsetString = "";

        if (synsetToEntries.containsKey(synsetId)) {
            ArrayList<String> synonyms = synsetToEntries.get(synsetId);
            for (int i = 0; i < synonyms.size(); i++) {
                String s = synonyms.get(i);
                synsetString += s+";";
            }
        }
        return synsetString;
    }
    public String getFirstSynsetString (String synsetId) {
        String synsetString = "";

        if (synsetToEntries.containsKey(synsetId)) {
            ArrayList<String> synonyms = synsetToEntries.get(synsetId);
            for (int i = 0; i < synonyms.size(); i++) {
                String s = synonyms.get(i);
                synsetString += s;
                break;
            }
        }
        return synsetString;
    }

    public ArrayList<String> getSynonyms (String synsetId) {
        ArrayList<String> synonyms = new ArrayList<String>();
        if (synsetToEntries.containsKey(synsetId)) {
            synonyms = synsetToEntries.get(synsetId);
        }
        return synonyms;
    }

    public int getAverageDepthBySynset () {
         int depth = 0;
        int nSynsets = 0;

        Set keySet = hyperRelations.keySet();
         Iterator keys = keySet.iterator();
         while (keys.hasNext()) {
             String key = (String) keys.next();
             ArrayList<ArrayList<String>> targetChains = new ArrayList<ArrayList<String>>();
             getMultipleHyperChain(key, targetChains);
             for (int i = 0; i < targetChains.size(); i++) {
                 nSynsets++;
                 ArrayList<String> targetChain =  targetChains.get(i);
                 depth+= targetChain.size();
             }
         }
         if (keySet.size()==0) {
            depth = 1;
         }
         else {
            depth = depth/(keySet.size()+nSynsets);
         }
         return depth;
    }

    public int getAverageDepthBySynset (String synset) {
         int depth = 0;
         ArrayList<ArrayList<String>> targetChains = new ArrayList<ArrayList<String>>();
         if (hyperRelations.containsKey (synset)) {
             getMultipleHyperChain(synset, targetChains);
             for (int i = 0; i < targetChains.size(); i++) {
                 ArrayList<String> targetChain =  targetChains.get(i);
                 depth+= targetChain.size();
             }
         }
         if (targetChains.size()==0) {
            depth = 1;
         }
         else {
            depth = depth/targetChains.size();
         }
         return depth;
    }

    public int getAverageDepthByWord () {
         int depth = 0;
        int nSynsets = 0;

        Set keySet = entryToSynsets.keySet();
         Iterator keys = keySet.iterator();
         while (keys.hasNext()) {
             String key = (String) keys.next(); ///word
             ArrayList<String> wordSynsets = entryToSynsets.get(key);
             for (int w = 0; w < wordSynsets.size(); w++) {
                 String wordSynset =  wordSynsets.get(w);
                 ArrayList<ArrayList<String>> targetChains = new ArrayList<ArrayList<String>>();
                 getMultipleHyperChain(wordSynset, targetChains);
                 for (int i = 0; i < targetChains.size(); i++) {
                     nSynsets++;  /// increment
                     ArrayList<String> targetChain =  targetChains.get(i);
                     depth+= targetChain.size();
                 }
             }
         }
         if (keySet.size()==0) {
            depth = 1;
         }
         else {
            depth = depth/(keySet.size()+nSynsets);
         }
         return depth;
    }

    public int getAverageDepthForWord (String word) {
         int depth = 0;
         int nSynsets = 0;
         if (entryToSynsets.containsKey(word)) {
             ArrayList<String> wordSynsets = entryToSynsets.get(word);
             nSynsets = wordSynsets.size();
             for (int w = 0; w < wordSynsets.size(); w++) {
                 String wordSynset =  wordSynsets.get(w);
                 ArrayList<ArrayList<String>> targetChains = new ArrayList<ArrayList<String>>();
                 getMultipleHyperChain(wordSynset, targetChains);
                 for (int i = 0; i < targetChains.size(); i++) {
                     nSynsets++;  /// increment
                     ArrayList<String> targetChain =  targetChains.get(i);
                     depth+= targetChain.size();
                 }
             }
         }
        if (nSynsets>0) {
             depth = depth/nSynsets;
         }
         return depth;
    }

    public int getMaxDepthBySynset () {
         int maxDepth = 0;
         Set keySet = hyperRelations.keySet();
         Iterator keys = keySet.iterator();
         while (keys.hasNext()) {
             String key = (String) keys.next();
             ArrayList<ArrayList<String>> targetChains = new ArrayList<ArrayList<String>>();
             getMultipleHyperChain(key, targetChains);
             for (int i = 0; i < targetChains.size(); i++) {
                 ArrayList<String> targetChain =  targetChains.get(i);
                 if (targetChain.size()>maxDepth) {
                     maxDepth+= targetChain.size();
                 }
             }
         }
         return maxDepth;
    }

    public int getMaxDepthByWord () {
        int maxDepth = 0;
        Set keySet = entryToSynsets.keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next(); ///word
            ArrayList<String> wordSynsets = entryToSynsets.get(key);
            for (int w = 0; w < wordSynsets.size(); w++) {
                String wordSynset =  wordSynsets.get(w);
                ArrayList<ArrayList<String>> targetChains = new ArrayList<ArrayList<String>>();
                getMultipleHyperChain(wordSynset, targetChains);
                for (int i = 0; i < targetChains.size(); i++) {
                    ArrayList<String> targetChain =  targetChains.get(i);
                    if (targetChain.size()>maxDepth) {
                        maxDepth+= targetChain.size();
                    }                }
            }
        }
        return maxDepth;
    }


    /**
     * @deprecated
     * This function assume single hyperonyms only, use getMultipleHypernyms instead
     * @param source
     * @param targetChain
     */
    public void getRelationChain (String source, ArrayList<String> targetChain) {
       // System.out.println("source = " + source);
            if (!targetChain.contains(source)) {
                targetChain.add(source);
            }
            if (hyperRelations.containsKey(source)) {
                ArrayList<String> targets = hyperRelations.get(source);
               // System.out.println("targets.toString() = " + targets.toString());
                for (int i = 0; i < targets.size(); i++) {
                    String target =  targets.get(i);
                    if (!target.equals(source)) {
                        if (!targetChain.contains(target)) {
                            targetChain.add(target);
                             //   System.out.println("source = " + source);
                             //   System.out.println("target = " + target);
                            getRelationChain(target, targetChain);
                        }
                        else {
                            ///circular
                            break;
                        }
                    }
                    else {
                        /// circular
                        break;
                    }
                }
            }
    }

    /**
     * @deprecated
     *  only assume single hypernyms
     * @param source
     * @param targetChain
     */
    public void getPlainHyperChain (String source, ArrayList<String> targetChain) {
            if (hyperRelations.containsKey(source)) {
                ArrayList<String> targets = hyperRelations.get(source);
               // System.out.println("targets.toString() = " + targets.toString());
                for (int i = 0; i < targets.size(); i++) {
                    String target =  targets.get(i);
                    if (!target.equals(source)) {
                        if (!targetChain.contains(target)) {
                            targetChain.add(target);
                             //   System.out.println("source = " + source);
                             //   System.out.println("target = " + target);
                            getPlainHyperChain(target, targetChain);
                        }
                        else {
                            ///circular
                            break;
                        }
                    }
                    else {
                        /// circular
                        break;
                    }
                }
            }
    }

    /**
     * Cuts the tree after the first hypernym
     * @param source
     * @param targetChain
     */
    public void getSingleHyperChain (String source, ArrayList<String> targetChain) {
            if (hyperRelations.containsKey(source)) {
                ArrayList<String> targets = hyperRelations.get(source);
               // System.out.println("targets.toString() = " + targets.toString());
                for (int i = 0; i < targets.size(); i++) {
                    String target =  targets.get(i);
                    if (!target.equals(source)) {
                        if (!targetChain.contains(target)) {
                            targetChain.add(target);
                             //   System.out.println("source = " + source);
                             //   System.out.println("target = " + target);
                            getSingleHyperChain(target, targetChain);
                        }
                        else {
                            ///circular
                            break;
                        }
                    }
                    else {
                        /// circular
                        break;
                    }
                    break;
                }
            }
    }

    /**
     * Cuts the tree after the first hypernym
     * @param source
     */
    public ArrayList<String> getSingleHyperChain (String source) {
        ArrayList<String> targetChain = new ArrayList<String>();
            if (hyperRelations.containsKey(source)) {
                ArrayList<String> targets = hyperRelations.get(source);
               // System.out.println("targets.toString() = " + targets.toString());
                for (int i = 0; i < targets.size(); i++) {
                    String target =  targets.get(i);
                    if (!target.equals(source)) {
                        if (!targetChain.contains(target)) {
                            targetChain.add(target);
                             //   System.out.println("source = " + source);
                             //   System.out.println("target = " + target);
                            getSingleHyperChain(target, targetChain);
                        }
                        else {
                            ///circular
                            break;
                        }
                    }
                    else {
                        /// circular
                        break;
                    }
                    break;
                }
            }
        return targetChain;
    }


    /**
     * Cuts the tree after the first hypernym of the first synset
     * @param word
     * @param targetChain
     */
    public void getSingleHyperChainForWord (String word, ArrayList<String> targetChain) {
        ArrayList<String> synsetIds = entryToSynsets.get(word);
        if (synsetIds!=null && synsetIds.size()>0) {
            String source = synsetIds.get(0);
            if (hyperRelations.containsKey(source)) {
                ArrayList<String> targets = hyperRelations.get(source);
                // System.out.println("targets.toString() = " + targets.toString());
                for (int i = 0; i < targets.size(); i++) {
                    String target =  targets.get(i);
                    if (!target.equals(source)) {
                        if (!targetChain.contains(target)) {
                            targetChain.add(target);
                            //   System.out.println("source = " + source);
                            //   System.out.println("target = " + target);
                            getSingleHyperChain(target, targetChain);
                        }
                        else {
                            ///circular
                            break;
                        }
                    }
                    else {
                        /// circular
                        break;
                    }
                    break;
                }
            }
        }
    }

    public ArrayList<String> getHyperonymsForWord (String word) {
        ArrayList<String> hypers = new ArrayList<String>();
        ArrayList<String> synsetIds = entryToSynsets.get(word);
        if (synsetIds!=null && synsetIds.size()>0) {
            String source = synsetIds.get(0);
            if (hyperRelations.containsKey(source)) {
                ArrayList<String> targets = hyperRelations.get(source);
                // System.out.println("targets.toString() = " + targets.toString());
                for (int i = 0; i < targets.size(); i++) {
                    String target =  targets.get(i);
                    if (!hypers.contains(target)) {
                        hypers.add(target);
                    }
                }
            }
        }
        return hypers;
    }

    public void getMultipleHyperChain (String source, ArrayList<ArrayList<String>> targetChain) {
        ArrayList<String> initChain = new ArrayList<String>();
        initChain.add(source);
        getHyperChains(source, targetChain, initChain);
        if (DEBUG) System.out.println("FINAL RESULT:"+getMatrixPrint(targetChain));
    }

    public String getMatrixPrint(ArrayList<ArrayList<String>> targetChain) {
        String str = "hyperchain:\n";
        for (int i = 0; i < targetChain.size(); i++) {
            ArrayList<String> strings = targetChain.get(i);
            str += "["+i+":";
            for (int j = 0; j < strings.size(); j++) {
                String s = strings.get(j);
                str += s+"#";
            }
            str += "]\n";
        }
        return str;
    }

    public void getHyperChains (String source, ArrayList<ArrayList<String>> targetChain, ArrayList<String> chain) {
            if (DEBUG) System.out.println("source = " + source);
            if (hyperRelations.containsKey(source)) {
                ArrayList<String> targets = hyperRelations.get(source);
                if (DEBUG) System.out.println("targets.toString() = " + targets.toString());
                        /// for every target hypernym, we will extend the chain, depth-first
                    if (DEBUG) System.out.println("chain = " + chain);
                    /// we first copy the chain to the currentChain so that we can fork in case of multipe hypernyms
                    for (int i = 0; i < targets.size(); i++) {
                        //// store the currentChain
                        ArrayList<String> currentChain = new ArrayList<String>();
                        for (int c = 0; c < chain.size(); c++) {
                            String s = chain.get(c);
                            currentChain.add(s);
                        }
                        String target =  targets.get(i);
                        if (DEBUG) {
                            String str = "target:"+i+":"+target+":";
                            ArrayList<String> synonym = synsetToEntries.get(target);
                            for (int j = 0; j < synonym.size(); j++) {
                                String s = synonym.get(j);
                                str += s+";";
                            }
                            System.out.println(str);
                        }
                        if (!currentChain.contains(target)) {
                            currentChain.add(target);
                            if (DEBUG) System.out.println("adding target  = " +  target );
                            if (DEBUG) System.out.println("currentChain = " + currentChain);
                            /// DEPTH FIRST ITERATION WITH CURRENTCHAIN
                            getHyperChains(target, targetChain, currentChain);
                        }
                        else {
                            ///circular so now we can add it to the result structure, the chain is done
                            //currentChain.add(target);
                            if (DEBUG) System.out.println("CIRCULAR: target"+target+" is in chain:"+chain.toString());
                            //targetChain.add(chain);
                            if (DEBUG) System.out.println(getMatrixPrint(targetChain));
                        }
                    }
            }
            else {
                //// if there are no hypernyms to add to the chain,the chain is ready and can be added to the result Array
                if (DEBUG) System.out.println("NOT IN HYPER RELATIONS MAP");
                targetChain.add(chain);
                if (DEBUG) System.out.println(getMatrixPrint(targetChain));
            }
    }

    public void getXposRelationChain (String source, ArrayList<String> targetChain) {
        /// first add the non-transitives
        if (otherRelations.containsKey(source)) {
            ArrayList<String> others = otherRelations.get(source);
            for (int i = 0; i < others.size(); i++) {
                String other =  others.get(i);
                if (!other.equals(source)) {
                    if (!targetChain.contains(other)) {
                        //  System.out.println("other = " + other);
                        targetChain.add(other);
                    }
                }
            }
        }
        if (hyperRelations.containsKey(source)) {
            ArrayList<String> targets = hyperRelations.get(source);
            for (int i = 0; i < targets.size(); i++) {
                String target =  targets.get(i);
                if (!target.equals(source)) {
                    if (!targetChain.contains(target)) {
                        targetChain.add(target);
                        //    System.out.println("source = " + source);
                        //    System.out.println("target = " + target);
                        getXposRelationChain(target, targetChain);
                    }
                    else {
                        ///circular
                        break;
                    }
                }
                else {
                    /// circular
                    break;
                }
            }
        }
    }


    /**
     * Retrieves the synsets from a set of words with the highest sharing factor.
     * If different words are in the same synset, this synset gets preferred
     * @param words
     * @return
     */
    public ArrayList<String> getSharedSynsets (String [] words) {
        ArrayList<String> sharedSynsets = new ArrayList<String>();
        Integer topCount = 0;
        HashMap<String, Integer> synsetMap = new HashMap<String, Integer>();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            ArrayList<String> synsetIds = entryToSynsets.get(word);
            if (synsetIds==null) {
                synsetIds = entryToSynsets.get(word.replaceAll(" ", "-"));
            }
            if (synsetIds==null) {
                synsetIds = entryToSynsets.get(word.replaceAll(" ", "_"));
            }
            if (synsetIds==null) {
                synsetIds = entryToSynsets.get(word.replaceAll(" ", ""));
            }
            if (synsetIds!=null) {
            for (int j = 0; j < synsetIds.size(); j++) {
                String synsetId = synsetIds.get(j);
                if (synsetMap.containsKey(synsetId)) {
                    Integer cnt = synsetMap.get(synsetId);
                    cnt++;
                    if (cnt > topCount) {
                        topCount = cnt;
                    }
                    synsetMap.put(synsetId, cnt);
                } else {
                    if (1 > topCount) {
                        topCount = 1;
                    }
                    synsetMap.put(synsetId, 1);
                }
            }
            }
        }
        Set keysSet = synsetMap.keySet();
        Iterator<String> keys = keysSet.iterator();
        while (keys.hasNext()) {
            String synsetid = keys.next();
            Integer cnt = synsetMap.get(synsetid);
            if (cnt==topCount) {
                sharedSynsets.add(synsetid);
            }
        }
        return sharedSynsets;
    }

    public ArrayList<String> getSharedHypers (String [] words) {
        ArrayList<String> sharedHypers = new ArrayList<String>();
        Integer topCount = 0;
        HashMap<String, Integer> hyperMap = new HashMap<String, Integer>();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            ArrayList<String> synsetIds = getHyperonymsForWord(word);
            for (int j = 0; j < synsetIds.size(); j++) {
                String synsetId = synsetIds.get(j);
                if (hyperMap.containsKey(synsetId)) {
                    Integer cnt = hyperMap.get(synsetId);
                    cnt++;
                    if (cnt>topCount) {
                        topCount = cnt;
                    }
                    hyperMap.put(synsetId, cnt);
                }
                else {
                    if (1>topCount) {
                        topCount = 1;
                    }
                    hyperMap.put(synsetId,1);
                }
            }
        }
        Set keysSet = hyperMap.keySet();
        Iterator<String> keys = keysSet.iterator();
        while (keys.hasNext()) {
            String synsetid = keys.next();
            Integer cnt = hyperMap.get(synsetid);
            if (cnt==topCount) {
                sharedHypers.add(synsetid);
            }
        }
        return sharedHypers;
    }

/*    public String getFirstEntryForSynset (String synset) {
        String word = synset;
        if (synsetToEntries.containsKey(synset)) {
            ArrayList<String> entries = synsetToEntries.get(synset);
            word = entries.get(0);
        }

        return word;
    }*/

  //  public String getSynonymsForSynset (String synset) {
    public String getFirstEntryForSynset (String synset) {
        String word = synset;
        if (synsetToEntries.containsKey(synset)) {
            ArrayList<String> entries = synsetToEntries.get(synset);
            for (int i = 0; i < entries.size(); i++) {
                word += ";"+ entries.get(i);
            }
        }

        return word;
    }

    public String toHyperString (ArrayList<String> synsets) {
        String str = "[";
        for (int i = 0; i < synsets.size(); i++) {
            String synset = synsets.get(i);
            if (synsetToEntries.containsKey(synset)) {
                ArrayList<String> entries = synsetToEntries.get(synset);
                for (int j = 0; j < entries.size(); j++) {
                    str += ";"+ entries.get(j);
                }
                str += "->";
            }
            else {
                str += synset+"->";
            }
        }
        str +="]";
        return str;
    }
    //[bombard,     eng-30-01507914-v, eng-30-05045208-n, eng-30-10413429-n, eng-30-01508368-v, eng-30-00104539-n, eng-30-10709529-n, eng-30-01511706-v, eng-30-00045250-n, eng-30-00104249-n, eng-30-00809790-a, eng-30-00842550-a, eng-30-03563460-n, eng-30-04011827-n, eng-30-14691822-n, eng-30-01850315-v, eng-30-00279835-n, eng-30-00280586-n, eng-30-01523724-a, eng-30-01526062-a, eng-30-08478482-n, eng-30-10336234-n]
    //[bombardment, eng-30-00978413-n, eng-30-01131902-v, eng-30-00972621-n, eng-30-01118449-v, eng-30-01119169-v, eng-30-00955060-n, eng-30-01109863-v, eng-30-00407535-n, eng-30-00927373-a, eng-30-01515280-a, eng-30-00030358-n, eng-30-01643657-v, eng-30-01649999-v, eng-30-02367363-v, eng-30-00029378-n, eng-30-00023100-n, eng-30-00002137-n, eng-30-00692329-v, eng-30-00001740-n]

    static void addToCount (ArrayList<Integer> levelCount, int i) {
        while (i>=levelCount.size()) {
            levelCount.add(0);
        }
        Integer cnt = levelCount.get(i);
        cnt++;
        levelCount.set(i, cnt);
    }

    public KafSense GetLowestCommonSubsumer (ArrayList<String> synsets) {
        KafSense lcs = null;
        boolean DEBUG = false;
       /* if (synsets.contains("eng-30-00941990-v")) {
            DEBUG = true;
        }*/
        HashMap<String, ArrayList<Integer>> synsetCount = new HashMap<String, ArrayList<Integer>>();
        for (int i = 0; i < synsets.size(); i++) {
            String synset = synsets.get(i);
            if (DEBUG) System.out.println("synset = " + synset);
            ArrayList<String> coveredHypers = new ArrayList<String>();
            ArrayList<ArrayList<String>> hyperChains = new ArrayList<ArrayList<String>>();
            getMultipleHyperChain(synset, hyperChains);
            if (DEBUG) {
                for (int j = 0; j < hyperChains.size(); j++) {
                    ArrayList<String> strings = hyperChains.get(j);
                    System.out.println("hypers = " + strings);
                }
            }
                ///breadth first
            ///makes sure the lowest occurrences of a hyper is counted
            boolean hasMoreHypers = true;
            int level = 0;
            while (hasMoreHypers) {
                hasMoreHypers = false;
                for (int j = 0; j < hyperChains.size(); j++) {
                    ArrayList<String> strings = hyperChains.get(j);
                    if (level < strings.size()) {
                        String hyper = strings.get(level);
                        if (!coveredHypers.contains(hyper)) {
                            coveredHypers.add(hyper); /// make sure every hyper is counted once
                            if (synsetCount.containsKey(hyper)) {
                                ArrayList<Integer> levelCounts = synsetCount.get(hyper);
                                addToCount(levelCounts, level);
                                synsetCount.put(hyper, levelCounts);
                            } else {
                                ArrayList<Integer> levelCounts = new ArrayList<Integer>();
                                addToCount(levelCounts, level);
                                synsetCount.put(hyper, levelCounts);
                            }
                        }
                        if (strings.size()>level+1) {
                            hasMoreHypers = true;
                        }
                    }
                }
                level++;
            }
            ///depth first
            /*for (int j = 0; j < hyperChains.size(); j++) {
                ArrayList<String> strings = hyperChains.get(j);
                for (int k = 0; k < strings.size(); k++) {
                    String hyper = strings.get(k);
                    if (!coveredHypers.contains(hyper)) {
                        coveredHypers.add(hyper); /// make sure every hyper is counted once
                        if (synsetCount.containsKey(hyper)) {
                            ArrayList<Integer> levelCounts = synsetCount.get(hyper);
                            addToCount(levelCounts, k);
                            synsetCount.put(hyper, levelCounts);
                        } else {
                            ArrayList<Integer> levelCounts = new ArrayList<Integer>();
                            levelCounts.add(1);
                            synsetCount.put(hyper, levelCounts);
                        }
                    }
                }
            }*/
        }
        int lowestLevel = -1;
        String lcsHyper = "";
        Set keySet = synsetCount.keySet();
        Iterator<String> keys = keySet.iterator() ;
        while (keys.hasNext()) {
            String key = keys.next();
            ArrayList<Integer> levelCount = synsetCount.get(key);
            if (DEBUG) {
                System.out.println("hyper = " + key);
                System.out.println("levelCount.toString() = " + levelCount.toString());
            }
            for (int i = 0; i < levelCount.size(); i++) {
                Integer integer = levelCount.get(i);
                if (integer.equals(synsets.size())) {
                    /// this is a hyper that occurs for all the synsets
                    if (lcsHyper.isEmpty()) {
                        lowestLevel = i;
                        lcsHyper = key;
                    }
                    else if (i<lowestLevel) {
                        lowestLevel = i;
                        lcsHyper = key;
                    }
                }
            }
        }
        if (!lcsHyper.isEmpty()) {
            lcs = new KafSense();
            lcs.setResource(resource);
            lcs.setSensecode(lcsHyper);
            lcs.setConfidence(1.0 / (lowestLevel+1));
            if (DEBUG) {
                System.out.println("lcs.getSensecode() = " + lcs.getSensecode());
                System.out.println("lcs.getConfidence() = " + lcs.getConfidence());
                System.out.println("lowestLevel = " + lowestLevel);
            }
        }

        return lcs;
    }

}
