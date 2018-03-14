package jdo.sta;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 
 *此类是用于主机名验证的基接口。
 * 
 *在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。
 * 
 *策略可以是基于证书的或依赖于其他身份验证方案。
 * 
 *当验证 URL 主机名使用的默认规则失败时使用这些回调
 * 
 */
public class TrustAnyHostnameVerifier implements HostnameVerifier {
	/**
	 * 验证主机名和服务器身份验证方案的匹配是可接受的
	 */
	public boolean verify(String hostname, SSLSession session) {
		return true;
	}
}
