package vu.wntools.util;

import eu.kyotoproject.kaf.KafWordForm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by piek on 11/05/15.
 */
public class CatToConll {

    static public void main (String[] args) {
        String pathToTextFile = "";
       // pathToTextFile = "/Users/piek/Desktop/MasterLanguage/CAT_XML_std-off_export_2015-05-13_11_28_34/hobbit.txt.xml";
        pathToTextFile = "/Users/piek/Desktop/MasterLanguage/CAT_XML_std-off_export_2015-05-13_11_28_34/NapoleonBO.txt.xml";

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--cat-file") && args.length>i+1) {
                pathToTextFile = args[i+1];
            }
        }
        String labeledText = "";
        CatParser catParser = new CatParser("ANNOTATION");
        catParser.relationnName = "COHERENCE";
        catParser.parseFile(pathToTextFile);
        labeledText += "Tokenid\tWord"+catParser.attributeListToString()+"\tCAT:"+"COHERENCE\n";
        for (int i = 0; i < catParser.kafWordFormArrayList.size(); i++) {
            KafWordForm kafWordForm = catParser.kafWordFormArrayList.get(i);
            labeledText += kafWordForm.getWid()+"\t"+kafWordForm.getWf();
            if (catParser.tokenToTerm.containsKey(kafWordForm.getWid())) {
                String termId = catParser.tokenToTerm.get(kafWordForm.getWid());
                if (catParser.annotations.containsKey(termId)) {
                    ArrayList<CatParser.Annotation> annotations = catParser.annotations.get(termId);
                    labeledText += catParser.annotationsToString(annotations);
                }
                else {
                    for (int j = 0; j < catParser.attributeList.size(); j++) {
                        labeledText += "\t";
                    }
                }
                labeledText += "\t"+catParser.relationListToString(termId);
            }
            else {
                for (int j = 0; j < catParser.attributeList.size(); j++) {
                    labeledText += "\t";
                }
                labeledText += "\t";
            }
            labeledText += "\n";
        }
        try {
            OutputStream fos = new FileOutputStream(pathToTextFile+".csv");
            fos.write(labeledText.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println(labeledText);
    }


}
