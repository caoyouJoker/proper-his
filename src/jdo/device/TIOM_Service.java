package jdo.device;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * <p>Title: WebService接口</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author lzk 2008.10.29
 * @version 1.0
 */
public class TIOM_Service
{
    /**
     * 调用Service
     * executeService("192.168.1.105",80,"/Damo/Service.asmx","http://tempuri.org/","UpDateNewRegisters",xml.toString())
     * @param host String 主机名称
     * @param port int 端口
     * @param path String 路径
     * @param namespace String 名称空间
     * @param functionName String 函数名
     * @param data String xml参数数据
     * @return String
     */
    public static String executeService(String host,int port,String path,String namespace,String functionName,String data)
    {
        byte xmlData[] = getXMLData(functionName,namespace,data);
        if(xmlData == null)
            return "xmlData 错误";
        byte linkData[] = getLinkData(host,path,namespace + functionName,xmlData.length);
        if(linkData == null)
            return "LinkData 错误";
        new DynamecMassage(host,port,functionName,linkData,xmlData);
        String result = "true";
        return result;
    }
    /**
     * 得到提交XML数据
     * 例如:getXMLData("UpDateNewRegisters","http://tempuri.org/",xml.toString());
     * @param functionName String 函数名
     * @param xmlns String
     * @param parmData String
     * @return byte
     */
    private static byte[] getXMLData(String functionName,String xmlns,String parmData)
    {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        xml.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">");
        xml.append("<soap:Body>");
        xml.append("<");
        xml.append(functionName);
        xml.append(" xmlns=\"");
        xml.append(xmlns);
        xml.append("\" >");
        xml.append(parmData);
        xml.append("</");
        xml.append(functionName);
        xml.append(">");
        xml.append("</soap:Body>");
        xml.append("</soap:Envelope>");
        try{
            return xml.toString().getBytes("UTF-8");
        }catch(Exception e){
            return null;
        }
    }
    /**
     * 得到连接字符串
     * 例如:getLinkData("192.168.1.105","/Damo/Service.asmx","http://tempuri.org/UpDateNewRegisters",xml);
     * @param host String 主机名或IP "192.168.1.105"
     * @param path String 网址路径 "/Damo/Service.asmx"
     * @param SOAPAction String 动作名 "http://tempuri.org/UpDateNewRegisters"
     * @param xmlSize int 数据长度
     * @return byte[]
     */
    private static byte[] getLinkData(String host,
                                      String path,
                                      String SOAPAction,
                                      int xmlSize)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("POST ");
        sb.append(path);
        sb.append(" HTTP/1.1\r\n");
        sb.append("Content-Type: text/xml; charset=utf-8\r\n");
        sb.append("SOAPAction: \"");
        sb.append(SOAPAction);
        sb.append("\"\r\n");
        sb.append("Host: ");
        sb.append(host);
        sb.append("\r\n");
        sb.append("Content-Length:" + xmlSize + "\r\n");

        sb.append("\r\n");
        try{
            return sb.toString().getBytes("UTF-8");
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 解析返回值
     * @param input InputStream
     * @return String
     */
    private static String returnValue(InputStream input)
    {
        boolean b = false;
        boolean pass = false;
        int count = -1;
        String data = "";
        do{
            String s = getLine(input);
            if(s.startsWith("Content-Length:"))
                count = Integer.parseInt(s.substring("Content-Length:".length()).trim());
            if(s.startsWith("HTTP/1.1 200 OK"))
                pass = true;
            if(s.length() == 0 && count >= 0)
            {
                try
                {
                    byte b1[] = new byte[count];
                    input.read(b1, 0, count);
                    data = new String(b1, "UTF-8");
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                b = true;
            }
        }
        while(!b);
        if(!pass)
        {
            data = "HTTP ERR:" + data;
            return data;
        }
        return data;
    }
    /**
     * 得到移行数据
     * @param input InputStream
     * @return String
     */
    private static String getLine(InputStream input)
    {
        if(input == null)
            return null;
        StringBuffer sb = new StringBuffer();
        try{
            char c = (char) input.read();
            while (c != '\n')
            {
                if(c != '\r')
                    sb.append(c);
                c = (char) input.read();
            }
        }catch(Exception e)
        {
        }
        return sb.toString();
    }
    public static void main(String args[])
    {
        StringBuffer xml = new StringBuffer();
        xml.append("<userName>tiis</userName>");
        xml.append("<userPwd>1</userPwd>");
        xml.append("<strRegisters>");
        xml.append("<string>1你好测试.</string>");
        xml.append("<string>2参数.</string>");
        xml.append("<string>3C.</string>");
        xml.append("<string>4D.</string>");
        xml.append("<string>5E.</string>");
        xml.append("</strRegisters>");
        System.out.println(executeService("192.168.1.4",80,"/service.asmx","http://tempuri.org/","TestCon",xml.toString()));
    }
    public static class DynamecMassage implements Runnable
    {
        String host;
        int port;
        byte[] linkData;
        byte[] xmlData;
        String functionName;
        public DynamecMassage(String host,int port,String functionName,byte[] linkData,byte[] xmlData)
        {
            this.host = host;
            this.port = port;
            this.linkData = linkData;
            this.xmlData = xmlData;
            this.functionName = functionName;
            new Thread(this).start();
        }
        String result;
        public void run()
        {
            try{
                Socket socket = new Socket(host,port);
                OutputStream out = socket.getOutputStream();
                out.write(linkData);
                out.write(xmlData);
                InputStream input = socket.getInputStream();
                result = returnValue(input);
                out.close();
                input.close();
                socket.close();
            }catch(Exception e)
            {
              e.printStackTrace();
                return;
            }
            /*if(result != null && !result.startsWith("HTTP ERR:"))
            {
                String s1 = "<" + functionName + "Result>";
                String s2 = "</" + functionName + "Result>";
                int i1 = result.indexOf(s1);
                int i2 = result.indexOf(s2);
                if(i1 > 0 && i2 > 0)
                    result = result.substring(i1 + s1.length(),i2);
            }
            */
        }
    }
}

