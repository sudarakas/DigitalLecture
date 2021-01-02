package tech.sudarakas.digitallecture.entities;

public class User {

    public String email;

    //For null users
    public User(){

    }
    //For users with validation
    public  User(String email){
        this.email = email;
    }
}
