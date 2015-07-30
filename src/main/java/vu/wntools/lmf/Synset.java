package vu.wntools.lmf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import vu.wntools.wordnet.WordnetLmfData;

import java.util.ArrayList;

/**
 * Created by piek on 02/06/15.
 */
public class Synset {
    /**
     *         <Synset id="mwn-569856-n" ili=""> <!-- PROPOSING A NEW ILI -->
     <Definition language= “en” gloss="cultural habit to walk on skates"/>
     </Synset>
     */
    private String synsetId;
    private String iliId;
    private ArrayList<Gloss> definitions;
    private ArrayList<SynsetRelation> relations;


    public Synset() {
        this.definitions = new ArrayList<Gloss>();
        this.relations = new ArrayList<SynsetRelation>();
        this.iliId = "";
        this.synsetId = "";
    }

    public void merge(Synset synset) {
        if (this.iliId.isEmpty()) {
            this.iliId = synset.iliId;
        }
        for (int i = 0; i < synset.getDefinitions().size(); i++) {
            Gloss gloss = synset.getDefinitions().get(i);
            definitions.add(gloss);
        }
        for (int i = 0; i < synset.getRelations().size(); i++) {
            SynsetRelation synsetRelation = synset.getRelations().get(i);
            this.relations.add(synsetRelation);
        }
    }

    public ArrayList<Gloss> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(ArrayList<Gloss> definitions) {
        this.definitions = definitions;
    }

    public void addDefinition(Gloss definition) {
        this.definitions.add(definition);
    }

    public String getIliId() {
        return iliId;
    }

    public void setIliId(String iliId) {
        this.iliId = iliId;
    }

    public String getSynsetId() {
        return synsetId;
    }

    public void setSynsetId(String synsetId) {
        this.synsetId = synsetId;
    }

    public ArrayList<SynsetRelation> getRelations() {
        return relations;
    }

    public void setRelations(ArrayList<SynsetRelation> relations) {
        this.relations = relations;
    }

    public void addRelations(SynsetRelation relation) {
        boolean hasRelation = false;
        for (int i = 0; i < relations.size(); i++) {
            SynsetRelation synsetRelation = relations.get(i);
            if (synsetRelation.getTarget().equals(relation.getTarget())
                    &&
                    synsetRelation.getRelType().equals(relation.getRelType())) {
                hasRelation = true;
                break;
            }
        }
        if (!hasRelation) {
            this.relations.add(relation);
        }
    }

    public Element toLmfXml(Document xmldoc) {
        Element root = xmldoc.createElement("Synset");
        if (!this.getSynsetId().isEmpty()) {
            root.setAttribute("id", this.getSynsetId());
        }
        if (!this.getIliId().isEmpty()) {
            root.setAttribute("ili", this.getIliId());
        }
        Element glosses = xmldoc.createElement("Glosses");
        for (int i = 0; i < this.getDefinitions().size(); i++) {
            Gloss definition = this.getDefinitions().get(i);
            glosses.appendChild(definition.toXml(xmldoc));
        }
        root.appendChild(glosses);
        Element relationElement = xmldoc.createElement("SynsetRelations");
        for (int i = 0; i < relations.size(); i++) {
            SynsetRelation synsetRelation = relations.get(i);
            relationElement.appendChild(synsetRelation.toLmfXML(xmldoc));
        }
        root.appendChild(relationElement);
        return root;
    }

    public String toString (WordnetLmfData wordnetLmfData) {
        String str = this.getSynsetId()+"\t[";
        ArrayList<String> syns = wordnetLmfData.getSynonyms(this);
        for (int i = 0; i < syns.size(); i++) {
            String s = syns.get(i);
            str += s;
            if (i+1<syns.size()) {
                str+=",";
            }
        }
        str +="][";

        for (int i = 0; i < definitions.size(); i++) {
            Gloss gloss = definitions.get(i);
            str += gloss.getText();
            if (i+1<definitions.size()) {
                str+=",";
            }
        }
        str +="][";
        for (int i = 0; i < relations.size(); i++) {
            SynsetRelation synsetRelation = relations.get(i);
            str += synsetRelation.getRelType()+":"+synsetRelation.getTarget();
            if (i+1<relations.size()) {
                str+=",";
            }
        }
        str +="]";
        return str;
    }

    public String toStringWithoutRelations (WordnetLmfData wordnetLmfData) {
        String str = this.getSynsetId()+"\t[";
        ArrayList<String> syns = wordnetLmfData.getSynonyms(this);
        for (int i = 0; i < syns.size(); i++) {
            String s = syns.get(i);
            str += s;
            if (i+1<syns.size()) {
                str+=",";
            }
        }
        str +="][";

        for (int i = 0; i < definitions.size(); i++) {
            Gloss gloss = definitions.get(i);
            str += gloss.getText();
            if (i+1<definitions.size()) {
                str+=",";
            }
        }
        str +="]";
        return str;
    }

    public String toStringWithoutRelations (ArrayList<String> syns) {
        String str = this.getSynsetId()+"\t[";
        for (int i = 0; i < syns.size(); i++) {
            String s = syns.get(i);
            str += s;
            if (i+1<syns.size()) {
                str+=",";
            }
        }
        str +="][";

        for (int i = 0; i < definitions.size(); i++) {
            Gloss gloss = definitions.get(i);
            str += gloss.getText();
            if (i+1<definitions.size()) {
                str+=",";
            }
        }
        str +="]";
        return str;
    }
}
