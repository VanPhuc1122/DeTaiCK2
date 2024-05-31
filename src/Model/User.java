
package Model;
import Model.*;
public class User {
    private String username;
    private String Email;
    private String phonenumber;
    private String password;
    private String confimpassword;

    public User() {
    }
    public User(String username, String Email, String phonenumber, String password, String confimpassword) {
        this.username = username;
        this.Email = Email;
        this.phonenumber = phonenumber;
        this.password = password;
        this.confimpassword = confimpassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfimpassword() {
        return confimpassword;
    }

    public void setConfimpassword(String confimpassword) {
        this.confimpassword = confimpassword;
    }
    
}
