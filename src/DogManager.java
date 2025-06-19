import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/*
 * DogManager class: stores all dogs and provides core system features.
 * This class lets us add, find, update, remove, and report on dog records.
 */
public class DogManager {
    // List to hold all Dog objects in memory for the current session
    private ArrayList<Dog> dogs;

    /*
     * Constructor: starts with an empty list of dogs.
     * We always use the same DogManager throughout the program.
     */
    public DogManager() {
        dogs = new ArrayList<>();
    }

    /*
     * Adds a new dog record if the ID doesn't exist yet.
     * Returns true if successful; false if the ID was a duplicate.
     */
    public boolean addDog(Dog dog) {
        if (findDogById(dog.getId()) != null) {
            // Don't add if a dog with this ID already exists (to avoid duplicates)
            return false;
        }
        dogs.add(dog);
        return true;
    }

    /*
     * Lets other classes see all the current dog records.
     * This is read-only; you can't change the list directly from outside.
     */
    public List<Dog> getAllDogs() {
        return Collections.unmodifiableList(dogs);
    }

    /*
     * Removes a dog by their unique ID.
     * Returns true if the dog was found and removed; false otherwise.
     */
    public boolean removeDog(int id) {
        Dog dog = findDogById(id);
        if (dog != null) {
            dogs.remove(dog);
            return true;
        }
        return false;
    }

    /*
     * Updates the entire dog record for a specific ID.
     * Returns true if successful, false if the ID was not found.
     */
    public boolean updateDog(int id, Dog updatedDog) {
        for (int i = 0; i < dogs.size(); i++) {
            if (dogs.get(i).getId() == id) {
                dogs.set(i, updatedDog); // Replace the old dog with the new info
                return true;
            }
        }
        return false;
    }

    /*
     * Searches for a dog by ID.
     * Returns the Dog object if found, or null if not found.
     */
    public Dog findDogById(int id) {
        for (Dog dog : dogs) {
            if (dog.getId() == id) {
                return dog;
            }
        }
        return null;
    }

    /*
     * Loads dog records from a comma-separated text file.
     * Each line should have: id,name,breed,dob,food,gender,spayedNeutered,checkedIn
     * Adds only new records (no duplicates). Skips malformed lines.
     * Returns the number of records successfully added.
     */
    public int loadFromFile(String filename) throws IOException {
        int addedCount = 0;
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            try {
                // Try to parse each field; skip if anything is wrong
                String[] parts = line.split(",");
                if (parts.length != 8) {
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
                // Only add if ID does not already exist in the system
                if (addDog(dog)) {
                    addedCount++;
                }
            } catch (Exception e) {
                // Skip any line with problems (bad number format, etc.)
            }
        }
        br.close();
        return addedCount;
    }

    /*
     * Custom action: creates a report of checked-in dogs and food types needed.
     * Now also lists each checked-in dog's info in detail.
     */
    public String generateAttendanceReport() {
        int checkedInCount = 0;
        int[] foodTotals = new int[4]; // Index: 0=no food, 1=dry, 2=wet, 3=customer provided
        StringBuilder checkedInList = new StringBuilder();

        // For every checked-in dog, add up food needs and collect info
        for (Dog dog : dogs) {
            if (dog.isCheckedIn()) {
                checkedInCount++;
                int food = dog.getFood();
                if (food >= 0 && food <= 3) {
                    foodTotals[food]++;
                }
                checkedInList.append(dog).append("\n"); // Use Dog's toString() to show all info
            }
        }

        // Create a formatted summary to print or return
        String report = "";
        report += "Attendance Report:\n";
        report += "Dogs currently checked in: " + checkedInCount + "\n";
        report += "-------------------------------------------\n";
        if (checkedInCount == 0) {
            report += "No dogs are currently checked in.\n";
        } else {
            report += "Checked-In Dog Details:\n";
            report += checkedInList.toString();
        }
        report += "-------------------------------------------\n";
        report += "Food Needed Today:\n";
        report += "  - No Food: " + foodTotals[0] + "\n";
        report += "  - Dry: " + foodTotals[1] + "\n";
        report += "  - Wet: " + foodTotals[2] + "\n";
        report += "  - Customer Provided: " + foodTotals[3] + "\n";
        return report;
    }

    // ---- Input validation helper methods ----

    /*
     * Validates that the dog's birth date is in YYYY-MM-DD format and not in the future.
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

    /*
     * Checks if gender is valid ("M" or "F").
     */
    public static boolean isValidGender(String gender) {
        return gender.equalsIgnoreCase("M") || gender.equalsIgnoreCase("F");
    }

    /*
     * Checks if spayedNeutered field is valid ("U", "Y", or "N").
     */
    public static boolean isValidSpayedNeutered(String sn) {
        return sn.equalsIgnoreCase("U") || sn.equalsIgnoreCase("Y") || sn.equalsIgnoreCase("N");
    }
}
