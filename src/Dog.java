/*
 * Dog class: represents a single dog's record in the system.
 * Each Dog object holds all the required information about a daycare guest.
 */
public class Dog {
    // The dog's unique ID number (used for lookups and updates)
    private int id;
    // The dog's name
    private String name;
    // The breed of the dog
    private String breed;
    // Date of birth in YYYY-MM-DD format; used for validation and age calculation
    private String dob;
    // Integer code for food type: 0=no food, 1=dry, 2=wet, 3=customer provided
    private int food;
    // The dog's gender ("M" or "F")
    private String gender;
    // Spayed/Neutered status: "U" (unknown), "Y" (yes), "N" (no)
    private String spayedNeutered;
    // Whether the dog is currently checked in at the daycare
    private boolean checkedIn;

    /*
     * This constructor sets all fields at once.
     * We require all information to create a Dog record.
     */
    public Dog(int id, String name, String breed, String dob, int food, String gender, String spayedNeutered, boolean checkedIn) {
        this.id = id;
        this.name = name;
        this.breed = breed;
        this.dob = dob;
        this.food = food;
        this.gender = gender;
        this.spayedNeutered = spayedNeutered;
        this.checkedIn = checkedIn;
    }

    // Getters for every field, to allow other classes to read the dog's info
    public int getId() { return id; }
    public String getName() { return name; }
    public String getBreed() { return breed; }
    public String getDob() { return dob; }
    public int getFood() { return food; }
    public String getGender() { return gender; }
    public String getSpayedNeutered() { return spayedNeutered; }
    public boolean isCheckedIn() { return checkedIn; }

    // Setters for each field, so info can be changed if needed
    public void setName(String name) { this.name = name; }
    public void setBreed(String breed) { this.breed = breed; }
    public void setDob(String dob) { this.dob = dob; }
    public void setFood(int food) { this.food = food; }
    public void setGender(String gender) { this.gender = gender; }
    public void setSpayedNeutered(String spayedNeutered) { this.spayedNeutered = spayedNeutered; }
    public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }

    /*
     * Converts this dog's record to a readable string.
     * This is used every time we print a dog, to make sure the info is clear.
     */
    public String toString() {
        // Choose a readable word for food code
        String foodString = "";
        if (food == 0) {
            foodString = "No Food";
        } else if (food == 1) {
            foodString = "Dry";
        } else if (food == 2) {
            foodString = "Wet";
        } else if (food == 3) {
            foodString = "Customer Provided";
        } else {
            foodString = "Unknown";
        }
        // Show whether the dog is checked in or not
        String checkStatus = checkedIn ? "Checked In" : "Not Checked In";
        // Build the display string (used everywhere in the program)
        return "ID: " + id +
                " | Name: " + name +
                " | Breed: " + breed +
                " | DOB: " + dob +
                " | Food: " + foodString +
                " | Gender: " + gender +
                " | Spayed/Neutered: " + spayedNeutered +
                " | " + checkStatus;
    }
}
