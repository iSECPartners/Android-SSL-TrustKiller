package com.android.SSLTrustKiller;

import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class UnsafeTrustManager implements X509TrustManager {
	
    private static TrustManager[] _ref = null;
    
    public static TrustManager[] getInstance(){
            if (_ref == null) {
            	_ref = new TrustManager[1];
            	_ref[0] = new UnsafeTrustManager();
            }
            return _ref;
    }
	
	@Override
	public void checkClientTrusted(
	            X509Certificate[] chain, String authType) {
    	// does not throw exceptions if invalid cert
	}
	
    @Override
	public void checkServerTrusted(
	            java.security.cert.X509Certificate[] chain, String authType) {
    	// does not throw exceptions if invalid cert
	}

	// returns an array of trusted CAs
	// -> return your proxy's CA here if other methods fail to work
	@Override
	public X509Certificate[] getAcceptedIssuers() {		
	    X509Certificate[] certs = new X509Certificate[0];
		return certs;
	}
}
