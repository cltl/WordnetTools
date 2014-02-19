package vu.wntools.wnsimilarity.corpus;

import vu.wntools.wordnet.CdbSynSaxParser;
import vu.wntools.wordnet.PwnSaxParser;
import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 4/17/12
 * Time: 4:28 PM
 * To change this template use File | Settings | File Templates.
 *
 * Approach:
 * We read the syn hyper relations and the lemma synset relations
 * We read a lemma + freq
 * We build up the list of hyper trees for each lemma, we store each hyper in a hashmap and increment the frequency with the frequency of the word
 * We save the hyper-frequency hashmap to be used to calculate the distances
 */
public class CumulateCorpusFrequency {

     /*
     worden/verb/3490102,00
kunnen/verb/1854277,00
zullen/verb/1570042,00
moeten/verb/1559258,00
jaar/noun/1482974,00
gaan/verb/1296115,00
komen/verb/1244479,00
maken/verb/949996,00
willen/verb/945113,00
zeggen/verb/893042,00
goed/adj/856420,00
groot/adj/828521,00
doen/verb/795530,00
      */


    static final String usage = "This program reads a wordnet file and a lemma frequency file and\n" +
                                "outputs a file with all subsumers of all meanings of all lemmas followed by\n" +
                                "the cumulative frequency of all descendants (following Resnik \n"+
                                "Use one of the following 3 formats to import a wordnet file:" +
                                "--cdb-file             <path to Cornetto export synset file\n"+
                                "--lmf-file             <path to the Wordnet LMF file\n" +
                                "--gwg-file             <path to the Global Wordnet Grid file\n"+
                                "---corpus-freq         <path to file with lemmas, pos and corpus frequencies, e.g. \"cat,noun,234560\" \n"+
                                "--separator            <character that is used to separate the fields in the corpus-freq file, e.g. \",\"\n"+
                                "--lemma-field          <nth position in corpus-freq file where the lemma can be found, counting from 0 to length-1\n, e.g. 0\n"+
                                "--pos-field          <nth position in corpus-freq file where the lemma can be found, counting from 0 to length-1\n, e.g. 1\n"+
                                "--freq-field          <nth position in corpus-freq file where the frequency can be found, counting from 0 to length-1\n, e.g. 2\n";
    /**
     *
     * @param args
     */
    static public void main (String [] args) {
        boolean parametersOK = true;
        WordnetData wordnetData = new WordnetData();
        String pathToWordnetFile = "";
        String pathToCorpusFrequencyFile = "";
        String separator = "/";
        int lemmaField = -1;
        int posField = -1;
        int freqField = -1;
        long nWords= 0;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            //System.out.println("arg = " + arg);
            if ((arg.equalsIgnoreCase("--cdb-file")) && args.length>i) {
                pathToWordnetFile = args[i+1];
                CdbSynSaxParser parser = new CdbSynSaxParser();
                parser.parseFile(pathToWordnetFile);
                wordnetData = parser.wordnetData;
            }
            else if ((arg.equalsIgnoreCase("--lmf-file")) && args.length>i) {
                pathToWordnetFile = args[i+1];
                WordnetLmfSaxParser parser = new WordnetLmfSaxParser();
                parser.parseFile(pathToWordnetFile);
                wordnetData = parser.wordnetData;
            }
            else if ((arg.equalsIgnoreCase("--gwg-file")) && args.length>i) {
                pathToWordnetFile = args[i+1];
                PwnSaxParser parser = new PwnSaxParser();
                parser.parseFile(pathToWordnetFile);
                wordnetData = parser.wordnetData;
            }
            else if ((arg.equalsIgnoreCase("--corpus-freq")) && args.length>i) {
                pathToCorpusFrequencyFile = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--separator")) && args.length>i) {
                separator = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--lemma-field")) && args.length>i) {
                try {
                    lemmaField = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            else if ((arg.equalsIgnoreCase("--pos-field")) && args.length>i) {
                try {
                    posField = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            else if ((arg.equalsIgnoreCase("--freq-field")) && args.length>i) {
                try {
                    freqField = Integer.parseInt(args[i+1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        if (pathToWordnetFile.isEmpty()) {
            System.out.println("Missing parameter " +
                    "--cdb-file: path to CDB synsets export file" +
                    "--lfm-file: path to WordnetLMF  file" +
                    "--gwg-file: path to Global Wordnet Grid file");
            parametersOK = false;
        }
        if (pathToCorpusFrequencyFile.isEmpty()) {
            System.out.println("Missing parameter --corpus-freq: path to file with corpus frequencies");
            parametersOK = false;
        }
        if (lemmaField<0) {
            System.out.println("You need to provide integer for lemma field");
            parametersOK = false;
        }
        if (freqField<0) {
            System.out.println("You need to provide integer for lemma field");
            parametersOK = false;
        }
        if (!parametersOK) {
            System.out.println(usage);
            return;
        }
        else {
            try {
                HashMap<String, Long> hyperFrequencies = new HashMap<String, Long>();
                System.out.println("pathToCorpusFrequencyFile = " + pathToCorpusFrequencyFile);
                System.out.println("pathToWordnetFile = " + pathToWordnetFile);
                System.out.println("separator = " + separator);
                System.out.println("lemmaField = " + lemmaField);
                System.out.println("posField = " + posField);
                System.out.println("freqField = " + freqField);
                FileOutputStream fos = new FileOutputStream(pathToCorpusFrequencyFile+".cum");
                FileInputStream fis = new FileInputStream(pathToCorpusFrequencyFile);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    /*
                    maken/verb/949996,00
                    willen/verb/945113,00
                    zeggen/verb/893042,00
                     */
                    String [] fields = inputLine.split(separator);
                    if ((fields.length>lemmaField) && (fields.length>freqField) && (fields.length>posField)){
                        nWords++;
                        String lemma = "";
                        String pos = "";
                        String freqString = "";
                        lemma = fields[lemmaField].trim();
                        if (posField>-1) pos = fields[posField].trim();
                        freqString = fields[freqField].trim();
                        Long freq = null;
                        try {
                            freq = Long.parseLong(freqString);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
/*
                        System.out.println("freqString = " + freqString);
                        System.out.println("freq = " + freq);
*/
                        if (wordnetData.entryToSynsets.containsKey(lemma)) {
                            ArrayList<String> synsets = wordnetData.entryToSynsets.get(lemma);
                            for (int i = 0; i < synsets.size(); i++) {
                                String synsetId = synsets.get(i);
                                ArrayList<ArrayList<String>> hyperChains = new ArrayList<ArrayList<String>>();
                                wordnetData.getMultipleHyperChain(synsetId, hyperChains);
                                for (int j = 0; j < hyperChains.size(); j++) {
                                    ArrayList<String> hypers = hyperChains.get(j);
                                    for (int k = 0; k < hypers.size(); k++) {
                                        String hyper = hypers.get(k);
                                        if (hyperFrequencies.containsKey(hyper)) {
                                            Long cnt = hyperFrequencies.get(hyper);
                                            cnt+= freq;
                                            hyperFrequencies.put(hyper, cnt);
                                        }
                                        else {
                                            hyperFrequencies.put(hyper, freq);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                String str = "nwords:"+nWords+"\n";
                fos.write(str.getBytes());
                fis.close();
                fis = new FileInputStream(pathToCorpusFrequencyFile);
                isr = new InputStreamReader(fis);
                in = new BufferedReader(isr);
                inputLine = "";
                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    /*
                    maken/verb/949996,00
                    willen/verb/945113,00
                    zeggen/verb/893042,00
                     */
                    String [] fields = inputLine.split(separator);
                    if ((fields.length>lemmaField) && (fields.length>freqField) && (fields.length>posField)){
                        String lemma = "";
                        String pos = "";
                        String freqString = "";
                        lemma = fields[lemmaField].trim();
                        if (posField>-1) pos = fields[posField].trim();
                        freqString = fields[freqField].trim();
                        Long freq = null;
                        try {
                            freq = Long.parseLong(freqString);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
/*
                        System.out.println("freqString = " + freqString);
                        System.out.println("freq = " + freq);
*/
                        if (wordnetData.entryToSynsets.containsKey(lemma)) {
                            ArrayList<String> synsets = wordnetData.entryToSynsets.get(lemma);
                            for (int i = 0; i < synsets.size(); i++) {
                                String synsetId = synsets.get(i);
                                if (hyperFrequencies.containsKey(synsetId)) {
                                    Long cnt = hyperFrequencies.get(synsetId);
                                    cnt+= freq;
                                    hyperFrequencies.put(synsetId, cnt);
                                }
                            }
                        }
                    }
                }
                fis.close();
                Set keySet = hyperFrequencies.keySet();
                Iterator keys = keySet.iterator();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    Long cnt = hyperFrequencies.get(key) ;
                    str = key+"/"+cnt+"\n";
                    fos.write(str.getBytes());
                }
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
