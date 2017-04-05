package assessment.viewer;

import assessment.controller.Controller;
import assessment.model.Data;
import assessment.model.Model;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;

/**
 * <h1>PPA assignment 14 </h1> <br>
 * Computer Science <br>
 * Year 1
 * <p>
 * This class is JPanel viewer for calendar
 *
 * @author Daniel Lopes De Castro (k1630458), Wonjoon Seol (k1631098),
 */
public class CalendarPanel extends JPanel implements Observer {

    private JTabbedPane tabbedPane;
    private JTextField jtfMessage;
    private JList<String> jlCalendar;
    private JList<String> jlReminder;
    private String[] calendarMessage;
    private int jlCalIndex;
    private String[] reminderMessage;
    private int jlRemIndex;
    private Controller controller;
    private Model model;

    public CalendarPanel(Controller controller, Model model) {
        super();
        this.model = model;
        calendarMessage = new String[100];
        reminderMessage = new String[100];
        this.controller = controller;
        initWidgets();
    }

    private void initWidgets() {
        setLayout(new BorderLayout());
        jlCalendar = new JList<String>(calendarMessage);
        jlCalendar.setPreferredSize(new Dimension(600, 300));
        jlCalendar.addMouseListener(controller);
        jlCalendar.setName("Cal");

        jlReminder = new JList<String>(reminderMessage);
        jlReminder.addMouseListener(controller);
        jlReminder.setName("Rem");

        jtfMessage = new JTextField();
        jtfMessage.setFocusable(true);
        jtfMessage.addKeyListener(controller);

        tabbedPane = new JTabbedPane();

        add(jtfMessage, BorderLayout.PAGE_END);
        add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab("Calender", null, jlCalendar, "Calender");
        tabbedPane.addTab("Reminders", null, jlReminder, "Reminders");
    }

    public void sendCurrentString() {
        model.setString(jtfMessage.getText());
    }

    /**
     * Returns the correct suffix for the last digit (1st, 2nd, .. , 13th, .. , 23rd)
     */
    public static String makeDaySuffix(int number) {
        switch (number < 20 ? number : number % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    /**
     * Formats the message to be displayed in the reminder panel.
     * @param data
     * @return formatted message
     */
    private String generateRemindMessage(Data data) {
        String message = "";
        if (data.getEvent() != null) {
            if (data.getDate() != null || data.getLocation() != null || data.getTime() != null) {
                message += "Event: " + data.getEvent() + " | ";
            } else {
                message += data.getEvent();
            }
        }
        if (data.getDate() != null)
            if (data.getLocation() != null || data.getTime() != null) {
                message += formatDate(message, data) + " | ";
            } else {
                message += formatDate(message, data) + " ";
            }
        if (data.getTime() != null) {
            if (data.getLocation() != null) {
                message += "Time: " + data.getTime() + " | ";
            } else {
                message += "Time: " + data.getTime();
            }
        }

        if (data.getLocation() != null) message += "Location: " + data.getLocation();
        return message;
    }

    /**
     * Formats the message to be displayed in the calendar panel.
     * @param data
     * @return formatted message
     */
    private String generateCalendarMessage(Data data) {
        String message = "";
        if (data.getEvent() != null) {
            message += "Event: " + data.getEvent() + " | ";
        } else {
            message += "Event: - | ";
        }
        if (data.getDate() != null) {
            message += formatDate(message, data) + " | ";
        } else {
            message += "Date: - | ";
        }

        if (data.getTime() != null) {
            message += "Time: " + data.getTime() + " | ";
        } else {
            message += "Time: - | ";
        }

        if (data.getLocation() != null) {
            message += "Location: " + data.getLocation();
        } else {
            message += "Location: - ";
        }
        return message;
    }

    /**
     * Formats the message to be in a date format.
     * @param message
     * @param data
     * @return formatted message
     */
    private String formatDate(String message, Data data) {
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("YYYY");
        DateTimeFormatter yearOrMonthFormatter;
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd");
        String dataYear = data.getDate().format(yearFormatter);
        String currentYear = LocalDate.now().format(yearFormatter);
        if (dataYear.equals(currentYear)) {
            yearOrMonthFormatter = DateTimeFormatter.ofPattern("MMMM");
        } else {
            yearOrMonthFormatter = DateTimeFormatter.ofPattern("MMMM YYYY");
        }
        int day = Integer.parseInt(data.getDate().format(dayFormatter));
        message = "Date: " + day + makeDaySuffix(day) + " " + data.getDate().format(yearOrMonthFormatter);
        return message;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {

            String command = (String) arg;
            String[] commandPart = command.split(" ");

            if (command.equals("Request Message")) {
                sendCurrentString();
                jtfMessage.setText("");
            } else if (commandPart[0].equalsIgnoreCase("calendar")) {
                // When an element is deleted, shift all array elements after the deletion to left
                int index = Integer.parseInt(commandPart[1]);
                for (int i = index; i < calendarMessage.length - 1; i++) {
                    calendarMessage[i] = calendarMessage[i + 1];
                }
                calendarMessage[calendarMessage.length - 1] = null;
                jlCalIndex--;
                jlCalendar.setCellRenderer(new DefaultListCellRenderer());

            } else if (commandPart[0].equalsIgnoreCase("reminder")) {
                int index = Integer.parseInt(commandPart[1]);
                for (int i = index; i < reminderMessage.length - 1; i++) {
                    reminderMessage[i] = reminderMessage[i + 1];
                }
                reminderMessage[reminderMessage.length - 1] = null;
                jlRemIndex--;
            }

        } else if (arg instanceof Data) {
            Data data = ((Data) arg);

            if (data.getStatus().equals("Calendar data")) {
                calendarMessage[jlCalIndex] = generateCalendarMessage(data);
                jlCalIndex++;
                jlCalendar.setCellRenderer(new DefaultListCellRenderer());
            } else {
                reminderMessage[jlRemIndex] = generateRemindMessage(data);
                jlRemIndex++;
            }
        }
        jlCalendar.setCellRenderer(new DefaultListCellRenderer());
        jlReminder.setCellRenderer(new DefaultListCellRenderer());
    }
}


