package vu.wntools.wnsimilarity.corpus;

import java.io.*;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 8/14/12
 * Time: 9:55 PM
 * To change this template use File | Settings | File Templates.
 *
 * This class reads a file with the frequency of the subsumers in wordnet in a corpus
 * The structure of the file is a list of synset-id/frequency :
 *
    d_n-27579/28999
    d_n-10440/99
    d_n-37214/2948
    d_n-37213/376

 For Dutch such a file is created using the function CumulateCorpusFrequencyUsingCdbSynsets
 */

public class SubsumersFrequencies {

    public HashMap<String, Long> data;
    public long nWords;
    public long maxFreq;

    public SubsumersFrequencies() {
        this.data = new HashMap<String, Long>();
        this.nWords = 0;
        this.maxFreq = 0;
    }

    public int readSubsumerFrequenciesFromFile (String pathToSubsumersFrequenciesFile) {
        if (!new File(pathToSubsumersFrequenciesFile).exists())   {
            System.out.println("Cannot find pathToSubsumersFrequenciesFile = " + pathToSubsumersFrequenciesFile);
            return -1;
        }
        try {
            FileInputStream fissub = new FileInputStream(pathToSubsumersFrequenciesFile);
            InputStreamReader isr = new InputStreamReader(fissub);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            //// FIRST LINE IS SUPPOSED TO CONTAIN THE NUMBER OF WORDS IN THE CORPUS
            if (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("nwords:")) {
                    String str = inputLine.substring(inputLine.indexOf(":")+1);
                   // System.out.println("nr of words = " + str);
                    try {
                        nWords = Long.parseLong(str);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                /*

                */
               // System.out.println("inputLine = " + inputLine);
                String [] fields = inputLine.split("/");
                if (fields.length==2) {
                    String hyper = fields[0].trim();
                    String freqString = fields[1].trim();
                    Long freq = Long.parseLong(freqString);
                    data.put(hyper, freq);
                    if (freq>maxFreq) {
                        maxFreq = freq;
                    }
                }
            }
            System.out.println("Nr of subsumers = " + data.size());
        } catch (IOException e) {
            e.printStackTrace();
            //To change body of catch statement use File | Settings | File Templates.
            return -1;
        }
        return data.size();
    }
}
