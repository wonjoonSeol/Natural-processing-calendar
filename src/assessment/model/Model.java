package assessment.model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.temporal.TemporalAdjusters.next;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

/**
 * <h1>PPA assignment 14 </h1> <br>
 * Computer Science <br>
 * Year 1
 * <p>
 * This class represents Calendar processor
 *
 * @author Daniel Lopes De Castro (k1630458), Wonjoon Seol (k1631098),
 */
public class Model extends Observable implements Serializable {

    private String userId;
    private String text;
    private ArrayList<Data> calendarData;
    private ArrayList<Data> reminderData;
    private Pattern datePattern;
    private Pattern weekdayPattern;
    private Pattern timePattern;
    private Pattern timePhrasePattern;
    private Pattern justWeekdayP;
    private Pattern monthPattern;
    private Pattern dayNumberPattern;
    private Pattern locationPattern;
    private Pattern dayMonthPattern;
    private Pattern yearPattern;

    public Model() {
        generatePatterns();
        calendarData = new ArrayList<>();
        reminderData = new ArrayList<>();
    }

    /**
     * sets the userID as the string provided in the parameter
     * @param userId
     */
    public void setUserID(String userId) {
        this.userId = userId;
    }

    /**
     * Uses regex's to create the patterns to be checked in the text provided to find dates, times, events and locations.
     */
    private void generatePatterns() {
        datePattern = Pattern.compile("(^|\\s)((on\\s)?((3[0-1])|([0-2]\\d))(/|-)((1[0-2])|(0\\d)|([1-9]))(/|-)(\\d{4}|\\d{2}))");
        weekdayPattern = Pattern.compile("(^|\\s)(((on|next)\\s)?(mon|tues|wednes|thurs|fri|satur|sun)day\\s)(((([1-2]\\d)|(3[0-1])|[1-9])(st|rd|th|nd)\\s)(january|feburary|march|april|june|july|august|september|october|november|december)?)?", Pattern.CASE_INSENSITIVE);
        monthPattern = Pattern.compile("(^|\\s)(january|feburary|march|april|june|july|august|september|october|november|december)", Pattern.CASE_INSENSITIVE);
        justWeekdayP = Pattern.compile("(^|\\s)(mon|tues|wednes|thurs|fri|satur|sun)day", Pattern.CASE_INSENSITIVE);
        dayNumberPattern = Pattern.compile("(^|\\s)(([1-2]\\d)|3[0-1]|[1-9])(rd|th|st|nd)", Pattern.CASE_INSENSITIVE);
        timePattern = Pattern.compile("(^|\\s)(((on\\s)?(1[0-2]|(0\\d)|\\d):[0-5]\\d))|((at\\s)?(([1-9]|1[0-2])(am|pm)))", Pattern.CASE_INSENSITIVE);
        timePhrasePattern = Pattern.compile("(^|\\s)(in(\\sthe)? (morn|even)ing)", Pattern.CASE_INSENSITIVE);
        locationPattern = Pattern.compile("(^|\\s)at", Pattern.CASE_INSENSITIVE);
        yearPattern = Pattern.compile("(^|\\s)\\d\\d\\d\\d");
        dayMonthPattern = Pattern.compile("(^|\\s)(on)?((([1-2]\\d)|(3[0-1])|[1-9])(st|rd|th|nd)\\s)(january|feburary|march|april|june|july|august|september|october|november|december)(\\s\\d\\d\\d\\d)?", Pattern.CASE_INSENSITIVE);
    }

    /**
     * Removes the data from the calendarData list, provided that it is within the range of the list's indexes
     * @param i index to be removed
     */
    public void removeCalendarData(int i) {
        if (i < calendarData.size()) {
            calendarData.remove(i);
            setChanged();
            notifyObservers("Calendar " + i);
        }
    }

    /**
     * Removes the data from the reminderData list, provided that it is within the range of the list's indexes
     * @param i index to be removed
     */
    public void removeReminderData(int i) {
        if (i < reminderData.size()) {
            reminderData.remove(i);
            setChanged();
            notifyObservers("Reminder " + i);
        }
    }

    /**
     * Checks to see if the new data is a calendar event or a reminder, so that the data can be added to the correct list of data.
     * It runs the matcherRecursion() method with the text provided and the data as parameters,
     * and also saves the data for the current user by using the saveData() method.
     */
    public void textProcessor() {
        Data data = new Data();

        if (reminderChecker()) {
            data.setDataStatus(Data.REMINDER_DATA);
            reminderData.add(data);
        } else {
            calendarData.add(data);
        }
        matcherRecursion(text, data);
        setChanged();
        notifyObservers(data);
        saveData(userId);
    }

    /**
     * Through the use of the regex's used in the generatePatterns() method, this recursion
     * identifies which parser method to use with the data so that it can be classified correctly.
     * @param text
     * @param data
     */
    private void matcherRecursion(String text, Data data) {
        Matcher dateMatcher = datePattern.matcher(text);
        Matcher weekdayMatcher = weekdayPattern.matcher(text);
        Matcher timeMatcher = timePattern.matcher(text);
        Matcher timePhraseMatcher = timePhrasePattern.matcher(text);
        Matcher locationMatcher = locationPattern.matcher(text);
        Matcher dayMonthMatcher = dayMonthPattern.matcher(text);
        String stringMiddle = "";
        String stringBefore = "";
        if (!text.isEmpty()) {
            if (dateMatcher.find()) {                                       // Check if text contains datePattern regex
                stringBefore = text.substring(0, dateMatcher.start());      // Substring before matching part
                stringMiddle = dateMatcher.group(0);                 // Matching part
                stringMiddle = stringMiddle.trim();                         // trim all whitespace beginning and end of the string
                dateParser(stringMiddle, data);                             // parse date information from the matching string
                recursionSubroutine(dateMatcher, data, text);               // generate substring after the matching part if condition is met and init recursion

            } else if (weekdayMatcher.find()) {
                stringBefore = text.substring(0, weekdayMatcher.start());
                weekdayParser(text, data);
                recursionSubroutine(weekdayMatcher, data, text);

            } else if (dayMonthMatcher.find()) {
                stringBefore = text.substring(0, dayMonthMatcher.start());
                weekdayParser(text, data);
                recursionSubroutine(dayMonthMatcher, data, text);

            } else if (timeMatcher.find()) {
                stringBefore = text.substring(0, timeMatcher.start());
                stringMiddle = timeMatcher.group(0);
                stringMiddle = stringMiddle.trim();
                timeParser(stringMiddle, data);
                recursionSubroutine(timeMatcher, data, text);

            } else if (timePhraseMatcher.find()) {
                stringBefore = text.substring(0, timePhraseMatcher.start());
                stringMiddle = timePhraseMatcher.group(0);
                stringMiddle = stringMiddle.trim();
                timePhraseParser(stringMiddle, data);
                recursionSubroutine(timePhraseMatcher, data, text);

            } else if (locationMatcher.find()) {
                int index = text.toLowerCase().indexOf("at");
                stringBefore = text.substring(0, index);
                if (index + 3 < text.length()) data.setLocation(new Location(text.substring(index + 3)));       //+3 to remove 'at ' from the string
                if (index != 0) matcherRecursion(stringBefore, data);

            } else {
                text = text.trim();
                data.setEvent(new Event(text));
            }
                matcherRecursion(stringBefore, data);
        }
    }

    private void recursionSubroutine(Matcher matcher, Data data, String text) {
        String stringAfter = "";
        if (matcher.end() < text.length()) {
            stringAfter = text.substring(matcher.end(), text.length());
            matcherRecursion(stringAfter, data);
        }
    }

    /**
     * Uses LocalDate to set the data as a date, depending on the information in the String provided in text.
     * @param text
     * @param data
     */
    private void dateParser(String text, Data data) {
        if (text.contains("on")) text = text.substring(3);
        String[] stringArray = text.split("/|-");
        data.setDate(LocalDate.of(Integer.parseInt(stringArray[2]), Integer.parseInt(stringArray[1]), Integer.parseInt(stringArray[0])));
    }

    /**
     * Uses LocalDate to set the data as a date. Depends on the format of the String provided in text, since it can vary a lot.
     * It identifies it to see, for example, if just a day number was provided, or a day with month.
     * @param text
     * @param data
     */
    private void weekdayParser(String text, Data data) {
        Matcher justDayMatcher = justWeekdayP.matcher(text);
        Matcher monthMatcher = monthPattern.matcher(text);
        Matcher dayNumMatcher = dayNumberPattern.matcher(text);
        Matcher yearMatcher = yearPattern.matcher(text);
        String day = "";
        String month = "";
        String dayNumber = "";
        int year = 0;

        if (justDayMatcher.find()) {
            day = justDayMatcher.group(0);
            day = day.trim();
        }

        if (monthMatcher.find()) {
          month = monthMatcher.group();
          month = month.trim();
        }

        if (dayNumMatcher.find()) {
            dayNumber = dayNumMatcher.group();
            dayNumber = dayNumber.trim();
        }

        if (yearMatcher.find()) {
            String years = yearMatcher.group();
            years = years.trim();
            year = Integer.parseInt(years);
        }

        LocalDate now = LocalDate.now();
        if (year == 0) year = now.getYear();
        String[] dayNum= dayNumber.split("rd|st|nd|th");

        if(text.toLowerCase().contains("next")) {
            nextDayAllocator(data, day, now);
        } else if (!month.isEmpty()) {
            data.setDate(LocalDate.of(year, monthToInteger(month), Integer.parseInt(dayNum[0])));
        } else {
            thisDayAllocator(data, day, now);
        }
    }

    /**
     * Converts a String provided (which should be a month) to its corresponding integer. Returns 0 as default if the String is not a month.
     * @param month
     * @return an integer corresponding to a month
     */
    private int monthToInteger(String month) {
        switch (month.toLowerCase()) {
            case "januray":
                return 1;
            case "feburary":
                return 2;
            case "march":
                return 3;
            case "april":
                return 4;
            case "may":
                return 5;
            case "june":
                return 6;
            case "july":
                return 7;
            case "august":
                return 8;
            case "september":
                return 9;
            case "october":
                return 10;
            case "november":
                return 11;
            case "december":
                return 12;
            default:
                return 0;
        }
    }

    /**
     * Uses LocalDate and DayOfWeek to set the date provided in the string as the correspondent DayOfWeek variable.
     * Does not set anything if the string does not match any day of the week.
     *
     * @param data
     * @param day
     * @param now
     */
    private void thisDayAllocator(Data data, String day, LocalDate now) {
        switch (day.toLowerCase()) {
            case "monday":
                data.setDate(now.with(nextOrSame(DayOfWeek.MONDAY)));
                break;
            case "tuesday":
                data.setDate(now.with(nextOrSame(DayOfWeek.TUESDAY)));
                break;
            case "wednesday":
                data.setDate(now.with(nextOrSame(DayOfWeek.WEDNESDAY)));
                break;
            case "thursday":
                data.setDate(now.with(nextOrSame(DayOfWeek.THURSDAY)));
                break;
            case "friday":
                data.setDate(now.with(nextOrSame(DayOfWeek.FRIDAY)));
                break;
            case "saturday":
                data.setDate(now.with(nextOrSame(DayOfWeek.SATURDAY)));
                break;
            case "sunday":
                data.setDate(now.with(nextOrSame(DayOfWeek.SUNDAY)));
                break;
            default:
                break;
        }
    }

    /**
     * Uses LocalDate and DayOfWeek to set the date provided in the string as the correspondent DayOfWeek variable for the next week.
     * Does not set anything if the string does not match any day of the week.
     *
     * @param data
     * @param day
     * @param now
     */
    private void nextDayAllocator(Data data, String day, LocalDate now) {
        switch (day.toLowerCase()) {
            case "monday":
                data.setDate(now.with(next(DayOfWeek.MONDAY)));
                break;
            case "tuesday":
                data.setDate(now.with(next(DayOfWeek.TUESDAY)));
                break;
            case "wednesday":
                data.setDate(now.with(next(DayOfWeek.WEDNESDAY)));
                break;
            case "thursday":
                data.setDate(now.with(next(DayOfWeek.THURSDAY)));
                break;
            case "friday":
                data.setDate(now.with(next(DayOfWeek.FRIDAY)));
                break;
            case "saturday":
                data.setDate(now.with(next(DayOfWeek.SATURDAY)));
                break;
            case "sunday":
                data.setDate(now.with(next(DayOfWeek.SUNDAY)));
                break;
            default:
                break;
        }
    }

    /**
     * Uses LocalTime to set the data to a time, depending on the format of the String provided.
     * For example, it checks whether the String has one of "am" or "pm", or if it's in "hh:mm" format.
     *
     * @param text
     * @param data
     */
    private void timeParser(String text, Data data) {
        if (text.toLowerCase().contains("am") || text.toLowerCase().contains("pm")) {
            String[] timeArray = text.split("(am|pm|\\s)");

            if (text.toLowerCase().contains("am")) {
                data.setTime(LocalTime.of(Integer.parseInt(timeArray[1]), 0));
            } else {
                int hour = Integer.parseInt(timeArray[1]) + 12;
                data.setTime(LocalTime.of(hour%24, 0));
            }
        } else {
            if (text.toLowerCase().contains("on") && text.length() > 3) text = text.substring(3);
            String[] timeArray = text.split(":");
            data.setTime(LocalTime.of((Integer.parseInt(timeArray[0])), Integer.parseInt(timeArray[1])));
        }
    }

    /**
     * Uses LocalTime to set the data to a time, if the String provided has "morning" or "evening" in it.
     * For the former, it sets the data to 09:00 time; for the latter, it sets the data to 20:00 time.
     *
     * @param text
     * @param data
     */
    private void timePhraseParser(String text, Data data) {
        if (text.toLowerCase().contains("morning")) {
            data.setTime(LocalTime.of(9, 0));
        } else if (text.toLowerCase().contains("evening")) {
            data.setTime(LocalTime.of(20, 0));
        }
    }

    /**
     * Checks whether the input text is a reminder by analysing it to see if it contains the string "remind me to".
     * It is case insensitive.
     * @return a boolean which is true if the text contains "remind me to". False otherwise.
     */
    private boolean reminderChecker() {
        int remindCheckLength = 11;
        if (text.length() > remindCheckLength && text.substring(0, remindCheckLength + 1).equalsIgnoreCase("remind me to")) {
            text = text.substring(remindCheckLength+1);
            return true;
        }
        return false;
    }

    /**
     * Sets the text as the String provided in the parameter.
     * @param string
     */
    public void setString(String string) {
        text = string;
        textProcessor();
    }

    /**
     * Request view to send the text entered in the text field using observer
     */
    public void requestMessage() {
        setChanged();
        notifyObservers("Request Message");
    }

    /**
     * Saves all the data collected from the inputs of the user (identified by userID) into a directory.
     * This way the events and reminders will still be displayed if the user closes the window and then logs in again.
     * @param userID
     */
    public void saveData(String userID) {
        String fileName = "";
        FileOutputStream fileOut;
        ObjectOutputStream out;
        try {
            for (int i = 0; i < calendarData.size(); i++) {
                fileName = "Data/Cal/" + userID + "/" + i + ".ser";
                Files.createDirectories(Paths.get("Data/Cal/" + userID));
                fileOut = new FileOutputStream(fileName);
                out = new ObjectOutputStream(fileOut);
                out.writeObject(calendarData.get(i));
            }

            for (int i = 0; i < reminderData.size(); i++) {
                fileName = "Data/Rem/" + userID + "/" + i + ".ser";
                Files.createDirectories(Paths.get("Data/Rem/" + userID));
                fileOut = new FileOutputStream(fileName);
                out = new ObjectOutputStream(fileOut);
                out.writeObject(reminderData.get(i));
            }
        }catch(IOException i) {
            i.printStackTrace();
        }
    }

    /**
     * Similar to the saveData() method, this method identifies the data file to be read based on the userID provided,
     * since there is directory for each user, where their Calendar data and Reminder data has been saved.
     * @param userID
     */
    public void loadData(String userID) {
        String fileName = "";
        FileInputStream fileIn = null;
        ObjectInputStream in = null;
        try {
            File folder = new File("Data/Cal/" + userID);
            File[] listOfFiles = folder.listFiles();

            int index = 0;
            if (listOfFiles != null) index = listOfFiles.length;

            for (int i = 0; i < index; i++) {
                fileName = "Data/Cal/" + userID + "/" + i + ".ser";
                fileIn = new FileInputStream(fileName);
                in = new ObjectInputStream(fileIn);
                Data data = (Data) in.readObject();
                calendarData.add(data);
            }

            folder = new File("Data/Rem/" + userID);
            listOfFiles = folder.listFiles();

            index = 0;
            if (listOfFiles != null) index = listOfFiles.length;

            for (int i = 0; i < index; i++) {
                fileName = "Data/Rem/" + userID + "/" + i + ".ser";
                fileIn = new FileInputStream(fileName);
                in = new ObjectInputStream(fileIn);
                Data data = (Data) in.readObject();
                reminderData.add(data);
            }
//            in.close();
//            fileIn.close();
        }catch(IOException i) {
            i.printStackTrace();
        }catch(ClassNotFoundException c) {
            c.printStackTrace();
        }

        initLoading();
    }

    /**
     * Copies the data to the calendar or reminder frame arrays in the view.
     */
    public void initLoading() {
        for (Data data : calendarData) {
            setChanged();
            notifyObservers(data);
        }

        for (Data data : reminderData) {
            setChanged();
            notifyObservers(data);
        }
    }
}
