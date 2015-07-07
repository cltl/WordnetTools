package vu.wntools.lmf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by piek on 02/06/15.
 */
public class LmfSense {

/*
    <Sense id="clapboard_1" synset="eng-30-01337412-v"/>
*/
    private String senseId;
    private String synset;
    private String definition;
    private String provenance;

    public LmfSense() {
        this.senseId = "";
        this.synset = "";
        this.definition = "";
        this.provenance = "";
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getSenseId() {
        return senseId;
    }

    public void setSenseId(String senseId) {
        this.senseId = senseId;
    }

    public String getSynset() {
        return synset;
    }

    public void setSynset(String synset) {
        this.synset = synset;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Element toLmfXML(Document xmldoc)
    {
        Element root = xmldoc.createElement("Sense");
        if (!senseId.isEmpty()) root.setAttribute("id", senseId);
        if (!provenance.isEmpty()) root.setAttribute("provenance", provenance);
        if (!synset.isEmpty()) root.setAttribute("synset", synset);
        if (!definition.isEmpty()) root.setAttribute("definition", definition);
        return root;
    }

}
