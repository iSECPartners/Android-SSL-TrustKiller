/**
 * Android-SSL-TrustKiller
 * 
 * Bypass SSL certificate pinning for most applications on a device
 * 
 * Leverages Cydia Substrate to hook various methods 
 * in order to bypass certificate pinning by accepting
 * any SSL certificate.
 * 
 * Usage: Install this application on a device where 
 * Cydia Substrate is installed
 * Warning: Use on test device only as it bypass cert 
 * validation across most apps on the device
 * 
 * Author: Marc Blanchou
 * https://github.com/iSECPartners/Android-SSL-TrustKiller
 * 
 * Previous iSEC tool to bypass certificate pinning and 
 * leveraging a debugger:
 * https://github.com/iSECPartners/android-ssl-bypass
 * 
 */

package com.android.SSLTrustKiller;

import java.lang.reflect.Method;
import java.net.Socket;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;

import android.util.Log;
import com.saurik.substrate.*;

public class Hook {
	public static String _TAG = "SSLTrusKiller";
	
	public static void initialize() {

		// This hook overrides the getTrustManagers method
		// from javax.net.ssl.TrustManagerFactory
		// and returns a Trust Manager that doesn't perform checks
		MS.hookClassLoad("javax.net.ssl.TrustManagerFactory",
				new MS.ClassLoadHook() {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public void classLoaded(Class<?> resources) {
						final String methodName = "getTrustManagers";
						Method lmethod;
						try {
							lmethod = resources.getMethod(methodName);
						} catch (NoSuchMethodException e) {
							Log.w(_TAG, "No such method: " + methodName);
							lmethod = null;
						}

						if (lmethod != null) {
							Log.i(_TAG, "Hooking " + methodName);
							final MS.MethodPointer old = new MS.MethodPointer();
							MS.hookMethod(resources, lmethod,
									new MS.MethodHook() {
										public Object invoked(Object resources,
												Object... args)
												throws Throwable {
											Log.i(_TAG, methodName + "() override");
											// returns a trustManager that doesn't perform checks
											return UnsafeTrustManager.getInstance();
										}
									}, old);
						}
					}
				});
		
		// This hook overrides the setSSLSocketFactory method
		// from javax.net.ssl.HttpsURLConnection
		// and sets a SSL Socket Factory that uses a Trust Manager 
		// that doesn't perform checks
		MS.hookClassLoad("javax.net.ssl.HttpsURLConnection",
				new MS.ClassLoadHook() {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public void classLoaded(Class<?> resources) {
						final String methodName = "setSSLSocketFactory";
						Method lmethod;
						try {
							lmethod = resources.getMethod(methodName, 
									javax.net.ssl.SSLSocketFactory.class);
						} catch (NoSuchMethodException e) {
							Log.w(_TAG, "No such method: " + methodName);
							lmethod = null;
						}

						if (lmethod != null) {
							Log.i(_TAG, "Hooking " + methodName);
							final MS.MethodPointer old = new MS.MethodPointer();
							MS.hookMethod(resources, lmethod,
									new MS.MethodHook() {
										public Object invoked(Object resources,
												Object... args)
												throws Throwable {
											Log.i(_TAG, methodName + "() override");
											// returns a SSLSocketFactory that doesn't perform checks
											SSLContext context = SSLContext.getInstance("TLS");
											context.init(null, UnsafeTrustManager.getInstance(), null);
											old.invoke(resources, context.getSocketFactory());
								            return null;
										}
									}, old);
						}
					}
				});		
		
		// This hook overrides the init method
		// from javax.net.ssl.SSLContext
		// and init the SSLContext with a Trust Manager that doesn't perform checks
		MS.hookClassLoad("javax.net.ssl.SSLContext",
				new MS.ClassLoadHook() {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public void classLoaded(Class<?> resources) {
						final String methodName = "init";
						Method lmethod;
						try {
							lmethod = resources.getMethod(methodName, 
									KeyManager[].class, TrustManager[].class, SecureRandom.class);
						} catch (NoSuchMethodException e) {
							Log.w(_TAG, "No such method: " + methodName);
							lmethod = null;
						}

						if (lmethod != null) {
							Log.i(_TAG, "Hooking " + methodName + " in " +
									"javax.net.ssl.SSLContext");
							final MS.MethodPointer old = new MS.MethodPointer();
							MS.hookMethod(resources, lmethod,
									new MS.MethodHook() {
										public Object invoked(Object resources,
												Object... args)
												throws Throwable {
											Log.i(_TAG, methodName + "() override in " +
													"javax.net.ssl.SSLContext");
											// init the SSL context with a Trust Manager that doesn't perform checks
											old.invoke(resources, null, UnsafeTrustManager.getInstance(), null);
								            return null;
										}
									}, old);
						}
					}
				});	
		
		// This hooks setHostnameVerifier
		// from org.apache.http.conn.ssl.SSLSocketFactory
		// and overrides the second parameter with 
		// "SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER"
		// to accept any hostname
		MS.hookClassLoad("org.apache.http.conn.ssl.SSLSocketFactory",
				new MS.ClassLoadHook() {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public void classLoaded(Class<?> resources) {
						Method lmethod;
						try {
							lmethod = resources.getMethod("setHostnameVerifier", X509HostnameVerifier.class);
						} catch (NoSuchMethodException e) {
							Log.w(_TAG, "No such method: setHostnameVerifier");
							lmethod = null;
						}

						if (lmethod != null) {
							final MS.MethodPointer old = new MS.MethodPointer();
							Log.i(_TAG, "Hooking setHostnameVerifier() from " +
									"org.apache.http.conn.ssl.SSLSocketFactory");
							MS.hookMethod(resources, lmethod,
									new MS.MethodHook() {
										public Object invoked(Object resources,
												Object... args)
												throws Throwable {
											Log.i(_TAG, 
													"setHostnameVerifier() called " +
													"(org.apache.http.conn.ssl.SSLSocketFactory)");
											old.invoke(
													resources,
													SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
											return null;
										}
									}, old);
						}
					}
				});
		
		// This hook forces the isSecure method
		// from org.apache.http.conn.ssl.SSLSocketFactory
		// to always return true
		MS.hookClassLoad("org.apache.http.conn.ssl.SSLSocketFactory",
				new MS.ClassLoadHook() {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public void classLoaded(Class<?> resources) {
						Method lmethod;
						try {
							lmethod = resources.getMethod("isSecure", Socket.class);
						} catch (NoSuchMethodException e) {
							Log.w(_TAG, "No such method: isSecure");
							lmethod = null;
						}

						if (lmethod != null) {
							final MS.MethodPointer old = new MS.MethodPointer();
							Log.i(_TAG, "Hooking isSecure() from " +
									"org.apache.http.conn.ssl.SSLSocketFactory");
							MS.hookMethod(resources, lmethod,
									new MS.MethodHook() {
										public Object invoked(Object resources,
												Object... args)
												throws Throwable {
											Log.i(_TAG, 
													"isSecure() called (org.apache.http.conn.ssl.SSLSocketFactory)");
											// iSecure method now always return true
											return true;
										}
									}, old);
						}
					}
				});
		
		// Overrides the setDefaultHostnameVerifier method 
		// from org.apache.http.conn.ssl.SSLSocketFactory
		// to accept any hostname
		MS.hookClassLoad("javax.net.ssl.HttpsURLConnection",
				new MS.ClassLoadHook() {
					@SuppressWarnings({ "unchecked", "rawtypes" })
					public void classLoaded(Class<?> resources) {
						Method lmethod;
						try {
							lmethod = resources.getMethod("setDefaultHostnameVerifier", HostnameVerifier.class);
						} catch (NoSuchMethodException e) {
							Log.w(_TAG, "No such method: setDefaultHostnameVerifier");
							lmethod = null;
						}

						if (lmethod != null) {
							final MS.MethodPointer old = new MS.MethodPointer();
							Log.i(_TAG, "Hooking setDefaultHostnameVerifier() from " +
									"org.apache.http.conn.ssl.HttpsURLConnection");
							MS.hookMethod(resources, lmethod,
									new MS.MethodHook() {
										public Object invoked(Object resources,
												Object... args)
												throws Throwable {
											Log.i(_TAG, 
													"setDefaultHostnameVerifier() called (org.apache.http.conn.ssl.HttpsURLConnection)");
											old.invoke(
													resources,
													SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
											return null;
										}
									}, old);
						}
					}
				});
	}
}
