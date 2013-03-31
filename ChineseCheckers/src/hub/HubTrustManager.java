package hub;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.locks.Lock;

import javax.net.ssl.X509TrustManager;

import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;


public class HubTrustManager implements X509TrustManager {
	
	private KeyStore trustStore;
	private Lock tsLock;
	private List<String> online;
	private Lock onlineLock;

	public HubTrustManager( KeyStore ts, Lock tsLock, List<String> online, Lock onlineLock ) {
		this.trustStore = ts;
		this.tsLock = tsLock;
		this.online = online;
		this.onlineLock = onlineLock;
	}
	
	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		X509CertImpl receivedCert = (X509CertImpl)chain[chain.length-1];
		String subject = (String)receivedCert.get(X509CertInfo.SUBJECT);
		PublicKey key = (PublicKey)receivedCert.get(X509CertInfo.KEY);
		
		try {
			tsLock.lock();
			X509CertImpl storedCert = (X509CertImpl)trustStore.getCertificate(subject);
			tsLock.unlock();
			if(!key.equals((PublicKey)storedCert.get(X509CertInfo.KEY))) {
				throw new CertificateException();
			}
		} catch (KeyStoreException e) {
			throw new CertificateException();
		}
		
		onlineLock.lock();
		if(!online.contains(subject)) {
			throw new CertificateException();
		}
		onlineLock.unlock();
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

}
