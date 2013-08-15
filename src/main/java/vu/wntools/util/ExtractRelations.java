package vu.wntools.util;

import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 8/14/13
 * Time: 7:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExtractRelations {


    static public void main (String[] args) {/*
        String pathToWnFile = args[0];
        String relation = args[1];*/
        String pathToWnFile = "/Tools/wordnet-tools.0.1/resources/cornetto2.1.lmf.xml";
        String relation = "causes;CAUSES;caused_by;CAUSED_BY";
        ArrayList<String> relations = new ArrayList<String>();
        String [] fields = relation.split(";");
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            relations.add(field);
        }
        WordnetLmfSaxParser wordnetLmfSaxParser = new WordnetLmfSaxParser();
        wordnetLmfSaxParser.setRelations(relations);
        wordnetLmfSaxParser.parseFile(pathToWnFile);
        wordnetLmfSaxParser.wordnetData.buildSynsetIndex();
        Set keySet = wordnetLmfSaxParser.wordnetData.getHyperRelations().keySet();
        Iterator keys = keySet.iterator();
        while (keys.hasNext()) {
            String source = (String) keys.next();
            String sourceSynset = wordnetLmfSaxParser.wordnetData.getSynsetString(source);
            ArrayList<String> targets = wordnetLmfSaxParser.wordnetData.getHyperRelations().get(source);
            for (int i = 0; i < targets.size(); i++) {
                String target = targets.get(i);
                String targetSynset = wordnetLmfSaxParser.wordnetData.getSynsetString(target);
                System.out.println(source+"["+sourceSynset+"]"+":"+target+"["+targetSynset+"]");
            }
        }
    }
}
