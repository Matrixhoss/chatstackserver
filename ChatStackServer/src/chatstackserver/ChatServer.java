/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatstackserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ChatServer {
    ServerSocket sc;
    boolean IsOpen=true;
    
    public ChatServer() {
        try {
            sc=new ServerSocket(4520);
            while(IsOpen){
                Socket s=sc.accept();
                ClientThread client=new ClientThread(s);
                client.run();
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
                System.out.println(userName);
            } catch (IOException ex) {
                Logger.getLogger(ChatServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
            
            @Override
            public void run() {
                try{
                    System.out.println("Client accepted");
//                    while(ThreadOpen){ 
                        this.out.writeUTF("Hi Client");
//                    }
                    this.out.close();
                }catch(IOException ex){
                    System.out.println(ex);
                }
                
            }
            
        } 
    
}
