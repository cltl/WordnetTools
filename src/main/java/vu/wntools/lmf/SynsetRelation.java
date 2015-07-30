package vu.wntools.lmf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by piek on 02/06/15.
 */
public class SynsetRelation {
    /**
     *  <SynsetRelation relType="owl:sameAs" target="mwn-569856-n"/>
     */

    private String relType;
    private String target;
    private String provenance;

    public SynsetRelation() {
        this.relType = "";
        this.target = "";
        this.provenance = "";
    }

    public String getRelType() {
        return relType;
    }

    public void setRelType(String relType) {
        this.relType = relType;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public Element toLmfXML(Document xmldoc)
    {
        Element root = xmldoc.createElement("SynsetRelation");
        if (!target.isEmpty()) root.setAttribute("target", target);
        if (!relType.isEmpty()) root.setAttribute("relType", relType);
        if (!provenance.isEmpty()) root.setAttribute("provenance", provenance);
        return root;
    }}
