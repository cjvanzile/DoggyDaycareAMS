import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private final DogManager manager = new DogManager();

    // Create food types Array
    String[] foodTypes = {"No Food", "Dry Food", "Wet Food", "Customer Provided"};

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    // Clears out dog info form
    public void clearForm(){
        chkShowAll.setSelected(false);
        lblDogNameHeader.setText(null);
        txtDogID.setText(null);
        txtName.setText(null);
        txtBreed.setText(null);
        txtDOB.setText(null);
        cmbFood.setSelectedIndex(0);
        btnMale.setSelected(false);
        btnFemale.setSelected(false);
        cmbSpayNeuter.setSelectedIndex(0);
        btnRemove.setEnabled(false);
        btnCheckIn.setEnabled(false);
        btnAdd.setText("Add");
    }

    // Populates dog info form from dog object
    public void populateForm(Dog dog) {
        lblDogNameHeader.setText(dog.getName());
        txtDogID.setText(Integer.toString(dog.getId()));
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
        btnAdd.setText("Update");
    }

    // Populate list with checked-in dogs
    public void displayCheckedInDogs() {
        manager.generateAttendanceReport();
        populateDogList(manager.dogsCheckedIn);
    }

    // Populate dog list on left side of screen from dog array list object
    public void populateDogList(ArrayList<Dog> dogListArray){
        List<String> dogListArray1 = new ArrayList<String>();
        for (Dog dog: dogListArray) {
            String dogDetails = dog.getId() + ": " + dog.getName() + " (" + dog.getBreed() + ") " + foodTypes[dog.getFood()];
            // Only show check-in if showing all dogs in the list
            if (chkShowAll.isSelected() && dog.isCheckedIn()) {
                dogDetails += " (CHECKED IN)";
            }
            dogListArray1.add(dogDetails);
        }
        dogList.setListData(dogListArray1.toArray());
    }

    // Show the form
    public DaycareUI() {
        setTitle("Doggy Daycare Attendance Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel1);
        setSize(700, 600);
        setLocationRelativeTo(null);

        // Add food to combo box
        cmbFood.addItem(new ComboItem("(select one)", "-1"));
        int cnt = 0;
        for(String food : foodTypes) {
            cmbFood.addItem(new ComboItem(food,Integer.toString(cnt++)));
        }

        // Add Spay/Neuter status to combo box
        cmbSpayNeuter.addItem(new ComboItem("Unknown", "U"));
        cmbSpayNeuter.addItem(new ComboItem("Yes", "Y"));
        cmbSpayNeuter.addItem(new ComboItem("No", "N"));

        setVisible(true);

        // Import button
        btnImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> dogListArray = new ArrayList<String>();
                clearForm();
                if (txtFileName.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter a file name", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    int cnt = manager.loadFromFile(txtFileName.getText());
                    if (cnt > 0) {
                        displayCheckedInDogs();
                    } else {
                        JOptionPane.showMessageDialog(null, "No dogs were added", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "File import failed", "Error", JOptionPane.ERROR_MESSAGE);
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
                Dog currentDog = manager.findDogById(id);
                String name = txtName.getText().trim();
                String breed = txtBreed.getText().trim();
                Object foodObj = cmbFood.getSelectedItem();
                int food = Integer.parseInt(((ComboItem)foodObj).getValue());
                // If adding a dog, set checked-in to false, otherwise leave it unchanged
                boolean checkedIn = (btnAdd.getText().equals("Update")) ? currentDog.isCheckedIn() : Boolean.parseBoolean("false".trim());
                String dob = txtDOB.getText().trim();
                String gender = btnMale.isSelected() ? "M" : btnFemale.isSelected() ? "F" : "X";
                // Use only first letter spay/neuter status
                String spayedNeutered = cmbSpayNeuter.getSelectedItem().toString().substring(0, 1);
                if (!manager.isValidDob(dob)) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid date of birth", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!manager.isValidGender(gender)) {
                    JOptionPane.showMessageDialog(null, "Please select a gender", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (food < 0) {
                    JOptionPane.showMessageDialog(null, "Please select a food choice", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Create dog object from items above
                Dog dog = new Dog(id, name, breed, dob, food, gender, spayedNeutered, checkedIn);
                if (btnAdd.getText().equals("Add")) {
                    // Add new dog
                    if (manager.addDog(dog))
                        JOptionPane.showMessageDialog(null, "Dog added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(null, "Dog not added", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Update existing dog
                    if (manager.updateDog(id, dog))
                        JOptionPane.showMessageDialog(null, "Dog updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(null, "Dog not updated", "Error", JOptionPane.ERROR_MESSAGE);
                }
                populateForm(dog); // Update form on right with dog
                displayCheckedInDogs();
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
                Dog updatedDog = manager.findDogById(id);
                // Invert dog check-in status
                updatedDog.setCheckedIn(!updatedDog.isCheckedIn());
                chkShowAll.setSelected(false);

                if (manager.updateDog(id, updatedDog)) {
                    // Change button text for check-out/in button
                    if (updatedDog.isCheckedIn()) {
                        btnCheckIn.setText("Check Out");
                    } else {
                        btnCheckIn.setText("Check In");
                    }
                    displayCheckedInDogs();
                } else {
                    JOptionPane.showMessageDialog(null, "Dog Update Failed. Check all data.", "Error", JOptionPane.ERROR_MESSAGE);

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
                if (manager.removeDog(id)) {
                    clearForm(); // Clear dog info from form
                    displayCheckedInDogs();
                    JOptionPane.showMessageDialog(null, "Dog removed.");
                } else {
                    JOptionPane.showMessageDialog(null, "Unable to remove dog", "Error", JOptionPane.ERROR_MESSAGE);
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
        dogList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (dogList.getSelectedValue() != null) {
                    String dogInfo = dogList.getSelectedValue().toString();
                    int id = Integer.parseInt(dogInfo.split(":")[0]);
                    Dog dog = manager.findDogById(id);
                    populateForm(dog); // Add selected dog info to form
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
                // If show-all check box is selected, the list will show all dogs, otherwise just checked-in dogs
                if (chkShowAll.isSelected()) {
                    lblListType.setText("Show All");
                    List<Dog> dogs = manager.getAllDogs();
                    ArrayList<Dog> allDogs = new ArrayList<>();
                    for (Dog dog : dogs) {
                        allDogs.add(dog); // Add dog to checked-in dogs
                    }
                    populateDogList(allDogs);
                } else {
                    lblListType.setText("Checked In Dogs");
                    displayCheckedInDogs();
                }
            }
        });
    }

    public static void main(String[] args) {
        new DaycareUI();
    }
}
