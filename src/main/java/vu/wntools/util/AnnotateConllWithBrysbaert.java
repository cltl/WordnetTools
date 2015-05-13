package vu.wntools.util;

import java.io.*;
import java.util.HashMap;

/**
 * Created by piek on 16/04/15.
 */
public class AnnotateConllWithBrysbaert {

    static public void main (String[] args) {
        int averageDepth = 0;
        String pathToCsv = "";
        String pathToConll = "";
        String nFields = "";
        pathToCsv = "/Users/piek/Desktop/MasterLanguage/software/resources/Concreteness ratings Brysbaert et al.csv";
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
            else if (arg.equals("--fields") && args.length>i+1) {
                nFields = args[i+1];
            }
        }
        dict = readCsvToHashMap(pathToCsv,nFields);

        try {
            String outputFile = pathToConll;
            int idx = outputFile.lastIndexOf(".");
            if (idx>-1) {
                outputFile = outputFile.substring(0, idx)+".anno.csv";
            }
            else {
                outputFile += ".anno.csv";
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
                if (dict.containsKey("HEADER")) {
                    str = dict.get("HEADER");
                    inputLine += str+"\n";
                    nColumns = str.split(";").length;
                }
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
                        for (int i = 0; i < nColumns; i++) {
                          inputLine += "\t";
                        }
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
     * stimulus;List;Concrete_m;Concrete_sd;Number_of_ratings;Number_of_N-respones;Number_of_subjects
     aai;4;4.07;1.21;14;1;15
     aaien;5;4.13;0.99;15;0;15
     aak;1;4.67;0.52;6;9;15
     */

    static HashMap<String, String> readCsvToHashMap (String filePath, String nFields) {
        String [] fields = nFields.split("#");
        HashMap<String, String> dict = new HashMap<String, String>();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            if (in.ready()&&(inputLine = in.readLine()) != null) {
                String [] substrings = inputLine.split(";");
                String str = "";
                for (int i = 1; i < substrings.length; i++) {
                    if (nFields.contains(new Integer(i).toString())) {
                   /* if (i>max) {
                        break;
                    }*/
                        str += "\tBrysbaert:" + substrings[i];
                    }
                }
                dict.put("HEADER", str);
            }
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (!inputLine.trim().isEmpty()) {
                    String [] substrings = inputLine.split(";");
                    String word = substrings[0].trim();
                    String str = "";
                    for (int i = 1; i < substrings.length; i++) {
                        if (nFields.contains(new Integer(i).toString())) {
                           /* if (i > max) {
                                break;
                            }*/
                            String substring = substrings[i];
                            str += "\t" + substring;
                        }
                    }
                    dict.put(word, str);
                }
            }
            return dict;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dict;
    }
}
