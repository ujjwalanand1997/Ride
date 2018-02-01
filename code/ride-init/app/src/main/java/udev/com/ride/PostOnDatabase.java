package udev.com.ride;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ujjwal on 8/14/2017.
 */

public class PostOnDatabase {

    String engage;
    String givelocation;
    String requested;
    String searched;
    String emailModdedUsername;
    String status;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference,userRefer,pinreference;

    public void setUsername(String email){
        emailModdedUsername = email.substring(0,email.indexOf('@'));
    }
    public String getUsername(){

        return emailModdedUsername;
    }

    public PostOnDatabase(){

        //referencing database references in the firebase realtime database
        reference = database.getReference("users");

        userRefer = database.getReference("userlist");
    }

    public void userlist(String value){
        userRefer.child(value).setValue(value);
    }

    public void setLocation(double latitude , double longitude) {
        reference.child(emailModdedUsername).child("Location").child("Latitude").setValue(latitude);
        reference.child(emailModdedUsername).child("Location").child("Longitude").setValue(longitude);
    }

    public void setStatus(String status) {
        this.status = status;
        reference.child(emailModdedUsername).child("Status").setValue(status);
    }

    public String getStatus() {
        return status;
    }

    public void setEngage(String engage) {
        if (!engage.equalsIgnoreCase("")) {
            this.engage = engage;
            reference.child(engage).child("Engage").setValue(emailModdedUsername);
        } else {
            this.engage = engage;
            reference.child(emailModdedUsername).child("Engage").setValue(engage);
        }
    }

    public void setGivelocation(String givelocation) {
        this.givelocation = givelocation;
        reference.child(emailModdedUsername).child("GiveLocation").setValue(givelocation);
    }

    public void setRequested(String requested) {
        if (!requested.equalsIgnoreCase("")) {
            this.requested = requested;
            reference.child(requested).child("Requested").setValue(emailModdedUsername);
        } else {
            this.requested = requested;
            reference.child(emailModdedUsername).child("Requested").setValue(requested);
        }
    }

    public void setSearched(String searched) {
        this.searched = searched;
        reference.child(emailModdedUsername).child("Searched").setValue(searched);
    }

    public double getLatitude() {

        return 1;
    }

    public double getLongitude() {

        return 1;
    }

    public String getEngage() {
        return engage;
    }

    public String getGivelocation() {
        return givelocation;
    }

    public String getRequested() {
        return requested;
    }

    public String getSearched() {
        return searched;
    }



}
