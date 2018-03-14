
package action.emr.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="fs" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/&gt;
 *         &lt;element name="x" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="y" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="w" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="h" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="p" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fs",
    "x",
    "y",
    "w",
    "h",
    "p"
})
@XmlRootElement(name = "signPDF")
public class SignPDF {

    protected byte[] fs;
    protected int x;
    protected int y;
    protected int w;
    protected int h;
    protected int p;

    /**
     * 获取fs属性的值。
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getFs() {
        return fs;
    }

    /**
     * 设置fs属性的值。
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFs(byte[] value) {
        this.fs = value;
    }

    /**
     * 获取x属性的值。
     * 
     */
    public int getX() {
        return x;
    }

    /**
     * 设置x属性的值。
     * 
     */
    public void setX(int value) {
        this.x = value;
    }

    /**
     * 获取y属性的值。
     * 
     */
    public int getY() {
        return y;
    }

    /**
     * 设置y属性的值。
     * 
     */
    public void setY(int value) {
        this.y = value;
    }

    /**
     * 获取w属性的值。
     * 
     */
    public int getW() {
        return w;
    }

    /**
     * 设置w属性的值。
     * 
     */
    public void setW(int value) {
        this.w = value;
    }

    /**
     * 获取h属性的值。
     * 
     */
    public int getH() {
        return h;
    }

    /**
     * 设置h属性的值。
     * 
     */
    public void setH(int value) {
        this.h = value;
    }

    /**
     * 获取p属性的值。
     * 
     */
    public int getP() {
        return p;
    }

    /**
     * 设置p属性的值。
     * 
     */
    public void setP(int value) {
        this.p = value;
    }

}
