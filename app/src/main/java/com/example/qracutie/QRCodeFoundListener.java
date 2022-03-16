package com.example.qracutie;

/**
 * Listener for functions when QR Code is found/not found
 */
public interface QRCodeFoundListener {
    void onQRCodeFound(String qrCode);
    void qrCodeNotFound();
}
