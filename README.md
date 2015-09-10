#PC

Generate QR code from various files and verify the authenticity of the file using digital signature.

AuthenticQR_SXC is the source directory for the PC modules

The digital signatute scheme has been implemented using:

-> Digital Signature Algorithm: ECDSA

-> Hash Algorithm: SHA-256

-> Elliptic curve: prime256v1

-> Provider: Bouncycastle

System requirements for PC:

-> Linux

-> JDK 1.8 or above

-> libgtk2.0-0

-> libgtk3-0-0

-> gtkdialog

-> zenity

Start the GUI by executing start.sh via the terminal(Ex:  foo@bar:~$ sh start.sh ) preferably in super user mode

Alternatively change the permission of 'start.sh' & make it executable($ chmod +x start.sh) for once. Then like any other app, just double click on start.sh to run the GUI.

#ANDROID APP

ECDSA provider: Spongycastle

SXCQR is the source directory for the android app

If a new public key is generated you need to put it in SXCQR/app/src/main/assets and then compile and execute the app using Android Studio


