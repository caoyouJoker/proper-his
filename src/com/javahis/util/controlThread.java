package com.javahis.util;

import java.io.*;
public class controlThread extends Thread
{
        private Point 		controlNode;
        private PrintWriter	outputStream;

        public controlThread(PrintWriter inOutputStream)
        {
                outputStream=inOutputStream;
        }


        public void run()
        {
                controlNode=new Point(180,150);

                for(int i=0;i<100;i++)
                {
                        try
                        {
                                sleep(400);
                        }
                        catch(java.lang.InterruptedException e)
                        {
                                System.out.println(e);
                        }
                        controlNode.mx=180+i*2;
                        outputStream.println(controlNode.toString()+"\0");
                        outputStream.flush();
                        System.out.println(controlNode.toString());
                }
                mainServer.sFlag=false;
        }
}
