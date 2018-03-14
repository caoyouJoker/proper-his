
package action.device.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the action.device.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Connect_QNAME = new QName("http://ws.demo.sunray.com/", "connect");
    private final static QName _DisconnectResponse_QNAME = new QName("http://ws.demo.sunray.com/", "disconnectResponse");
    private final static QName _StartScanResponse_QNAME = new QName("http://ws.demo.sunray.com/", "startScanResponse");
    private final static QName _StartScan_QNAME = new QName("http://ws.demo.sunray.com/", "startScan");
    private final static QName _ConnectResponse_QNAME = new QName("http://ws.demo.sunray.com/", "connectResponse");
    private final static QName _StopScanResponse_QNAME = new QName("http://ws.demo.sunray.com/", "stopScanResponse");
    private final static QName _StopScan_QNAME = new QName("http://ws.demo.sunray.com/", "stopScan");
    private final static QName _Disconnect_QNAME = new QName("http://ws.demo.sunray.com/", "disconnect");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: action.device.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StartScan }
     * 
     */
    public StartScan createStartScan() {
        return new StartScan();
    }

    /**
     * Create an instance of {@link Connect }
     * 
     */
    public Connect createConnect() {
        return new Connect();
    }

    /**
     * Create an instance of {@link DisconnectResponse }
     * 
     */
    public DisconnectResponse createDisconnectResponse() {
        return new DisconnectResponse();
    }

    /**
     * Create an instance of {@link StartScanResponse }
     * 
     */
    public StartScanResponse createStartScanResponse() {
        return new StartScanResponse();
    }

    /**
     * Create an instance of {@link StopScan }
     * 
     */
    public StopScan createStopScan() {
        return new StopScan();
    }

    /**
     * Create an instance of {@link Disconnect }
     * 
     */
    public Disconnect createDisconnect() {
        return new Disconnect();
    }

    /**
     * Create an instance of {@link ConnectResponse }
     * 
     */
    public ConnectResponse createConnectResponse() {
        return new ConnectResponse();
    }

    /**
     * Create an instance of {@link StopScanResponse }
     * 
     */
    public StopScanResponse createStopScanResponse() {
        return new StopScanResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Connect }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.demo.sunray.com/", name = "connect")
    public JAXBElement<Connect> createConnect(Connect value) {
        return new JAXBElement<Connect>(_Connect_QNAME, Connect.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DisconnectResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.demo.sunray.com/", name = "disconnectResponse")
    public JAXBElement<DisconnectResponse> createDisconnectResponse(DisconnectResponse value) {
        return new JAXBElement<DisconnectResponse>(_DisconnectResponse_QNAME, DisconnectResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartScanResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.demo.sunray.com/", name = "startScanResponse")
    public JAXBElement<StartScanResponse> createStartScanResponse(StartScanResponse value) {
        return new JAXBElement<StartScanResponse>(_StartScanResponse_QNAME, StartScanResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StartScan }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.demo.sunray.com/", name = "startScan")
    public JAXBElement<StartScan> createStartScan(StartScan value) {
        return new JAXBElement<StartScan>(_StartScan_QNAME, StartScan.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConnectResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.demo.sunray.com/", name = "connectResponse")
    public JAXBElement<ConnectResponse> createConnectResponse(ConnectResponse value) {
        return new JAXBElement<ConnectResponse>(_ConnectResponse_QNAME, ConnectResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StopScanResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.demo.sunray.com/", name = "stopScanResponse")
    public JAXBElement<StopScanResponse> createStopScanResponse(StopScanResponse value) {
        return new JAXBElement<StopScanResponse>(_StopScanResponse_QNAME, StopScanResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StopScan }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.demo.sunray.com/", name = "stopScan")
    public JAXBElement<StopScan> createStopScan(StopScan value) {
        return new JAXBElement<StopScan>(_StopScan_QNAME, StopScan.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Disconnect }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.demo.sunray.com/", name = "disconnect")
    public JAXBElement<Disconnect> createDisconnect(Disconnect value) {
        return new JAXBElement<Disconnect>(_Disconnect_QNAME, Disconnect.class, null, value);
    }

}
