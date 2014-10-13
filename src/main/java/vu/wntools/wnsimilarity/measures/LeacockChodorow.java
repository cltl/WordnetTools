package vu.wntools.wnsimilarity.measures;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 4/16/12
 * Time: 11:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class LeacockChodorow {
    public static String match = "";
    /**
     *  Calculates the distance according to Leacock & Chodorow by
     * @param averageDepth
     * @param hyp1
     * @param hyp2
     * @return
     */
    public static double GetDistance(int averageDepth, ArrayList<String> hyp1, ArrayList<String> hyp2) {
        /// Adapted Leacock Chodorow algorithm (1988). Average depth can be based on the depth of the words in the document,
        //// average of all wordnet or of the two synsets compared
        double distance = -1;
        match = "";
        for (int i = 0; i < hyp1.size(); i++) {
            String s = hyp1.get(i);
            int i2 = hyp2.indexOf(s);
            if (i2>-1) {
                distance = 1+i+i2; /// node counting additional 1 for dividing by zero
                match = s;
                break;
            }
            else {
                //// there is no intersection so the distance remains -1
            }
        }
        if (distance>-1) {
            distance = - Math.log(distance/(2*averageDepth));
        }
        return distance;
    }
}
