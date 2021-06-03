public class Term {
    private String sourceName;
    private int sourceOcurrence;
    private int outputOcurrence;
    private String outputName;

    public Term(String sourceName, String outputName) {
        this.sourceName = sourceName;
        this.outputName = outputName;
        this.sourceOcurrence = 0;
        this.outputOcurrence = 0;
    }

    public int getSourceOcurrence() {
        return sourceOcurrence;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getOutputName() {
        return outputName;
    }

    public int getOutputOcurrence() {
        return outputOcurrence;
    }


    public void incrementSourceOcurrence(){
        this.sourceOcurrence = this.sourceOcurrence+1;
    }

    public void incrementOutputOcurrence(){
        this.outputOcurrence = this.outputOcurrence+1;
    }

    @Override
    public String toString() {
        return "Term{" +
                "sourceName='" + sourceName + '\'' +
                ", sourceOcurrence=" + sourceOcurrence +
                ", outputOcurrence=" + outputOcurrence +
                ", outputName='" + outputName + '\'' +
                '}';
    }
}
