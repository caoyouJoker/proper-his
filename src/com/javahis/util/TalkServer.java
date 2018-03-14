package com.javahis.util;
import java.io.*;
import java.net.*;
import java.applet.Applet;

public class TalkServer
{
        public static void main(String args[])
        {
                try
                {
                        ServerSocket server=null;

                        server=new ServerSocket(9100);

                        Socket socket=null;

                        while(true){


                                System.out.println("I am a java sever");
                                socket = server.accept();
                                System.out.println("client has connected\n");


                            String ss = getRequestData(socket);
//                            System.out.println("" + ss);
                            socket.getOutputStream().write("SSSSSSSSSS\n".getBytes());
                            //os.close();
                            socket.close();
                        }
                }
                catch(Exception e)
                {
                System.out.println("Error:"+e);
                }
        }
        /**
         *
         * @param socket Socket
         * @return String
         */
        public static String getRequestData(Socket socket) throws IOException {
            byte[] l = new byte[8129];
            byte p = (byte) socket.getInputStream().read();
            int k = 0;
            while (p != '\n') {
                l[k] = p;
                p = (byte) socket.getInputStream().read();
                k++;
            }
            String str = new String(l, 0, k);
            return str;
        }

}
