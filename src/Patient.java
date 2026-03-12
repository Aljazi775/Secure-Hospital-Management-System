public class Patient {
    private int id;
    private String firstName;
    private String lastName;
    private String dob;
    private String gender;
    private String phone;
    private String address;


    // constructor
    public Patient(String fName, String lName, String dateOfBirth, String pGender, String pPhone, String pAddress) {
        this.firstName = fName;
        this.lastName = lName;
        this.dob = dateOfBirth;
        this.gender = pGender;
        this.phone = pPhone;
        this.address = pAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
