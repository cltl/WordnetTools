package vu.wntools.wnsimilarity.main;

import vu.wntools.wordnet.SynsetNode;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by piek on 30/04/15.
 */
public class TagWordListWithType {


    static public void main (String[] args) {

        String pathToWnLmFFile = "";
        String pathToWordList = "";
        pathToWnLmFFile = "/Users/piek/Desktop/NWA/keywords/resources/cornetto2.1.lmf.xml";
        pathToWordList = "/Users/piek/Desktop/NWA/keywords/term-lemma.txt";
        //pathToWnLmFFile = args[0];
        //pathToWordList = args[1];

        WordnetLmfSaxParser wordnetLmfSaxParser = new WordnetLmfSaxParser();
        wordnetLmfSaxParser.parseFile(pathToWnLmFFile);
        wordnetLmfSaxParser.wordnetData.buildLexicalUnitIndex();
        wordnetLmfSaxParser.wordnetData.buildSynsetIndex();
        try {
            OutputStream fos = new FileOutputStream(pathToWordList+".hyp");
            FileInputStream fis = new FileInputStream(pathToWordList);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (!inputLine.trim().isEmpty()) {
                    String str = inputLine;
                    String [] substrings = inputLine.split("\t");
                    String lastLemma = substrings[substrings.length-1];
                    if (lastLemma.indexOf(" ")==-1) {
                        int depth = GetDepthForWords.getAverageDepthForWord(wordnetLmfSaxParser.wordnetData, lastLemma);
                        if (depth > 5) {
                            str += "\t";
                            String synsets = "";
                            ArrayList<SynsetNode> hypers = ExpandWord.getWordHypernymsMinimalDepth(wordnetLmfSaxParser.wordnetData, lastLemma, depth);
                            for (int i = 0; i < hypers.size(); i++) {
                                SynsetNode synsetNode = hypers.get(i);
                                String[] synsetField = synsetNode.getSynset().split(",");
                                for (int j = 0; j < synsetField.length; j++) {
                                    String synonym = synsetField[j];
                                    if (!str.contains(synonym)) {
                                        str += synonym;
                                    }
                                }
                            }
                        }
                    }
                    str  += "\n";
                    fos.write(str.getBytes());
                }
            }
            in.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
