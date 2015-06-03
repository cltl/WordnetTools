package vu.wntools.lmf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by piek on 02/06/15.
 */
public class Gloss {
    private String language;
    private String text;

    public Gloss() {
        this.language = "";
        this.text = "";
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

    public Element toXml (Document xmldoc) {
        Element defElement = xmldoc.createElement("Definition");
        defElement.setAttribute("language", this.getLanguage());
        defElement.setAttribute("gloss", this.getText());
        return defElement;

    }

}
