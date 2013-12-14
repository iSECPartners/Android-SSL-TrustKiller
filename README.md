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

* Ensure that Cydia Substrate has been deployed on your test device. The installer requires a rooted device and can be found on the Google Play store at https://play.google.com/store/apps/details?id=com.saurik.substrate&hl=en 
* Download the pre-compiled APK available at https://github.com/iSECPartners/Android-SSL-TrustKiller/releases
* Install the APK package on the device:

        adb install Android-SSL-TrustKiller.apk

* Add the CA certificate of your proxy tool to the device's trust store.

Notes
-----

Use on test devices only as it disables certificate validation for any app doing certificate pinning on the device.

License
-------

See ./LICENSE.

Authors
-------

Marc Blanchou
