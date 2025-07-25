// Clay VanZile, CEN 3024C-31774, 06/18/25
// Software Development I
// Module 7 | DMS Project Phase 1: Logic and Input Validation

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;
import java.io.File;

/**
 * This is the main application class for the CLI version of the program.
 * This is the entry point of the program and handles all menu logic and user interaction.
 */
public class DoggyDaycareAMS {
    // DogManager object handles all data storage and CRUD operations
    private final DogManager manager = new DogManager();
    // Scanner is used to read user input from the command line
    private final Scanner scanner = new Scanner(System.in);

    /**
     * This is the main method CLI version.
     * @param args Unused
     * @throws SQLException Exceptions handled by respective methods.
     */
    public static void main(String[] args) throws SQLException {
       DoggyDaycareAMS app = new DoggyDaycareAMS();
       Connection conn = null;
        app.run(conn);
    }

    /**
     * This is the main loop for the program.
     * It displays the menu and keeps the program running until the user chooses to exit.
     * @param conn This is the active database connection.
     * @throws SQLException Exceptions handled by respective methods.
     */
    public void run(Connection conn) throws SQLException {
        System.out.println("Welcome to Doggy Daycare Attendance Management System!");
        boolean running = true; // Controls whether the menu keeps looping
        while (running) {
            printMenu(); // Show the user all possible options
            int choice = getIntInput("Enter menu option: "); // Get user choice, with validation
            // Now check which menu option was chosen and call the right method
            if (choice == 1) {
                // User wants to load data from a file
                handleLoadFile();
            } else if (choice == 2) {
                // User wants to display all dogs currently in the system
                handleDisplayDogs(conn);
            } else if (choice == 3) {
                // User wants to add a new dog record
                handleAddDog(conn);
            } else if (choice == 4) {
                // User wants to update an existing dog's information
                handleUpdateDog(conn);
            } else if (choice == 5) {
                // User wants to remove a dog by ID
                handleRemoveDog(conn);
            } else if (choice == 6) {
                // User wants to see the custom attendance report
                handleAttendanceReport(conn);
            } else if (choice == 7) {
                // User wants to exit the program
                System.out.println("Exiting program. Goodbye!");
                running = false;
            } else {
                // User typed something not in 1-7
                System.out.println("Invalid menu option. Please enter a number 1-7.");
            }
        }
    }

    /**
     * Prints the main menu options to the console.
     * Called at the start of every loop so the user always knows their choices.
     */
    private void printMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Load data from file");
        System.out.println("2. Display all dogs");
        System.out.println("3. Add new dog");
        System.out.println("4. Update dog info");
        System.out.println("5. Remove dog");
        System.out.println("6. Generate attendance report");
        System.out.println("7. Exit");
        System.out.println("------------------");
    }

    /**
     * Handles loading dog data from a text file.
     * Explains the result to the user.
     */
    private void handleLoadFile() {
        System.out.print("Enter filename to load (ex: data.txt): ");
        String filename = scanner.nextLine().trim();
        if (!loadFile(filename)) {
            System.out.println("File not found or not readable.");
        }
    }

    /**
     * Checks to see if the file exists.
     * @param filename The file name.
     * @return Returns true if the file exists, otherwise false.
     */
    public boolean loadFile (String filename) {
        File file = new File(filename);
        // Check that file actually exists before trying to read
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        return false;
    }

    /**
     * Prints all dogs currently in the system.
     * This helps the user verify records after any change.
     * @param conn This is the active database connection.
     * @throws SQLException Exceptions handled by respective methods.
     */
    private void handleDisplayDogs(Connection conn) throws SQLException {
        List<Dog> dogs = manager.getDogs(false, conn);
        if (dogs.isEmpty()) {
            System.out.println("No dogs in the system.");
        } else {
            System.out.println("---- Dog List ----");
            // Print each dog's details
            for (Dog dog : dogs) {
                System.out.println(dog);
            }
        }
    }

    /**
     * Allows the user to add a new dog with step-by-step input validation.
     * Each field is validated so the user can't crash the program.
     * @param conn This is the active database connection.
     * @throws SQLException Exceptions handled by respective methods.
     */
    private void handleAddDog(Connection conn) throws SQLException {
        System.out.println("Enter new dog information:");

        // Loop until the user enters a unique integer for ID
        int id;
        while (true) {
            id = getIntInput("ID (integer, unique): ");

            if (manager.findDogById(id, conn) != null) {
                System.out.println("ID already exists. Please enter a different ID.");
            } else {
                break;
            }
        }

        // For each other field, use helper functions to make sure input is valid
        String name = getNonEmptyString("Name: ");
        String breed = getNonEmptyString("Breed: ");
        String dob;
        while (true) {
            dob = getNonEmptyString("DOB (YYYY-MM-DD): ");
            if (!DogManager.isValidDob(dob)) {
                System.out.println("Invalid DOB. Please use YYYY-MM-DD and make sure it's not in the future.");
            } else {
                break;
            }
        }

        // Get food type from user.
        int food;
        while (true) {
            food = getIntInput("Food type (0=no food, 1=dry, 2=wet, 3=customer provided): ");
            if (food < 0 || food > 3) {
                System.out.println("Invalid food type. Please enter 0, 1, 2, or 3.");
            } else {
                break;
            }
        }

        // Get gender from user.
        String gender;
        while (true) {
            gender = getNonEmptyString("Gender (M/F): ").toUpperCase();
            if (!DogManager.isValidGender(gender)) {
                System.out.println("Gender must be M or F.");
            } else {
                break;
            }
        }

        // Get spayed/neutered from user.
        String spayedNeutered;
        while (true) {
            spayedNeutered = getNonEmptyString("Spayed/Neutered (U=unknown, Y=yes, N=no): ").toUpperCase();
            if (!DogManager.isValidSpayedNeutered(spayedNeutered)) {
                System.out.println("Enter U, Y, or N.");
            } else {
                break;
            }
        }

        // For checked-in status, prompt for true/false
        boolean checkedIn = getBooleanInput("Checked in? (true/false): ");

        // Now create the Dog object and add it to the system
        Dog newDog = new Dog(id, name, breed, dob, food, gender, spayedNeutered, checkedIn);
        if (manager.addDog(newDog, conn)) {
            System.out.println("Dog added successfully.");
        } else {
            System.out.println("Failed to add dog.");
        }

        // After adding, print all dogs to show the update
        handleDisplayDogs(conn);
    }

    /**
     * Lets the user update any field of an existing dog.
     * For each field, user can press Enter to keep the old value.
     * @param conn This is the active database connection.
     * @throws SQLException Exceptions handled by respective methods.
     */
    private void handleUpdateDog(Connection conn) throws SQLException {
        // Get dog ID.
        int id = getIntInput("Enter ID of dog to update: ");
        Dog dog = manager.findDogById(id, conn);
        if (dog == null) {
            System.out.println("Dog with that ID does not exist.");
            return;
        }
        System.out.println("Enter new info (press enter to keep current value):");

        // For each field, if the user leaves it blank, keep the old value
        String name = getUpdateString("Name", dog.getName());
        String breed = getUpdateString("Breed", dog.getBreed());

        // Get date of birth.
        String dob;
        while (true) {
            String input = getUpdateString("DOB (YYYY-MM-DD)", dog.getDob());
            if (input.equals(dog.getDob()) || DogManager.isValidDob(input)) {
                dob = input;
                break;
            } else {
                System.out.println("Invalid DOB.");
            }
        }

        // Get food type.
        int food;
        while (true) {
            String input = getUpdateString("Food type (0=no food, 1=dry, 2=wet, 3=customer provided)", Integer.toString(dog.getFood()));
            try {
                int foodVal = Integer.parseInt(input);
                if (foodVal >= 0 && foodVal <= 3) {
                    food = foodVal;
                    break;
                } else {
                    System.out.println("Invalid food type.");
                }
            } catch (NumberFormatException e) {
                if (input.equals(Integer.toString(dog.getFood()))) {
                    food = dog.getFood();
                    break;
                } else {
                    System.out.println("Invalid input.");
                }
            }
        }

        // Get gender.
        String gender;
        while (true) {
            String input = getUpdateString("Gender (M/F)", dog.getGender()).toUpperCase();
            if (DogManager.isValidGender(input)) {
                gender = input;
                break;
            } else if (input.equalsIgnoreCase(dog.getGender())) {
                gender = dog.getGender();
                break;
            } else {
                System.out.println("Gender must be M or F.");
            }
        }

        // Get spayed/neutered status.
        String spayedNeutered;
        while (true) {
            String input = getUpdateString("Spayed/Neutered (U=unknown, Y=yes, N=no)", dog.getSpayedNeutered()).toUpperCase();
            if (DogManager.isValidSpayedNeutered(input)) {
                spayedNeutered = input;
                break;
            } else if (input.equalsIgnoreCase(dog.getSpayedNeutered())) {
                spayedNeutered = dog.getSpayedNeutered();
                break;
            } else {
                System.out.println("Enter U, Y, or N.");
            }
        }

        // Get check in status.
        boolean checkedIn = getBooleanInput("Checked in? (true/false): ", dog.isCheckedIn());

        // Make a new Dog object with updated info, and update it in the manager
        Dog updatedDog = new Dog(id, name, breed, dob, food, gender, spayedNeutered, checkedIn);
        if (manager.updateDog(id, updatedDog, conn)) {
            System.out.println("Dog updated.");
        } else {
            System.out.println("Failed to update dog.");
        }

        // Show all dogs to prove the update worked
        handleDisplayDogs(conn);
    }

    /**
     * Lets the user remove a dog from the system by ID.
     * If the dog is not found, informs the user.
     * @param conn This is the active database connection.
     * @throws SQLException Exceptions handled by respective methods.
     */
    private void handleRemoveDog(Connection conn) throws SQLException {
        int id = getIntInput("Enter ID of dog to remove: ");
        if (manager.removeDog(id, conn) != null) {
            System.out.println("Dog removed.");
        } else {
            System.out.println("No dog found with that ID.");
        }
        // Always show updated list afterwards
        handleDisplayDogs(conn);
    }

    /**
     * Calls the custom action: show an attendance and food report.
     * This is not a CRUD operation, but it gives business insight.
     * @param conn This is the active database connection.
     * @throws SQLException Exceptions handled by respective methods.
     */
    private void handleAttendanceReport(Connection conn) throws SQLException {
        String report = manager.generateAttendanceReport(conn);
        System.out.println(report);
    }

    // ----- Helper input methods -----

    /**
     * Repeatedly asks the user for input until they enter a non-empty string.
     * This prevents blank fields.
     * @param prompt The string asking the user what to input.
     * @return The users non-empty response.
     */
    private String getNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("This field cannot be blank.");
        }
    }

    /**
     * Asks the user for an integer until a valid integer is typed.
     * @param prompt The string asking the user what to input.
     * @return The users non-empty response.
     */
    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    /**
     * Asks the user for true/false until the input is understood.
     * @param prompt The string asking the user what to input.
     * @return The users Boolean response.
     */
    private boolean getBooleanInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true") || input.equals("t") || input.equals("yes") || input.equals("y")) {
                return true;
            } else if (input.equals("false") || input.equals("f") || input.equals("no") || input.equals("n")) {
                return false;
            } else {
                System.out.println("Please enter true or false.");
            }
        }
    }

    /**
     * Used for updating Boolean values. If the user presses enter, keeps the current value.
     * @param prompt The string asking the user what to input.
     * @param currentValue This is the current Boolean value.
     * @return The current value (if unchanged) or the new value.
     */
    private boolean getBooleanInput(String prompt, boolean currentValue) {
        System.out.print(prompt + " (current: " + currentValue + "): ");
        String input = scanner.nextLine().trim().toLowerCase();
        if (input.isEmpty()) {
            return currentValue;
        } else if (input.equals("true") || input.equals("t") || input.equals("yes") || input.equals("y")) {
            return true;
        } else if (input.equals("false") || input.equals("f") || input.equals("no") || input.equals("n")) {
            return false;
        } else {
            System.out.println("Invalid input, using current value.");
            return currentValue;
        }
    }

    /**
     * Used for updating Boolean values. If the user presses enter, keeps the current value.
     * @param prompt The string asking the user what to input.
     * @param currentValue This is the current string value.
     * @return The current value (if unchanged) or the new value.
     */
    private String getUpdateString(String prompt, String currentValue) {
        System.out.print(prompt + " (current: " + currentValue + "): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return currentValue;
        }
        return input;
    }
}
