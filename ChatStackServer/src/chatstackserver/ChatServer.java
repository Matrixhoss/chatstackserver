/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatstackserver;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ChatServer {
    ServerSocket sc;
    boolean IsOpen=true;//to know if server opened
    ArrayList<ClientThread> clients=new ArrayList<ClientThread>();//array list of all clients
    Database db;
    public ChatServer() {
        try {
            db =new Database();
            sc=new ServerSocket(4520);//open the socket
            //when accpet connection put the socket in new thread and save it in arraylist
            while(IsOpen){
                Socket s=sc.accept();
                ClientThread cl=new ClientThread(s);
                cl.start();
                this.clients.add(cl);
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    
    class ClientThread extends Thread{
        private String userName;
        private boolean ThreadOpen=true;
        private DataOutputStream out;
        private DataInputStream in;
        public ClientThread(Socket s) {
            try {
                this.out=new DataOutputStream(s.getOutputStream());
                this.in=new DataInputStream(s.getInputStream());
                userName=new String(in.readUTF());
                System.out.println(userName+" is Entered");
            } catch (IOException ex) {
                Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
            
            @Override
            public void run() {
                try{
                    while(ThreadOpen){ 
                        String m=new String(in.readUTF());
                        this.SendToGroup(m);
                    }
                    this.out.close();
                }catch(IOException ex){
                    System.out.println(ex);
                }
                
            }
            
            public void SendToGroup(String m) throws IOException{
                for (ClientThread client : clients) {
                    
                        client.out.writeUTF(m);
                    
                }
            }
        } 
    
}
