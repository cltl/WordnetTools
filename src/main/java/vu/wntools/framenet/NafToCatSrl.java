package vu.wntools.framenet;

import eu.kyotoproject.kaf.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by piek on 24/03/2017.
 */
public class NafToCatSrl {

    static public void main(String[] args) {
        String nafFile = "/Users/piek/Desktop/DutchFrameNet/solar/SolarSystem-wiki-nl.processed.naf";
        KafSaxParser kafSaxParser = new KafSaxParser();
        kafSaxParser.parseFile(nafFile);
        String cat = makeCat(new File(nafFile).getName(), kafSaxParser);
        try {
            OutputStream fos = new FileOutputStream(nafFile+".cat");
            fos.write(cat.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String makeCat(String filename, KafSaxParser kafSaxParser) {


        String cat = "<Document doc_name=\""+filename+"\">\n";
        /// tokens
        for (int i = 0; i < kafSaxParser.kafWordFormList.size(); i++) {
            KafWordForm kafWordForm = kafSaxParser.kafWordFormList.get(i);
            cat += "<token t_id=\"" + kafWordForm.getWid()+"\"";
            cat += " sentence=\"" +kafWordForm.getSent()+"\"";
            cat += ">"+kafWordForm.getWf()+"</token>\n";
        }
        // <Markables
        cat += "<Markables>\n";
        for (int i = 0; i < kafSaxParser.kafEventArrayList.size(); i++) {
            KafEvent kafEvent = kafSaxParser.kafEventArrayList.get(i);
            /*
            <EVENT_MENTION m_id="17" time="NON_FUTURE" aspect="NONE" special_cases="NONE" certainty="CERTAIN" polarity="POS" tense="PRESENT" modality="" pred="omvatten" comment="" pos="VERB"  >
<token_anchor t_id="142"/>
</EVENT_MENTION>
             */
            cat += "<EVENT_MENTION m_id=\""+kafEvent.getId()+"\"";
            String lemma = "";
            String tokens = "";
            for (int j = 0; j < kafEvent.getSpanIds().size(); j++) {
                String s = kafEvent.getSpanIds().get(j);
                KafTerm kafTerm = kafSaxParser.getTerm(s);
                if (kafTerm!=null) {
                    lemma += kafTerm.getLemma();
                    for (int k = 0; k < kafTerm.getSpans().size(); k++) {
                        String tokenId = kafTerm.getSpans().get(k);
                        tokens += "<token_anchor t_id=\""+tokenId+"\"/>\n";
                    }
                }

            }
            cat += " pred=\""+lemma+"\">\n";
            cat += tokens;
            cat += "</EVENT_MENTION>\n";
            for (int j = 0; j < kafEvent.getParticipants().size(); j++) {
                KafParticipant kafParticipant = kafEvent.getParticipants().get(j);
                cat += "<ENTITY_MENTION m_id=\""+kafParticipant.getId()+"\">\n";
                for (int k = 0; k < kafParticipant.getSpanIds().size(); k++) {
                    String s = kafParticipant.getSpanIds().get(k);
                    KafTerm kafTerm = kafSaxParser.getTerm(s);
                    if (kafTerm!=null) {
                        for (int l = 0; l < kafTerm.getSpans().size(); l++) {
                            String tokenId = kafTerm.getSpans().get(l);
                            cat += "<token_anchor t_id=\""+tokenId+"\"/>\n";
                        }
                    }
                }
                cat += "</ENTITY_MENTION>\n";
            }
        }
        /*
        <ENTITY_MENTION m_id="39" syntactic_type="NAM" head="" comment=""  >
<token_anchor t_id="60"/>
</ENTITY_MENTION>
<ENTITY_MENTION m_id="71" head="" syntactic_type="" comment=""  >
<token_anchor t_id="138"/>
</ENTITY_MENTION>
         */
        cat += "</Markables>\n";
        cat += "<Relations>\n";
        /*
        <HAS_PARTICIPANT r_id="264423" frame="" sem_role_framework="PROPBANK" sem_role="Arg0" comment="" frame_element="" >
<source m_id="11" />
<target m_id="47" />
</HAS_PARTICIPANT>
         */
        int nRoles = 0;
        for (int i = 0; i < kafSaxParser.kafEventArrayList.size(); i++) {
            KafEvent kafEvent = kafSaxParser.kafEventArrayList.get(i);
            for (int j = 0; j < kafEvent.getParticipants().size(); j++) {
                KafParticipant kafParticipant = kafEvent.getParticipants().get(j);
                nRoles++;
                cat += "<HAS_PARTICIPANT r_id=\""+nRoles+"\"";
                cat += " frame=\"\" sem_role_framework=\"PROPBANK\" sem_role=\""+kafParticipant.getRole()+"\" comment=\"\" frame_element=\"\" >\n";
                cat += "<source m_id=\""+kafEvent.getId()+"\" />\n";
                cat += "<target m_id=\""+kafParticipant.getId()+"\" />\n";
                cat += "</HAS_PARTICIPANT>\n";
            }
        }
        cat += "</Relations>\n";
        cat += "</Document>\n";

        return cat;
    }
}
