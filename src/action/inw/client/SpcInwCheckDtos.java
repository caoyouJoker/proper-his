
package action.inw.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spcInwCheckDtos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spcInwCheckDtos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="regionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="spcInwCheckDtos" type="{http://services.spc.action/}spcInwCheckDto" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="stationCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spcInwCheckDtos", propOrder = {
    "regionCode",
    "spcInwCheckDtos",
    "stationCode"
})
public class SpcInwCheckDtos {

    protected String regionCode;
    @XmlElement(nillable = true)
    protected List<SpcInwCheckDto> spcInwCheckDtos;
    protected String stationCode;

    /**
     * Gets the value of the regionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * Sets the value of the regionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegionCode(String value) {
        this.regionCode = value;
    }
   
    /**
     * Gets the value of the spcInwCheckDtos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spcInwCheckDtos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpcInwCheckDtos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpcInwCheckDto }
     * 
     * 
     */
    public List<SpcInwCheckDto> getSpcInwCheckDtos() {
        if (spcInwCheckDtos == null) {
            spcInwCheckDtos = new ArrayList<SpcInwCheckDto>();
        }
        return this.spcInwCheckDtos;
    }
   
    public void setSpcInwCheckDtos(List<SpcInwCheckDto> spcInwCheckDtos) {
		this.spcInwCheckDtos = spcInwCheckDtos;
	}
    /**
     * Gets the value of the stationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStationCode() {
        return stationCode;
    }

    /**
     * Sets the value of the stationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStationCode(String value) {
        this.stationCode = value;
    }

}
