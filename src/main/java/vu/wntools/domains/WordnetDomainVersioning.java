package vu.wntools.domains;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 2/14/13
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordnetDomainVersioning {


    static public void main (String [] args ) {
        try {
            String pathToWnMappingFile = "/Resources/wn-vu.wntools.domains-3.2/mapping-20-30/wn20-30.noun";
            String pathToWordnetDomainFile = "/Resources/wn-vu.wntools.domains-3.2/wn-vu.wntools.domains-3.2-20070223";
            String prefix = "eng-30-";
            String postag = "-n";
            HashMap<String, ArrayList<String>> domainMap = new HashMap<String, ArrayList<String>>();
            HashMap<String, ArrayList<String>> wnMap = new HashMap<String, ArrayList<String>>();
            domainMap = readDomainFileToHashMapArrayList(pathToWordnetDomainFile);
            System.out.println("domainMap = " + domainMap.size());
            wnMap = readWnFileToHashMapArrayList(pathToWnMappingFile, postag);
            System.out.println("wnMap.size() = " + wnMap.size());
            FileOutputStream fos = new FileOutputStream(pathToWordnetDomainFile+".v30."+postag);
            Set keySet = domainMap.keySet();
            Iterator keys = keySet.iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                //System.out.println("key = " + key);
                ArrayList<String> values = domainMap.get(key);
                if (wnMap.containsKey(key)) {
                    ArrayList<String> synsets = wnMap.get(key);
                    for (int i = 0; i < synsets.size(); i++) {
                        String synset =  synsets.get(i);
                        for (int v = 0; v < values.size(); v++) {
                            String domain = values.get(v);
                            String str = prefix+synset+ " " + domain+"\n";
                            fos.write(str.getBytes());
                        }
                    }
                }
                else {
                    if (key.equals("06843539-n")) {
                        System.out.println("could not find key = " + key);
                    }
                }

            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    static public HashMap<String, ArrayList<String>> readDomainFileToHashMapArrayList (String fileName) {
        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
        try {
            FileInputStream fis = new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                    String [] fields = inputLine.split("\t");
                    if (fields.length>=2) {
                        String key = fields[0].trim();
                        String value = fields[1].trim();
                        if (map.containsKey(key)) {
                            ArrayList<String> values = map.get(key);
                            values.add(value);
                            map.put(key,values);
                        }
                        else {
                            ArrayList<String> values = new ArrayList<String>();
                            values.add(value);
                            map.put(key,values);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return map;
    }

    static public HashMap<String, ArrayList<String>> readWnFileToHashMapArrayList (String fileName, String postag) {
        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
        try {
            FileInputStream fis = new FileInputStream(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                    /*
                    00042699 01246148 1
00042825 00040325 1
00043034 00805115 0.183 02010441 0.5 02106366 0.317
                     */
                    String [] fields = inputLine.split(" ");
                    if (fields.length>=2) {
                        String key = fields[0].trim()+postag;
                        ArrayList<String> values = new ArrayList<String>();
                        if (map.containsKey(key)) {
                            values = map.get(key);
                        }
                        else {
                        }
                        for (int i = 1; i < fields.length; i++) {
                            String value = fields[i].trim()+postag;
                            i++;
                            values.add(value);

                        }
                        map.put(key,values);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return map;
    }
}
