package vu.wntools.corpus;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 5/23/13
 * Time: 6:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordData {

    private String word;
    private int freq;
    private int dispersion;

    public WordData () {
        word = "";
        freq = 0;
        dispersion = 0;
    }

    public int getDispersion() {
        return dispersion;
    }

    public void setDispersion(int dispersion) {
        this.dispersion = dispersion;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

}
