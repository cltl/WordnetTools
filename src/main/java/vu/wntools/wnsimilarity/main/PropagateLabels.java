package vu.wntools.wnsimilarity.main;

import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 6/13/13
 * Time: 1:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class PropagateLabels {

    static public ArrayList<String> readFileToArrayList (String file) {
        ArrayList<String> words = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                    words.add(inputLine.trim());
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;

    }

    static final String usage = "Takes a labeled list of synsets and propagates these labels to all other synsets related through specified semantic relations:\n" +
            "--wn-lmf\t\t<path to a wordnet file in wordn-lmf format> \n" +
            "--relations\t\t<path to a text file with the relations y=that should be used to build the graph> \n" +
            "--input-file\t\t<path to the input file that contains of a synset identifier and a label on each separate line>\n" +
            "--separator\t\t<String that separates the synsets from the label>\n";

    static public void main (String [] args) {
        if (args.length==0) {
            System.out.println("Usage = \n" + usage);
            return;
        }
        //System.out.println("Usage = \n" + usage);

        ArrayList<String> relations = new ArrayList<String>();
        relations.add("has_hyperonym");
        relations.add("HAS_HYPERONYM");
        String pathToRelationsFile = "";
        String separator = ",";
        String pathToWordnetLmfFile = "/Users/kyoto/Desktop/CDB/2013-MAY-18/Cornetto-LMF/cornetto.lmf";
        String pathToInputFile = "/Projects/THeSiS/june-12/toppen-bc-ont.csv";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--wn-lmf") && (args.length-1>i)) {
                pathToWordnetLmfFile = args[i+1].trim();
            }
            else if (arg.equals("--input-file") && (args.length-1>i)) {
                pathToInputFile = args[i+1].trim();
            }
            else if (arg.equals("--relations") && (args.length-1>i)) {
                pathToRelationsFile = args[i+1].trim();
            }
            else if (arg.equals("--separator") && (args.length-1>i)) {
                separator = args[i+1].trim();
            }
        }
        WordnetLmfSaxParser wordnetLmfSaxParser = new WordnetLmfSaxParser();
        if (!pathToRelationsFile.isEmpty()) {
            relations = readFileToArrayList(pathToRelationsFile);
        }
        wordnetLmfSaxParser.setRelations(relations);
        wordnetLmfSaxParser.parseFile(pathToWordnetLmfFile);
        System.out.println("wordnetLmfSaxParser.wordnetData.getHyperRelations().size() = " + wordnetLmfSaxParser.wordnetData.getHyperRelations().size());
        System.out.println("wordnetLmfSaxParser.wordnetData.entryToSynsets.size() = " + wordnetLmfSaxParser.wordnetData.entryToSynsets.size());
        wordnetLmfSaxParser.wordnetData.buildSynsetIndex();
        try {
            HashMap<String, String> typeMap = new HashMap<String, String>();
            FileOutputStream fos = new FileOutputStream (pathToInputFile+".synset-ont.txt");
            FileInputStream fis = new FileInputStream(pathToInputFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                    String [] fields = inputLine.split(separator);
                    if (fields.length==2) {
                        typeMap.put(fields[0].trim(), fields[1].trim());
                        String str = fields[0].trim() + " sc_subClassOf "+ fields[1].trim()+"\n";
                        fos.write(str.getBytes());
                    }
                }
            }
            Set keySet = wordnetLmfSaxParser.wordnetData.getHyperRelations().keySet();
            Iterator keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                ArrayList<ArrayList<String>> hyperChains = new ArrayList<ArrayList<String>>();
                wordnetLmfSaxParser.wordnetData.getMultipleHyperChain(key, hyperChains);
                for (int i = 0; i < hyperChains.size(); i++) {
                    ArrayList<String> hypers =  hyperChains.get(i);
                    for (int k = 0; k < hypers.size(); k++) {
                        String s = hypers.get(k);
                        if (typeMap.containsKey(s)) {
                            String type = typeMap.get(s);
                            String str = key + " sc_subClassOf "+ type+"\n";
                            fos.write(str.getBytes());
                        }
                    }
                }
            }
            in.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
