package jdo.ope.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import jdo.ope.client.OnSaveOpePackage;

@XmlRegistry 
public class ObjectFactory
{
  private static final QName _OnSaveOpePackage_QNAME = new QName("http://ope.jdo/", "onSaveOpePackage");
  private static final QName _OnSaveOpePackageResponse_QNAME = new QName("http://ope.jdo/", "onSaveOpePackageResponse");

  public OnSaveOpePackage createOnSaveOpePackage()  
  {
    return new OnSaveOpePackage();
  }

  public OnSaveOpePackageResponse createOnSaveOpePackageResponse()
  {
    return new OnSaveOpePackageResponse();
  }

  @XmlElementDecl(namespace="http://ope.jdo/", name="onSaveOpePackage")
  public JAXBElement<OnSaveOpePackage> createOnSaveOpePackage(OnSaveOpePackage value)
  {
    return new JAXBElement(_OnSaveOpePackage_QNAME, OnSaveOpePackage.class, null, value);
  }

  @XmlElementDecl(namespace="http://ope.jdo/", name="onSaveOpePackageResponse")
  public JAXBElement<OnSaveOpePackageResponse> createOnSaveOpePackageResponse(OnSaveOpePackageResponse value)
  {
    return new JAXBElement(_OnSaveOpePackageResponse_QNAME, OnSaveOpePackageResponse.class, null, value);
  }
}