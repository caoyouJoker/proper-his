package jdo.sys;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>Title: 邮件实体类</p>
 *
 * <p>Description: 发送邮件时进行邮件实体</p>
 *
 * <p>Copyright: Copyright (c) 2010-04-25</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author li.xiang@gmail.com
 * @version 1.0
 */
public class MailVO
    implements Serializable {
    private static final long serialVersionUID = -2537535129464080231L;

    public MailVO() {
    }
    //邮件中文编码
    private String charset="GB2312";
    //邮件内容的类型
    private String contentType="text/plain";
    /** 发送方 */
    private String from = null;
    /** 接收方 */
    private List<String> toAddress = new ArrayList<String> ();
    /** 抄送 */
    private List<String> ccAddress = new ArrayList<String> ();
    /** 主题 */
    private String subject = null;
    /** 正文 */
    private String content = null;
    /** URL附件 */
    /** byte[]附件*/
   private List<MailAttachment> attachByteArrays=new ArrayList<MailAttachment>();


    public String getCharset(){
        return charset;
    }
    public void setCharset(){
        this.charset=charset;
    }

    public String getContentType(){
        return contentType;
    }
    public void setContentType(String contentType){
        this.contentType=contentType;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getToAddress() {
        return toAddress;
    }

    public List<String> getCcAddress() {
        return ccAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    /**
     * 加入附件；
     * @return List<MailAttachment>
     */
    public List<MailAttachment> getAttachByteArrays(){
        return attachByteArrays;
    }

}
