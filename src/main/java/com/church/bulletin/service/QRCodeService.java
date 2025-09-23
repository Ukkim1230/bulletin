package com.church.bulletin.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QRCodeService {

    /**
     * 주어진 URL로 QR 코드를 생성하고 Base64 인코딩된 이미지 문자열을 반환합니다.
     * 
     * @param url QR 코드에 포함할 URL
     * @param width QR 코드 이미지 너비
     * @param height QR 코드 이미지 높이
     * @return Base64로 인코딩된 QR 코드 이미지 문자열
     */
    public String generateQRCodeImage(String url, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("QR 코드 생성 중 오류 발생", e);
        }
    }
}
