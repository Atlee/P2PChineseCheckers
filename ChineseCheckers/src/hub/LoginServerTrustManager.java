package hub;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.locks.Lock;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class LoginServerTrustManager implements X509TrustManager {
	
	X509TrustManager pkixTrustManager;
	KeyStore trustStore;
	Lock tsLock;

    public LoginServerTrustManager( KeyStore trustStore, Lock tsLock ) throws Exception {
    	this.trustStore = trustStore;
    	this.tsLock = tsLock;
    	
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
        tsLock.lock();
        try {
        	tmf.init(trustStore);
        } finally {
        	tsLock.unlock();
        }

        TrustManager[] tms = tmf.getTrustManagers();

        for (int i = 0; i < tms.length; i++) {
            if (tms[i] instanceof X509TrustManager) {
                pkixTrustManager = (X509TrustManager) tms[i];
                return;
            }
        }

        throw new Exception("Couldn't initialize");
    }
	
	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// accept all certificates
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		// unused (the Hub is always a server)
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return pkixTrustManager.getAcceptedIssuers();
	}


}
