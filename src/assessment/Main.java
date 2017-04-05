package assessment;

import assessment.controller.Controller;
import assessment.model.Login;
import assessment.model.Model;
import assessment.viewer.CalendarPanel;
import assessment.viewer.LoginFrame;

/**
 * <h1>PPA assignment 14 </h1> <br>
 * Computer Science <br>
 * Year 1
 * <p>
 * This class is a driver class
 *
 * @author Daniel Lopes De Castro (k1630458), Wonjoon Seol (k1631098),
 */
public class Main {

    public static void main(String[] args) {

        Model model = new Model();
        Login login = new Login();
        Controller controller = new Controller(login, model);
        CalendarPanel calendarPanel = new CalendarPanel(controller, model);
        LoginFrame loginFrame = new LoginFrame(controller, calendarPanel, model);
        login.addObserver(loginFrame);
        model.addObserver(calendarPanel);
        loginFrame.setVisible(true);
    }
}
