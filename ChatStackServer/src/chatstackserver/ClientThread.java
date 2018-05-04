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
    
    public ClientThread(Socket s) {
        try {
            this.s = s;
            this.out = new ObjectOutputStream(s.getOutputStream());
            this.in = new ObjectInputStream(s.getInputStream());
            
            System.out.println(userName + " is Entered");
        } catch (IOException ex) {
            Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            while (ThreadOpen) {
                String m = new String(in.readUTF());
                this.SendToGroup(m);
            }
            this.out.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }

    public void SendToGroup(String m) throws IOException {
        for (ClientThread client : ChatServer.clients) {

            client.out.writeUTF(m);

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
