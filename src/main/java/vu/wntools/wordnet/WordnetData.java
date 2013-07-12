package vu.wntools.wordnet;

import java.io.FileOutputStream;
import java.io.IOException;
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
    private HashMap<String, ArrayList<String>> hyperRelations = new HashMap<String, ArrayList<String>>();
    private HashMap<String, ArrayList<String>> otherRelations = new HashMap<String, ArrayList<String>>();
    public HashMap<String, ArrayList<String>> entryToSynsets = new HashMap<String, ArrayList<String>>();
    public HashMap<String, ArrayList<String>> synsetToEntries = new HashMap<String, ArrayList<String>>();
    public HashMap<String, ArrayList<String>> childRelations = new HashMap<String, ArrayList<String>>();
    private int nAverageNounDepth = 0;
    private int nAverageVerbDepth = 0;
    private int nAverageAdjectiveDepth = 0;


    public WordnetData() {
        init();
    }

    public void init () {
        hyperRelations = new HashMap<String, ArrayList<String>>();
        otherRelations = new HashMap<String, ArrayList<String>>();
        entryToSynsets = new HashMap<String, ArrayList<String>>();
        synsetToEntries = new HashMap<String, ArrayList<String>>();
        childRelations = new HashMap<String, ArrayList<String>>();
        nAverageNounDepth = 0;
        nAverageVerbDepth = 0;
        nAverageAdjectiveDepth = 0;
    }

    public HashMap<String, ArrayList<String>> getHyperRelations() {
        return hyperRelations;
    }

    public void setHyperRelations(HashMap<String, ArrayList<String>> hyperRelations) {
        this.hyperRelations = hyperRelations;
    }

    public void addHyperRelation(String sourceId, ArrayList<String> targetIds) {
        this.hyperRelations.put(sourceId, targetIds);
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
        this.otherRelations.put(sourceId, targetIds);
    }


    public void writeTreeString (WordnetData wordnetData, ArrayList<String> topNodes, int level, FileOutputStream fos) {
        String str = "";
        for (int i = 0; i < topNodes.size(); i++) {
            String hper = topNodes.get(i);
            str = "";
            if (wordnetData.childRelations.containsKey(hper)) {
                for (int j = 0; j < level; j++) {
                    str += "  ";

                }
                str += hper+"\n";
                //  System.out.println("str = " + str);
                try {
                    fos.write(str.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                ArrayList<String> children = wordnetData.childRelations.get(hper);
                if (children.size()>0) {
                    writeTreeString(wordnetData, children, level+1, fos);
                }
                else {
                    //     System.out.println("leaf node.getSynset() = " + node.getSynset());
                }
            }
        }
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
                   topNodes.add(synsetId);
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
                   synonyms.add(entry);
                   synsetToEntries.put(synsetId, synonyms);
                }
                else {
                    ArrayList<String> synonyms = new ArrayList<String>();
                    synonyms.add(entry);
                    synsetToEntries.put(synsetId, synonyms);
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



    public int getAverageDepthBySynset () {
         int depth = 0;
         Set keySet = hyperRelations.keySet();
         Iterator keys = keySet.iterator();
         while (keys.hasNext()) {
             String key = (String) keys.next();
             ArrayList<ArrayList<String>> targetChains = new ArrayList<ArrayList<String>>();
             getMultipleHyperChain(key, targetChains);
             for (int i = 0; i < targetChains.size(); i++) {
                 ArrayList<String> targetChain =  targetChains.get(i);
                 depth+= targetChain.size();
             }
         }
         if (keySet.size()==0) {
            depth = 0;
         }
         else {
            depth = depth/keySet.size();
         }
         return depth;
    }

    public int getAverageDepthByWord () {
         int depth = 0;
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
                     depth+= targetChain.size();
                 }
             }
         }
         if (keySet.size()==0) {
            depth = 0;
         }
         else {
            depth = depth/keySet.size();
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

    public void getMultipleHyperChain (String source, ArrayList<ArrayList<String>> targetChain) {
        ArrayList<String> initChain = new ArrayList<String>();
        initChain.add(source);
        getHyperChains(source, targetChain, new ArrayList<String>());
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

    //[bombard,     eng-30-01507914-v, eng-30-05045208-n, eng-30-10413429-n, eng-30-01508368-v, eng-30-00104539-n, eng-30-10709529-n, eng-30-01511706-v, eng-30-00045250-n, eng-30-00104249-n, eng-30-00809790-a, eng-30-00842550-a, eng-30-03563460-n, eng-30-04011827-n, eng-30-14691822-n, eng-30-01850315-v, eng-30-00279835-n, eng-30-00280586-n, eng-30-01523724-a, eng-30-01526062-a, eng-30-08478482-n, eng-30-10336234-n]
    //[bombardment, eng-30-00978413-n, eng-30-01131902-v, eng-30-00972621-n, eng-30-01118449-v, eng-30-01119169-v, eng-30-00955060-n, eng-30-01109863-v, eng-30-00407535-n, eng-30-00927373-a, eng-30-01515280-a, eng-30-00030358-n, eng-30-01643657-v, eng-30-01649999-v, eng-30-02367363-v, eng-30-00029378-n, eng-30-00023100-n, eng-30-00002137-n, eng-30-00692329-v, eng-30-00001740-n]

}
