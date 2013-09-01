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
public class Lin {

    public static String match = "";
    public static double valueIc1 = 0;
    public static double valueIc2 = 0;
    public static double valueIcLcs = 0;


    public static double GetDistance (HashMap<String, Long> hyperFrequencies, Long nWords, ArrayList<String> hyp1, ArrayList<String> hyp2) {
        double distance = -1;
        match = "";
        double ic1 = -1;
        double ic2 = -1;
        double icLcs = -1;
        for (int i = 0; i < hyp1.size(); i++) {
            String s = hyp1.get(i);
            if (hyperFrequencies.containsKey(s)) {
                double prob = (double) hyperFrequencies.get(s);
                ic1 = -Math.log(prob/(double) nWords);
                valueIc1 = ic1;
                break;
            }
        }
        for (int i = 0; i < hyp2.size(); i++) {
            String s = hyp2.get(i);
            if (hyperFrequencies.containsKey(s)) {
                double prob = (double) hyperFrequencies.get(s);
                ic2 = -Math.log(prob/(double) nWords);
                valueIc2 = ic2;
                break;
            }
        }
        for (int i = 0; i < hyp1.size(); i++) {
            String s = hyp1.get(i);
            int i2 = hyp2.indexOf(s);
            if (i2>-1) {
                double prob = 0;
                if (hyperFrequencies.containsKey(s)) {
                    prob = (double) hyperFrequencies.get(s);
                }
                icLcs = -Math.log(prob/(double) nWords);
                valueIcLcs = icLcs;
                match = s;
                break;
            }
        }

        distance = 2*icLcs/(ic1+ic2);
        //distance = Math.log(2*icLcs/(ic1+ic2));
        return distance;
    }
}
