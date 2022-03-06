package vn.edu.hcmus.student.sv19127584.Server;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * vn.edu.hcmus.student.sv19127584.Server
 * Created by 19127584 - Mạch Cảnh Toàn
 * Date 1/14/2022 - 5:28 PM
 * Description: ...
 */
public class ClientHandle implements Runnable{
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private String username;
    private String password;
    private boolean logIn;

    public ClientHandle(Socket socket, String username, String password, boolean logIn) throws Exception{
        this.socket = socket;
        this.username = username;
        this.password = password;
        this.logIn = logIn;
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
    }

    public ClientHandle(String username, String password,boolean logIn){
        this.username = username;
        this.password = password;
        this.logIn = logIn;
    }

    public void setLogIn(boolean logIn){
        this.logIn = logIn;
    }

    public void setSocket(Socket socket){
        this.socket = socket;
        try {
            this.is = new DataInputStream(socket.getInputStream());
            this.os = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
                is.close();
                os.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean getIsLoggedIn() {
        return this.logIn;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public DataOutputStream getDostream() {
        return this.os;
    }

    public DataInputStream getDistream() {
        return this.is;
    }

    @Override
    public void run() {
        while(true){
            try{
                String message = null;
                message = is.readUTF();
                if (message.equals("@@LOGOUT")) {
                    ServerFrame.updateOnline(false);
                    ServerFrame.status.append(username + " leaved chat app\n");
                    os.writeUTF("@@LEAVE");
                    os.flush();
                    socket.close();
                    logIn = false;
                    Server.updateOnline();
                    break;
                }
                else if (message.contains("@123")) {
                    String user = message.substring(4);
                    String[] usr = user.split("@");
                    Lock lock = new ReentrantLock();
                    for (ClientHandle client : Server.clients) {
                        if (client.getUsername().equals(usr[1])) {
                            lock.lock();
                            try {
                                client.getDostream().writeUTF("@@CONFIRM_CHAT@" + usr[0] + "@" + usr[1]);
                                client.getDostream().flush();
                                break;
                            } finally {
                                lock.unlock();
                            }
                        }
                    }
                }
                else if (message.equals("@@TEXT")) {//#msgtext
                    String receiver = is.readUTF();
                    String content = is.readUTF();
                    Lock lock = new ReentrantLock();
                    for (ClientHandle client : Server.clients) {
                        if (client.getUsername().equals(receiver)) {
                            lock.lock();
                            try {
                                client.getDostream().writeUTF("@@TEXT");
                                client.getDostream().writeUTF(this.username);
                                client.getDostream().writeUTF(content);
                                client.getDostream().flush();
                                break;
                            } finally {
                                lock.unlock();
                            }
                        }
                    }
                }

            }
            catch (EOFException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Stop Server!", "Server Announcment", JOptionPane.ERROR_MESSAGE);
                closeSocket();
                System.exit(0);
            } catch (SocketException e2) {
                e2.printStackTrace();
                JOptionPane.showMessageDialog(null, "Stop Server!", "Server Announcment", JOptionPane.ERROR_MESSAGE);
                closeSocket();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        }
}

