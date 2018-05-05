/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatstackserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServer {

    ServerSocket sc, CheckSocket;
    Thread online;
    boolean IsOpen = true;//to know if server opened
    public static ArrayList<ClientThread> clients = new ArrayList<ClientThread>();//array list of all clients
    Database db;
    String Ip;

    public ChatServer() {
        try {
            db = new Database();
            sc = new ServerSocket(4520);//open the socket
            Ip = this.getPublicIp();
            System.out.println("IP : " + Ip);
            CheckSocket = new ServerSocket(55555);
//            db.addIpServer(Ip);
            this.makeOnline();
            this.whenClosed();
            //when accpet connection put the socket in new thread and save it in arraylist
            while (IsOpen) {
                Socket s = sc.accept();
                ClientThread cl = new ClientThread(s);
                cl.start();
                this.clients.add(cl);
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    //when terminate the program
    public void whenClosed() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (ClientThread client : clients) {
                        client.closeConnection();
                        client.stop();
                    }
                    sc.close();
                    db.closeServer();
                    System.out.println("Server closed");
                } catch (IOException ex) {
                    Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }));
    }

    //get public ip for your server
    private String getPublicIp() {
        String ip = "";
        try {
            URL connection = new URL("http://checkip.amazonaws.com/");
            URLConnection con = connection.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            ip = reader.readLine();
        } catch (MalformedURLException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ip;
    }

    //make thread for tell client that server online 
    public void makeOnline() {
        online = new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream in;
                DataOutputStream out;
                while (true) {

                    try {
                        Socket s = CheckSocket.accept();
                        in = new DataInputStream(s.getInputStream());
                        out = new DataOutputStream(s.getOutputStream());
                        String m = new String(in.readUTF());
                        //client ask server if it online
                        if (m.equals("online ?")) {
                            out.writeUTF("yes online");
                            String timeStamp = new SimpleDateFormat("[dd/MM/yyyy-HH:mm:ss]").format(Calendar.getInstance().getTime());
                            System.out.println(timeStamp + " Client from " + s.getInetAddress() + " have checked if server is online.");
                        }
                        in.close();
                        out.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });
        online.start();
    }

}
