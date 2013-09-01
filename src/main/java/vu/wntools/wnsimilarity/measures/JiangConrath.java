package vu.wntools.wnsimilarity.measures;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 3/11/13
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class JiangConrath {

    public static String match = "";
    public static double valueIc1 = 0;
    public static double valueIc2 = 0;
    public static double valueIcLcs = 0;

    public static double GetDistance (HashMap<String, Long> hyperFrequencies, Long nWords, ArrayList<String> hyp1, ArrayList<String> hyp2) {
        double distance = -1;
        match = "";
        int i1 = -1;
        int i2 = -1;
        double ic1 = 0;
        double ic2 = 0;
        double icLcs = 0;
        for (int i = 0; i < hyp1.size(); i++) {
            String s = hyp1.get(i);
            if (hyperFrequencies.containsKey(s)) {
                double prob = (double) hyperFrequencies.get(s);
                ic1 = -Math.log(prob/(double) nWords);
                valueIc1 = ic1;
               // System.out.println("s = " + s);
               // System.out.println("ic1 = " + ic1);
                break;
            }
        }
        for (int i = 0; i < hyp2.size(); i++) {
            String s = hyp2.get(i);
            if (hyperFrequencies.containsKey(s)) {
                double prob = (double) hyperFrequencies.get(s);
                ic2 = -Math.log(prob/(double) nWords);
                valueIc2 = ic2;
                //System.out.println("s = " + s);
                //System.out.println("ic2 = " + ic2);
                break;
            }
        }
        for (int i = 0; i < hyp1.size(); i++) {
            String s = hyp1.get(i);
            i2 = hyp2.indexOf(s);
            if (i2>-1) {
                i1 = i;
                double prob = 0;
                if (hyperFrequencies.containsKey(s)) {
                    prob = (double) hyperFrequencies.get(s);
                }
                icLcs = -Math.log(prob/(double) nWords);
                valueIcLcs = icLcs;
                match = s;
                //System.out.println("s = " + s);
                //System.out.println("icLcs = " + icLcs);
                break;
            }
        }
       // System.out.println("ic1 = " + ic1);
       // System.out.println("ic2 = " + ic2);
       // System.out.println("icLcs = " + icLcs);

        //we often do not have the frequency of the word and get the frequency of the hypernym
        //to avoid dividing by zero, we divide the frequency by two
        if (ic1==0 || ic2==0)    {
            distance = 0;
        }
        else if (i1==0 && i2==0)    {
            /// the source and target are synonyms
            distance = 128676699.5;
        }
        else {
            distance = (1/(ic1+ic2-(2*icLcs)));
        }
        //System.out.println("JC distance = " + distance);
        return distance;
    }
}
