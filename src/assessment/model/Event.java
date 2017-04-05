package assessment.model;

import java.io.Serializable;

/**
 * <h1>PPA assignment 14 </h1> <br>
 * Computer Science <br>
 * Year 1
 * <p>
 * This class holds Event data
 *
 * @author Daniel Lopes De Castro (k1630458), Wonjoon Seol (k1631098),
 */
public class Event implements Serializable{
    private String event;

    public Event(String string) {
        event = string;
    }

    public String toString() {
        return event;
    }

}

