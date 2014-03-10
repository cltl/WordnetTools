package vu.wntools.wnsimilarity.main;

import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kaf.KafSense;
import eu.kyotoproject.kaf.KafTerm;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 6/13/13
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class TagKafFilesWithSynsets {


    static public void main (String[] args) {
        String pathToKafFolder = "";
        String fileExtension = "";
        String pathToWordnetLmfFile = "";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--wn-lmf") && (args.length-1>i)) {
                pathToWordnetLmfFile = args[i+1].trim();
            }
            else if ((arg.equalsIgnoreCase("--input-folder")) && (args.length>(i+1))) {
                pathToKafFolder = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--extension")) && (args.length>(i+1))) {
                fileExtension = args[i+1];

            }
        }
        String lmfName = new File(pathToWordnetLmfFile).getName();
        WordnetLmfSaxParser wordnetLmfSaxParser = new WordnetLmfSaxParser();
        wordnetLmfSaxParser.parseFile(pathToWordnetLmfFile);
        System.out.println("wordnetLmfSaxParser.wordnetData.getHyperRelations().size() = " + wordnetLmfSaxParser.wordnetData.getHyperRelations().size());
        System.out.println("wordnetLmfSaxParser.wordnetData.entryToSynsets.size() = " + wordnetLmfSaxParser.wordnetData.entryToSynsets.size());
        wordnetLmfSaxParser.wordnetData.buildSynsetIndex();
        KafSaxParser kafSaxParser = new KafSaxParser();
        ArrayList<String> kafFiles = makeRecursiveFileListAll(pathToKafFolder, fileExtension);
        for (int f = 0; f < kafFiles.size(); f++) {
            String pathToKafFile =  kafFiles.get(f);
            System.out.println("pathToKafFile = " + pathToKafFile);
            kafSaxParser.parseFile(pathToKafFile);
            for (int i = 0; i < kafSaxParser.getKafTerms().size(); i++) {
                KafTerm kafTerm = kafSaxParser.getKafTerms().get(i);
                if (wordnetLmfSaxParser.wordnetData.entryToSynsets.containsKey(kafTerm.getLemma())) {
                    ArrayList<String> synsetIds = wordnetLmfSaxParser.wordnetData.entryToSynsets.get(kafTerm.getLemma());
                    for (int j = 0; j < synsetIds.size(); j++) {
                        String synsetId = synsetIds.get(j);
                        KafSense kafSense = new KafSense();
                        kafSense.setSensecode(synsetId);
                        kafSense.setResource(lmfName);
                        kafTerm.addSenseTag(kafSense);
                    }
                }
            }
            try {
                FileOutputStream fos = new FileOutputStream(pathToKafFile+".wordnet.kaf");
                kafSaxParser.writeNafToStream(fos);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    static public ArrayList<String> makeRecursiveFileListAll(String inputPath, String extension) {
            ArrayList<String> acceptedFileList = new ArrayList<String>();
            ArrayList<String>  nestedFileList = new ArrayList<String>();
            File[] theFileList = null;
            File lF = new File(inputPath);
            if ((lF.canRead()) && lF.isDirectory()) {
                theFileList = lF.listFiles();
                for (int i = 0; i < theFileList.length; i++) {
                    String newFilePath = theFileList[i].getAbsolutePath();
                    //   System.out.println("newFilePath = " + newFilePath);
                    if (theFileList[i].isDirectory()) {
                        nestedFileList = makeRecursiveFileListAll(newFilePath, extension);
                        for (int j = 0; j < nestedFileList.size(); j++) {
                            String s = nestedFileList.get(j);
                            if (s.endsWith(extension)) {
                                acceptedFileList.add(s);
                            }
                        }
                    } else {
                        if (newFilePath.endsWith(extension)) {
                            acceptedFileList.add(newFilePath);
                        }
                    }
                }
            }
            return acceptedFileList;
    }
}
