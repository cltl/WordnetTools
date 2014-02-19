package vu.wntools.wordnet;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 5/22/13
 * Time: 10:59 PM
 * To change this template use File | Settings | File Templates.
 */

public class SynsetNode {
    private String synset;
    private String synsetId;
    private int depth;
    private int freq;
    private int cum;
    private int nDescendants;
    private ArrayList<SynsetNode> children;

    public SynsetNode() {
        this.synset = "";
        this.synsetId = "";
        this.nDescendants = 0;
        this.depth = 0;
        this.freq = 0;
        this.cum = 0;
        this.children = new ArrayList<SynsetNode>();
    }

    public int getCum() {
        return cum;
    }

    public void setCum(int cum) {
        this.cum = cum;
    }

    public void addCum(int cum) {
        this.cum += cum;
    }

    public void incrementCum() {
        this.cum++;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public void addFreq(int freq) {
        this.freq +=freq;
    }

    public void incremFreq() {
        this.freq++;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getSynsetId() {
        return synsetId;
    }

    public void setSynsetId(String synsetId) {
        this.synsetId = synsetId;
    }

    public String getSynset() {
        return synset;
    }

    public void setSynset(String synset) {
        this.synset = synset;
    }

    public int getnDescendants() {
        return nDescendants;
    }

    public void setnDescendants(int nDescendants) {
        this.nDescendants = nDescendants;
    }

    public void incrementDescendants() {
        this.nDescendants++;
    }

    public ArrayList<SynsetNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<SynsetNode> children) {
        this.children = children;
    }

    public void addChildren(SynsetNode child) {
        this.children.add(child);
    }

    @Override
    public String toString() {
        return "SynsetNode{" +
                "synsetId='" + synsetId + '\'' +
                "synset='" + synset + '\'' +
                ", nDescendants=" + nDescendants +
                ", depth=" + depth +
                ", freq=" + freq +
                ", cum=" + cum +
                ", value =" + nDescendants*depth +
                ", children=" + children.size() +
                "}\n";
    }
    public String toCsv() {
        return synsetId +
                "\t" + synset +
                "\t" + nDescendants +
                "\t" + depth +
                "\t" + freq +
                "\t" + cum +
                "\t" + nDescendants*depth +
                "\t" + children.size()+
                "\n";
    }

    static public String toCsvHeader () {
       String keystr = "synset id\tsynset\tdescendants\tdepth\tfreq\tcumulation\tvalue(descendants*depth)\tchildren\n";
       return keystr;
    }
}
