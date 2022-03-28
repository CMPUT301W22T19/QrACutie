package com.example.qracutie;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import java.nio.ByteBuffer;

import static android.graphics.ImageFormat.YUV_420_888;
import static android.graphics.ImageFormat.YUV_422_888;
import static android.graphics.ImageFormat.YUV_444_888;

/**
 * Analyzes the image of the QR and extracts the data from it
 *
 * This entire class was implemented using -
 * From: Learn To Android
 * URL:https://learntodroid.com/how-to-create-a-qr-code-scanner-app-in-android/
 * Author:Jarrod Lilkendey
 */
public class QRCodeImageAnalyzer implements ImageAnalysis.Analyzer {
    private final QRCodeFoundListener listener;

    /**
     * generates a new instance of the QRCodeAnalyzer class, having a specific listener
     * interface
     * @param listener an implementation of QRCodeFoundListener
     */
    public QRCodeImageAnalyzer(QRCodeFoundListener listener) {
        this.listener = listener;
    }

    /**
     * analyzes image of the QR code and extracts data from it
     * @param image an ImageProxy object
     */
    @Override
    public void analyze(@NonNull ImageProxy image) {
        if (image.getFormat() == YUV_420_888 || image.getFormat() == YUV_422_888 || image.getFormat() == YUV_444_888) {
            ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
            byte[] imageData = new byte[byteBuffer.capacity()];
            byteBuffer.get(imageData);

            PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                    imageData,
                    image.getWidth(), image.getHeight(),
                    0, 0,
                    image.getWidth(), image.getHeight(),
                    false
            );

            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            try {
                Result result = new QRCodeMultiReader().decode(binaryBitmap);
                listener.onQRCodeFound(result.getText());
            } catch (FormatException | ChecksumException | NotFoundException e) {
                listener.qrCodeNotFound();
            }
        }

        image.close();
    }
}
