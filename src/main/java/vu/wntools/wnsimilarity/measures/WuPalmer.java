package vu.wntools.wnsimilarity.measures;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 3/11/13
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class WuPalmer {


    public static String match = "";

    /**
     *  Calculates the distance according to Wu and Palmer
     * @param hyp1
     * @param hyp2
     * @return
     */
    public static double GetDistance (ArrayList<String> hyp1, ArrayList<String> hyp2) {
        /// Adapted Wu & palmer. Average depth can be based on the depth of the words in the document,
        //// average of all wordnet or of the two synsets compared
        double distance = -1;
        match = "";
        int i2 = -1;
        int i1 = -1;
        for (int i = 0; i < hyp1.size(); i++) {
            String s = hyp1.get(i);
            i2 = hyp2.indexOf(s);
           // System.out.println("i2 = " + i2);
            if (i2>-1) {
                i1 = i;
                distance = 1+i+i2; /// initial one for node counting, avoid zero values
                match = s;
                break;
            }
            else {
                //// there is no intersection so the distance remain -1
            }
        }
/*     original variant that does not take the fact into account that there can be multiple paths due
       to multiple hypernyms. The LCs thus can be at different depths depending on which chain is used
       */
/*       if (distance>-1) {
            double depthLcs = hyp2.size()-i2;
            System.out.println("hyp1 = " + hyp1);
            System.out.println("hyp2 = " + hyp2);
            System.out.println("distance = " + distance);
            System.out.println("depthLcs = " + depthLcs);
            distance = 2*depthLcs / (double) ((hyp1.size()-1)+(hyp2.size()-1));
            System.out.println("score = " + distance);
        }*/

        /** New variant that takes the depths of each hyper chain into account

         */
        if (distance>-1) {
            double depthLcs1 = hyp1.size()-i1;
            double depthLcs2 = hyp2.size()-i2;
/*
            System.out.println("hyp1 = " + hyp1);
            System.out.println("hyp2 = " + hyp2);
            System.out.println("distance = " + distance);
            System.out.println("depthLcs1 = " + depthLcs1);
            System.out.println("depthLcs2 = " + depthLcs2);
*/
            distance = (depthLcs1+depthLcs2) / (double) ((hyp1.size())+(hyp2.size()));
//            System.out.println("score = " + distance);
        }
        return distance;
    }


}
