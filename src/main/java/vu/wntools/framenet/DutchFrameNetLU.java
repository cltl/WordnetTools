package vu.wntools.framenet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by piek on 12/05/15.
 */
public class DutchFrameNetLU {

//    <lu hasAnnotation="false" frameID="1903" frameName="Accuracy" status="Created" name="accurately.adv" ID="14135"/>
        static public void main (String[] args) {
            String pathToPredicateMatrixFile = "/Code/vu/WordnetTools/resources/PredicateMatrix_nl_withESO.v0.2.role.3.txt";
            HashMap<String,ArrayList<String>> iliToSynMap = new HashMap<String, ArrayList<String>>();
            try {
                   /*
                   lu-lemma:weten
                   odwn-eq_synonym:r_v-10123
                   pm-id:eng
                   pm-pos-id:v
                   pm-sense-id:know.01
                   vn-class:characterize-29.2
                   vn-class-nr:29.2
                   vn-subclass:characterize-29.2-1
                   vn-subclass-nr:29.2-1
                   vn-lemma:cognise wn:cognise%2:31:00
                   mcr:ili-30-00594621-v
                   fn:Familiarity
                   pb-sense:know.01
                   mcr-class:1
                   mcr-class:psychology
                   mcr-class:Remembering
                   mcr-class:Experience;Mental;Property;
                   mcr-sumo:cognition
                   mcr-sense:know%2:31:01 w
                   n-file:585
                   wn-sense-nr:012
                   SEMLINK;FN_FE;SYNONYMS
                   fn-pb-role:Cognizer#0
                   vn-lemma:cognize
                   wn:cognize%2:31:00
                   vn-lemma:know
                   wn:know%2:31:01 S
                   EMLINK;FN_FE
                   fn-pb-role:Entity#1
                   fn-pb-role:Role#2
                   pm-pos-id:n
                   pm-sense-id:cognoscenti.01
                   pm-sense-id:connoisseur.01
                   pm-sense-id:know-how.01
                   pm-sense-id:knowledge.01
                   pm-sense-id:known.01
                   pm-sense-id:mind.01
                   pm-sense-id:mystery.01
                   pm-sense-id:renown.01
                   pm-sense-id:unknown.01
                   pm-id:spa pm-sense-id:conocer.1.default
                   pm-sense-id:conocer.1.impersonal
                   pm-sense-id:conocer.1.passive
                   pm-sense-id:entender.3.default
                   pm-sense-id:enterar.1.default
                   pm-sense-id:saber.1.default
                   pm-sense-id:saber.1.passive
                   pm-sense-id:saber.2.default
                   pm-sense-id:saber.4.default
                   pm-sense-id:autoconocimiento.1.default
                   pm-sense-id:conocimiento.1.default
                   pm-sense-id:conocimiento.2.default
                   pm-sense-id:conocimiento.3.default
                   pm-sense-id:conocimiento.4.default

                    */
                OutputStream fos = new FileOutputStream(new File(pathToPredicateMatrixFile).getParent()+"/"+"nl-luIndex.xml");
                FileInputStream fis = new FileInputStream(pathToPredicateMatrixFile);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader in = new BufferedReader(isr);
                String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<?xml-stylesheet type=\"text/xsl\" href=\"luIndex.xsl\"?>\n" +
                        "<luIndex xsi:schemaLocation=\"schema/luIndex.xsd\" xmlns=\"http://framenet.icsi.berkeley.edu\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n";
                fos.write(str.getBytes());
                String inputLine = "";
                String luField = "";
                String posField="";
                String fnField="";
                while (in.ready() && (inputLine = in.readLine()) != null) {
                    posField = "";
                    luField = "";
                    fnField = "";
                    if (inputLine.trim().length() > 0) {
                        String[] fields = inputLine.split("\t");
                        // System.out.println("fields = " + fields.length);
                        if (fields.length == 1) {
                            fields = inputLine.split(" ");
                        }
                        for (int i = 0; i < fields.length; i++) {
                            String field = fields[i];
                            if (field.startsWith("fn:")) {
                                fnField = field.trim().substring(3);
                            }
                            else if (field.startsWith("lu-lemma:")) {
                                luField = field.trim().substring(9);
                            }
                            else if (field.startsWith("pm-pos-id:")) {
                                posField = field.trim().substring(10);
                            }
                        }
                        if (!luField.isEmpty() && !fnField.isEmpty()) {
                            str = "<lu hasAnnotation=\"false\" frameName=\""+fnField+"\" name=\""+luField+"."+posField+"\"/>"+"\n";
                            fos.write(str.getBytes());
                        }
                    }
                }
                str = "</luIndex>\n";
                fos.write(str.getBytes());
                fos.close();
                fis.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
}
