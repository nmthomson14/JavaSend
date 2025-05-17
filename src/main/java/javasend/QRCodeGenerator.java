package javasend;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import java.awt.*;
import java.awt.image.BufferedImage;

public class QRCodeGenerator {

    // Pass in text to encode, returns a QR code at size specified as BuffedImage
    public static BufferedImage generateQRCodeImage(String textToEncode, int width, int height) {

        // Object that encodes given text into a bit matrix object (from ZXing library)
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            // Bit matrix is a 2D vector filled with true (black square) / false (white square) values
            BitMatrix bitMatrix = qrCodeWriter.encode(textToEncode, BarcodeFormat.QR_CODE, width, height);

            // Image with 8-bit RGB color, initially blank
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Loop through each pixel in the image, filling it in which the appropriate color based on bit matrix
            for (int x = 0; x < width; x++) {

                for (int y = 0; y < height; y++) {

                    // If matrix value at specified column (x) and row (y) is true, then that space should be black
                    if (bitMatrix.get(x, y)) {
                        image.setRGB(x, y, Color.BLACK.getRGB());
                    // If matrix value at specified column (x) and row (y) is false, then that space should be white
                    } else {
                        image.setRGB(x, y, Color.WHITE.getRGB());
                    }
                }
            }
            return image;

        // QR code encoder can give us an error for a few reasons:
        // 1: null string
        // 2: Invalid dimensions (not positive)
        // 3: Too many characters in too small of given dimensions
        } catch (WriterException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
