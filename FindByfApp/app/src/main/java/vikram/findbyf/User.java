package vikram.findbyf;

/**
 * Created by vikram on 22/7/17.
 */

public class User {
    public String email;
    public String fullname;
    public String myContactNo;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email,String contactNo) {
        this.fullname = name;
        this.email = email;
        this.myContactNo=contactNo;
    }

}