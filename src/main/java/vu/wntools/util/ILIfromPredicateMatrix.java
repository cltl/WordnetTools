package vu.wntools.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 1/23/15.
 */
public class ILIfromPredicateMatrix {

    static public void main (String[] args) {
        String pathToPredicateMatrixFile = "/Code/vu/WordnetTools/resources/PredicateMatrix_withESO.v0.2.txt";
        HashMap<String,ArrayList<String>> iliToSynMap = new HashMap<String, ArrayList<String>>();
        try {
           /*
           VN_CLASS VN_CLASS_NUMBER VN_SUBCLASS VN_SUBCLASS_NUMBER VN_LEMA WN_SENSE VN_THEMROLE FN_FRAME FN_LEXENT FN_ROLE PB_ROLESET PB_ARG MCR_ILIOFFSET MCR_DOMAIN MCR_SUMO MC_LEXNAME
vn:comprehend-87.2 vn:87.2 vn:null vn:null vn:misconstrue wn:misconstrue%2:31:01 vn:Experiencer fn:NULL fn:NULL fn:NULL pb:misconstrue.01 pb:0 mcr:ili-30-00619869-v mcr:factotum mcr:Communication mcr:cognition
vn:comprehend-87.2 vn:87.2 vn:null vn:null vn:misconstrue wn:misconstrue%2:31:01 vn:Attribute fn:NULL fn:NULL fn:NULL pb:misconstrue.01 pb:1 mcr:ili-30-00619869-v mcr:factotum mcr:Communication mcr:cognition
vn:comprehend-87.2 vn:87.2 vn:null vn:null vn:misconstrue wn:misconstrue%2:31:01 vn:Stimulus fn:NULL fn:NULL fn:NULL pb:misconstrue.01 pb:NULL mcr:ili-30-00619869-v mcr:factotum mcr:Communication mcr:cognition
vn:comprehend-87.2 vn:87.2 vn:null vn:null vn:misinterpret wn:misinterpret%2:31:02 vn:Experiencer fn:NULL fn:NULL fn:NULL pb:misinterpret.01 pb:0 mcr:ili-30-00619869-v mcr:factotum mcr:Communication mcr:cognition
vn:comprehend-87.2 vn:87.2 vn:null vn:null vn:misinterpret wn:misinterpret%2:31:02 vn:Attribute fn:NULL fn:NULL fn:NULL pb:misinterpret.01 pb:2 mcr:ili-30-00619869-v mcr:factotum mcr:Communication mcr:cognition

1_VN_CLASS      2_VN_CLASS_NUMBER       3_VN_SUBCLASS   4_VN_SUBCLASS_NUMBER    5_VN_LEMA       6_VN_ROLE       7_WN_SENSE      8_MCR_iliOffset 9_FN_FRAME      10_FN_LE        11_FN_FRAME_ELEMENT     12_PB_ROLESET   13_PB_ARG       14_MCR_BC       15_MCR_DOMAIN   16_MCR_SUMO     17_MCR_TO       18_MCR_LEXNAME  19_MCR_BLC      20_WN_SENSEFREC 21_WN_SYNSET_REL_NUM    22_SOURC
vn:accept-77	vn:77	vn:NULL	vn:NULL	vn:accept	vn:Agent	wn:accept%2:31:01	mcr:ili-30-00719231-v	fn:Receiving	fn:NULL	fn:Theme	pb:accept.01	pb:0	mcr:0	mcr:factotum	mcr:IntentionalPsychologicalProcess	mcr:Agentive;Dynamic;	mcr:cognition	mcr:act%2:41:00	wn:12	wn:009	SEMLINK;FN_FE
vn:accept-77	vn:77	vn:NULL	vn:NULL	vn:accept	vn:Agent	wn:accept%2:31:01	mcr:ili-30-00719231-v	fn:Respond_to_proposal	fn:NULL	fn:Speaker	pb:accept.01	pb:0	mcr:0	mcr:factotum	mcr:IntentionalPsychologicalProcess	mcr:Agentive;Dynamic;	mcr:cognition	mcr:act%2:41:00	wn:12	wn:009	SEMLINK;FN_FE

            */
            FileInputStream fis = new FileInputStream(pathToPredicateMatrixFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            String wnField = "";
            String iliField="";
            while (in.ready() && (inputLine = in.readLine()) != null) {
                wnField = "";
                iliField = "";
                if (inputLine.trim().length() > 0) {
                    String[] fields = inputLine.split("\t");
                    // System.out.println("fields = " + fields.length);
                    if (fields.length == 1) {
                        fields = inputLine.split(" ");
                    }
                    for (int i = 0; i < fields.length; i++) {
                        String field = fields[i];
                        if (field.startsWith("wn:") && field.indexOf("%")>-1) {
                            wnField = field.trim().substring(3);
                        }
                        else if (field.startsWith("mcr:ili")) {
                            iliField = field.trim().substring(4);
                        }
                    }
                    if (!wnField.isEmpty() && !iliField.isEmpty()) {
                        if (iliToSynMap.containsKey(iliField)) {
                            ArrayList<String> syns = iliToSynMap.get(iliField);
                            if (!syns.contains(wnField)) {
                                syns.add(wnField);
                                iliToSynMap.put(iliField, syns);
                            }
                        }
                        else {
                            ArrayList<String> syns = new ArrayList<String>();
                            syns.add(wnField);
                            iliToSynMap.put(iliField, syns);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            OutputStream fos = new FileOutputStream(pathToPredicateMatrixFile+".ili");
            Set keySet = iliToSynMap.keySet();
            Iterator<String> keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                ArrayList<String> syns = iliToSynMap.get(key);
                String str = key+"\t";
                for (int i = 0; i < syns.size(); i++) {
                    String s = syns.get(i);
                    str += s+";";
                }
                str += "\n";
                fos.write(str.getBytes());
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
