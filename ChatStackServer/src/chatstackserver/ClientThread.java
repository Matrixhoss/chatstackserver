/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatstackserver;

import static chatstackserver.ChatServer.db;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocol.chatStackProtocol;

/**
 *
 * @author Hassan
 */
//serverthread which communicuate with client
public class ClientThread extends Thread {

    private String userName;
    private boolean ThreadOpen = true;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket s;
    private chatStackProtocol p;

    public ClientThread(Socket s) {
        try {
            this.s = s;
            this.out = new ObjectOutputStream(s.getOutputStream());
            this.in = new ObjectInputStream(s.getInputStream());
            this.SendToGroup(new chatStackProtocol(1, "server", ""));

        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            //recive any protcol
            while (ThreadOpen) {
                p = (chatStackProtocol) in.readObject();
                int id = p.getId();
                if (id == 0) {
                    System.out.println("User : " + p.getUser() + " is DisConnected ");
                    this.SendToGroup(new chatStackProtocol(1, "server", ""));
                    this.closeConnection();
                    ChatServer.clients.remove(this);
                    this.stop();
                }
                if (id == 2) {
                    System.out.println("New Group is Added ");
                    this.SendToGroup(new chatStackProtocol(2, "server", ""));
                }
                if (id == 3) {
                    System.out.println("New Member is entered in  " + p.getGroup());
                    this.SendToGroup(new chatStackProtocol(3, "server", ""));
                }
                if (id == 4) {
                    System.out.println("[Message =>" + p.getGroup() + " ] User: " + p.getUser() + " Send :" + p.getMessage());
                    this.SendToGroup(new chatStackProtocol(4, p.getUser(), p.getMessage(), p.getGroup()));
                }
                if(id==5){
                    System.out.println("User : "+p.getMessage()+" is kicked");
                    this.SendToGroup(new chatStackProtocol(5, p.getUser(), p.getMessage(), p.getGroup()));
                }
            }
            this.out.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void SendToGroup(chatStackProtocol p) throws IOException {
        for (ClientThread client : ChatServer.clients) {
            if (!client.equals(this)) {
                client.out.writeObject(p);
            }
        }
    }

    public void SendMessageToGroup(chatStackProtocol p) throws IOException {
        try {
            ArrayList<String> ips = ChatServer.db.getUsersIpInGroup(p.getGroup());
            for (ClientThread client : ChatServer.clients) {
                for (String ip : ips) {
                    ip = "/" + ip;
                    if (!client.equals(this) && ip.equals(client.s.getInetAddress())) {
                        client.out.writeObject(p);
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Send Message => " + ex.getMessage());
        }
    }

    public void closeConnection() {
        try {
            this.in.close();
            this.out.close();
            this.s.close();

        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
