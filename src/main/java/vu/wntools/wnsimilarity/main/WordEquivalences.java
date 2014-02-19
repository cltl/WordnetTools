package vu.wntools.wnsimilarity.main;

import vu.wntools.wordnet.WordnetData;

import java.io.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 7/31/13
 * Time: 10:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordEquivalences {

    static final String usage = "\n" +
            "   --lex           <path to wordnet file in lmf format\n" +
            "   --input         <file with words on single lines separate with \"/\">\n"
            ;


    static public void main (String[] args) {
        WordnetData wordnetData = new WordnetData();
        String pathToLex = "/Tools/wordnet-tools.0.1/resources/cdb2.0-nldsynset_domain_graph_equi.lex";
        String pathToInputFile = "/Tools/wordnet-tools.0.1/input/sim-processing/official_mc_zonder_magier.input";
       /* if (args.length==0) {
            System.out.println(usage);
        }
*/
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ((arg.equalsIgnoreCase("--lex")) && args.length>i) {
                pathToLex = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--input")) && args.length>i) {
                pathToInputFile = args[i+1];
            }
        }
        try {
            FileInputStream fiswords = new FileInputStream(pathToInputFile);
            InputStreamReader isr = new InputStreamReader(fiswords);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            ArrayList<String> words = new ArrayList<String>();
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                  if (!inputLine.isEmpty()) {
                      String [] fields = inputLine.split("\t");
                      for (int i = 0; i < fields.length; i++) {
                          String field = fields[i];
                          if (!words.contains(field)) {
                              words.add(field);
                          }
                      }
                  }
            }

            FileOutputStream fos = new FileOutputStream(pathToInputFile+".equi");
            FileInputStream fislex = new FileInputStream(pathToLex);
            isr = new InputStreamReader(fislex);
            in = new BufferedReader(isr);
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                String [] fields = inputLine.split(" ");
                if (fields.length>1) {
                    String word = fields[0].trim();
                   // System.out.println("word = " + word);
                    if (words.contains(word)) {
                        inputLine+= "\n";
                        fos.write(inputLine.getBytes());
                    }
                }
            }
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
