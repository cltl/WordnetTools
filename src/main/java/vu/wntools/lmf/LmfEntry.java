package vu.wntools.lmf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * Created by piek on 02/06/15.
 */
public class LmfEntry {

    /**
     * <LexicalEntry id="clapboard">
     *     <Lemma partOfSpeech="v" writtenForm="clapboard" />
     <Sense id="clapboard_1"
     synset="eng-30-01337412-v">
     <MonolingualExternalRefs>
     <MonolingualExternalRef externalReference="clapboard%2:35:00::" externalSystem="Wordnet3.0" />
     </MonolingualExternalRefs>
     </Sense>
     </LexicalEntry>

     * RBN entries
     * <LexicalEntry id="A-n-1" partOfSpeech="noun">
     <Lemma writtenForm="A"/>
     <WordForms>
     <WordForm writtenForm="A" grammaticalNumber="singular" article=""/>
     </WordForms>
     <Morphology/>
     <MorphoSyntax/>
     <SyntacticBehaviour/>
     <Sense senseId="o_n-107531373" synset="eng-30-06868986-n" definition="zesde toon van de diatonische en tiende toon van de chromatische toonschaal, uitgaande van de grondtoon C">

     */

    private String entryId;
    private String pos;
    private String writtenForm;
    private ArrayList<LmfSense> senses;

    public LmfEntry() {
        this.entryId = "";
        this.pos = "";
        this.senses = new ArrayList<LmfSense>();
        this.writtenForm = "";
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public ArrayList<LmfSense> getSenses() {
        return senses;
    }

    public void setSenses(ArrayList<LmfSense> senses) {
        this.senses = senses;
    }

    public void addSense(LmfSense sense) {
        this.senses.add(sense);
    }

    public String getWrittenForm() {
        return writtenForm;
    }

    public void setWrittenForm(String writtenForm) {
        this.writtenForm = writtenForm;
    }

    public Element toLmfXml(Document xmldoc) {
        Element root = xmldoc.createElement("LexicalEntry");
        if (!this.getEntryId().isEmpty()) {
            root.setAttribute("id", this.getEntryId());
        }
        Element lemma = xmldoc.createElement("Lemma");
        if (!this.getPos().isEmpty()) {
            lemma.setAttribute("partOfSpeech", this.getPos());
        }
        if (!this.getWrittenForm().isEmpty()) {
            lemma.setAttribute("writtenForm", this.getWrittenForm());
        }
        root.appendChild(lemma);
        for (int i = 0; i < senses.size(); i++) {
            LmfSense lmfSense = senses.get(i);
            root.appendChild(lmfSense.toLmfXML(xmldoc));
        }
        return root;

    }

}
