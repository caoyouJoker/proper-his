package com.javahis.util;

import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.data.TSocket;
import java.io.File;
import com.dongyang.config.TConfig;
import java.util.Map;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BpelUtil {
    private static BpelUtil instanceObject;
    public BpelUtil() {
    }

    public static synchronized BpelUtil getInstance() {
        if (instanceObject == null) {
            instanceObject = new BpelUtil();
        }
        return instanceObject;
    }
    /**
     * 拿文件内容
     * @param path String
     * @return String
     */
    public String getFileText(String path){
        String rootName = TIOM_FileServer.getRoot();
        String bpelPath = TIOM_FileServer.getPath("BpelFilePath");
        TSocket socket = TIOM_FileServer.getSocket();
        String filePath = rootName+bpelPath+path;
         byte bytEmrData[] = TIOM_FileServer.readFile(socket,filePath);
         if (bytEmrData == null) {
             return "";
         }
         String data = new String(bytEmrData);
         String temp = data.replaceAll("\r\n","\n");
         return temp;
    }
    /**
     * 重送文件
     * @param path String
     * @return boolean
     */
    public boolean sendFile(String path){
        boolean falg = true;
        String rootName = TIOM_FileServer.getRoot();
        String bpelPath = TIOM_FileServer.getPath("BpelFilePath");
        TSocket socket = TIOM_FileServer.getSocket();
        String filePath = rootName + bpelPath + path;
        File sendFile = new File(filePath);
        String fileName = sendFile.getName();
//        System.out.println("文件名:"+fileName);
        byte bytEmrData[] = TIOM_FileServer.readFile(socket, filePath);
        if (bytEmrData == null) {
            falg = false;
        }
        TSocket bpelSocket = getBpelSocket("Main");
        if(bpelSocket == null)
            falg = false;
        File toSendFile = new File(getSendPath("Main"));
        if(!toSendFile.exists())
            toSendFile.mkdirs();
        String bpelSendPath = getSendPath("Main")+fileName;
//        System.out.println("传送文件名:"+bpelSendPath);
        falg = TIOM_FileServer.writeFile(bpelSocket,bpelSendPath, bytEmrData);
        return falg;
    }
    /**
     * 重送文件
     * @param path String
     * @return boolean
     */
    public boolean sendFile(String path,String fileData){
        boolean falg = true;
        String rootName = TIOM_FileServer.getRoot();
        String bpelPath = TIOM_FileServer.getPath("BpelFilePath");
        String filePath = rootName + bpelPath + path;
        File sendFile = new File(filePath);
        String fileName = sendFile.getName();
//        System.out.println("文件名:"+fileName);
        TSocket bpelSocket = getBpelSocket("Main");
        if(bpelSocket == null)
            falg = false;
        File toSendFile = new File(getSendPath("Main"));
        if(!toSendFile.exists())
            toSendFile.mkdirs();
        String bpelSendPath = getSendPath("Main")+fileName;
//        System.out.println("传送文件名:"+bpelSendPath);
//        System.out.println("文件内容===>"+fileData);
        String temp = fileData.replaceAll("\n","\r\n");
        falg = TIOM_FileServer.writeFile(bpelSocket,bpelSendPath, fileData.getBytes());
        return falg;
    }

    /**
     * 得到文件服务器连接
     * @param name String
     * @return TSocket
     */
    public static TSocket getBpelSocket(String name)
    {
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        String ip = config.getString("","BpelServer." + name + ".IP");
        TSocket socket = new TSocket(ip,TSocket.FILE_SERVER_PORT);
        if(!appIsRun(socket))
            return null;
        return socket;
    }
    /**
     * 得到BPEL服务器重送文件夹位置
     * @param name String
     * @return String
     */
    public static String getSendPath(String name){
        TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
        String sendStr = config.getString("","BpelServer." + name + ".BpelSendFile");
        return sendStr+"\\";
    }
    /**
    * 服务器是否启动
    * @param socket TSocket
    * @return boolean true 启动 false 没有启动
    */
   public static boolean appIsRun(TSocket socket)
   {
       TParm parm = new TParm();
       parm.setData("ACTION",0xFF);
       Object obj = socket.doSocket(parm.getData());
       if(obj == null)
       {
           return false;
       }
       parm = new TParm((Map)obj);
       if(parm.getErrCode() < 0)
       {
           return false;
       }
       return true;
   }
   public static void main(String[] args) {
       String data  = "a a a b b b c c a ";
       String temp = data.replaceAll("a","C");
//       System.out.println("data"+temp);
   }

}
