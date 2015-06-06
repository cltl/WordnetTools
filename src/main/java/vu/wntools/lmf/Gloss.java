package vu.wntools.lmf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by piek on 02/06/15.
 */
public class Gloss {
    private String language;
    private String text;
    private String provenance;

    public Gloss() {
        this.language = "";
        this.text = "";
        this.provenance = "";
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public Element toXml (Document xmldoc) {
        Element defElement = xmldoc.createElement("Definition");
        if (!this.getLanguage().isEmpty()) defElement.setAttribute("language", this.getLanguage());
        defElement.setAttribute("gloss", this.getText());
        if (!this.getProvenance().isEmpty()) defElement.setAttribute("provenance", this.getProvenance());
        return defElement;

    }

}
