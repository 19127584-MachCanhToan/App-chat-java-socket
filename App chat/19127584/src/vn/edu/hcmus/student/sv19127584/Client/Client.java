package vn.edu.hcmus.student.sv19127584.Client;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * vn.edu.hcmus.student.sv19127584.Client
 * Created by 19127584 - Mạch Cảnh Toàn
 * Date 1/15/2022 - 10:52 PM
 * Description: ...
 */
public class Client {
    public static Socket socket;
    public static DataInputStream is;
    public static DataOutputStream os;
    public static String localhost = "localhost";
    public static void Connect(){
        try{
            if(socket != null){
                socket.close();
            }
            socket = new Socket(localhost,1509);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Cannot connect to server!","Connection Failed",JOptionPane.ERROR_MESSAGE);
        }

    }
}
