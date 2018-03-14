package jdo.sys;

import com.javahis.device.EMRPad30;
import com.javahis.system.root.RootClientListener;

public class MessageTool {
    /**
     * 得到消息文字
     * @param name String
     * @return String
     */
    public String getMessage(String name)
    {
        if(name == null || name.length() == 0)
            return "";
        if("en".equals(Operator.getLanguage()))
            return DictionaryTool.getInstance().getEnName("SYS_MESSAGE",name);

        return DictionaryTool.getInstance().getName("SYS_MESSAGE",name);
    }
    /**
     * 得到拼音
     * @param text String
     * @return String
     */
    public String getPy(String text)
    {
        if(text == null || text.length() == 0)
            return "";
        return SYSHzpyTool.getInstance().charToCode(text);
    }
    /**
     * 得到省
     * @param postNo3 String
     * @return String
     */
    public String getState(String postNo3)
    {
        if(postNo3 == null || postNo3.length() == 0)
            return "";
        return SYSPostTool.getInstance().getState(postNo3);
    }
    /**
     * 得到城市
     * @param postNo3 String
     * @return String
     */
    public String getCity(String postNo3)
    {
        if(postNo3 == null || postNo3.length() == 0)
            return "";
        return SYSPostTool.getInstance().getCity(postNo3);
    }
    /**
     * 得到结构化OCK对象
     * @return EMRPad30
     */
    public EMRPad30 newEMRPad30()
    {
        return new EMRPad30();
    }
    /**
     * 得到在线通讯客户端监听对象
     * @return RootClientListener
     */
    public RootClientListener getRootClient()
    {
        return RootClientListener.getInstance();
    }
}
