package action.ins;

import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.ObjectInputStream;
import com.dongyang.data.TParm;
import java.util.Map;

/**
 *
 * <p>Title: 天津医保服务类</p>
 *
 * <p>Description: 天津医保服务类</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2011.12.07
 * @version 1.0
 */
public class INSServer
    implements Runnable {
    /**
     * socket ref
     */
    private Socket socket;
    /**
     * Is init for Dll
     *  true init
     *  false not init
     */
    private static boolean isInit;
    /**
     * Tianjin INS DLL Driver
     */
    private TJSafeDriver safe = new TJSafeDriver();

    public int IO = 1;
    public static String _TYPE_;
    public static String _PATH_;
    public static String _ISDEBUG_;
    public INSServer(Socket socket) {
        this.socket = socket;
    }

    /**
     * 处理数据流
     */
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.
                getOutputStream());
            TParm parm = new TParm( (Map) in.readObject());
            TParm result = work(parm);
            out.writeObject(result.getData());
            in.close();
            out.close();
            socket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 分析客户端请求
     * @param parm ActionParm
     * @return ActionParm
     */
    public TParm work(TParm parm) {
        //System.out.println("分析客户端请求" + parm);
        TParm result = new TParm();
        //客户响应动作
        switch (parm.getInt("_ACTION_")) {
            case 0x1: //是否初始化
                result.setData("_IS_INIT_", isInit);
                break;

            case 0x2: //初始化
                if (IO == 0) {
                    isInit = safe.init(parm.getInt("_TYPE_"),
                                       parm.getValue("_PATH_"),
                                       parm.getInt("_ISDEBUG_")) == 1;
                }
                else {
                    _TYPE_ = "" + parm.getInt("_TYPE_");
                    _PATH_ = parm.getValue("_PATH_");
                    _ISDEBUG_ = "" + parm.getInt("_ISDEBUG_");
                    isInit = true;
                }
                result.setData("_IS_INIT_", isInit);
                break;

            case 0x3: //调用医保接口

                //函数名
                String pipeLine = parm.getValue("PIPELINE");

                //传入数据
                String param = parm.getValue("PARAM");

                //类型
                String plotType = parm.getValue("PLOT_TYPE");
                if (IO == 0) {
                    //分配返回数组大小
                    byte[] returnData = new byte[parm.getInt(
                        "RETURN_DATA_COUNT")];

//                    System.out.println("PipeLine:" + pipeLine + "(" + plotType +
//                                       ")->" +
//                                       returnData.length);

                    //函数分流
                    if ("DataDown_rs".equalsIgnoreCase(pipeLine))
                        result.setData("_RETURN_",
                                       safe.DataDown_rs(param, plotType,
                            returnData));
                    else if ("DataDown_sp1".equalsIgnoreCase(pipeLine))
                        result.setData("_RETURN_",
                                       safe.DataDown_sp1(param, plotType,
                            returnData));
                    else if ("DataDown_sp".equalsIgnoreCase(pipeLine))
                        result.setData("_RETURN_",
                                       safe.DataDown_sp(param, plotType,
                            returnData));
                    else if ("DataUpload".equalsIgnoreCase(pipeLine))
                        result.setData("_RETURN_",
                                       safe.DataUpload(param, plotType,
                            returnData));
                    else if ("DataDown_yb".equalsIgnoreCase(pipeLine))
                        result.setData("_RETURN_",
                                       safe.DataDown_yb(param, plotType,
                            returnData));
                    else if ("DataDown_czys".equalsIgnoreCase(pipeLine))
                        result.setData("_RETURN_",
                                       safe.DataDown_czys(param, plotType,
                            returnData));
                    else if ("DataDown_czyd".equalsIgnoreCase(pipeLine))
                        result.setData("_RETURN_",
                                       safe.DataDown_czyd(param, plotType,
                            returnData));
                    else if ("DataDown_cmts".equalsIgnoreCase(pipeLine))
                        result.setData("_RETURN_",
                                       safe.DataDown_cmts(param, plotType,
                            returnData));
                    else if ("DataDown_cmtd".equalsIgnoreCase(pipeLine))
                        result.setData("_RETURN_",
                                       safe.DataDown_cmtd(param, plotType,
                            returnData));
                    else if ("DataDown_mts".equalsIgnoreCase(pipeLine))
                        result.setData("_RETURN_",
                                       safe.DataDown_mts(param, plotType,
                            returnData));
                    else if ("DataDown_mtd".equalsIgnoreCase(pipeLine))
                        result.setData("_RETURN_",
                                       safe.DataDown_mtd(param, plotType,
                            returnData));
                    else if ("DataDown_jk".equalsIgnoreCase(pipeLine))
                        result.setData("_RETURN_",
                                       safe.DataDown_jk(param, plotType,
                            returnData));

                    //设置返回结果
                    result.setData("RETURN_DATA", returnData);
                }
                else {
                    //String s = "t003 " + _TYPE_ + " \"" + _PATH_ + "\" " + pipeLine + " " + plotType + " \"" + param + "\"";
                    //String s1 [] = exe(s);
//                    String s1[] = exe("t003", new String[] {_TYPE_, _PATH_,
//                                      pipeLine, plotType, param});
//                    System.out.println("t003入参" + _TYPE_ + "|" + _PATH_ + "|" +
//                                       pipeLine + "|" + plotType + "|" + param);
                    String s1[] = exe("t003", new String[] {_TYPE_, _PATH_,
                                      pipeLine, plotType, param});
                    for (int i = 0; i < s1.length; i++){
                       System.out.println("t003出参" + s1[i]);
                    }
                    //
                    result.setData("_RETURN_", s1[0]);
                    result.setData("RETURN_DATA",
                                   s1[1] != null ? s1[1].getBytes() :
                                   "".getBytes());
                }
                break;
        }
        return result;
    }

    public static String[] exe(String cmd) {
        String r[] = new String[2];
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            BufferedReader br = new BufferedReader(ir);
            r[0] = br.readLine();
            r[1] = "";
            String s = br.readLine();
            while (s != null) {
                r[1] += s;
                s = br.readLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return r;
    }

    public static String[] exe(String cmd, String[] data) {
        String r[] = new String[2];        
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                process.getOutputStream()));
            for (int i = 0; i < data.length; i++) {
                bw.write(data[i]);
                bw.newLine();
            }
            bw.close();
            //
            BufferedReader br = new BufferedReader(new InputStreamReader(
                process.getInputStream()));
/*            String s0=br.readLine();
            //
            System.out.println("======s0====="+s0);
            if(s0!=null){
	            if(s0.indexOf("Thread attach")==-1){
	            	//
	            	 if(s0.indexOf("Process detach.")==-1){          
		            	System.out.println("======s0 late====="+s0);
		            	r[0] = s0;
	            	}
	        	}
            }*/
            //            
            r[1] = "";
            String s = br.readLine();
           //System.out.println("======s1====="+s);
          // boolean f=false;
           int i=0;
            while (s != null) {
            	//System.out.println("======s====="+s);
            	if(s.indexOf("attach")==-1){
            		if(s.indexOf("detach")==-1){
	            		//System.out.println("======s late====="+s);
	            		i++;
	            		if(i==1){
	            			r[0] = s;
	            		}else{
	            			r[1] += s;
	            		}
            		}
            	}
                s = br.readLine();
                
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return r;
    }

    /**
     * Ins Application Server Main function
     * @param args String[] parm size one is server port
     */
    public static void main(String args[]) {
        int port = 8002;
        if (args.length == 1)
            try {
                port = Integer.parseInt(args[0]);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        try {
            ServerSocket server = new ServerSocket(port);
           // System.out.println("INS Server Start");
            while (true) {
                try {
                    Socket socket = server.accept();
                    new Thread(new INSServer(socket)).start();
                }
                catch (Exception ep) {
                    ep.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*public static void main(String args[])
       {
      //String s[] = new String[]{"t003","0","C:/INS/log.txt","DataUpload","B","MZ0517071129102791|20071109|1|000517|200137|诺氟沙星片*|0|薄膜衣片|0.1g × 24|2.0300|1.000|2.03|2.03|0|0|0||01||0.2g|两次/日 (8-4)1|国药准字H10870002#"};
      //String s = "t003 1 C:/INS/log.txt DataUpload B \"MZ0517071129102791|20071109|1|000517|200137|诺氟沙星片*|0|薄膜衣片|0.1g × 24|2.0300|1.000|2.03|2.03|0|0|0||01||0.2g|两次/日 (8-4)1|国药准字H10870002#\"";
      String s2 = "";
      for(int i = 1;i< 10000;i++)
        s2 += "1234567890";
      String s = "t003 0 C:/INS/log.txt DataUpload B \"" + s2 + "\"";
      String s1 [] = exe("t003",new String[]{"0","C:/INS/log.txt","DataUpload","B",s2});
      System.out.println(s1[0]);
      System.out.println(s1[1]);
       }*/

}
