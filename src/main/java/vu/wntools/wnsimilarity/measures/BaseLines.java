package vu.wntools.wnsimilarity.measures;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 8/14/12
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseLines {

    public static String match = "";

    public static double GetPath (ArrayList<String> hyp1, ArrayList<String> hyp2) {
        double distance = -1;
        for (int i = 0; i < hyp1.size(); i++) {
            String s = hyp1.get(i);
            int i2 = hyp2.indexOf(s);
            if (i2>-1) {
                distance = 1+i+i2;
                match = s;
                break;
            }
        }
        distance = 1/distance;
        return distance;
    }

    public static int GetDistanceHyperChain (ArrayList<String> hyp1, ArrayList<String> hyp2) {
        int distance = -1;
        for (int i = 0; i < hyp1.size(); i++) {
            String s = hyp1.get(i);
            int i2 = hyp2.indexOf(s);
            if (i2>-1) {
                distance = 1+i+i2;
                match = s;
                break;
            }
        }
        return distance;
    }

    public static double GetDistanceWordBaseline (int averageDepth, ArrayList<String> hyp1, ArrayList<String> hyp2) {
        /// Adapated Leacock Chodorow algorithm (1988). Average depth is based on the depth of the words in the document
        double distance = -1;
        match = "";
        if (hyp1.get(0).equals(hyp2.get(0))) {
            distance = 1;
        }
        if (distance>-1) {
            distance = - Math.log(distance/(2*averageDepth));
            match = hyp1.get(0);
        }
        return distance;
    }

    public static double GetDistanceSynonymBaseline (int averageDepth, ArrayList<String> hyp1, ArrayList<String> hyp2) {
        /// Adapated Leacock Chodorow algorithm (1988). Average depth is based on the depth of the words in the document
        double distance = -1;
        match = "";
        if (hyp1.get(0).equals(hyp2.get(0))) {
            distance = 1;
            match = hyp1.get(0);
        }
        else if ((hyp1.size()>1) && (hyp2.size()>1)) {
            if (hyp1.get(1).equals(hyp2.get(1))) {
                match = hyp1.get(1);
                distance = 2;
            }
        }
        if (distance>-1) {
            distance = - Math.log(distance/(2*averageDepth));
        }
        return distance;
    }

    public static String GetDistanceHyperChainDebug (ArrayList<String> hyp1, ArrayList<String> hyp2) {
        String distance = "";
        match = "";
        for (int i = 0; i < hyp1.size(); i++) {
            String s = hyp1.get(i);
            int i2 = hyp2.indexOf(s);
            if (i2>-1) {
                distance = (1+i+i2)+":"+s;
                match = s;
                break;
            }
        }
        return distance;
    }
}
