package vu.wntools.util;

import org.json.JSONException;
import org.json.JSONObject;
import vu.wntools.lmf.Gloss;
import vu.wntools.wordnet.WordnetData;
import vu.wntools.wordnet.WordnetLmfSaxParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by piek on 09/06/15.
 */
public class JsonForRadialReingoldTilfordTree {

    /*

   "name": "animate",
   "children": [
    {"name": "Easing", "size": 17010},
    {"name": "FunctionSequence", "size": 5842},
    {
     "name": "interpolate",
     "children": [
      {"name": "ArrayInterpolator", "size": 1983},
      {"name": "ColorInterpolator", "size": 2047},
      {"name": "DateInterpolator", "size": 1375},
      {"name": "Interpolator", "size": 8746},
      {"name": "MatrixInterpolator", "size": 2202},
      {"name": "NumberInterpolator", "size": 1382},
      {"name": "ObjectInterpolator", "size": 1629},
      {"name": "PointInterpolator", "size": 1675},
      {"name": "RectangleInterpolator", "size": 2042}
     ]
    },
    {"name": "ISchedulable", "size": 1041},
    {"name": "Parallel", "size": 5176},
    {"name": "Pause", "size": 449},
    {"name": "Scheduler", "size": 5593},
    {"name": "Sequence", "size": 5534},
    {"name": "Transition", "size": 9201},
    {"name": "Transitioner", "size": 19975},
    {"name": "TransitionEvent", "size": 1116},
    {"name": "Tween", "size": 6006}
   ]
  },
     */

        public static JSONObject createJsonSynset (String name,
                                                   String size,
                                                   String color) throws JSONException {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", name);
            jsonObject.put("size", size);
            jsonObject.put("color", color);
            return jsonObject;
        }

    //http://bl.ocks.org/mbostock/4063550#index.html
  //  static ArrayList<JSONObject> jsonObjectArrayList = new ArrayList<JSONObject>();
  //  static JSONObject data = new JSONObject();
    static ArrayList<String> odwnNoDef = new ArrayList<String>();
    static ArrayList<String> odwnOneWordDef =  new ArrayList<String>();
    static ArrayList<String> odwnNoMatch =  new ArrayList<String>();
    static ArrayList<String> odwnBottom =  new ArrayList<String>();
    static ArrayList<String> odwnMiddle =  new ArrayList<String>();
    static ArrayList<String> odwnTop =  new ArrayList<String>();
    static ArrayList<String> allOdwn = new ArrayList<String>();

    static public void main(String[] args) {
        String targetFolderName = "odwn-new-verb-3";
        String targetFolderPath = "";
        String pos = "v";
        String provenanceFilter = "pwn";

        String pathToFile = "";
        String pathToPwnFile = "";

        pathToFile = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf";
        pathToPwnFile = "/Tools/wordnet-tools.0.1/resources/wneng-30.lmf.xml";

        targetFolderPath = new File(pathToFile).getParentFile().getAbsolutePath()+"/"+targetFolderName;
        File targetDir = new File(targetFolderPath);
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }

        ArrayList<String> relations = new ArrayList<String>();
        relations.add("has_hyperonym");
        relations.add("has_hypernym");
        relations.add("hypernym");

        WordnetLmfSaxParser parser = new WordnetLmfSaxParser();
        WordnetLmfSaxParser pwnparser = new WordnetLmfSaxParser();
        parser.setRelations(relations);
        parser.provenanceFilter = provenanceFilter;
        parser.parseFile(pathToFile);

        pwnparser.parseFile(pathToPwnFile);
        pwnparser.setRelations(relations);

        parser.wordnetData.buildSynsetIndex();
        parser.wordnetData.buildLexicalUnitIndex();
        parser.wordnetData.buildLemmaIndex();

        pwnparser.wordnetData.buildSynsetIndex();
        pwnparser.wordnetData.buildLexicalUnitIndex();
       // pwnparser.wordnetData.buildLemmaIndex();
       // System.out.println("pwnparser.wordnetData.synsetToEntries.size() = "+        pwnparser.wordnetData.synsetToEntries.size());
        String pathToNoDef = "";
        String pathToOneWordDef = "";
        String pathToNoMatch = "";
        String pathToBottom = "";
        String pathToMiddle = "";
        String pathToTop = "";

        pathToNoDef = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/lemma-translation/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf.new.not-mapped.matchNoGloss";
        pathToOneWordDef = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/lemma-translation/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf.new.not-mapped.matchOneWordGloss";
        pathToNoMatch = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/lemma-translation/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf.new.not-mapped.matchZero";
        pathToBottom = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/lemma-translation/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf.new.not-mapped.matchBottom";
        pathToMiddle = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/lemma-translation/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf.new.not-mapped.matchMiddle";
        pathToTop = "/Users/piek/Desktop/GWG/nl/startedFromOdwnRbnLmf/lemma-translation/odwn_1.0.xml.lmf.pwn-glosses.google-glosses.ili.lmf.new.not-mapped.matchTop";

        odwnNoDef = readOdwnFromFile(pathToNoDef);
        odwnOneWordDef = readOdwnFromFile(pathToOneWordDef);
        odwnNoMatch = readOdwnFromFile(pathToNoMatch);
        odwnBottom = readOdwnFromFile(pathToBottom);
        odwnMiddle = readOdwnFromFile(pathToMiddle);
        odwnTop = readOdwnFromFile(pathToTop);
        allOdwn = new ArrayList<String>();
        allOdwn.addAll(odwnNoDef);
        allOdwn.addAll(odwnOneWordDef);
        allOdwn.addAll(odwnNoMatch);
        allOdwn.addAll(odwnMiddle);
        allOdwn.addAll(odwnBottom);
        allOdwn.addAll(odwnTop);
        writeJson(parser.wordnetData, pwnparser.wordnetData, pos, targetFolderPath);
    }


    static public void writeJson (WordnetData wordnetData,
                                  WordnetData pwnData,
                                  String pos,
                                  String targetFolderPath) {
        try {

            String htmlStr = "<html>\n<body>\n";
            String overviewHtmlFile = targetFolderPath+"/"+pos+".overview.html";
            OutputStream overviewFos = new FileOutputStream(overviewHtmlFile);
            overviewFos.write(htmlStr.getBytes());
            ArrayList<String> topNodes = new ArrayList<String>();

            topNodes = buildChildRelationsFromids(wordnetData);
            if (pos.equalsIgnoreCase("n")) {
                ArrayList<String> topNouns = getPosSynsets(topNodes, "n");
              //  System.out.println("topNouns = " + topNouns.toString());
                JSONObject data = new JSONObject();
                int nConcepts = buildTree(wordnetData, pwnData, topNouns, data, 0);
                storeResult(targetFolderPath, pos, "all synsets:", nConcepts, "all", 1, overviewFos, data);
                /*String jsonFile = targetFolderPath+"/"+ "all" +   "." +pos+".json";
                String scriptFile = targetFolderPath+"/"+ "all" + "." +pos+".html";
                String link = "<h3><a href=\""+ scriptFile+"\" target=\"_blank\">"+"all synsets depth 2"+":"+nConcepts+"</a></h3>\n\n";
                htmlStr = link;
                overviewFos.write(htmlStr.getBytes());
                try {
                    OutputStream fos = new FileOutputStream(jsonFile);
                    StringWriter out = new StringWriter();
                    data.write(out);
                    fos.write(out.toString().getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    OutputStream fos = new FileOutputStream(scriptFile);
                    String script = jScriptFile.makeFile(jsonFile, nConcepts);
                    fos.write(script.getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                ArrayList<String> nextLevel = getNextLevel(topNouns, wordnetData);

                for (int i = 0; i < nextLevel.size(); i++) {
                    data = new JSONObject();
                    String s =  nextLevel.get(i);
                    String synonyms = wordnetData.getSynsetString(s);
                    String engSynonyms = pwnData.getSynsetString(s);
                    synonyms += "["+engSynonyms+"]";
                    ArrayList<String> singleTop = new ArrayList<String>();
                    singleTop.add(s);
                    nConcepts = buildTree(wordnetData, pwnData, singleTop, data, 0);
                    storeResult(targetFolderPath, pos, synonyms, nConcepts, s, 2, overviewFos, data);
                    ArrayList<String> nextNextLevel = getNextLevel(s, wordnetData);
                    for (int j = 0; j < nextNextLevel.size(); j++) {
                        String s1 = nextNextLevel.get(j);
                        if (!nextLevel.contains(s1)) {
                            data = new JSONObject();
                            synonyms = wordnetData.getSynsetString(s1);
                            engSynonyms = pwnData.getSynsetString(s1);
                            synonyms += "[" + engSynonyms + "]";
                            singleTop = new ArrayList<String>();
                            singleTop.add(s1);
                            nConcepts = buildTree(wordnetData, pwnData, singleTop, data, 0);
                            storeResult(targetFolderPath, pos, synonyms, nConcepts, s1, 3, overviewFos, data);
                        }
                    }
                    /*jsonFile = targetFolderPath+"/"+ pos +   "."+ s + ".json";
                    scriptFile = targetFolderPath+"/"+ pos +"."+ s + ".html";
                    link = "<h3><a href=\""+ scriptFile+"\" target=\"_blank\">"+synonyms+":"+nConcepts+"</a></h3>\n";
                    htmlStr = link;
                    overviewFos.write(htmlStr.getBytes());

                    try {
                        OutputStream fos = new FileOutputStream(jsonFile);
                        StringWriter out = new StringWriter();
                        data.write(out);
                        fos.write(out.toString().getBytes());
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        OutputStream fos = new FileOutputStream(scriptFile);
                        String script = jScriptFile.makeFile(jsonFile, nConcepts);
                        fos.write(script.getBytes());
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/


                }

            }
            else if (pos.equalsIgnoreCase("v")) {
                ArrayList<String> topVerbs = getPosSynsets(topNodes, "v");
                System.out.println("topVerbs = " + topVerbs);
                JSONObject data = new JSONObject();
                int nConcepts = buildTree(wordnetData, pwnData, topVerbs, data, 0);
                String jsonFile = targetFolderPath+"/"+ "all" +   "." +pos+".json";
                String scriptFile = targetFolderPath+"/"+ "all" + "." +pos+".html";
                String link = "<h3><a href=\""+ scriptFile+"\" target=\"_blank\">"+"all synsets depth 2"+":"+nConcepts+"</a></h3>\n\n";
                htmlStr = link;
                overviewFos.write(htmlStr.getBytes());
                try {
                    OutputStream fos = new FileOutputStream(jsonFile);
                    StringWriter out = new StringWriter();
                    data.write(out);
                    fos.write(out.toString().getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    OutputStream fos = new FileOutputStream(scriptFile);
                    String script = jScriptFile.makeFile(jsonFile, nConcepts);
                    fos.write(script.getBytes());
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            htmlStr = "</body>\n";
            overviewFos.write(htmlStr.getBytes());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void  storeResult (String targetFolderPath,
                                String pos,
                                String synonyms,
                                int nConcepts,
                                String s,
                                int level,
                                OutputStream overviewFos,
                                JSONObject data) throws IOException, JSONException {
        String jsonFile = targetFolderPath+"/"+ pos +   "."+ s + ".json";
        String scriptFile = targetFolderPath+"/"+ pos +"."+ s + ".html";
        String link = "<h"+level+"><a href=\""+ scriptFile+"\" target=\"_blank\">"+synonyms+":"+nConcepts+"</a></h"+level+">\n";
        overviewFos.write(link.getBytes());

        try {
            OutputStream fos = new FileOutputStream(jsonFile);
            StringWriter out = new StringWriter();
            data.write(out);
            fos.write(out.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            OutputStream fos = new FileOutputStream(scriptFile);
            String script = jScriptFile.makeFile(jsonFile, nConcepts);
            fos.write(script.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int buildTree (WordnetData wordnetData,WordnetData pwnData,
                           ArrayList<String> topNodes, JSONObject data, final int depth) throws JSONException {
        ArrayList<String> coveredSynsets = new ArrayList<String>();
        int nConcepts = 0;
        nConcepts = addJson(wordnetData, pwnData, topNodes, 0, depth, coveredSynsets, data);
        return nConcepts;
    }


    static int addJson (WordnetData wordnetData,WordnetData pwnData,
                           ArrayList<String> synsets,
                           int level,
                           final int depth,
                           ArrayList<String> coveredNodes,
                           JSONObject data) throws JSONException {
        int nConcepts = 0;
     //   if (depth==0 || level< depth) {
            for (int i = 0; i < synsets.size(); i++) {
                String s = synsets.get(i);
/*            String str = level+":";
            for (int j = 0; j < level; j++) {
                str += "-";
            }
            System.out.println(str + s);*/

                if (!coveredNodes.contains(s)) {
                    nConcepts++;
                   // System.out.println("nConcepts = " + nConcepts);
                    coveredNodes.add(s);
                    String synonyms = wordnetData.getFirstSynsetString(s);
                    String sizeString = "";
                    String color = "";
                    if (s.startsWith("odwn")) {
                        if (wordnetData.synsetToGlosses.containsKey(s)) {
                            ArrayList<Gloss> glosses = wordnetData.synsetToGlosses.get(s);
                            for (int j = 0; j < glosses.size(); j++) {
                                Gloss gloss = glosses.get(j);
                                if (gloss.getLanguage().equals("en")) {
                                    synonyms += "["+gloss.getText()+"]";
                                }
                            }
                        }
                        sizeString = "600";
                        color = "#ffc0cb"; //pink
                    } else {
                        if (synonyms.isEmpty()) {
                            synonyms = "[" + pwnData.getFirstSynsetString(s) + "]";
                            sizeString = "500";
                            color = "#37c3e0"; //blue
                        } else {
                            String engSynonyms = "[" + pwnData.getFirstSynsetString(s) + "]";
                            synonyms += engSynonyms;
                            sizeString = "400";
                            color = "#990000";   /// dark red
                        }

                    }
                    if (synonyms.isEmpty()) {
                        System.out.println("s = " + s);
                        synonyms = s;
                        sizeString = "200";
                        color = "#5cbe7f";   /// green
                    }

                    //   System.out.println("synonyms = " + synonyms);
                    JSONObject synset = createJsonSynset(synonyms, sizeString, color);

                    if (wordnetData.childRelations.containsKey(s)) {
                        ArrayList<String> children = wordnetData.childRelations.get(s);
                        level++;
                        // System.out.println("children = " + children.toString());
                        nConcepts += addJson(wordnetData, pwnData, children, level, depth, coveredNodes, synset);
                    } else {
                        //   System.out.println("NO CHILDREN");
                    }

                    data.append("children", synset);
                } else {
                    //   System.out.println("already covered: "+coveredNodes.size());
                }
            }
       // }
        return nConcepts;
    }

    static void printTree (WordnetData wordnetData,WordnetData pwnData,
                           String pos, OutputStream fos)  throws IOException {
        ArrayList<String> topNodes = wordnetData.getTopNodes();
        topNodes = getPosSynsets(topNodes, pos);
        ArrayList<String> coveredSynsets = new ArrayList<String>();
        printTree(wordnetData, pwnData, pos, topNodes, 0, coveredSynsets, fos);
    }
    static void printTree (WordnetData wordnetData,WordnetData pwnData,
                           String pos,
                           ArrayList<String> synsets,
                           int level,
                           ArrayList<String> coveredNodes, OutputStream fos) throws IOException {
        for (int i = 0; i < synsets.size(); i++) {
            String s = synsets.get(i);
            if (!coveredNodes.contains(s)) {
                coveredNodes.add(s);
                String str = "";
                for (int j = 0; j < level; j++) {
                    str += "-";
                }
                String synonyms = wordnetData.getSynsetString(s);
                if (synonyms.isEmpty()) {
                    synonyms ="PWN:"+ pwnData.getSynsetString(s);
                }
                str += s +":"+synonyms+ "\n";
                fos.write(str.getBytes());
                // System.out.println("s = " + s);
                if (wordnetData.childRelations.containsKey(s)) {
                    ArrayList<String> children = wordnetData.childRelations.get(s);
                    if (children!=null) {
                        ArrayList<String> posChildren = getPosSynsets(children, pos);
                        printTree(wordnetData, pwnData, pos, posChildren, level++, coveredNodes, fos);
                    }
                }
            }
        }
    }
    static public ArrayList<String> readOdwnFromFile (String fileName) {
        ArrayList<String> odwns = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                    String [] fields = inputLine.split("\t");
                    if (fields.length>1) {
                        String id = fields[1];
                        if (id.startsWith("odwn")) {
                            if (!odwns.contains(id))  odwns.add(id);
                        }
                    }
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return odwns;
    }

    static ArrayList<String> getPosSynsets (ArrayList<String> synsetIds, String pos) {
        ArrayList<String> posSynsets = new ArrayList<String>();
        for (int i = 0; i < synsetIds.size(); i++) {
            String s = synsetIds.get(i);
            if (s.endsWith(pos)) {
                posSynsets.add(s);
            }
        }
        return posSynsets;
    }

    static ArrayList<String> getNextLevel (ArrayList<String> synsetIds, WordnetData wordnetData) {
        ArrayList<String> nextLevel = new ArrayList<String>();
        for (int i = 0; i < synsetIds.size(); i++) {
            String s = synsetIds.get(i);
            if (wordnetData.childRelations.containsKey(s)) {
                ArrayList<String> children = wordnetData.childRelations.get(s);
                for (int j = 0; j < children.size(); j++) {
                    String child = children.get(j);
                    if (!nextLevel.contains(child)) {
                        nextLevel.add(child);
                    }
                }
            }
        }
        return nextLevel;
    }

    static ArrayList<String> getNextLevel (String s, WordnetData wordnetData) {
        ArrayList<String> nextLevel = new ArrayList<String>();
        if (wordnetData.childRelations.containsKey(s)) {
            ArrayList<String> children = wordnetData.childRelations.get(s);
            for (int j = 0; j < children.size(); j++) {
                String child = children.get(j);
                if (!nextLevel.contains(child)) {
                    nextLevel.add(child);
                }
            }
        }
        return nextLevel;
    }

    static ArrayList<String> buildChildRelationsFromids (WordnetData wordnetData) {
        ArrayList<String> topNodes = new ArrayList<String>();
        wordnetData.childRelations = new HashMap<String, ArrayList<String>>();
        ArrayList<String> odwnHypers = new ArrayList<String>();
        for (int i = 0; i < allOdwn.size(); i++) {
            String synsetId = allOdwn.get(i);
            if (wordnetData.hyperRelations.containsKey(synsetId)) {
                ArrayList<String> hypers = wordnetData.hyperRelations.get(synsetId);
                if (hypers != null) {
                    for (int j = 0; j < hypers.size(); j++) {
                        String hyper = hypers.get(j);
                        if (wordnetData.synsetArrayList.contains(hyper) || hyper.startsWith("eng")) {
                            if (!odwnHypers.contains(hyper)) odwnHypers.add(hyper);
                            if (wordnetData.childRelations.containsKey(hyper)) {
                                ArrayList<String> children = wordnetData.childRelations.get(hyper);
                                if (children.contains(synsetId)) children.add(synsetId);
                                wordnetData.childRelations.put(hyper, children);
                            } else {
                                ArrayList<String> children = new ArrayList<String>();
                                children.add(synsetId);
                                wordnetData.childRelations.put(hyper, children);
                            }
                        }
                        else {
                            System.out.println("Missing target = " + hyper);
                        }
                    }
                }
                else {
                    if (!topNodes.contains(synsetId)) topNodes.add(synsetId);
                }
            }
            else {
                if (!topNodes.contains(synsetId)) topNodes.add(synsetId);
            }
        }
        buildHyperIndex(wordnetData, odwnHypers, allOdwn, topNodes);
        return topNodes;
    }

    static void buildHyperIndex (WordnetData wordnetData, ArrayList<String> ids, ArrayList<String> covered, ArrayList<String> topNodes) {
        ArrayList<String> nextLevel = new ArrayList<String>();
        for (int i = 0; i < ids.size(); i++) {
            String synsetId =  ids.get(i);
            if (!covered.contains(synsetId)) {
                covered.add(synsetId);
                if (wordnetData.hyperRelations.containsKey(synsetId)) {
                    ArrayList<String> hypers = wordnetData.hyperRelations.get(synsetId);
                    if (hypers != null) {
                        for (int j = 0; j < hypers.size(); j++) {
                            String hyper = hypers.get(j);
                            if (wordnetData.synsetArrayList.contains(hyper) || hyper.startsWith("eng")) {
                                if (!nextLevel.contains(hyper)) nextLevel.add(hyper);
                                if (wordnetData.childRelations.containsKey(hyper)) {
                                    ArrayList<String> children = wordnetData.childRelations.get(hyper);
                                    if (!children.contains(synsetId)) {
                                        children.add(synsetId);
                                        wordnetData.childRelations.put(hyper, children);
                                    }
                                } else {
                                    ArrayList<String> children = new ArrayList<String>();
                                    children.add(synsetId);
                                    wordnetData.childRelations.put(hyper, children);
                                }
                            }
                        }
                    }
                    else {
                        if (!topNodes.contains(synsetId)) topNodes.add(synsetId);
                    }
                }
                else {
                    if (!topNodes.contains(synsetId)) topNodes.add(synsetId);
                }
            }
        }
        //   System.out.println("nextLevel.toString() = " + nextLevel.toString());
        if (nextLevel.size()>0) {
            buildHyperIndex(wordnetData, nextLevel, covered, topNodes);
        }
    }

}

