package jdo.device.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="host" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="msgType" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;element name="msg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "host",
    "port",
    "msgType",
    "msg"
})
@XmlRootElement(name = "SendMsg")
public class SendMsg {

    protected String host;
    protected int port;
    protected short msgType;
    protected String msg;

    /**
     * ��ȡhost���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHost() {
        return host;
    }

    /**
     * ����host���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHost(String value) {
        this.host = value;
    }

    /**
     * ��ȡport���Ե�ֵ��
     * 
     */
    public int getPort() {
        return port;
    }

    /**
     * ����port���Ե�ֵ��
     * 
     */
    public void setPort(int value) {
        this.port = value;
    }

    /**
     * ��ȡmsgType���Ե�ֵ��
     * 
     */
    public short getMsgType() {
        return msgType;
    }

    /**
     * ����msgType���Ե�ֵ��
     * 
     */
    public void setMsgType(short value) {
        this.msgType = value;
    }

    /**
     * ��ȡmsg���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsg() {
        return msg;
    }

    /**
     * ����msg���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsg(String value) {
        this.msg = value;
    }

}
