package com.javahis.util;
import java.io.*;
import java.net.*;

public class mainServer
{
        public static boolean sFlag;

        public static void main(String args[])
        {
                try
                {
                        ServerSocket server=null;
                        try
                        {
                                server=new ServerSocket(9100);
                        }
                        catch(Exception e)
                        {
                                System.out.println("can not listen to :"+e);
                        }

                        Socket socket=null;

                        try
                        {
                                System.out.println("Java Server Start:");
                                socket=server.accept();
                                System.out.println("client has connected\n");
                        }
                        catch(Exception e)
                        {
                                System.out.println("Error:"+e);
                        }


                //	String line;

                        BufferedReader is=new BufferedReader(new InputStreamReader(socket.getInputStream()));//*correct get the input from the client
                        PrintWriter 	     os=new PrintWriter(socket.getOutputStream());					    //*correct  make os the output to client

                        sFlag=true;

                        //BufferedReader sin=new BufferedReader(new InputStreamReader(System.in));		    //input data from keyboard.
                        while(sFlag)
                        {
                            controlThread testController = new controlThread(os);
                            testController.start();

                        }


                        System.out.println("server ends successfully!\n");
                        is.close();
                        //os.close();
                        socket.close();
                        server.close();
                }
                catch(Exception e)
                {
                System.out.println("Error:"+e);
                }
        }
}
