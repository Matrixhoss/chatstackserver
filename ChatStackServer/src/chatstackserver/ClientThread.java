/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatstackserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
                System.out.println("ID : " + p.getId() + "From User : " + p.getUser() + "message : " + p.getMessage());
                int id = p.getId();
                if (id == 0) {
                    this.SendToGroup(new chatStackProtocol(1, "server", ""));
                    this.closeConnection();
                    ChatServer.clients.remove(this);
                    this.stop();
                }
                if (id == 2) {
                    this.SendToGroup(new chatStackProtocol(2, "server", ""));
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
