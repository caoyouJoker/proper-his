package jdo.sta;

import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
/**
 * 此接口的实例管理使用哪一个 X509 证书来验证远端的安全套接字。
 * 决定是根据信任的证书授权、证书撤消列表、在线状态检查或其他方式做出的。
 * 
 */
public class iTrustManager implements X509TrustManager {
	public iTrustManager() {

	}

	// 给出同位体提供的部分或完整的证书链，构建到可任的根的证书路径，
	// 并且返回是否可以确认和信任将其用于基于身份验证类型的客户端 SSL 身份验证
	public void checkClientTrusted(X509Certificate chain[], String authType)
			throws CertificateException {
		System.out.println("check client trust status");
	}

	// 给出同位体提供的部分或完整的证书链，构建到可任的根的证书路径，
	// 并且返回是否可以确认和信任将其用于基于身份验证类型的服务器 SSL 身份验证。
	public void checkServerTrusted(X509Certificate chain[], String authType)
			throws CertificateException {
		System.out.println("check Server trust status");
	}

	// 返回受身份验证同位体信任的认证中心的数组
	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	}

	// 给出同位体提供的部分或完整的证书链，构建到可任的根的证书路径，
	// 并且返回是否可以确认和信任将其用于基于身份验证类型的客户端 SSL 身份验证
	public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
			String authType) throws java.security.cert.CertificateException {
		// TODO Auto-generated method stub
	}
	// 给出同位体提供的部分或完整的证书链，构建到可任的根的证书路径，
	// 并且返回是否可以确认和信任将其用于基于身份验证类型的服务器 SSL 身份验证。
	public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
			String authType) throws java.security.cert.CertificateException {
		// TODO Auto-generated method stub
	}

}
