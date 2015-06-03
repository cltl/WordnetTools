package vu.wntools.lmf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
        this.relations.add(relation);
    }

    public Element toLmfXml(Document xmldoc) {
        Element root = xmldoc.createElement("Synset");
        if (!this.getSynsetId().isEmpty()) {
            root.setAttribute("id", this.getSynsetId());
        }
        if (!this.getIliId().isEmpty()) {
            root.setAttribute("ili", this.getIliId());
        }
        for (int i = 0; i < this.getDefinitions().size(); i++) {
            Gloss definition = this.getDefinitions().get(i);
            root.appendChild(definition.toXml(xmldoc));
        }
        for (int i = 0; i < relations.size(); i++) {
            SynsetRelation synsetRelation = relations.get(i);
            root.appendChild(synsetRelation.toLmfXML(xmldoc));
        }
        return root;
    }
}
