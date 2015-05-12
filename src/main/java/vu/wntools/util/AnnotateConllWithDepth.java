package vu.wntools.util;

import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.*;

/**
 * Created by piek on 16/04/15.
 */
public class AnnotateConllWithDepth {

    static public void main (String[] args) {
        int averageDepth = 0;
        String pathToWnLmfFile = "";
        String pathToConll = "";
//        pathToWnLmfFile = args[0];
//        pathToTextFile = args[1];
        pathToWnLmfFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.lmf.xml";
        pathToConll = "/Users/piek/Desktop/MasterLanguage/CAT_XML_std-off_export_2015-05-11_22_31_46/hobbit.txt.xml.csv";
        WordnetLmfSaxParser wordnetLmfSaxParser = new WordnetLmfSaxParser();
        wordnetLmfSaxParser.parseFile(pathToWnLmfFile);
        wordnetLmfSaxParser.wordnetData.buildLexicalUnitIndex();
        wordnetLmfSaxParser.wordnetData.buildSynsetIndex();
        int nDepth = 0;
        try {
            FileInputStream fis = new FileInputStream(pathToConll);
            String outputFile = pathToConll;
            int idx = outputFile.lastIndexOf(".");
            if (idx>-1) {
                outputFile = outputFile.substring(0, idx)+".depth.csv";
            }
            else {
                outputFile += ".depth.csv";
            }
            OutputStream fos = new FileOutputStream(outputFile);
            System.out.println("outputFile = " + outputFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            if (in.ready()&&(inputLine = in.readLine()) != null) {
               inputLine += "\tWN-DEPTH"+"\n";
                fos.write(inputLine.getBytes());
            }
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (!inputLine.trim().isEmpty()) {
                    String [] substrings = inputLine.split("\t");
                    String word = substrings[1].trim();
                   // System.out.println("word = " + word);
                    int depth = wordnetLmfSaxParser.wordnetData.getAverageDepthForWord(word);
                    if (depth>0) {
                      //  System.out.println("depth = " + depth);
                        nDepth++;
                        averageDepth += depth;
                        inputLine += "\t" + depth + "\n";
                    }
                    else {
                        inputLine += "\t" + depth + "\n";
                    }
                    fos.write(inputLine.getBytes());
                }
            }
           // String str = "\n"+"AverageDepth = "+averageDepth/nDepth+"\n";
           // fos.write(str.getBytes());
            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            //To change body of catch statement use File | Settings | File Templates.
        }
    }


}
