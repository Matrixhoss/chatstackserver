package chatstackserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
        s = stmt.executeQuery("SELECT `IP` FROM `Server` WHERE `online` LIKE '1'");
        IP = s.getString("IP");
        boolean online = false;
        try{
        online = CheckIfOnline(IP);
        }
        catch(IOException ex){
        System.out.println(ex);
        }
        if(IP.equals("") || !online)
            return "0";
        return IP;
    }
    
    public String getGroup(String Username) throws SQLException{
        s = stmt.executeQuery("SELECT `Group` FROM `Users` WHERE `username` LIKE '" + Username + "'");
        String Group="";
        while (s.next()) {
            Group = s.getString("Group");
        }
        return Group;
    }
    
    public boolean CheckIfOnline(String IP) throws IOException {
        
        Socket s=new Socket(IP,5555);
            DataInputStream in=new DataInputStream(s.getInputStream());
            DataOutputStream out=new DataOutputStream(s.getOutputStream());
            out.writeUTF("online ?");
            String response=new String(in.readUTF());
            in.close();
            out.close();
            s.close();
            if(response.equals("yes online"))
                return true;
            else 
                return false;
    }
    
    public void addIpServer(String ip) throws SQLException{
        con.prepareStatement("DELETE FROM `Server` WHERE 1").executeUpdate();
        con.prepareStatement("INSERT INTO `Server` (`ip`, `online`) VALUES (\""+ip+"\", 1)").executeUpdate();
    
    }
    
    public void closeServer() throws SQLException{
        con.prepareStatement("DELETE FROM `Server` WHERE 1").executeUpdate();
    }
    
    public ArrayList<String> getUsersIpInGroup(String group) throws SQLException {

        ArrayList<String> ips = new ArrayList<String>();
        s = stmt.executeQuery("SELECT `ip` FROM `Users` WHERE `group` like '"+group+"'");
        while (s.next()) {
            ips.add(s.getString("username"));
        }
        return ips;

    }
    
}

