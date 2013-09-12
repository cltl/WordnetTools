package vu.wntools.wnsimilarity.corpus;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 7/31/13
 * Time: 10:37 PM
 * To change this template use File | Settings | File Templates.
 * This class is used to convert the semcor-cumulative frequency file to the WordnetTools format
 * We assume that the frequencies of synsets are already
 */
public class ConvertIcSemCor {
    static public void main(String [] args) {
        //String filePath = args[0];
        String filePath = "/Tools/wordnet-tools.0.1/resources/ic-semcor.dat";
        if (!new File(filePath).exists())   {
            System.out.println("Cannot find file = " + filePath);
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(filePath+".cum");
            FileInputStream fissub = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fissub);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            String str = "";
            int nLines = 0;
            //// FIRST LINE IS wnver wnver::eOS9lXC6GvMWznF1wkZofDdtbBU
            if (in.ready()&&(inputLine = in.readLine()) != null) {
                //////
            }
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                nLines++;
                /*
                    1740n 128767 ROOT
                    1930n 69661 -> this is a high value because the frequencies are passed on to this hypernym
                    2137n 59062
                    2452n 3669
                    2684n 39997
                    3553n 32734
                    3993n 0
                    4258n 20896
                    4475n 20800

                    ENG-30-01244853-v

                    ENG-30-00648224-v
                */
                // System.out.println("inputLine = " + inputLine);
                String [] fields = inputLine.split(" ");
                if (fields.length>=2) {
                    String synset = fields[0].trim();
                    String properSynset = "ENG-30-";
                    for (int i = 0; i < (9-synset.length()); i++) {
                        properSynset += "0";
                    }
                    properSynset += synset.substring(0, synset.length()-1);
                    properSynset += "-" + synset.substring(synset.length()-1);
                    String freqString = fields[1].trim();
                    if (!freqString.equals("0")) {
                      //  System.out.println("properSynset = " + properSynset);
                        str += properSynset+"/"+freqString+"\n";
                    }
                }
            }
            str = "nwords:"+nLines+"\n"+str;
            fos.write(str.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
