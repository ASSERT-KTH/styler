

/*
* The above interface is used by the reporters to update the live commentary
* on the commentary object. It’s an optional interface just to follow the code
* to interface principle, not related to the Observer pattern
* */
public interface Commentary {

    // set the description for the commenraty
    public void setDesc(String desc);
}
