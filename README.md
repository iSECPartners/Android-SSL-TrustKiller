Android-SSL-TrustKiller
==================
Bypass SSL certificate pinning for most applications on a device.

Description
==================
This tool leverages Cydia Substrate to hook various methods 
in order to bypass certificate pinning by accepting
any SSL certificate - allowing to proxy most applications.

Usage
==================
* Install Android-SSL-TrustKiller.apk on a device where Cydia Substrate is installed with:

        adb install Android-SSL-TrustKiller.apk

* Cydia Substrate can be found on Google Play 
(https://play.google.com/store/apps/details?id=com.saurik.substrate&hl=en) 
and requires a rooted device.
* Add the CA of your proxy tool to the device.

Notes
==================
* Use on test device only as it bypass cert 
validation across all apps on the device

* License: MIT - See LICENSE.txt