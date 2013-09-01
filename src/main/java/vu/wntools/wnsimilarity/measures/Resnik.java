package vu.wntools.wnsimilarity.measures;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 4/18/12
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class Resnik {

    public static String match = "";
    public static double value = 0;

    public static double GetDistance(HashMap<String, Long> hyperFrequencies, Long nWords, ArrayList<String> hyp1, ArrayList<String> hyp2) {
        double distance = -1;
        match = "";
        for (int i = 0; i < hyp1.size(); i++) {
            String s = hyp1.get(i);
            int i2 = hyp2.indexOf(s);
            if (i2>-1) {
                double freq = 0;
                double p = -1;
                if (hyperFrequencies.containsKey(s)) {
                    freq = (double) hyperFrequencies.get(s);
                    p = freq/(double)nWords;
                    value = p;
                }
                distance = -Math.log(p);
                //distance = p;
                match = s;
                break;
            }
        }
        return distance;
    }
}
