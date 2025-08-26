package com.example.sqliteproject;

//Model
//It is a database object representation
public class LoginInfo {

    int id;
    String username;
    String email;
    String password;

    private byte[] image;

    public LoginInfo(int id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }


    public LoginInfo(int id, String username, String email, String password, byte[] image) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


//    public LoginInfo(int id, String movie_name, String pythonMovie, String reactMovie, String mssqlMovie, String javaMovie, int year, int duration, float ratingBar, String gender, String male) { }

    @Override
    public String toString() {
        return "LoginInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}