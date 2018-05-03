package chatstackserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    protected Connection con;
    protected ResultSet s = null;
    protected Statement stmt;

    public Database() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("\u001B[34m"+"initialize connection to database"+"\u001B[0m");
            con = DriverManager.getConnection("jdbc:mysql://db4free.net/stackusers", "stackchat", "12345678");
            stmt = con.createStatement();
            System.out.println("\u001B[32m"+"Connected to database successfully"+"\u001B[0m");

        } catch (Exception e) {
            System.err.println("Error in database Connection");
        }

    }


    public void addUser(String Username, String Password, String Email) throws SQLException {

        if (checkUsername(Username) && checkEmail(Email)) {
            con.prepareStatement("INSERT INTO `Users` (`id`, `username`, `password`, `email`) VALUES (NULL, '" + Username + "', '" + Password + "', '" + Email + "'); ").executeUpdate();
            System.out.println("user added");
        } else {
            System.out.println("Username or email is arraly used");
        }
    }
    

    public boolean checkUsername(String Username) throws SQLException {
        String name = "";
        s = stmt.executeQuery("SELECT `username` FROM `Users` WHERE `username` LIKE '" + Username + "'");

        while (s.next()) {
            name = s.getString("username");
            
        }
        if (name.equals(Username)) {
            return false;
        }

        return true;

    }
    
     public int getID(String Username) throws SQLException {
        String id = "";
        s = stmt.executeQuery("SELECT `id` FROM `Users` WHERE `username` LIKE '" +Username + "'");

        while (s.next()) {
            id = s.getString("id");
        }return Integer.parseInt(id);

    }

    public boolean checkEmail(String Email) throws SQLException {
        String name = "";
        s = stmt.executeQuery("SELECT `email` FROM `Users` WHERE `email` LIKE '" + Email + "'");

        while (s.next()) {
            name = s.getString("email");
        }
        if (name.equals(Email)) {
            return false;
        }

        return true;

    }

    public boolean checkLogin(String Username, String Password) throws SQLException {

        String pass = "";
        s = stmt.executeQuery("SELECT `password` FROM `Users` WHERE `username` LIKE '" + Username + "'");

        while (s.next()) {
            pass = s.getString("password");
        }
        if (pass.equals(Password)) {
            return true;
        }

        return false;

    }

    public void CloseDatabaseConnection() throws SQLException {
        this.con.close();
    }
    
    public void OpenDatabaseConnection(){
         try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("\u001B[34m"+"initialize connection to database"+"\u001B[0m");
            con = DriverManager.getConnection("jdbc:mysql://db4free.net/stackusers", "stackchat", "12345678");
            stmt = con.createStatement();
            System.out.println("\u001B[32m"+"Connected to database successfully"+"\u001B[0m");

        } catch (Exception e) {
            System.err.println("Error in database Connection");
        }
    }
    
    public String CheckServerIP() throws SQLException{
        String IP = "N/A";
        s = stmt.executeQuery("SELECT `username` FROM `Users` WHERE `username` LIKE '" + "'");
        IP = s.getString("IP");
        return IP;
    }

}