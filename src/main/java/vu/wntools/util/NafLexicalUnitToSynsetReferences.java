package vu.wntools.util;

import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kaf.KafSense;
import eu.kyotoproject.kaf.KafTerm;
import eu.kyotoproject.kaf.LP;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.util.ArrayList;

/**
 * Created by piek on 9/10/14.
 */
public class NafLexicalUnitToSynsetReferences {


        static final String layer = "terms";
        static final String name = "vua-lu-to-synset-tagger";
        static final String version = "1.0";

    static public void main (String[] args) {
        String pathToKafFile = "/Tools/wordnet-tools.0.1/input/kaf-processing/example-lu.naf";
        String pathToWnLmfFile = "/Tools/wordnet-tools.0.1/resources/cornetto2.1.lmf.xml";
        String format = "naf";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ((arg.equalsIgnoreCase("--kaf-file")) && (args.length>(i+1))) {
                pathToKafFile = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--naf-file")) && (args.length>(i+1))) {
                pathToKafFile = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--wn-lmf")) && (args.length>(i+1))) {
                pathToWnLmfFile = args[i+1];
            }
            else if ((arg.equalsIgnoreCase("--format")) && (args.length>(i+1))) {
                format = args[i+1];
            }
        }

        String strBeginDate = eu.kyotoproject.util.DateUtil.createTimestamp();
        String strEndDate = null;
        WordnetLmfSaxParser wordnetLmfSaxParser = new WordnetLmfSaxParser();
        wordnetLmfSaxParser.parseFile(pathToWnLmfFile);
        wordnetLmfSaxParser.wordnetData.buildLexicalUnitIndex();
        KafSaxParser kafSaxParser = new KafSaxParser();
        if (pathToKafFile.isEmpty()) {
            kafSaxParser.parseFile(System.in);
        }
        else {
            kafSaxParser.parseFile(pathToKafFile);
        }
        for (int i = 0; i < kafSaxParser.kafTermList.size(); i++) {
            KafTerm kafTerm = kafSaxParser.kafTermList.get(i);
            for (int j = 0; j < kafTerm.getSenseTags().size(); j++) {
                KafSense kafSense = kafTerm.getSenseTags().get(j);
                if (wordnetLmfSaxParser.wordnetData.lexicalUnitsToSynsets.containsKey(kafSense.getSensecode())) {
                    ArrayList<String> synsets = wordnetLmfSaxParser.wordnetData.lexicalUnitsToSynsets.get(kafSense.getSensecode());
                    for (int k = 0; k < synsets.size(); k++) {
                        String synsetId = synsets.get(k);
                        KafSense synset = new KafSense();
                        synset.setSensecode(synsetId);
                        synset.setConfidence(kafSense.getConfidence());
                        synset.setResource(kafSense.getResource());
                        kafTerm.addSenseTag(synset);
                    }
                }
            }
        }

        strEndDate = eu.kyotoproject.util.DateUtil.createTimestamp();
        LP lp = new LP(name,version, strBeginDate, strBeginDate, strEndDate);
        kafSaxParser.getKafMetaData().addLayer(name, lp);
        if (format.equalsIgnoreCase("naf")) {
            kafSaxParser.writeNafToStream(System.out);
/*            try {
                OutputStream fos = new FileOutputStream("/Tools/ontotagger-v1.0/naf-example/89007714_06.ont.srl.naf");
                kafSaxParser.writeNafToStream(fos);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
        else if (format.equalsIgnoreCase("kaf")) {
            kafSaxParser.writeKafToStream(System.out);
        }
    }

}
