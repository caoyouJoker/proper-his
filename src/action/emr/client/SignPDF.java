
package action.emr.client;

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
     * ��ȡfs���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getFs() {
        return fs;
    }

    /**
     * ����fs���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFs(byte[] value) {
        this.fs = value;
    }

    /**
     * ��ȡx���Ե�ֵ��
     * 
     */
    public int getX() {
        return x;
    }

    /**
     * ����x���Ե�ֵ��
     * 
     */
    public void setX(int value) {
        this.x = value;
    }

    /**
     * ��ȡy���Ե�ֵ��
     * 
     */
    public int getY() {
        return y;
    }

    /**
     * ����y���Ե�ֵ��
     * 
     */
    public void setY(int value) {
        this.y = value;
    }

    /**
     * ��ȡw���Ե�ֵ��
     * 
     */
    public int getW() {
        return w;
    }

    /**
     * ����w���Ե�ֵ��
     * 
     */
    public void setW(int value) {
        this.w = value;
    }

    /**
     * ��ȡh���Ե�ֵ��
     * 
     */
    public int getH() {
        return h;
    }

    /**
     * ����h���Ե�ֵ��
     * 
     */
    public void setH(int value) {
        this.h = value;
    }

    /**
     * ��ȡp���Ե�ֵ��
     * 
     */
    public int getP() {
        return p;
    }

    /**
     * ����p���Ե�ֵ��
     * 
     */
    public void setP(int value) {
        this.p = value;
    }

}
