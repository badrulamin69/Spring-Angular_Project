package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.entity.AdmitCard;
import com.badrulamin.University_Management.entity.AdmissionTest;
import com.badrulamin.University_Management.entity.PreAdmissionRegistration;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AdmitCardPdfService {

    public byte[] generateAdmitCardPdf(AdmitCard admitCard) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 30, 30, 30, 30);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            Paragraph title = new Paragraph("ADMISSION TEST ADMIT CARD", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Paragraph subtitle = new Paragraph("Smart University | UMS-ERP", subFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(15);
            document.add(subtitle);

            PdfPTable lineTable = new PdfPTable(1);
            lineTable.setWidthPercentage(100);
            PdfPCell lineCell = new PdfPCell();
            lineCell.setBorder(PdfPCell.BOTTOM);
            lineCell.setBorderColor(Color.BLUE);
            lineCell.setBorderWidth(2);
            lineCell.setFixedHeight(5);
            lineTable.addCell(lineCell);
            document.add(lineTable);
            document.add(new Paragraph("\n"));

            AdmissionTest test = admitCard.getTest();
            PreAdmissionRegistration reg = admitCard.getRegistration();

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10);
            infoTable.setSpacingAfter(10);

            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            addInfoRow(infoTable, "Admit Card No:", admitCard.getAdmitCardNumber(), labelFont, valueFont);
            addInfoRow(infoTable, "Roll Number:", admitCard.getRollNumber(), labelFont, valueFont);
            addInfoRow(infoTable, "Candidate Name:", reg.getFirstName() + " " + reg.getLastName(), labelFont, valueFont);
            addInfoRow(infoTable, "Father's Name:", reg.getFatherName() != null ? reg.getFatherName() : "N/A", labelFont, valueFont);
            addInfoRow(infoTable, "Date of Birth:", reg.getDateOfBirth() != null ? reg.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "N/A", labelFont, valueFont);
            addInfoRow(infoTable, "Gender:", reg.getGender() != null ? reg.getGender() : "N/A", labelFont, valueFont);
            addInfoRow(infoTable, "Test Name:", test.getName(), labelFont, valueFont);
            addInfoRow(infoTable, "Test Date:", test.getTestDate() != null ? test.getTestDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "N/A", labelFont, valueFont);
            addInfoRow(infoTable, "Start Time:", test.getStartTime() != null ? test.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A", labelFont, valueFont);
            addInfoRow(infoTable, "Duration:", test.getDurationMinutes() != null ? test.getDurationMinutes() + " minutes" : "N/A", labelFont, valueFont);
            addInfoRow(infoTable, "Exam Center:", admitCard.getCenterName() != null ? admitCard.getCenterName() : "TBD", labelFont, valueFont);
            addInfoRow(infoTable, "Building:", admitCard.getBuildingName() != null ? admitCard.getBuildingName() : "TBD", labelFont, valueFont);
            addInfoRow(infoTable, "Room:", admitCard.getRoomName() != null ? admitCard.getRoomName() : "TBD", labelFont, valueFont);
            addInfoRow(infoTable, "Seat Number:", admitCard.getSeatNumber() != null ? admitCard.getSeatNumber() : "TBD", labelFont, valueFont);

            document.add(infoTable);

            document.add(new Paragraph("\n"));
            Paragraph qrTitle = new Paragraph("Scan QR Code for Verification", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
            qrTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(qrTitle);

            try {
                byte[] qrImage = generateQrCodeImage(admitCard.getAdmitCardNumber(), 150, 150);
                Image qrImageObj = Image.getInstance(qrImage);
                qrImageObj.setAlignment(Element.ALIGN_CENTER);
                qrImageObj.scaleAbsolute(120, 120);
                document.add(qrImageObj);
            } catch (WriterException | IOException e) {
                Paragraph qrError = new Paragraph("QR Code: " + admitCard.getAdmitCardNumber(), subFont);
                qrError.setAlignment(Element.ALIGN_CENTER);
                document.add(qrError);
            }

            document.add(new Paragraph("\n"));
            Paragraph instrTitle = new Paragraph("Instructions:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
            document.add(instrTitle);

            Font instrFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            document.add(new Paragraph("1. Bring this admit card and a valid photo ID to the exam center.", instrFont));
            document.add(new Paragraph("2. Arrive at least 30 minutes before the test start time.", instrFont));
            document.add(new Paragraph("3. No electronic devices (phones, calculators) are allowed.", instrFont));
            document.add(new Paragraph("4. Follow all instructions from exam invigilators.", instrFont));
            document.add(new Paragraph("5. This admit card must be presented at the entry gate.", instrFont));

            if (test.getInstructions() != null && !test.getInstructions().isEmpty()) {
                document.add(new Paragraph("\n"));
                Paragraph testInstr = new Paragraph("Additional Instructions:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10));
                document.add(testInstr);
                document.add(new Paragraph(test.getInstructions(), instrFont));
            }

            document.add(new Paragraph("\n"));
            PdfPTable footerLineTable = new PdfPTable(1);
            footerLineTable.setWidthPercentage(100);
            PdfPCell footerLineCell = new PdfPCell();
            footerLineCell.setBorder(PdfPCell.BOTTOM);
            footerLineCell.setBorderColor(Color.GRAY);
            footerLineCell.setBorderWidth(1);
            footerLineCell.setFixedHeight(3);
            footerLineTable.addCell(footerLineCell);
            document.add(footerLineTable);
            Paragraph footer = new Paragraph("Generated: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")), subFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            writer.close();

        } catch (DocumentException e) {
            throw new BusinessException("Failed to generate admit card PDF: " + e.getMessage());
        }

        return baos.toByteArray();
    }

    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setPadding(4);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setPadding(4);
        valueCell.setBackgroundColor(new Color(245, 245, 245));

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private byte[] generateQrCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }
}
