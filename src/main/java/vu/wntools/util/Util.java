package vu.wntools.util;

import vu.wntools.wordnet.WordnetData;

import java.io.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 9/13/13
 * Time: 7:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    static public void writeTreeString (WordnetData wordnetData, ArrayList<String> topNodes, int level, FileOutputStream fos) {
        String str = "";
        for (int i = 0; i < topNodes.size(); i++) {
            String hper = topNodes.get(i);
            str = "";
            if (wordnetData.childRelations.containsKey(hper)) {
                for (int j = 0; j < level; j++) {
                    str += "  ";

                }
                str += hper+"\n";
                //  System.out.println("str = " + str);
                try {
                    fos.write(str.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                ArrayList<String> children = wordnetData.childRelations.get(hper);
                if (children.size()>0) {
                    writeTreeString(wordnetData, children, level+1, fos);
                }
                else {
                    //     System.out.println("leaf node.getSynset() = " + node.getSynset());
                }
            }
        }
    }

    static public void writeTreeString (WordnetData wordnetData, String parent, int level, FileOutputStream fos, ArrayList<String> done) {
        String str = "";
        str = "";

        for (int j = 0; j < level; j++) {
            str += "  ";

        }
        str += "["+parent;
        if (wordnetData.synsetToEntries.containsKey(parent)) {
            ArrayList<String> entries = wordnetData.synsetToEntries.get(parent);
            for (int j = 0; j < entries.size(); j++) {
                str += ";"+ entries.get(j);
            }
        }

        str += "]\n";
        done.add(parent);
        //  System.out.println("str = " + str);
        try {
            fos.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (wordnetData.childRelations.containsKey(parent)) {
            ArrayList<String> children = wordnetData.childRelations.get(parent);
            if (children.size()>0) {
                for (int i = 0; i < children.size(); i++) {
                    String child =  children.get(i);
                    if (!done.contains(child)) {
                        writeTreeString(wordnetData, child, level+1, fos, done);
                    }
                }
            }
            else {
                //     System.out.println("leaf node.getSynset() = " + node.getSynset());
            }
        }
        else {
           // System.out.println("No relations found");
        }
    }



    static public ArrayList<String> readRelationsFile (String pathToRelationFile) {
        ArrayList<String> relations = new ArrayList<String>();
        if (!new File(pathToRelationFile).exists())   {
            System.out.println("Cannot find pathToRelationFile = " + pathToRelationFile);
        }
        try {
            FileInputStream fis = new FileInputStream(pathToRelationFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (!inputLine.trim().isEmpty()) {
                    if (!inputLine.startsWith("#"))
                        relations.add(inputLine.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //To change body of catch statement use File | Settings | File Templates.
        }
        return relations;
    }

    static public ArrayList<String> readFile (String pathToFile) {
        ArrayList<String> relations = new ArrayList<String>();
        if (!new File(pathToFile).exists())   {
            System.out.println("Cannot find pathToFile = " + pathToFile);
        }
        try {
            FileInputStream fis = new FileInputStream(pathToFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (!inputLine.trim().isEmpty()) {
                        relations.add(inputLine.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            //To change body of catch statement use File | Settings | File Templates.
        }
        return relations;
    }


}
