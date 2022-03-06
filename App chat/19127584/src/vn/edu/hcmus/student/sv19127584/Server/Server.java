package vn.edu.hcmus.student.sv19127584.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * vn.edu.hcmus.student.sv19127584.Server
 * Created by 19127584 - Mạch Cảnh Toàn
 * Date 1/14/2022 - 3:52 PM
 * Description: ...
 */
public class Server {
    private ServerSocket serverSocket;
    private Socket socket;
    public static Vector<ClientHandle> clients = new Vector<>();
    private String accountFile = "userFile.txt";

    private void getAccountFromFile() {
        File f = new File(accountFile);
        if (f.exists())
            try {
                BufferedReader br = new BufferedReader(new FileReader(f.getName()));
                String line = br.readLine();
                while (line != null) {
                    String[] acc = line.split(",");
                    clients.add(new ClientHandle(acc[0], acc[1], false));
                    line = br.readLine();
                }
                br.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
    }

    private void saveAccountToFile(String username, String password) throws IOException {
        FileWriter writer = new FileWriter(accountFile, true);
        BufferedWriter buffer = new BufferedWriter(writer);
        buffer.write(username + "," + password + "\n");
        buffer.close();
    }

    public static void updateOnline() {
        String message = " ";
        for (ClientHandle client : clients) {
            if (client.getIsLoggedIn() == true) {
                message += ",";
                message += client.getUsername();
            }
        }
        for (ClientHandle client : clients) {
            if (client.getIsLoggedIn() == true) {
                try {
                    client.getDostream().writeUTF("@@ONLINE_USERS");
                    client.getDostream().writeUTF(message);
                    client.getDostream().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Server() throws IOException {
        DataInputStream distream = null;
        DataOutputStream dostream = null;
        try {
            // Update account list from file
            this.getAccountFromFile();
            // process server
            serverSocket = new ServerSocket(1509);
            while (true) {
                // wait request from clients
                socket = serverSocket.accept();
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                distream = new DataInputStream(is);
                dostream = new DataOutputStream(os);
                // process signin signup
                String request = distream.readUTF();
                if (request.equals("@@REGISTER")) {//#signup
                    String username = distream.readUTF();
                    String password = distream.readUTF();
                    //Check exist user by username and password
                    if (isExistedUser(username) == false) {
                        //add client to array storing
                        ClientHandle newHandler = new ClientHandle(socket, username, password, false);
                        clients.add(newHandler);
                        //Save account
                        this.saveAccountToFile(username, password);
                        dostream.writeUTF("@@REGISTER_SUCCESS"); //#signupsuccessful
                        dostream.flush();
                    } else {
                        dostream.writeUTF("@@ACCOUNT_EXIST"); //"#alreadyused"
                        dostream.flush();
                    }
                } else if (request.equals("@@LOGIN")) {
                    String username = distream.readUTF();
                    String password = distream.readUTF();
                    //Check exist user by username and password
                    if (isExistedUser(username) == true) {
                        for (ClientHandle client : clients) {
                            if (client.getUsername().equals(username)) {
                                // Check Password
                                if (password.equals(client.getPassword())) {
                                    ServerFrame.updateOnline(true);
                                    ServerFrame.status.append(username + " joined chat app\n");
                                    // Set object handler when login successfull
                                    ClientHandle newHandler = client;
                                    newHandler.setSocket(socket);
                                    newHandler.setLogIn(true);
                                    // announce user
                                    dostream.writeUTF("@@LOGIN_SUCCESS");//#logok
                                    dostream.flush();
                                    // Create thread for user to send and receive message
                                    new Thread(newHandler).start();
                                    // Update user online
                                    updateOnline();
                                } else {
                                    dostream.writeUTF("@@WRONG_PASSWORD");//#incorrectpw
                                    dostream.flush();
                                }
                                break;
                            }
                        }
                    } else {
                        dostream.writeUTF("@@WRONG_PASSWORD");
                        dostream.flush();
                    }
                } else if (request.equals("@@STOP_APP")) {//#stopserver
                    if (serverSocket != null) {
                        serverSocket.close();
                        distream.close();
                        dostream.close();
                    }
                }
            }

        } catch (Exception ex) {
            System.err.println(ex);
        } finally {
            if (serverSocket != null) {
                serverSocket.close();
                distream.close();
                dostream.close();
            }
        }
    }

    public boolean isExistedUser(String name) {
        for (ClientHandle client : clients) {
            if (client.getUsername().equals(name)) {
                return true;
            }
        }
        return false;
    }
}

