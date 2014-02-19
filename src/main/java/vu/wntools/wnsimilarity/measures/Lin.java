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


    public static double GetDistance (HashMap<String, Long> hyperFrequencies, Long topFrequency, ArrayList<String> hyp1, ArrayList<String> hyp2) {
        double distance = -1;
        match = "";
        valueIc1 = 0;
        valueIc2 = 0;
        valueIcLcs = 0;

        int i1 = -1;
        int i2 = -1;
        double ic1 = 0;
        double ic2 = 0;
        double icLcs = 0;

        if (hyperFrequencies.containsKey(hyp1.get(0))) {
            double prob = (double) hyperFrequencies.get(hyp1.get(0));
            ic1 = -Math.log(prob/(double) topFrequency);
            valueIc1 = ic1;
         //   System.out.println("valueIc1 = " + valueIc1);
        }

        if (hyperFrequencies.containsKey(hyp2.get(0))) {
            double prob = (double) hyperFrequencies.get(hyp2.get(0));
            ic2 = -Math.log(prob/(double) topFrequency);
            valueIc2 = ic2;
         //   System.out.println("valueIc2 = " + valueIc2);
        }

/*      hypernym fallback but it creates INFINITY values
        example:
           hyp1.get(0) is hyp2.get(1) and hyp2.get(0) has no entry in hyperFrequencies
           In that case, the ic1 equals ic2 equals icLcs while the target (2) is still a child of source (1)
hyp1 = [eng-30-03151500-n, eng-30-03873064-n, eng-30-00021939-n, eng-30-00003553-n, eng-30-00002684-n, eng-30-00001930-n, eng-30-00001740-n]
hyp2 = [eng-30-03938244-n, eng-30-03151500-n, eng-30-03873064-n, eng-30-00021939-n, eng-30-00003553-n, eng-30-00002684-n, eng-30-00001930-n, eng-30-00001740-n]
i1 = 0
i2 = 1
match = eng-30-03151500-n
ic1 = 9.568535271308354
ic2 = 9.568535271308354
icLcs = 9.568535271308354
ic1+ic2 = 9.5685352713083549.568535271308354
(ic1+ic2-(2*icLcs)) = 0.0
distance = Infinity
*/
        /*

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
*/

        for (int i = 0; i < hyp1.size(); i++) {
            String s = hyp1.get(i);
            i2 = hyp2.indexOf(s);
            if (i2>-1) {
                i1= i;
                double prob = 0;
                if (hyperFrequencies.containsKey(s)) {
                    prob = (double) hyperFrequencies.get(s);
                }
                icLcs = -Math.log(prob / (double) topFrequency);
                if (icLcs==-0) {
                  //  System.out.println("prob = " + prob);
                  //  System.out.println("nWords = " + topFrequency);
                }
                valueIcLcs = icLcs;
                match = s;
                // System.out.println("prob = " + prob);
                // System.out.println("nWords = " + nWords);
                // System.out.println("s = " + s);
                // System.out.println("icLcs = " + icLcs);
                break;
            }
        }

        //we often do not have the frequency of the word and get the frequency of the hypernym
        //to avoid dividing by zero, we set distance to 0
        if (ic1==0 || ic2==0)    {
            distance = 0;
        }
        else if (i1==0 && i2==0)    {
            /// the source and target are synonyms
            distance = 128676699.5;
        }
        else if (ic1==ic2 && ic2==icLcs)    {
            /// apparently our hypernym chain deviates from the hypernym chain that was used to calculate
            /// the information content. The latter hypernym chain considers them to be synonyms, resulting
            /// in equal values for the information content of synsets that are in a hyponymy relation
            /// according to our hypernym chain. The reason for this is probably that we use multiple hypernym
            /// chains and the others do not. Another reason could be that different versions of wordnet have
            /// been used to calculate the information content and in this tool set.

            /// we assume the synonymy relation here to make the results compatible with the information content approach
            /// we therefore set the distance to the highest value
            distance = 128676699.5;
        }
        else {
            distance = (2*icLcs)/(ic1+ic2);
        }

/*        if (distance==-0.0) {
            System.out.println("hyp1.toString() = " + hyp1.toString());
            System.out.println("hyp2.toString() = " + hyp2.toString());
            System.out.println("i1 = " + i1);
            System.out.println("i2 = " + i2);
            System.out.println("match = " + match);
            System.out.println("ic1 = " + ic1);
            System.out.println("ic2 = " + ic2);
            System.out.println("icLcs = " + icLcs);
            System.out.println("ic1+ic2 = " + ic1 + ic2);
            System.out.println("(ic1+ic2-(2*icLcs)) = " + (ic1 + ic2 - (2 * icLcs)));
            System.out.println("distance = " + distance);
        }*/
        return distance;
    }
}
