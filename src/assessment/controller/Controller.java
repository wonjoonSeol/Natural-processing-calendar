package assessment.controller;

import assessment.model.Login;
import assessment.model.Model;

import javax.swing.*;
import java.awt.event.*;

/**
 * <h1>PPA assignment 14 </h1> <br>
 * Computer Science <br>
 * Year 1
 * <p>
 * This class act as a controller between viewer and calendar processor
 *
 * @author Daniel Lopes De Castro (k1630458), Wonjoon Seol (k1631098),
 */
public class Controller implements KeyListener, MouseListener, ActionListener {
    private Model model;
    private Login login;

    public Controller(Login login, Model model) {
        this.login = login;
        this.model = model;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    /**
     * Checks when the user has pressed the Enter key on the keyboard (represented by KeyEvent.VK_ENTER). When the key is released,
     * the requestMessage() method in the model is run so that the text in the text field is moved to the text area in the format
     * desired.
     *
     * @param e the event of the key being released
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ENTER) {
            model.requestMessage();
        }

    }

    /**
     * Checks when the user clicked on a reminder or an event in the text area in order to remove it.
     * Events and reminders are kept on list, so when the user double clicks (as seen by e.getClickCount() == 2),
     * then the event or reminder is removed from their respective list and thus the text area in the view.
     *
     * @param e the event of the mouse being clicked
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        JList<String> list = (JList<String>)e.getSource();
        if (e.getClickCount() == 2) {
            if (list.getName().equals("Cal")) {
                model.removeCalendarData(list.locationToIndex(e.getPoint()));
            } else {
                model.removeReminderData(list.locationToIndex(e.getPoint()));
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        if (button.getName().equals("Login")) {
            login.update();
        }
    }
}
