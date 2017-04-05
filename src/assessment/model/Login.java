package assessment.model;

import java.io.*;
import java.util.Observable;

/**
 * <h1>PPA assignment 14 </h1> <br>
 * Computer Science <br>
 * Year 1
 * <p>
 *
 * This class represents login system.
 *
 * @author Daniel Lopes De Castro (k1630458), Wonjoon Seol (k1631098),
 */
public class Login extends Observable{

    public static final String csvFile = "userData.csv";
    public static FileWriter writer;

    public Login() {
        try {
            writer = new FileWriter(csvFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new user with provided userId and password (if they did not exist before).
     * It uses a FileWriter to write character files.
     * @param userId
     * @param password
     */
    public static void makeNewUser(String userId, String password) {
        try {
            writer.append(userId + "," + password+"\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks the UserId to see if it already exists.
     * @param userId
     * @return true if found; false otherwise
     */
    public static boolean checkUserId(String userId) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            String line = "";
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] userData = line.split(",");
                if (userData[0].equals(userId)) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Similar to checkUserId(), but with the password.
     * @param password
     * @return true if found; false otherwise.
     */
    public static boolean checkPassword(String password) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            String line = "";
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] userData = line.split(",");
                if (userData[1].equals(password)) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Notifies the view that it changed, so that it can be updated.
     */
    public void update() {
        setChanged();
        notifyObservers("Login");
    }
}
