package vikram.findbyf;

/**
 * Created by vikram on 22/7/17.
 */

public class BusyDetail {
    public String BusyWithContactNo;
    public String BusyWithName;
    public String DateAndTime;

    public BusyDetail() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public BusyDetail(String BusyWithContactNo, String BusyWithName,String DateAndTime) {
        this.BusyWithContactNo = BusyWithContactNo;
        this.BusyWithName = BusyWithName;
        this.DateAndTime=DateAndTime;
    }
}
