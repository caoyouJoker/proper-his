package jdo.sys;

import java.io.Serializable;

/**
 * <p>Title: 邮件附件实体类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author li.xiang790130@gmail.com
 * @version 1.0
 */
public class MailAttachment implements Serializable{
    private static final long serialVersionUID = -2537535129464090235L;

    public MailAttachment() {
    }
    //附件名称;
    private String name;
    //附件数据;
    private byte[] data;

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData(){
       return data;
   }

   public void setData(byte[] data) {
       this.data = data;
   }





}
