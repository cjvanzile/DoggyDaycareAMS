import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DaycareUI extends JFrame.
 */
public class DaycareUI extends JFrame {
    private JPanel panel1;
    private JPanel mainTop;
    private JButton btnImport;
    private JButton btnQuit;
    private JPanel mainMiddle;
    private JPanel mainLeft;
    private JPanel mainRight;
    private JPanel scrollHeader;
    private JPanel dogInfoHeader;
    private JButton btnCheckIn;
    private JPanel dogInfo;
    private JTextField txtDogID;
    private JTextField txtName;
    private JTextField txtBreed;
    private JTextField txtDOB;
    private JComboBox cmbFood;
    private JRadioButton btnMale;
    private JRadioButton btnFemale;
    private JComboBox cmbSpayNeuter;
    private JButton btnRemove;
    private JButton btnClear;
    private JButton btnAdd;
    private JLabel lblDogID;
    private JLabel lblDogName;
    private JLabel lblBreed;
    private JLabel lblDob;
    private JLabel lblFood;
    private JLabel lblSpay;
    private JLabel lblGender;
    private JLabel lblListType;
    private JLabel lblDogNameHeader;
    private JTextField txtFileName;
    private JList dogList;
    private JButton btnAddNewDog;
    private JCheckBox chkShowAll;
    private JButton btnAttendanceReport;
    private JLabel txtStatus;

    // Connection String for database
    String dbURL;

    private final DogManager manager = new DogManager();

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    /**
     * Clears the form on the right side of the window.
     */
    public void clearForm(){
        chkShowAll.setSelected(false);
        lblDogNameHeader.setText(null);
        txtDogID.setText(null);
        txtDogID.setEditable(true);
        txtName.setText(null);
        txtBreed.setText(null);
        txtDOB.setText(null);
        cmbFood.setSelectedIndex(0);
        btnMale.setSelected(false);
        btnFemale.setSelected(false);
        cmbSpayNeuter.setSelectedIndex(0);
        btnRemove.setEnabled(false);
        btnCheckIn.setEnabled(false);
        txtStatus.setText(null);
        btnAdd.setText("Add");
        lblDogNameHeader.setText("(add dog)");
    }

    /**
     * Populates the form on the right side of the window with dog object.
     * @param dog The dog to display.
     */
    public void populateForm(Dog dog) {
        lblDogNameHeader.setText(dog.getName());
        txtDogID.setText(Integer.toString(dog.getId()));
        txtDogID.setEditable(false);
        txtName.setText(dog.getName());
        txtBreed.setText(dog.getBreed());
        txtDOB.setText(dog.getDob());
        cmbFood.setSelectedIndex(dog.getFood()+1);
        btnMale.setSelected(dog.getGender().equals("M"));
        btnFemale.setSelected(dog.getGender().equals("F"));
        // Converts first letter of spay/neuter status to integer
        cmbSpayNeuter.setSelectedIndex(dog.getSpayedNeutered().equals("U") ? 0 : dog.getSpayedNeutered().equals("Y") ? 1 : 2);
        // Enable remove and check-in buttons
        btnRemove.setEnabled(true);
        btnCheckIn.setEnabled(true);
        // Change check-in button text depending dog is checked in or not
        btnCheckIn.setText(dog.isCheckedIn() ? "Check Out" : "Check In");
        txtStatus.setText(dog.isCheckedIn() ? "(checked in)" : "(checked out)");
        btnAdd.setText("Update");
    }

    /**
     * Populate dog list on left side of window from database.
     * Uses chkShowAll to display either checked-in or all dogs.
     * @param conn This is the active database connection.
     * @throws SQLException Exception is handled by other methods.
     */
    public void populateDogList(Connection conn) throws SQLException {

        ArrayList<Dog> dogListTemp = new ArrayList<>();
        List<String> dogListArray = new ArrayList<>();
        String report = null;

        if (chkShowAll.isSelected()) {  // Create an array list of all dogs
            lblListType.setText("All Dogs");
        } else {  // Create an array list of the checked-in dogs (attendance list)
            lblListType.setText("Attendance Report");
            report = manager.generateAttendanceReport(conn);
        }

        // Get dog list from database, if checkShowAll is checked, include all dogs
        List<Dog> dogs = manager.getDogs(!chkShowAll.isSelected(),conn);

        for (Dog dog : dogs) {
            dogListTemp.add(dog);
        }

        dogListArray.add("<html>Checked in dogs are displayed in green<br><br></html");

        // Make display list colorful
        for (Dog dog: dogListTemp) {
            String dogDetails = "[" + dog.getId() + "] " + dog.getName() + " (" + dog.getBreed() + ") " + manager.foodTypes[dog.getFood()];
            if (dog.isCheckedIn()) {  // Display the line in green
                dogDetails = "<html><font color='#008800'>" + dogDetails + "</font></html>";
            } else {  // Display the line in red
                dogDetails = "<html><font color='#880000'>" + dogDetails + "</font></html>";
            }
            dogListArray.add(dogDetails);
        }

        if (!chkShowAll.isSelected()) {
            String[] reportParts = report.split("--");
            String summary = "<html><br>"
                    + reportParts[1] // Number of dogs checked-in
                    + "<br>"
                    + reportParts[3].replace("\n", "<br>") // Food preference counts
                    + "</html>";

            dogListArray.add(summary);
        }

        // Display list on left side of window
        dogList.setListData(dogListArray.toArray());
    }

    /**
     * Get database from user.
     * Ask user for database name and attempt a connection.
     * If a database does not exist, it will be created and the dog table will be added.
     * If database exists, we will just use it.
     */
    public void setupDatabase() {
        Boolean haveDB =  false; // Will be set true when database connection is successful
        do {
            String dbFilename = JOptionPane.showInputDialog("Enter Database Name (including full path).\n" +
                    "If database does not exist, it will be created.","dogs.db");

            if (dbFilename == null) { // User clicked cancel or 'X' in pop-up
                if( JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                    System.exit(0);
                continue;
            }

            if (dbFilename.equals("")) { // User did not enter file name
                JOptionPane.showMessageDialog(null, "Please enter a file name", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // Create Database connect string
            dbURL = "jdbc:sqlite:" + dbFilename;

            // Connect to Database
            try (Connection conn = DriverManager.getConnection(dbURL)) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(null, "Error creating database connection.", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                // Creates "dogs" table if it does not exist
                String sql = "CREATE TABLE IF NOT EXISTS dogs ("
                        + "id integer PRIMARY KEY,"
                        + "name text NOT NULL,"
                        + "breed text NOT NULL,"
                        + "dob text NOT NULL,"
                        + "food integer NOT NULL,"
                        + "gender text NOT NULL,"
                        + "spayedneutered text NOT NULL,"
                        + "checkedin boolean NOT NULL);";
                Statement stmt = conn.createStatement();
                stmt.execute(sql);

                // Close Database connection
                conn.close();

                JOptionPane.showMessageDialog(null, "Database connected.\n\n" +
                        "Use settings in upper left of main window to import dogs.\n\n");

                haveDB = true;
            } catch (SQLException e) { // Couldn't connect to Database
                JOptionPane.showMessageDialog(null, "Error creating database connection.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } while (!haveDB);
    }

    /**
     * Shows the main program window.
     * Sets up action listeners for all buttons in the window, and in the dog list.
     * @throws SQLException
     */
    public DaycareUI() throws SQLException {
        setTitle("Doggy Daycare Attendance Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel1);
        setSize(700, 600);
        setLocationRelativeTo(null);

        // Add food to combo box
        cmbFood.addItem(new ComboItem("(select one)", "-1"));
        int cnt = 0;
        for(String food : manager.foodTypes) {
            cmbFood.addItem(new ComboItem(food,Integer.toString(cnt++)));
        }

        // Add Spay/Neuter status to combo box
        cmbSpayNeuter.addItem(new ComboItem("Unknown", "U"));
        cmbSpayNeuter.addItem(new ComboItem("Yes", "Y"));
        cmbSpayNeuter.addItem(new ComboItem("No", "N"));

        setVisible(true);

        setupDatabase();

        // Connection "conn" will be passed into multiple methods
        Connection conn = DriverManager.getConnection(dbURL);

        populateDogList(conn);

        // Import button
        btnImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
                if (txtFileName.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter a file name", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    manager.loadFromFile(txtFileName.getText(), conn);

                    populateDogList(conn);

                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error loading file", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error loading file", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        // Add/Update button - this button changes based on context
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = 0;
                // Check to see if ID is an integer
                try {
                     id = Integer.parseInt(txtDogID.getText().trim());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Enter a valid dog ID", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Dog currentDog = null;
                try {
                    currentDog = manager.findDogById(id, conn);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Unable to add dog", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String name;
                if (!txtName.getText().trim().equals("")) {  // Dog name cannot be empty
                    name = txtName.getText().trim();
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a dog name", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String breed;
                if (!txtBreed.getText().trim().equals("")) {
                    breed = txtBreed.getText().trim();
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a dog breed", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Object foodObj = cmbFood.getSelectedItem();
                int food = Integer.parseInt(((ComboItem)foodObj).getValue());
                // If adding a dog, set checked-in to false, otherwise leave it unchanged
                boolean checkedIn = (btnAdd.getText().equals("Update")) ? currentDog.isCheckedIn() : Boolean.parseBoolean("false".trim());
                String dob = txtDOB.getText().trim();
                if (!manager.isValidDob(dob)) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid date of birth", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String gender = btnMale.isSelected() ? "M" : btnFemale.isSelected() ? "F" : "X";
                if (!manager.isValidGender(gender)) {
                    JOptionPane.showMessageDialog(null, "Please select a gender", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Use only first letter spay/neuter status
                String spayedNeutered = cmbSpayNeuter.getSelectedItem().toString().substring(0, 1);
                if (food < 0) {
                    JOptionPane.showMessageDialog(null, "Please select a food choice", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Create dog object from items above
                Dog dog = new Dog(id, name, breed, dob, food, gender, spayedNeutered, checkedIn);
                if (btnAdd.getText().equals("Add")) {
                    // Add new dog
                    try {
                        if (manager.addDog(dog, conn))
                            JOptionPane.showMessageDialog(null, "Dog added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        else {
                            JOptionPane.showMessageDialog(null, "Dog not added", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Unable to add dog", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    // Update existing dog
                    try {
                        if (manager.updateDog(id, dog, conn))
                            JOptionPane.showMessageDialog(null, "Dog updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        else {
                            JOptionPane.showMessageDialog(null, "Dog not updated", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Unable to update dog", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                populateForm(dog); // Update form on right with dog
                try {
                    populateDogList(conn);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Unable to populate dog list", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        // Add New Dog button
        btnAddNewDog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm(); // Clear out all fields for new dog
            }
        });

        // Check-in button
        btnCheckIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = 0;
                // Check to see if ID is an integer
                try {
                    id = Integer.parseInt(txtDogID.getText().trim());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Enter a valid dog ID", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Get dog info
                Dog updatedDog = null;
                try {
                    updatedDog = manager.findDogById(id, conn);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Unable to check dog in", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Invert dog check-in status
                updatedDog.setCheckedIn(!updatedDog.isCheckedIn());

                try {
                    if (manager.updateDog(id, updatedDog, conn)) {
                        // Change button text for check-out/in button
                        if (updatedDog.isCheckedIn()) {
                            btnCheckIn.setText("Check Out");
                        } else {
                            btnCheckIn.setText("Check In");
                        }
                        populateForm(updatedDog);
                    } else {
                        JOptionPane.showMessageDialog(null, "Unable to check dog in/out.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Unable to check dog in/out.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    populateDogList(conn);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Unable to check dog in/out.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        // Remove button
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = 0;
                // Check to see if ID is an integer
                try {
                    id = Integer.parseInt(txtDogID.getText().trim());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Enter a valid dog ID", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    Dog removedDog = manager.removeDog(id, conn);
                    if (removedDog != null) {
                        clearForm(); // Clear dog info from form
                        populateDogList(conn);
                        JOptionPane.showMessageDialog(null,  removedDog.getName()
                                + " (ID # " + removedDog.getId() + ") has been removed from the database");
                    } else {
                        JOptionPane.showMessageDialog(null, "Unable to remove dog", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Unable to remove dog", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        // Clear form button
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        // Select dog from list
        dogList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (dogList.getSelectedValue() != null) {
                    String dogInfo = dogList.getSelectedValue().toString();
                    try {
                        // Extract dogID from []
                        dogInfo = dogInfo.split("\\[")[1];
                        dogInfo = dogInfo.split("\\]")[0];
                        int id = Integer.parseInt(dogInfo);
                        Dog dog = manager.findDogById(id, conn);
                        populateForm(dog); // Add selected dog info to form
                    }
                    catch (Exception ex) {
                        // User didn't click on a line starting with an integer (i.e., a dog)
                    }
                }
            }
        });

        // Quit button
        btnQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if( JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });

        // Show all check-box
        chkShowAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Repopulate dogList based on current check box state
                    populateDogList(conn);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Unable to update dog list.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        btnAttendanceReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String report = null;
                try {
                    report = manager.generateAttendanceReport(conn);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Unable to display detailed attendance report.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(null, report, "Attendance Report", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    /**
     * This is the main method.
     * @param args Unused.
     * @throws SQLException Exceptions are handled respective methods.
     */
    public static void main(String[] args) throws SQLException {
        new DaycareUI();
    }
}
