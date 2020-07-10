package roboy.linguistics;

import java.util.Objects;


public class Concept {
	private String id;

	public Concept(String id) {
	    this.id = id;
    }

    public String getId() {
	    return id;
    }

    @Override
    public String toString() {
        return "Concept{" +
                "id='" + id + '\'' +
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

        Concept comparableObject = (Concept) obj;
        return Objects.equals(getId(), comparableObject.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
