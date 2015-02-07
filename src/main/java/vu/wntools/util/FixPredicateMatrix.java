package vu.wntools.util;

import java.io.*;

/**
 * Created by piek on 1/19/15.
 */
public class FixPredicateMatrix {


    /**
     * 0 id:eng
     * 1 id:v
     * 2 id:abandon.01
     * 3 id:A0
     * 4 vn:leave-51.2
     * 5 vn:51.2
     * 6 vn:NULL
     * 7 vn:NULL
     * 8 vn:abandon
     * 9 vn:Theme		-> role
     * 10 wn:abandon%2:38:00
     * 11 mcr:ili-30-02076676-v
     * 12 fn:Departing
     * 13 fn:abandon.v
     * 14 fn:Theme	-> role
     * 15 pb:abandon.01
     * 16 pb:0		-> role
     * 17 mcr:0
     * 18 mcr:factotum
     * 19 mcr:Removing
     * 20 mcr:BoundedEvent;Location;
     * 21 mcr:motion
     * 22 mcr:leave%2:38:01
     * 23 wn:6
     * 24 wn:002
     * 25 eso:Leaving
     * 26 eso:NULL	   	-> role
     * 27 SEMLINK;PREDICATE_MAPPING;ESO3

     */


    static public void main (String[] args) {
        String pathToPredicateMatrixFile = "/Code/vu/WordnetTools/resources/PredicateMatrix_withESO.v0.2.txt";
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
            String[] headers = null;
            FileInputStream fis = new FileInputStream(pathToPredicateMatrixFile);
            OutputStream fos = new FileOutputStream(pathToPredicateMatrixFile+".role");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";

            while (in.ready() && (inputLine = in.readLine()) != null) {
                if (inputLine.trim().length() > 0) {
                    String[] fields = inputLine.split("\t");
                    // System.out.println("fields = " + fields.length);
                    if (fields.length == 1) {
                        fields = inputLine.split(" ");
                    }
                    String str = "";
                    if (fields.length>1) {
                        /**
                         * 0 id:eng
                         * 1 id:v
                         * 2 id:abandon.01
                         * 3 id:A0
                         * 4 vn:leave-51.2
                         * 5 vn:51.2
                         * 6 vn:NULL
                         * 7 vn:NULL
                         * 8 vn:abandon
                         * 9 vn:Theme		-> role
                         * 10 wn:abandon%2:38:00
                         * 11 mcr:ili-30-02076676-v
                         * 12 fn:Departing
                         * 13 fn:abandon.v
                         * 14 fn:Theme	-> role
                         * 15 pb:abandon.01
                         * 16 pb:0		-> role
                         * 17 mcr:0
                         * 18 mcr:factotum
                         * 19 mcr:Removing
                         * 20 mcr:BoundedEvent;Location;
                         * 21 mcr:motion
                         * 22 mcr:leave%2:38:01
                         * 23 wn:6
                         * 24 wn:002
                         * 25 eso:Leaving
                         * 26 eso:NULL	   	-> role
                         * 27 SEMLINK;PREDICATE_MAPPING;ESO3

                         */


                        fields[0] = "pm-id:"+fields[0].substring(3);
                        fields[1] = "pm-pos-id:"+fields[1].substring(3);
                        fields[2] = "pm-sense-id:"+fields[2].substring(3);
                        fields[3] = "pm-role:"+fields[3].substring(3);
                        fields[4] = "vn-class:"+fields[4].substring(3);
                        fields[5] = "vn-class-nr:"+fields[5].substring(3);
                        fields[6] = "vn-subclass:"+fields[6].substring(3);
                        fields[7] = "vn-subclass-nr:"+fields[7].substring(3);
                        fields[8] = "vn-lemma:"+fields[8].substring(3);
                        fields[9] = "vn-role:"+fields[9].substring(3);
                        //fields[10] = "wn-sense:"+fields[10].substring(3);
                        //fields[11] = "mcr-ili:"+fields[11].substring(4);
                        //fields[12] = "fn-frame:"+fields[12].substring(3);
                        fields[13] = "fn-entry:"+fields[13].substring(3);
                        fields[14] = "fn-role:"+fields[14].substring(3);
                        fields[15] = "pb-sense:"+fields[15].substring(3);
                        fields[16] = "pb-role:"+fields[16].substring(3);
                        fields[17] = "mcr-class:"+fields[17].substring(4);
                        fields[18] = "mcr-class:"+fields[18].substring(4);
                        fields[19] = "mcr-class:"+fields[19].substring(4);
                        fields[20] = "mcr-class:"+fields[20].substring(4);
                        fields[21] = "mcr-sumo:"+fields[21].substring(4);
                        fields[22] = "mcr-sense:"+fields[22].substring(4);
                        fields[23] = "wn-file:"+fields[23].substring(3);
                        fields[24] = "wn-sense-nr:"+fields[24].substring(3);
                        //fields[25] = "eso-class:"+fields[25].substring(4);
                        if (fields.length>=27) {
                            fields[26] = "eso-role:" + fields[26].substring(4);
                        }
                    }
                    for (int i = 0; i < fields.length; i++) {
                        String field = fields[i];
                        str += field+"\t";
                    }
                    str = str.trim()+"\n";
                        fos.write(str.getBytes());
                }
            }
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
