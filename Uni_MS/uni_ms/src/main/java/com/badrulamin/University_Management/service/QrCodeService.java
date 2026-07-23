package com.badrulamin.University_Management.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.oned.Code128Writer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class QrCodeService {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private static final int QR_SIZE = 250;

    public String generateRegistrationQrCodeBase64(String registrationNumber) {
        String statusUrl = frontendUrl + "/pre-admission/status/" + registrationNumber;
        try {
            return generateQrCodeBase64(statusUrl);
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] generateRegistrationQrCode(String registrationNumber) {
        String statusUrl = frontendUrl + "/pre-admission/status/" + registrationNumber;
        try {
            return generateQrCodePng(statusUrl);
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] generateBarcode(String data, int width, int height) {
        try {
            Code128Writer barcodeWriter = new Code128Writer();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix bitMatrix = barcodeWriter.encode(data, BarcodeFormat.CODE_128, width, height, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    public String generateBarcodeBase64(String data, int width, int height) {
        byte[] barcode = generateBarcode(data, width, height);
        if (barcode != null) {
            return Base64.getEncoder().encodeToString(barcode);
        }
        return null;
    }

    private String generateQrCodeBase64(String data) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    private byte[] generateQrCodePng(String data) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }
}