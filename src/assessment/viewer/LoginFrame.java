package assessment.viewer;

import assessment.controller.Controller;
import assessment.model.Login;
import assessment.model.Model;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * <h1>PPA assignment 14 </h1> <br>
 * Computer Science <br>
 * Year 1
 * <p>
 * This class is a Main JFrame
 *
 * @author Daniel Lopes De Castro (k1630458), Wonjoon Seol (k1631098),
 */
public class LoginFrame extends JFrame implements Observer {

    private JPanel cardPanel;
    private Controller controller;
    private JLabel jlMessage;
    private JTextField jtfUserID;
    private JPasswordField jtfPassword;
    private JPanel calendarPanel;
    private Model model;

    public LoginFrame(Controller controller, CalendarPanel calendarPanel, Model model) {
        this.controller = controller;
        this.calendarPanel = calendarPanel;
        this.model = model;
        initLogInPanel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(600, 300));
        setLocationRelativeTo(null);
        pack();
    }

    private void initLogInPanel() {
        cardPanel = new JPanel(new CardLayout());
        JPanel logInPanel = new JPanel(new BorderLayout());
        String string = "<html><div Style='text-align: center;'><b><br><br><br>Welcome to CW14: Calendar app.</b><br><br> Please Log in. <br>If the supplied User ID is not on the database, new one will be created with the supplied password<br><br><br></div></html>";
        jlMessage = new JLabel(string, SwingConstants.CENTER);
        JPanel fieldPanel = new JPanel(new FlowLayout());
        jtfUserID = new JTextField("User ID");

        jtfPassword = new JPasswordField("User Password");
        jtfPassword.setPreferredSize(new Dimension(200,20));
        jtfUserID.setPreferredSize(new Dimension(150,20));
        JButton jOk = new JButton("OK");
        jOk.setName("Login");
        jOk.addActionListener(controller);

        fieldPanel.add(jtfUserID);
        fieldPanel.add(jtfPassword);
        fieldPanel.add(jOk);

        logInPanel.add(jlMessage, BorderLayout.PAGE_START);
        logInPanel.add(fieldPanel, BorderLayout.CENTER);

        cardPanel.add(logInPanel);
        cardPanel.add(calendarPanel, "Card main");
        add(cardPanel);
    }

    /**
     * Uses methods from Login class to check if a user exists or not, and if not it creates a new one.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String command = (String) arg;
            char[] pass = jtfPassword.getPassword();
            String passString = new String(pass);
            if (command.equals("Login")) {
                if (Login.checkUserId(jtfUserID.getText())) {
                    if (Login.checkPassword((passString))) {
                        model.setUserID(jtfUserID.getText());
                        model.loadData(jtfUserID.getText());
                        CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
                        cardLayout.show(cardPanel, "Card main");
                    } else {
                        jlMessage.setText("<html><div Style='text-align: center;'><b><br><br><br>Welcome to CW14: Calendar app.</b><br><br><font color=\"red\">Supplied Password is Incorrect.</font><br>If the supplied User ID is not on the database, new one will be created with the supplied password<br><br><br></div></html>");
                    }
                } else {
                    Login.makeNewUser(jtfUserID.getText(), passString);
                    CardLayout cardLayout = (CardLayout) cardPanel.getLayout();
                    cardLayout.show(cardPanel, "Card main");
                    model.setUserID(jtfUserID.getText());
                }
            }
        }
    }
}
