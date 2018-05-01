package vu.wntools.framenet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 24/03/2017.
 */
public class FrameNetLexiconFromAnnotation {

    /*
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<?xml-stylesheet type="text/xsl" href="luIndex.xsl"?>
<luIndex xsi:schemaLocation="schema/luIndex.xsd" xmlns="http://framenet.icsi.berkeley.edu" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<lu hasAnnotation="false" frameName="Familiarity" name="weten.n"/>
<lu hasAnnotation="false" frameName="Desiring" name="gunnen.v"/>
<lu hasAnnotation="false" frameName="Placing" name="deponeren.n"/>
     */
    public static class FrameCount {
        private String frame;
        private int annotationCount;

        public FrameCount(String frame, int annotationCount) {
            this.frame = frame;
            this.annotationCount = annotationCount;
        }

        public FrameCount() {
            this.frame = "";
            this.annotationCount = 0;
        }

        public String getFrame() {
            return frame;
        }

        public void setFrame(String frame) {
            this.frame = frame;
        }

        public int getAnnotationCount() {
            return annotationCount;
        }

        public void setAnnotationCount(int annotationCount) {
            this.annotationCount = annotationCount;
        }
        public void addAnnotationCount(int annotationCount) {
            this.annotationCount += annotationCount;
        }
    }

    public static class FrameNetEntry {
        private String lemma;
        private String pos;
        private ArrayList<FrameCount> frames;

        public FrameNetEntry(String lemma, String pos) {
            this.lemma = lemma;
            this.pos = pos;
            this.frames = new ArrayList<FrameCount>();
        }

        public FrameNetEntry() {
            this.lemma = "";
            this.pos = "";
            this.frames = new ArrayList<FrameCount>();
        }

        public String getLemma() {
            return lemma;
        }

        public void setLemma(String lemma) {
            this.lemma = lemma;
        }

        public String getPos() {
            return pos;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }

        public ArrayList<FrameCount> getFrames() {
            return frames;
        }

        public void setFrames(ArrayList<FrameCount> frames) {
            this.frames = frames;
        }

        public void addFrameCount(FrameCount frameCount) {
            boolean MATCH = false;
            for (int i = 0; i < frames.size(); i++) {
                FrameCount count = frames.get(i);
                if (count.getFrame().equals(frameCount.getFrame())) {
                    count.addAnnotationCount(frameCount.getAnnotationCount());
                    MATCH = true;
                    break;
                }
            }
            if (!MATCH) this.frames.add(frameCount);
        }
    }

    static public void main (String[] args) {
        String pathToFrameLemmaFile = "/Users/piek/Desktop/DutchFrameNet/DFNannotations/DFNlexicon.txt";
        try {
            HashMap<String, FrameNetEntry> lexicon = new HashMap<String, FrameNetEntry>();
            FileInputStream fis = new FileInputStream(pathToFrameLemmaFile);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader in = new BufferedReader(isr);
            String inputLine = "";
            if (in.ready()&&(inputLine = in.readLine()) != null) {
            }
            while (in.ready()&&(inputLine = in.readLine()) != null) {
                if (inputLine.trim().length()>0) {
                    String [] fields = inputLine.split(";") ;
                    //Attaching;koppelen;2
                    String frame = fields[0];

                    String word = "";
                    int cnt = 0;

                    if (fields.length>3) {
                        for (int i = 1; i < fields.length-1; i++) {
                            word += fields[i];
                        }
                        cnt = Integer.parseInt(fields[fields.length-1]);
                    }
                    else if (fields.length==3) {
                        word = fields[1];
                        cnt = Integer.parseInt(fields[2]);
                    }
                    else {
                        System.out.println("inputLine = " + inputLine);
                        continue;
                    }
                    FrameCount frameCount = new FrameCount(frame, cnt);
                    if (lexicon.containsKey(word)) {
                         FrameNetEntry frameNetEntry = lexicon.get(word);
                         frameNetEntry.addFrameCount(frameCount);
                         lexicon.put(word,frameNetEntry);
                    }
                    else {
                        FrameNetEntry frameNetEntry = new FrameNetEntry(word, "v");
                        frameNetEntry.addFrameCount(frameCount);
                        lexicon.put(word,frameNetEntry);
                    }
                }
            }
            try {
                OutputStream fos = new FileOutputStream(pathToFrameLemmaFile+".lex");
                String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
                str += "<fnLexicon lang=\"nl\">\n";
                fos.write(str.getBytes());
                Set keySet = lexicon.keySet();
                Iterator<String> keys = keySet.iterator();
                while (keys.hasNext()) {
                    String word = keys.next();
                    FrameNetEntry frameNetEntry = lexicon.get(word);
                    str = "<ENTRY lemma=\""+word+"\""+ " pos=\""+frameNetEntry.getPos()+"\">\n";
                    ArrayList<FrameCount> frames = frameNetEntry.getFrames();
                    for (int i = 0; i < frames.size(); i++) {
                        FrameCount frameCount = frames.get(i);
                        str += "\t<frameAnnotation frame=\""+frameCount.getFrame()+"\""+" annotations=\""+frameCount.getAnnotationCount()+"\"/>\n";
                    }
                    str += "</ENTRY>\n";
                    fos.write(str.getBytes());
                }
                str += "</fnLexicon>\n";
                fos.write(str.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
