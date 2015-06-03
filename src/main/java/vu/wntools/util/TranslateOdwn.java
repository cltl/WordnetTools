package vu.wntools.util;

import vu.wntools.wordnet.WordnetLmfSenseSaxParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 01/06/15.
 */
public class TranslateOdwn {

    static public void main (String[] args) {
        try {
            String pathToOdwnFile = "/Code/vu/WordnetTools/resources/odwn1.0.lmf";
            WordnetLmfSenseSaxParser parser = new WordnetLmfSenseSaxParser();
            parser.parseFile(pathToOdwnFile);
            OutputStream fos = new FileOutputStream(pathToOdwnFile+"trans");
            Set keySet = parser.synsetDefinitionMap.keySet();
            Iterator<String> keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                ArrayList<String> definitions = parser.synsetDefinitionMap.get(key);
                for (int i = 0; i < definitions.size(); i++) {
                    String def = definitions.get(i);
                    String str = key+"# "+def+"\n";
                    fos.write(str.getBytes());
                }
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
