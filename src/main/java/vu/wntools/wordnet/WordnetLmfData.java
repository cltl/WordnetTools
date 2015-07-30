package vu.wntools.wordnet;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import vu.wntools.lmf.LmfEntry;
import vu.wntools.lmf.Synset;
import vu.wntools.lmf.SynsetRelation;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 02/06/15.
 */
public class WordnetLmfData {
    private String globalLabel;
    private String languageEncoding;
    private String lexiconLabel;
    private String language;
    private String owner;
    private String version;
    public ArrayList<LmfEntry> entries;
    public ArrayList<Synset> synsets;
    public HashMap<String, ArrayList<LmfEntry>> entryMap;
    public HashMap<String, Synset> synsetMap;
    public HashMap<String, ArrayList<String>> synsetToEntriesMap;
    public HashMap<String, ArrayList<String>> entryToSynsetsMap;
    public HashMap<String, ArrayList<String>> parentToChildMap;


    public WordnetLmfData() {
        this.synsets = new ArrayList<Synset>();
        this.entries = new ArrayList<LmfEntry>();
        this.globalLabel = "";
        this.language = "";
        this.languageEncoding = "";
        this.lexiconLabel = "";
        this.owner = "";
        this.version = "";
        synsetToEntriesMap = new HashMap<String, ArrayList<String>>();
        entryToSynsetsMap = new HashMap<String, ArrayList<String>>();
        entryMap = new HashMap<String, ArrayList<LmfEntry>>();
        synsetMap = new HashMap<String, Synset>();
        parentToChildMap = new HashMap<String, ArrayList<String>>();
    }

    public void buildParentToChildMap () {
        for (int i = 0; i < synsets.size(); i++) {
            Synset synset = synsets.get(i);
            ArrayList<String> hypernyms = getHyperonyms(synset);
            for (int j = 0; j < hypernyms.size(); j++) {
                String hyperId = hypernyms.get(j);
                if (parentToChildMap.containsKey(hyperId)) {
                    ArrayList<String> children = parentToChildMap.get(hyperId);
                    if (!children.contains(synset.getSynsetId())) {
                        children.add(synset.getSynsetId());
                        parentToChildMap.put(hyperId, children);
                    }
                }
                else {
                    ArrayList<String> children = new ArrayList<String>();
                    children.add(synset.getSynsetId());
                    parentToChildMap.put(hyperId, children);
                }
            }
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ArrayList<LmfEntry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<LmfEntry> entries) {
        this.entries = entries;
    }

    public void addEntry(LmfEntry entry) {
        this.entries.add(entry);
    }

    public String getGlobalLabel() {
        return globalLabel;
    }

    public void setGlobalLabel(String globalLabel) {
        this.globalLabel = globalLabel;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguageEncoding() {
        return languageEncoding;
    }

    public void setLanguageEncoding(String languageEncoding) {
        this.languageEncoding = languageEncoding;
    }

    public String getLexiconLabel() {
        return lexiconLabel;
    }

    public void setLexiconLabel(String lexiconLabel) {
        this.lexiconLabel = lexiconLabel;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList<Synset> getSynsets() {
        return synsets;
    }

    public void setSynsets(ArrayList<Synset> synsets) {
        this.synsets = synsets;
    }

    public boolean addSynset(Synset synset) {
        for (int i = 0; i < synsets.size(); i++) {
            Synset synset1 = synsets.get(i);
            if (synset1.getSynsetId().equals(synset.getSynsetId())) {
                ////merge
                synset1.merge(synset);
                this.synsetMap.put(synset1.getSynsetId(), synset1);

                return false;
            }
        }
        this.synsets.add(synset);
        return true;
    }

        /*
    <?xml version="1.0" encoding="UTF-8"?>
<LexicalResource>
  <GlobalInformation label="Cornetto-LMF"/>
  <Lexicon languageCoding="ISO 639-2" label="Cornetto2.1" language="nl" owner="VUA">
     */

    public void serialize(OutputStream stream)
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();

            Document xmldoc = impl.createDocument(null, "LexicalResource", null);
            xmldoc.setXmlStandalone(false);
            Element root = xmldoc.getDocumentElement();

            if (!globalLabel.isEmpty())  {
                Element text = xmldoc.createElement("GlobalInformation");
                text.setAttribute("label", getGlobalLabel());
                root.appendChild(text);
            }
            Element lexicon = xmldoc.createElement("Lexicon");
            if (!languageEncoding.isEmpty()) {
                lexicon.setAttribute("languageCoding", this.getLanguageEncoding());
            }
            if (!this.getLexiconLabel().isEmpty()) {
                lexicon.setAttribute("label", this.getLexiconLabel());
            }
            if (!this.getLanguage().isEmpty()) {
                lexicon.setAttribute("language", this.getLanguage());
            }
            if (!this.getOwner().isEmpty()) {
                lexicon.setAttribute("owner", this.getOwner());
            }
            if (!this.getVersion().isEmpty()) {
                lexicon.setAttribute("version", this.getVersion());
            }
            root.appendChild(lexicon);
            for (int i = 0; i < entries.size(); i++) {
                LmfEntry lmfEntry = entries.get(i);
                root.appendChild(lmfEntry.toLmfXml(xmldoc));
            }
            for (int i = 0; i < synsets.size(); i++) {
                Synset synset = synsets.get(i);
                root.appendChild(synset.toLmfXml(xmldoc));
            }
            // Serialisation through Tranform.
            DOMSource domSource = new DOMSource(xmldoc);
            TransformerFactory tf = TransformerFactory.newInstance();
            //tf.setAttribute("indent-number", 4);
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT,"yes");
            //serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
            serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            //serializer.setParameter("format-pretty-print", Boolean.TRUE);
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            //StreamResult streamResult = new StreamResult(new OutputStreamWriter(stream, encoding));
            StreamResult streamResult = new StreamResult(new OutputStreamWriter(stream));
            serializer.transform(domSource, streamResult);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void serializeMap(OutputStream stream)
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();

            Document xmldoc = impl.createDocument(null, "LexicalResource", null);
            xmldoc.setXmlStandalone(false);
            Element root = xmldoc.getDocumentElement();

            if (!globalLabel.isEmpty())  {
                Element text = xmldoc.createElement("GlobalInformation");
                text.setAttribute("label", getGlobalLabel());
                root.appendChild(text);
            }
            Element lexicon = xmldoc.createElement("Lexicon");
            if (!languageEncoding.isEmpty()) {
                lexicon.setAttribute("languageCoding", this.getLanguageEncoding());
            }
            if (!this.getLexiconLabel().isEmpty()) {
                lexicon.setAttribute("label", this.getLexiconLabel());
            }
            if (!this.getLanguage().isEmpty()) {
                lexicon.setAttribute("language", this.getLanguage());
            }
            if (!this.getOwner().isEmpty()) {
                lexicon.setAttribute("owner", this.getOwner());
            }
            if (!this.getVersion().isEmpty()) {
                lexicon.setAttribute("version", this.getVersion());
            }
            root.appendChild(lexicon);
            for (int i = 0; i < entries.size(); i++) {
                LmfEntry lmfEntry = entries.get(i);
                root.appendChild(lmfEntry.toLmfXml(xmldoc));
            }
            Set keySet = synsetMap.keySet();
            Iterator<String> keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                Synset synset = synsetMap.get(key);
                root.appendChild(synset.toLmfXml(xmldoc));
            }
            /*for (int i = 0; i < synsets.size(); i++) {
                Synset synset = synsets.get(i);
                root.appendChild(synset.toLmfXml(xmldoc));
            }*/
            // Serialisation through Tranform.
            DOMSource domSource = new DOMSource(xmldoc);
            TransformerFactory tf = TransformerFactory.newInstance();
            //tf.setAttribute("indent-number", 4);
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.INDENT,"yes");
            //serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
            serializer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            //serializer.setParameter("format-pretty-print", Boolean.TRUE);
            serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            //StreamResult streamResult = new StreamResult(new OutputStreamWriter(stream, encoding));
            StreamResult streamResult = new StreamResult(new OutputStreamWriter(stream));
            serializer.transform(domSource, streamResult);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

/**
 *  KYOTO WordnetLMF structure
 <?xml version='1.0'?>
 <LexicalResource>
 <GlobalInformation label="Princeton WordNet lexical resource (KYOTO LMF created by wn2lmf.py)" />
 <Lexicon label="Princeton WordNet 3.0" language="eng" languageCoding="ISO 639-3" owner="KYOTO project" version="3.0">
 <LexicalEntry id="systema_respiratorium">
 <Lemma partOfSpeech="n" writtenForm="systema_respiratorium" />
 <Sense id="systema_respiratorium_1" synset="eng-30-05509889-n">
    <MonolingualExternalRefs><MonolingualExternalRef externalReference="systema_respiratorium%1:08:00::" externalSystem="Wordnet3.0" />
    </MonolingualExternalRefs></Sense>
 </LexicalEntry>
 <LexicalEntry id="clapboard"><Lemma partOfSpeech="v" writtenForm="clapboard" />
 <Sense id="clapboard_1" synset="eng-30-01337412-v">
 <MonolingualExternalRefs><MonolingualExternalRef externalReference="clapboard%2:35:00::" externalSystem="Wordnet3.0" />
 </MonolingualExternalRefs></Sense>
 </LexicalEntry>
 <LexicalEntry id="great_australian_desert"><Lemma partOfSpeech="n" writtenForm="Great_Australian_Desert" />
 <Sense id="great_australian_desert_1" synset="eng-30-09168336-n"><MonolingualExternalRefs><MonolingualExternalRef externalReference="great_australian_desert%1:15:00::" externalSystem="Wordnet3.0" /></MonolingualExternalRefs></Sense>
 </LexicalEntry>

 <Synset baseConcept="1" id="eng-30-15036211-n">
    <Definition gloss="any toxin that affects the kidneys" />
    <SynsetRelations>
        <SynsetRelation relType="has_hyperonym" target="eng-30-15034074-n" />
    </SynsetRelations>
    <MonolingualExternalRefs><MonolingualExternalRef externalReference="dummy" externalSystem="Domain" />
    </MonolingualExternalRefs></Synset>
 <Synset baseConcept="1" id="eng-30-04242587-n">
    <Definition gloss="a sailing or steam warship having cannons on only one deck" />
    <SynsetRelations><SynsetRelation relType="has_hyperonym" target="eng-30-04552696-n" />
    </SynsetRelations><
    MonolingualExternalRefs>
    <MonolingualExternalRef externalReference="dummy" externalSystem="Domain" />
    </MonolingualExternalRefs>
 </Synset>
 */



    public ArrayList<String> getHyperonyms (Synset synset) {
        ArrayList<String> hypers = new ArrayList<String>();
        for (int i = 0; i < synset.getRelations().size(); i++) {
            SynsetRelation synsetRelation = synset.getRelations().get(i);
            if (synsetRelation.getRelType().toLowerCase().equalsIgnoreCase("has_hyperonym")) {
                hypers.add(synsetRelation.getTarget());
            }
            else if (synsetRelation.getRelType().toLowerCase().equalsIgnoreCase("has_hypernym")) {
                hypers.add(synsetRelation.getTarget());
            }
            else if (synsetRelation.getRelType().toLowerCase().equalsIgnoreCase("hyperonym")) {
                hypers.add(synsetRelation.getTarget());
            }
            else if (synsetRelation.getRelType().toLowerCase().equalsIgnoreCase("hypernym")) {
                hypers.add(synsetRelation.getTarget());
            }
        }
        return hypers;
    }


    public ArrayList<String> getCoHyponyms (Synset synset) {
        ArrayList coHyponyms = new ArrayList();
        ArrayList<String> hyperonyms = getHyperonyms(synset);
        for (int j = 0; j < hyperonyms.size(); j++) {
            String hyperId = hyperonyms.get(j);
            ArrayList<String> children = parentToChildMap.get(hyperId);
            for (int i = 0; i < children.size(); i++) {
                String childId = children.get(i);
                if (!childId.equals(synset.getSynsetId())) {
                    if (!coHyponyms.contains(childId)) {
                        coHyponyms.add(childId);
                    }
                }

            }
        }
        return coHyponyms;
    }

    public ArrayList<String> getCoCoHyponyms (Synset synset) {
        ArrayList coHyponyms = getCoHyponyms(synset);
        ArrayList<String> hyperonyms = getHyperonyms(synset);
        for (int j = 0; j < hyperonyms.size(); j++) {
            String hyperId = hyperonyms.get(j);
            if (synsetMap.containsKey(hyperId)) {
                Synset hyperSynset = synsetMap.get(hyperId);
                ArrayList<String> coco = getCoHyponyms(hyperSynset);
                for (int i = 0; i < coco.size(); i++) {
                    String childId = coco.get(i);
                    if (!childId.equals(synset.getSynsetId())) {
                        if (!coHyponyms.contains(childId)) {
                            coHyponyms.add(childId);
                        }
                    }
                }
            }
        }
        return coHyponyms;
    }


    public ArrayList<String> getSynonyms (Synset synset) {
        ArrayList<String> syns = new ArrayList<String>();
        if (this.synsetToEntriesMap.containsKey(synset.getSynsetId())) {
            syns = synsetToEntriesMap.get(synset.getSynsetId());
        }
        return syns;
    }

    public void getHyperChains (String source, ArrayList<ArrayList<String>> targetChain, ArrayList<String> chain) {
        boolean DEBUG = false;
        if (DEBUG) System.out.println("source = " + source);
        if (synsetMap.containsKey(source)) {
            Synset synset = synsetMap.get(source);
            ArrayList<String> targets = getHyperonyms(synset);
            if (DEBUG) System.out.println("targets.toString() = " + targets.toString());
            /// for every target hypernym, we will extend the chain, depth-first
            if (DEBUG) System.out.println("chain = " + chain);
            /// we first copy the chain to the currentChain so that we can fork in case of multipe hypernyms
            for (int i = 0; i < targets.size(); i++) {
                //// store the currentChain
                ArrayList<String> currentChain = new ArrayList<String>();
                for (int c = 0; c < chain.size(); c++) {
                    String s = chain.get(c);
                    currentChain.add(s);
                }
                String target =  targets.get(i);
                if (DEBUG) {
                    String str = "target:"+i+":"+target+":";
                    ArrayList<String> synonym = synsetToEntriesMap.get(target);
                    for (int j = 0; j < synonym.size(); j++) {
                        String s = synonym.get(j);
                        str += s+";";
                    }
                    System.out.println(str);
                }
                if (!currentChain.contains(target)) {
                    currentChain.add(target);
                    if (DEBUG) System.out.println("adding target  = " +  target );
                    if (DEBUG) System.out.println("currentChain = " + currentChain);
                    /// DEPTH FIRST ITERATION WITH CURRENTCHAIN
                    getHyperChains(target, targetChain, currentChain);
                }
                else {
                    ///circular so now we can add it to the result structure, the chain is done
                    //currentChain.add(target);
                   // System.out.println("CIRCULAR: target"+target+" is in chain:"+chain.toString());
                    //targetChain.add(chain);
                }
            }
        }
        else {
            //// if there are no hypernyms to add to the chain,the chain is ready and can be added to the result Array
            if (DEBUG) System.out.println("NOT IN HYPER RELATIONS MAP");
            targetChain.add(chain);
        }
    }
}
