package vu.wntools.util;

import java.io.*;
import java.util.HashMap;

/**
 * Created by piek on 16/04/15.
 */
public class AnnotateConllWithAdjectives {

    static public void main (String[] args) {
        String pathToCsv = "";
        String pathToConll = "";
        String label = "perceptual";
        pathToCsv = "/Users/piek/Desktop/MasterLanguage/software/resources/perceptual.csv";
        pathToConll = "/Users/piek/Desktop/MasterLanguage/CAT_XML_std-off_export_2015-05-13_11_28_34/NapoleonBO.txt.xml.csv";
        HashMap<String, String> dict = new HashMap<String, String>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--csv-annotation") && args.length>i+1) {
                pathToCsv = args[i+1];
            }
            else if (arg.equals("--conll") && args.length>i+1) {
                pathToConll = args[i+1];
            }
            else if (arg.equals("--label") && args.length>i+1) {
                label = args[i+1];
            }
        }
        dict = readCsvToHashMap(pathToCsv);

        try {
            String outputFile = pathToConll;
            int idx = outputFile.lastIndexOf(".");
            if (idx>-1) {
                outputFile = outputFile.substring(0, idx)+"."+label+".csv";
            }
            else {
                outputFile += "."+label+".csv";
            }
            OutputStream fos = new FileOutputStream(outputFile);
            System.out.println("outputFile = " + outputFile);
            FileInputStream fis = new FileInputStream(pathToConll);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            int nColumns = 0;
            String str = "";
            if (in.ready()&&(inputLine = in.readLine()) != null) {
                inputLine += "\tADJ:"+label+"\n";
                fos.write(inputLine.getBytes());
            }
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (!inputLine.trim().isEmpty()) {
                    String [] substrings = inputLine.split("\t");
                    String word = substrings[1].trim();
                    if (!dict.containsKey(word))  {
                        if (word.length() > 3) {
                            word = word.substring(0, word.length() - 1);
                            if  (!dict.containsKey(word)) {
                                word = word.substring(0, word.length() - 1);
                                if  (!dict.containsKey(word)) {
                                    if (word.length() > 4) {
                                        word = word.substring(0, word.length() - 1);
                                    }
                                }
                            }
                        }
                    }
                    if  (dict.containsKey(word)) {
                        str = dict.get(word);
                        inputLine += str.replace(";", "\t") + "\n";

                    }
                    else {
                        inputLine += "\t";
                        inputLine+= "\n";
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

    /**
     (on)diepingen	0.0	2
     schilders-fotografen	0.0	1
     rajasthaanse	0.0	3
     thorenaar	0.0	2

     */

    static HashMap<String, String> readCsvToHashMap (String filePath) {
        HashMap<String, String> dict = new HashMap<String, String>();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (!inputLine.trim().isEmpty()) {
                    String [] substrings = inputLine.split("\t");
                    String word = substrings[0].trim();
                    try {
                        Double prop = 100*(Double.parseDouble(substrings[1]));
                        Integer cnt = Integer.parseInt(substrings[2]);
                        String str = "";
                        if (cnt>10 && prop>0) {
                            str += "\t"+prop.intValue();
                            dict.put(word, str);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
            return dict;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dict;
    }
}
