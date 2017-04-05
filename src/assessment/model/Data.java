package assessment.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * <h1>PPA assignment 14 </h1> <br>
 * Computer Science <br>
 * Year 1
 * <p>
 * This class holds instance of calendar data
 *
 * @author Daniel Lopes De Castro (k1630458), Wonjoon Seol (k1631098),
 */
public class Data implements Serializable{
    private Event event;
    private Location location;
    private LocalDate date;
    private LocalTime time;
    private int dataStatus;

    public static final int CALENDAR_DATA = 0;
    public static final int REMINDER_DATA = 1;

    public Data() {
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setDataStatus(int dataStatus) {
        this.dataStatus = dataStatus;
    }

    public Location getLocation() {
        return location;
    }

    public LocalTime getTime() {
        return time;
    }

    /**
     * @return Returns the status of the data to see if it is for the Calendar or the Reminder.
     */
    public String getStatus() {
        switch (dataStatus) {
            case 0 : return "Calendar data";
            case 1 : return "Reminder data";
            default : return null;
        }
    }

    public Event getEvent() {
        return event;
    }

    public LocalDate getDate() {
        return date;
    }
}
