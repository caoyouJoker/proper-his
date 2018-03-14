
package jdo.pha.inf.client;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for spcOpdOrderDtos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spcOpdOrderDtos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="spcOpdDiagrecDtos" type="{http://inf.pha.jdo/}spcOpdDiagrecDto" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="spcOpdDrugAllergyDtos" type="{http://inf.pha.jdo/}spcOpdDrugAllergyDto" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="spcOpdOrderDtos" type="{http://inf.pha.jdo/}spcOpdOrderDto" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="spcRegPatadmDtos" type="{http://inf.pha.jdo/}spcRegPatadmDto" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="spcSysPatinfoDtos" type="{http://inf.pha.jdo/}spcSysPatinfoDto" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spcOpdOrderDtos", propOrder = {
    "spcOpdDiagrecDtos",
    "spcOpdDrugAllergyDtos",
    "spcOpdOrderDtos",
    "spcRegPatadmDtos",
    "spcSysPatinfoDtos"
})
public class SpcOpdOrderDtos {

    @XmlElement(nillable = true)
    protected List<SpcOpdDiagrecDto> spcOpdDiagrecDtos;
    @XmlElement(nillable = true)
    protected List<SpcOpdDrugAllergyDto> spcOpdDrugAllergyDtos;
    @XmlElement(nillable = true)
    protected List<SpcOpdOrderDto> spcOpdOrderDtos;
    @XmlElement(nillable = true)
    protected List<SpcRegPatadmDto> spcRegPatadmDtos;
    @XmlElement(nillable = true)
    protected List<SpcSysPatinfoDto> spcSysPatinfoDtos;

    /**
     * Gets the value of the spcOpdDiagrecDtos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spcOpdDiagrecDtos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpcOpdDiagrecDtos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpcOpdDiagrecDto }
     * 
     * 
     */
    public List<SpcOpdDiagrecDto> getSpcOpdDiagrecDtos() {
        if (spcOpdDiagrecDtos == null) {
            spcOpdDiagrecDtos = new ArrayList<SpcOpdDiagrecDto>();
        }
        return this.spcOpdDiagrecDtos;
    }

    /**
     * Gets the value of the spcOpdDrugAllergyDtos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spcOpdDrugAllergyDtos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpcOpdDrugAllergyDtos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpcOpdDrugAllergyDto }
     * 
     * 
     */
    public List<SpcOpdDrugAllergyDto> getSpcOpdDrugAllergyDtos() {
        if (spcOpdDrugAllergyDtos == null) {
            spcOpdDrugAllergyDtos = new ArrayList<SpcOpdDrugAllergyDto>();
        }
        return this.spcOpdDrugAllergyDtos;
    }

    /**
     * Gets the value of the spcOpdOrderDtos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spcOpdOrderDtos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpcOpdOrderDtos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpcOpdOrderDto }
     * 
     * 
     */
    public List<SpcOpdOrderDto> getSpcOpdOrderDtos() {
        if (spcOpdOrderDtos == null) {
            spcOpdOrderDtos = new ArrayList<SpcOpdOrderDto>();
        }
        return this.spcOpdOrderDtos;
    }

    /**
     * Gets the value of the spcRegPatadmDtos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spcRegPatadmDtos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpcRegPatadmDtos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpcRegPatadmDto }
     * 
     * 
     */
    public List<SpcRegPatadmDto> getSpcRegPatadmDtos() {
        if (spcRegPatadmDtos == null) {
            spcRegPatadmDtos = new ArrayList<SpcRegPatadmDto>();
        }
        return this.spcRegPatadmDtos;
    }

    /**
     * Gets the value of the spcSysPatinfoDtos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the spcSysPatinfoDtos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpcSysPatinfoDtos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpcSysPatinfoDto }
     * 
     * 
     */
    public List<SpcSysPatinfoDto> getSpcSysPatinfoDtos() {
        if (spcSysPatinfoDtos == null) {
            spcSysPatinfoDtos = new ArrayList<SpcSysPatinfoDto>();
        }
        return this.spcSysPatinfoDtos;
    }

}
