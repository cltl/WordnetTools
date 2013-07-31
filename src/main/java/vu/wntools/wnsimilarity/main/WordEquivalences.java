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
        String pathToLex = "";
        String pathToInputFile = "";
        if (args.length==0) {
            System.out.println(usage);
        }

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
                      words.add(inputLine.trim());
                  }
            }

            FileOutputStream fos = new FileOutputStream(pathToLex+".equi");
            FileInputStream fislex = new FileInputStream(pathToLex);
            isr = new InputStreamReader(fislex);
            in = new BufferedReader(isr);
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                String [] fields = inputLine.split(" ");
                if (fields.length>1) {
                    String word = fields[0];
                    if (words.contains(word)) {
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
