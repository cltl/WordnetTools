package vu.wntools.util;

import java.io.*;

/**
 * Created by piek on 04/06/15.
 */
public class FixGoogleTranslateOutput {

    static public void main (String[] args) {
        String pathToFile = "/Users/piek/Desktop/GWG/nl/translations/trans/total.txt";
        try {
            OutputStream fosGood = new FileOutputStream(pathToFile+".good");
            OutputStream fosFixed = new FileOutputStream(pathToFile+".fixed");
            OutputStream fosRest = new FileOutputStream(pathToFile+".rest");
            FileInputStream fis = new FileInputStream(pathToFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                    String [] fields = inputLine.split("#");
                    if (fields.length!=2) {
                    }
                    else {
                        String key = fields[0].trim();
                        String gloss = fields[1].trim();
                        if (key.indexOf(" ")>1) {
                            String [] keyFields = key.split(" ");
                            int keyPart = -1;
                            for (int i = 0; i < keyFields.length; i++) {
                                String keyField = keyFields[i];
                                if (validKey(keyField)) {
                                    keyPart = i;
                                    key = keyField;
                                }
                            }
                            if (keyPart>-1) {
                                String glossInsert = "";
                                for (int i = 0; i < keyFields.length; i++) {
                                    String keyField = keyFields[i];
                                    if (i!=keyPart) {
                                        glossInsert += keyField+" ";
                                    }
                                }
                                gloss = glossInsert+gloss;
                                String str = key+" # "+gloss+"\n";
                                fosFixed.write(str.getBytes());
                            }
                            else {
/*
                                String str = inputLine+"\n";
                                fosRest.write(str.getBytes());
*/
                            }
                        }
                        else if (validKey(key)) {
                            String str = inputLine+"\n";
                            fosGood.write(str.getBytes());
                        }
                        else {
/*
                            odwn-n # 10-100677673 part V.E. garment around the waist
                            odwn-n # 10-100678002-track route V.E. walk
*/
                            if (key.equals("odwn-n") && gloss.startsWith("10-")) {
                               // System.out.println(key+" # " + gloss);

                                key = "odwn-"+gloss.substring(0,12)+"-n";
                                gloss = gloss.substring(13);

                                String str = key+" # "+gloss+"\n";
                                fosFixed.write(str.getBytes());
                            }
                            else {
                                String str = inputLine + "\n";
                                fosRest.write(str.getBytes());
                            }
                        }
                    }
                }
            }
            fosFixed.close();
            fosGood.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean validKey (String key) {
        //eng-30-02152278-v
        //odwn-10-101748785-v

        if (key.startsWith("odwn-10-")
                || key.length()==19 & (key.endsWith("-v") || key.endsWith("-n"))) {
            /// valid odwn key
            return true;

        }
        else if (key.startsWith("eng-30-")
                || key.length()==17 & (key.endsWith("-v") || key.endsWith("-n"))) {
            /// valid pwn key
            return true;
        }
        else {
           // System.out.println("key = " + key);
            return false;
        }
    }
    static String fixKey (String phrase) {
        String key = "";
        return key;
    }
}
