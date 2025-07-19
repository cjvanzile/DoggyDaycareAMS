import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * DogManager class: stores all dogs and provides core system features.
 * This class lets us add, find, update, remove, and report on dog records.
 */
public class DogManager {
    // List to hold all Dog objects in memory for the current session
    private ArrayList<Dog> dogs;

    /**
     * This holds the dogs that are currently checked in.
     */
    public ArrayList<Dog> dogsCheckedIn;

    // Info for attendance report
    int checkedInCount = 0;
    /**
     * This array counts the type of dog food for checked in dogs.
     */
    // Create food types Array
    String[] foodTypes = {"No Food", "Dry Food", "Wet Food", "Customer Provided"};
    int[] foodTotals = new int[foodTypes.length]; // Food totals based on food types above.

    StringBuilder checkedInList = new StringBuilder();

    /**
     * Constructor: starts with an empty list of all dogs and dogs checked in.
     * We always use the same DogManager throughout the program.
     */
    public DogManager() {
        dogs = new ArrayList<>();
        dogsCheckedIn = new ArrayList<>();
    }

    /*
     *
     * Returns true if successful; false if the ID was a duplicate.
     */

    /**
     * Adds a new dog record if the ID doesn't exist yet.
     * @param dog This is a dog object, containing all the dog's attributes.
     * @param conn This is the active database connection.
     * @return True if the dog is added, otherwise false.
     * @throws SQLException Exceptions are handled by returning false.
     */
    public boolean addDog(Dog dog, Connection conn) throws SQLException {
        if (findDogById(dog.getId(), conn) != null) {
            // Don't add if a dog with this ID already exists (to avoid duplicates)
            return false;
        }

        String sqlInsert = "INSERT INTO dogs (id, name, breed, dob, food, gender, spayedneutered, checkedin) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert);

        stmtInsert.setInt(1, dog.getId());
        stmtInsert.setString(2, dog.getName());
        stmtInsert.setString(3, dog.getBreed());
        stmtInsert.setString(4, dog.getDob());
        stmtInsert.setInt(5,  dog.getFood());
        stmtInsert.setString(6, dog.getGender());
        stmtInsert.setString(7, dog.getSpayedNeutered());
        stmtInsert.setBoolean(8, dog.isCheckedIn());

        try {
            stmtInsert.executeUpdate();
        }  catch (SQLException ex) {
            return false;
        }

        return  true;
    }

    /**
     * Get dogs from the database based on the checked in status passed in as a parameter.
     * @param checkedIn If true, return only checked in dogs, otherwise all dogs.
     * @param conn This is the active database connection.
     * @return Returns a list of dog objects.
     * @throws SQLException Exceptions are handled by returning null.
     */
    public List<Dog> getDogs(Boolean checkedIn, Connection conn) throws SQLException {
        List<Dog> allDogs = new ArrayList<>();

        String sqlSelect = "SELECT * FROM dogs" + (checkedIn ? " WHERE checkedin = true" : "")
                + " ORDER BY name ASC";
        Statement stmtSelect = conn.createStatement();
        try (ResultSet rs = stmtSelect.executeQuery(sqlSelect)) {
            while (rs.next()) {
                Dog dog = new Dog(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("breed"),
                        rs.getString("dob"),
                        rs.getInt("food"),
                        rs.getString("gender"),
                        rs.getString("spayedneutered"),
                        rs.getBoolean("checkedin")
                );
                allDogs.add(dog);
            }
        } catch (SQLException ex) {
            return null;
        }
        return allDogs;
    }

    /**
     * Removes a dog by their unique ID.
     * @param id The ID of the dog to be removed.
     * @param conn This is the active database connection.
     * @return Returns a dog object for the removed dog, or null if the dog was not found.
     * @throws SQLException Exceptions are handled by returning null.
     */
    public Dog removeDog(int id,  Connection conn) throws SQLException {
        Dog dog = findDogById(id, conn);
        if (dog == null) {
            // Don't add if a dog with this ID already exists (to avoid duplicates)
            return null;
        }

        String sqlDelete = "DELETE FROM dogs WHERE id = ?;";
        PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete);
        stmtDelete.setInt(1, id);

        try {
            stmtDelete.executeUpdate();
        }  catch (SQLException ex) {
            return null;
        }

        return dog;
    }

    /**
     * Updates the entire dog record for a specific ID.
     * @param id The ID of the dog to be updated.
     * @param updatedDog An object of type Dog of the dog to be updated.
     * @param conn This is the active database connection.
     * @return Returns true if the dog is updated, otherwise false.
     * @throws SQLException Exceptions are handled by returning false.
     */
    public boolean updateDog(int id, Dog updatedDog, Connection conn) throws SQLException {
        if (findDogById(id, conn) == null) {
            // Don't add if a dog with this ID already exists (to avoid duplicates)
            return false;
        }

        String sqlUpdate = "UPDATE dogs SET " +
                "name = ?, " +
                "breed = ?, " +
                "dob = ?, " +
                "food = ?, " +
                "gender = ?, " +
                "spayedneutered = ?, " +
                "checkedin = ? " +
                "WHERE id = ?;";

        PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);

        stmtUpdate.setString(1, updatedDog.getName());
        stmtUpdate.setString(2, updatedDog.getBreed());
        stmtUpdate.setString(3, updatedDog.getDob());
        stmtUpdate.setInt(4,  updatedDog.getFood());
        stmtUpdate.setString(5, updatedDog.getGender());
        stmtUpdate.setString(6, updatedDog.getSpayedNeutered());
        stmtUpdate.setBoolean(7, updatedDog.isCheckedIn());
        stmtUpdate.setInt(8, id);

        try {
            stmtUpdate.executeUpdate();
        }  catch (SQLException ex) {
            return false;
        }

        return  true;
    }

    /**
     * Finds a dog by a specific ID.
     * @param id The ID of the dog to be selected from the database.
     * @param conn This is the active database connection.
     * @return Returns the Dog object if found, or null if not found.
     * @throws SQLException Exceptions are handled by returning null.
     */
    public Dog findDogById(int id, Connection conn) throws SQLException {
        String sqlSelect = "SELECT * FROM dogs WHERE ID = ?";
        PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect);
        stmtSelect.setInt(1, id);
        try (ResultSet rs = stmtSelect.executeQuery()) {
            if (rs.next()) {
                Dog dog = new Dog(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("breed"),
                        rs.getString("dob"),
                        rs.getInt("food"),
                        rs.getString("gender"),
                        rs.getString("spayedneutered"),
                        rs.getBoolean("checkedin")
                );
                return dog;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            return null;
        }
    }

    /**
     * Loads dog records from a comma-separated text file.
     * Each line should have: id,name,breed,dob,food,gender,spayedNeutered,checkedIn
     * Adds only new records (no duplicates). Skips malformed lines.
     * @param filename The name of the file containing dog data.
     * @param conn This is the active database connection.
     * @throws IOException Errors in file are skipped.
     * @throws SQLException Handled in "addDog" method.
     */
    public void loadFromFile(String filename, Connection conn) throws IOException, SQLException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;

        int cntSuccess = 0;
        int cntFail = 0;

        while ((line = br.readLine()) != null) {
            try {
                // Try to parse each field; skip if anything is wrong
                String[] parts = line.split(",");
                if (parts.length != 8) {
                    cntFail++;
                    continue; // Not enough fields
                }

                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                String breed = parts[2].trim();
                String dob = parts[3].trim();
                int food = Integer.parseInt(parts[4].trim());
                String gender = parts[5].trim();
                String spayedNeutered = parts[6].trim();
                boolean checkedIn = Boolean.parseBoolean(parts[7].trim());

                Dog dog = new Dog(id, name, breed, dob, food, gender, spayedNeutered, checkedIn);

                if (addDog(dog, conn))
                    cntSuccess++;
                else
                    cntFail++;

            } catch (Exception e) {
                // Skip any line with problems (bad number format, etc.)
                cntFail++;
            }
        }
        br.close();
        JOptionPane.showMessageDialog(null, "Loaded " + cntSuccess + " dog(s) successfully!\n\n"
            + "failed to load " + cntFail + " dogs.");
    }

    /**
     * Custom action: creates a report of checked-in dogs, food types needed, and each checked-in dog's info in detail.
     * @param conn This is the active database connection.
     * @return Returns a formatted String of the attendance report.
     * @throws SQLException Handled in "getDogs" method.
     */
    public String generateAttendanceReport(Connection conn) throws SQLException {
        dogsCheckedIn = new ArrayList<>(); // Clear out checked-in list
        checkedInCount = 0;
        foodTotals = new int[foodTypes.length]; // Index: 0=no food, 1=dry, 2=wet, 3=customer provided
        checkedInList = new StringBuilder();

        List<Dog> dogs = getDogs(true, conn);

        // For every checked-in dog, add up food needs and collect info
        for (Dog dog : dogs) {
            checkedInCount++;
            int food = dog.getFood();
            if (food >= 0 && food < foodTypes.length) {
                foodTotals[food]++;
            }
            checkedInList.append(dog).append("\n"); // Use Dog's toString() to show all info
            dogsCheckedIn.add(dog); // Add dog to checked-in dogs
        }

        // Create a formatted summary to print or return
        String report = "";
        report += "Attendance Report:\n";
        report += "--\n";
        report += "Dogs currently checked in: " + checkedInCount + "\n";
        report += "--\n";
        if (checkedInCount == 0) {
            report += "No dogs are currently checked in.\n";
        } else {
            report += "Checked-In Dog Details:\n";
            report += checkedInList.toString();
        }
        report += "--\n";
        report += "Food Needed Today:\n";
        for (int i = 0; i < foodTypes.length; i++) {
            report += "  - " + foodTypes[i] + ": " + foodTotals[i] + "\n";
        }
        return report;
    }

    // ---- Input validation helper methods ----

    /**
     * Validates that the dog's birth date is in YYYY-MM-DD format and not in the future.
     * @param dob A string in the format of "YYYY-MM-DD".
     * @return Returns true if the date is valid and not in the future, otherwise false.
     */
    public static boolean isValidDob(String dob) {
        try {
            LocalDate date = LocalDate.parse(dob, DateTimeFormatter.ISO_LOCAL_DATE);
            if (date.isAfter(LocalDate.now())) {
                // Dog can't be born in the future!
                return false;
            }
            return true;
        } catch (DateTimeParseException e) {
            // If the format is wrong, return false
            return false;
        }
    }

    /**
     * Checks if gender is valid ("M" or "F").
     * @param gender A string in the format of "M" or "F".
     * @return Returns true if valid, otherwise false.
     */
    public static boolean isValidGender(String gender) {
        return gender.equalsIgnoreCase("M") || gender.equalsIgnoreCase("F");
    }

    /**
     * Checks if spayedNeutered field is valid ("U", "Y", or "N").
     * @param sn A string in the format of "U", "Y", or "N".
     * @return Returns true if valid, otherwise false.
     */
    public static boolean isValidSpayedNeutered(String sn) {
        return sn.equalsIgnoreCase("U") || sn.equalsIgnoreCase("Y") || sn.equalsIgnoreCase("N");
    }
}
