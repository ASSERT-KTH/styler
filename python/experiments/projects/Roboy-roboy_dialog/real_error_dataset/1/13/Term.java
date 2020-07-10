package roboy.linguistics;

import java.util.List;
import java.util.Objects;


public class Term {
    private List<String> pos = null;
	private float probability = 0;
	private String concept = null;

    public List<String> getPos() {
        return pos;
    }

    public void setPos(List<String> pos) {
        this.pos = pos;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float prob) {
        this.probability = prob;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    @Override
    public String toString() {
        return "Term{" +
                "pos=" + pos +
                ", prob=" + probability +
                ", concept='" + concept + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Term comparableObject = (Term) obj;
        return Float.compare(comparableObject.getProbability(), getProbability()) == 0 &&
                Objects.equals(getPos(), comparableObject.getPos()) &&
                Objects.equals(getConcept(), comparableObject.getConcept());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPos(), getProbability(), getConcept());
    }
}
