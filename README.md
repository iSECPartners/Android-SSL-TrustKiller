Android-SSL-TrustKiller
=======================

Blackbox tool to bypass SSL certificate pinning for most applications 
running on a device.

Description
-----------

This tool leverages Cydia Substrate to hook various methods 
in order to bypass certificate pinning by accepting
any SSL certificate.

Usage
-----

* Ensure that Cydia Substrate has been deployed on your test device. The installer requires a rooted device and the APK file can be found at http://www.cydiasubstrate.com/download/com.saurik.substrate.apk
* Download the pre-compiled APK available at https://github.com/iSECPartners/Android-SSL-TrustKiller/releases
* Install the APK package on the device:

        adb install Android-SSL-TrustKiller.apk

* Add the CA certificate of your proxy tool to the device's trust store.

Notes
-----

Use only on a test devices as anyone on the same network can intercept traffic from a number of applications including Google apps. This extension will soon be integrated into Introspy-Android (https://github.com/iSECPartners/Introspy-Android) in order to allow you to proxy only selected applications.

License
-------

See ./LICENSE.

Authors
-------

Marc Blanchou
