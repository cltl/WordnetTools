package vu.wntools.util;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 5/8/13
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class Pos {


    static public String convertToShortPos (String pos) {
        String shortPos = pos;
        if (pos.equalsIgnoreCase("noun")) {
            shortPos = "n" ;
        }
        else if (pos.equalsIgnoreCase("verb")) {
            shortPos = "v" ;
        }
        else if (pos.equalsIgnoreCase("adj")) {
            shortPos = "a" ;
        }
        else if (pos.equalsIgnoreCase("adjective")) {
            shortPos = "a" ;
        }
        else if (pos.equalsIgnoreCase("adverb")) {
            shortPos = "r" ;
        }
        return shortPos;
    }
}
