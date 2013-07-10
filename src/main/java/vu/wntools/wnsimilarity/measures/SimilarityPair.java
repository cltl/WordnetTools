package vu.wntools.wnsimilarity.measures;;

/**
 * Created with IntelliJ IDEA.
 * User: kyoto
 * Date: 8/14/12
 * Time: 10:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimilarityPair {

    private String sourceId;
    private String targetId;
    double score;
    double normalizedScore;

    public SimilarityPair() {
        this.normalizedScore = -1;
        this.score = -1;
        this.sourceId = "";
        this.targetId = "";
    }

    public double getNormalizedScore() {
        return normalizedScore;
    }

    public void setNormalizedScore(double topScore) {
        this.normalizedScore = score/topScore;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}
