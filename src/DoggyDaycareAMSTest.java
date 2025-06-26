import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class DoggyDaycareAMSTest {

    //create an object to be tested

    String filename;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {

    }

    @org.junit.jupiter.api.Test
    @DisplayName("Load File Tests")
    void loadFileTests() {
        DoggyDaycareAMS app = new DoggyDaycareAMS();

        // Test valid file name
        assertTrue(app.loadFile("dogs.txt"),"Couldn't open legitimate file."); // File exists
        assertFalse(app.loadFile("bad.txt"),"Did not fail on non-existent file."); // File does not exist
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Test Add Dog")
    void addDogTest() {
        DogManager manager = new DogManager();

        // This is the test dog
        Dog dog = new Dog(1, "Buddy", "Boxer", "2023-05-04", 1, "M", "N", true);

        // Test adding a dog
        assertTrue(manager.addDog(dog), "Couldn't add dog");

        // Test adding same dog again
        assertFalse(manager.addDog(dog),"Adding duplicate dog allowed, should have failed.");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Get All Dogs")
    void getAllDogsTest() {
        DogManager manager = new DogManager();

        // This is the test dog
        Dog dog = new Dog(1, "Buddy", "Boxer", "2023-05-04", 1, "M", "N", true);

        // Test to make sure we can get an empty dog list
        assertTrue(manager.getAllDogs().isEmpty(), "Couldn't get empty list.");

        // Add a dog
        manager.addDog(dog);

        // Test Get All Dogs
        assertFalse(manager.getAllDogs().isEmpty(), " Get all dogs failed.");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Test Removing a Dog")
    void removeDogTest() {
        DogManager manager = new DogManager();

        // This is the test dog
        Dog dog = new Dog(1, "Buddy", "Boxer", "2023-05-04", 1, "M", "N", true);

        // Add a dog
        manager.addDog(dog);

        // Attempt to remove non-existent dog
        assertFalse(manager.removeDog(2), "Did not fail when removing non-existent dog.");

        // Attempt to remove existing dog
        assertTrue(manager.removeDog(1), "Did not remove existing dog.");

        // Make sure dog was deleted
        assertTrue(manager.getAllDogs().isEmpty(), "Remove dog did not remove the dog from the list.");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Test Finding Dog by ID")
    void FindDogByIDTest() {
        DogManager manager = new DogManager();

        // This is the test dog
        Dog dog = new Dog(1, "Buddy", "Boxer", "2023-05-04", 1, "M", "N", true);

        // Add a dog
        manager.addDog(dog);

        // Make sure we can get the dog by ID
        assertNotNull(manager.findDogById(1),"Couldn't find dog by ID.");

        // Make sure we don't get non-existent dog
        assertNull(manager.findDogById(2),"Getting non-existent dog did not fail.");

        // Test to ensure retrieved dog is correct
        // Get the dog
        Dog theDog = manager.findDogById(1);

        // Ensure the dog was retrieved
        assertEquals(dog, theDog, "Get Dog by ID failed.");

    }

    @org.junit.jupiter.api.Test
    @DisplayName("Test Update Dog")
    void updateDogTest() {
        DogManager manager = new DogManager();

        // This is the test dog
        Dog dog = new Dog(1, "Buddy", "Boxer", "2023-05-04", 1, "M", "N", true);

        // This is the updated test dog
        Dog updatedDog = new Dog(1, "Buddie", "Poodle", "2023-05-05", 0, "F", "Y", false);

        // Add a dog
        manager.addDog(dog);

        // Test updating the dog
        assertTrue(manager.updateDog(1, updatedDog), "Update dog failed.");

        // Test updating non-existent dog
        assertFalse(manager.updateDog(2, updatedDog), "Updating non-existent dog did not fail.");

        // Get the updated dog
        Dog theDog = manager.findDogById(1);

        // Ensure the dog was updated
        assertEquals(updatedDog, theDog, "Update dog failed to update the object.");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Test DOB")
    void isValidDobTest() {
        DogManager manager = new DogManager();

        // Test for a valid DOB
        assertTrue(manager.isValidDob("2023-05-04"), "DOB failed for valid date.");

        // Test for invalid DOB
        assertFalse(manager.isValidDob("2023-13-04"), "Bad DOB did not fail for invalid date.");
        assertFalse(manager.isValidDob("2023-13-0A"), "Bad DOB did not fail for Non-numeric date.");
        assertFalse(manager.isValidDob("3023-12-04"), "Bad DOB did not fail for future date.");
        assertFalse(manager.isValidDob("1/27/2024"), "Bad DOB did not fail for invalid format.");

    }

    @org.junit.jupiter.api.Test
    @DisplayName("Test Gender")
    void isValidGenderTest() {
        DogManager manager = new DogManager();

        // Test for valid Gender (M/F)
        assertTrue(manager.isValidGender("M"), "Gender failed for M.");
        assertTrue(manager.isValidGender("F"), "Gender failed for F.");

        // Test for invalid Gender
        assertFalse(manager.isValidGender("K"), "Gender failed for invalid gender.");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Test Spay/Neuter")
    void isValidSpayNeuterTest() {
        DogManager manager = new DogManager();

        // Test for valid Spay/Neuter/Unknown (Y/N/U)
        assertTrue(manager.isValidSpayedNeutered("Y"),"Spay/Neuter failed for Y.");
        assertTrue(manager.isValidSpayedNeutered("N"),"Spay/Neuter failed for N.");
        assertTrue(manager.isValidSpayedNeutered("U"),"Spay/Neuter failed for U.");

        // Test for invalid Spay/Neuter
        assertFalse(manager.isValidSpayedNeutered("S"),"Spay/Neuter failed for S.");
    }

    @org.junit.jupiter.api.Test
    @DisplayName("Test Attendance Report")
    void generateAttendanceReportTest() {
        DogManager manager = new DogManager();

        // Test to ensure Attendance Report is generated
        // If attendance report fails, an empty string is returned
        assertNotEquals("", manager.generateAttendanceReport(), "Attendance Report failed.");
    }
}