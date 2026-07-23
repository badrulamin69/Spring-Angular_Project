package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.PreAdmissionRegistration;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.badrulamin.University_Management.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class RegistrationPdfService {

    @Autowired
    private QrCodeService qrCodeService;

    private static final Color PRIMARY = new Color(30, 64, 175);
    private static final Color PRIMARY_DARK = new Color(29, 78, 216);
    private static final Color HEADER_BG = new Color(30, 64, 175);
    private static final Color LIGHT_BG = new Color(248, 250, 252);
    private static final Color TEXT_DARK = new Color(30, 41, 59);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color ACCENT_BG = new Color(239, 246, 255);
    private static final Color SUCCESS_BG = new Color(240, 253, 244);
    private static final Color SUCCESS_BORDER = new Color(134, 239, 172);
    private static final Color SUCCESS_TEXT = new Color(22, 101, 52);
    private static final Color WARNING_BG = new Color(254, 243, 199);
    private static final Color WARNING_BORDER = new Color(252, 211, 77);
    private static final Color WARNING_TEXT = new Color(146, 64, 14);
    private static final Color WATERMARK_COLOR = new Color(226, 232, 240);

    public byte[] generateRegistrationReceiptPdf(PreAdmissionRegistration reg) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 35, 35, 25, 25);
        PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);
        pdfWriter.setPageEvent(new WatermarkPageEvent("PRE-ADMISSION"));

        document.open();

        addHeader(document, reg);
        addRegistrationInfoBox(document, reg);
        addPhotoQrCodeBarcodeRow(document, reg);
        addPersonalInfo(document, reg);
        addAcademicInfo(document, reg);
        addAcademicProgram(document, reg);
        addPaymentStatus(document, reg);
        addInstructions(document);
        addFooter(document, reg);

        document.close();
        return baos.toByteArray();
    }

    private void addHeader(Document document, PreAdmissionRegistration reg) {
        PdfPTable outerTable = new PdfPTable(1);
        outerTable.setWidthPercentage(100);

        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(HEADER_BG);
        headerCell.setPadding(20);
        headerCell.setBorder(Rectangle.NO_BORDER);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font logoFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.WHITE);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 11, new Color(191, 219, 254));

        Paragraph logoText = new Paragraph("SMART UNIVERSITY", logoFont);
        logoText.setAlignment(Element.ALIGN_CENTER);
        logoText.setSpacingAfter(6);
        headerCell.addElement(logoText);

        Paragraph title = new Paragraph("PRE-ADMISSION REGISTRATION", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(4);
        headerCell.addElement(title);

        Paragraph subtitle = new Paragraph("Registration Confirmation Receipt", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(2);
        headerCell.addElement(subtitle);

        String dateStr = reg.getCreatedAt() != null
                ? reg.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a"))
                : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a"));
        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(191, 219, 254));
        Paragraph dateLine = new Paragraph("Generated: " + dateStr, dateFont);
        dateLine.setAlignment(Element.ALIGN_CENTER);
        headerCell.addElement(dateLine);

        outerTable.addCell(headerCell);
        document.add(outerTable);
        document.add(Chunk.NEWLINE);
    }

    private void addRegistrationInfoBox(Document document, PreAdmissionRegistration reg) {
        PdfPTable boxTable = new PdfPTable(1);
        boxTable.setWidthPercentage(100);
        boxTable.setSpacingAfter(10);

        PdfPCell boxCell = new PdfPCell();
        boxCell.setBackgroundColor(ACCENT_BG);
        boxCell.setBorderColor(PRIMARY);
        boxCell.setBorderWidth(1);
        boxCell.setPadding(12);

        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{25, 25, 25, 25});

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_MUTED);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK);

        addCompactCell(infoTable, "Registration No", reg.getRegistrationNumber(), labelFont, valueFont);
        addCompactCell(infoTable, "Applicant ID", reg.getRegistrationNumber(), labelFont, valueFont);
        addCompactCell(infoTable, "Tracking No", reg.getTrackingNumber() != null ? reg.getTrackingNumber() : "N/A", labelFont, valueFont);
        addCompactCell(infoTable, "Status", reg.getStatus() != null ? reg.getStatus() : "SUBMITTED", labelFont, valueFont);

        boxCell.addElement(infoTable);
        boxTable.addCell(boxCell);
        document.add(boxTable);
    }

    private void addPhotoQrCodeBarcodeRow(Document document, PreAdmissionRegistration reg) {
        PdfPTable mainTable = new PdfPTable(3);
        mainTable.setWidthPercentage(100);
        mainTable.setWidths(new float[]{34, 33, 33});
        mainTable.setSpacingAfter(10);

        PdfPCell photoCell = createBorderedCell();
        photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, TEXT_MUTED);
        Paragraph photoLabel = new Paragraph("PHOTOGRAPH", labelFont);
        photoLabel.setAlignment(Element.ALIGN_CENTER);
        photoLabel.setSpacingAfter(4);
        photoCell.addElement(photoLabel);

        if (reg.getPhotoUrl() != null && !reg.getPhotoUrl().isEmpty()) {
            try {
                String base64Data = reg.getPhotoUrl().contains(",")
                        ? reg.getPhotoUrl().substring(reg.getPhotoUrl().indexOf(",") + 1)
                        : reg.getPhotoUrl();
                byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Data);
                Image photo = Image.getInstance(imageBytes);
                photo.scaleToFit(100, 120);
                photo.setAlignment(Element.ALIGN_CENTER);
                photoCell.addElement(photo);
            } catch (Exception e) {
                photoCell.addElement(new Phrase("[Photo]", FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED)));
            }
        } else {
            photoCell.addElement(new Phrase("[No Photo]", FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED)));
        }
        mainTable.addCell(photoCell);

        PdfPCell qrCell = createBorderedCell();
        qrCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        qrCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph qrLabel = new Paragraph("SCAN TO CHECK STATUS", labelFont);
        qrLabel.setAlignment(Element.ALIGN_CENTER);
        qrLabel.setSpacingAfter(4);
        qrCell.addElement(qrLabel);

        try {
            byte[] qrBytes = qrCodeService.generateRegistrationQrCode(reg.getRegistrationNumber());
            if (qrBytes != null) {
                Image qrImage = Image.getInstance(qrBytes);
                qrImage.scaleToFit(110, 110);
                qrImage.setAlignment(Element.ALIGN_CENTER);
                qrCell.addElement(qrImage);
            } else {
                qrCell.addElement(new Phrase("[QR Code]", FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED)));
            }
        } catch (Exception e) {
            qrCell.addElement(new Phrase("[QR Code]", FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED)));
        }
        mainTable.addCell(qrCell);

        PdfPCell barcodeCell = createBorderedCell();
        barcodeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        barcodeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph barcodeLabel = new Paragraph("BARCODE", labelFont);
        barcodeLabel.setAlignment(Element.ALIGN_CENTER);
        barcodeLabel.setSpacingAfter(4);
        barcodeCell.addElement(barcodeLabel);

        try {
            byte[] barcodeBytes = qrCodeService.generateBarcode(reg.getRegistrationNumber(), 200, 60);
            if (barcodeBytes != null) {
                Image barcodeImage = Image.getInstance(barcodeBytes);
                barcodeImage.scaleToFit(180, 50);
                barcodeImage.setAlignment(Element.ALIGN_CENTER);
                barcodeCell.addElement(barcodeImage);

                Font barcodeNumFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_DARK);
                Paragraph barcodeNum = new Paragraph(reg.getRegistrationNumber(), barcodeNumFont);
                barcodeNum.setAlignment(Element.ALIGN_CENTER);
                barcodeNum.setSpacingBefore(4);
                barcodeCell.addElement(barcodeNum);
            } else {
                barcodeCell.addElement(new Phrase("[Barcode]", FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED)));
            }
        } catch (Exception e) {
            barcodeCell.addElement(new Phrase("[Barcode]", FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED)));
        }
        mainTable.addCell(barcodeCell);

        document.add(mainTable);
    }

    private void addPersonalInfo(Document document, PreAdmissionRegistration reg) {
        addSectionHeader(document, "PERSONAL INFORMATION");

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{50, 50});
        table.setSpacingAfter(8);

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK);

        addInfoCell(table, "Full Name", reg.getFirstName() + " " + reg.getLastName(), labelFont, valueFont);
        addInfoCell(table, "Email", reg.getEmail() != null ? reg.getEmail() : "N/A", labelFont, valueFont);
        addInfoCell(table, "Phone", reg.getPhone() != null ? reg.getPhone() : "N/A", labelFont, valueFont);
        addInfoCell(table, "Date of Birth", reg.getDateOfBirth() != null ? reg.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) : "N/A", labelFont, valueFont);
        addInfoCell(table, "Gender", reg.getGender() != null ? reg.getGender() : "N/A", labelFont, valueFont);
        addInfoCell(table, "Blood Group", reg.getBloodGroup() != null ? reg.getBloodGroup() : "N/A", labelFont, valueFont);
        addInfoCell(table, "Father Name", reg.getFatherName() != null ? reg.getFatherName() : "N/A", labelFont, valueFont);
        addInfoCell(table, "Mother Name", reg.getMotherName() != null ? reg.getMotherName() : "N/A", labelFont, valueFont);
        addInfoCell(table, "Guardian Phone", reg.getGuardianPhone() != null ? reg.getGuardianPhone() : "N/A", labelFont, valueFont);

        PdfPCell addrLabel = new PdfPCell(new Phrase("Address", labelFont));
        addrLabel.setBackgroundColor(LIGHT_BG);
        addrLabel.setPadding(8);
        addrLabel.setBorder(Rectangle.NO_BORDER);
        addrLabel.setBorderColorBottom(BORDER);
        addrLabel.setBorderWidthBottom(0.5f);
        addrLabel.setBorderColorRight(BORDER);
        addrLabel.setBorderWidthRight(0.5f);
        table.addCell(addrLabel);

        PdfPCell addrValue = new PdfPCell(new Phrase(reg.getAddress() != null ? reg.getAddress() : "N/A", valueFont));
        addrValue.setPadding(8);
        addrValue.setBorder(Rectangle.NO_BORDER);
        addrValue.setBorderColorBottom(BORDER);
        addrValue.setBorderWidthBottom(0.5f);
        table.addCell(addrValue);

        document.add(table);
    }

    private void addAcademicInfo(Document document, PreAdmissionRegistration reg) {
        addSectionHeader(document, "ACADEMIC INFORMATION");

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{50, 50});
        table.setSpacingAfter(8);

        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, TEXT_DARK);

        addInfoCell(table, "SSC GPA", reg.getSscGpa() != null ? String.valueOf(reg.getSscGpa()) : "N/A", labelFont, valueFont);
        addInfoCell(table, "SSC Passing Year", reg.getSscYear() != null ? String.valueOf(reg.getSscYear()) : "N/A", labelFont, valueFont);
        addInfoCell(table, "SSC Board", reg.getSscBoard() != null ? reg.getSscBoard() : "N/A", labelFont, valueFont);
        addInfoCell(table, "HSC GPA", reg.getHscGpa() != null ? String.valueOf(reg.getHscGpa()) : "N/A", labelFont, valueFont);
        addInfoCell(table, "HSC Passing Year", reg.getHscYear() != null ? String.valueOf(reg.getHscYear()) : "N/A", labelFont, valueFont);
        addInfoCell(table, "HSC Board", reg.getHscBoard() != null ? reg.getHscBoard() : "N/A", labelFont, valueFont);

        document.add(table);
    }

    private void addAcademicProgram(Document document, PreAdmissionRegistration reg) {
        addSectionHeader(document, "ACADEMIC PROGRAM");

        PdfPTable prefTable = new PdfPTable(3);
        prefTable.setWidthPercentage(100);
        prefTable.setWidths(new float[]{33, 33, 34});
        prefTable.setSpacingAfter(8);

        addPreferenceCell(prefTable, "1st Preference", reg.getProgramPreference1());
        addPreferenceCell(prefTable, "2nd Preference", reg.getProgramPreference2());
        addPreferenceCell(prefTable, "3rd Preference", reg.getProgramPreference3());

        document.add(prefTable);
    }

    private void addPaymentStatus(Document document, PreAdmissionRegistration reg) {
        PdfPTable payTable = new PdfPTable(1);
        payTable.setWidthPercentage(100);
        payTable.setSpacingAfter(10);

        PdfPCell payCell = new PdfPCell();
        payCell.setBackgroundColor(WARNING_BG);
        payCell.setBorderColor(WARNING_BORDER);
        payCell.setBorderWidth(1);
        payCell.setPadding(10);

        Font payLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, WARNING_TEXT);
        Font payValueFont = FontFactory.getFont(FontFactory.HELVETICA, 9, WARNING_TEXT);

        PdfPTable payInfo = new PdfPTable(2);
        payInfo.setWidthPercentage(100);
        payInfo.setWidths(new float[]{30, 70});

        PdfPCell labelCell = new PdfPCell(new Phrase("Payment Status", payLabelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(4);
        payInfo.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase("PENDING — To be paid at the time of admission", payValueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(4);
        payInfo.addCell(valueCell);

        payCell.addElement(payInfo);
        payTable.addCell(payCell);
        document.add(payTable);
    }

    private void addInstructions(Document document) {
        addSectionHeader(document, "IMPORTANT INSTRUCTIONS");

        PdfPTable instrTable = new PdfPTable(1);
        instrTable.setWidthPercentage(100);
        instrTable.setSpacingAfter(10);

        PdfPCell instrCell = new PdfPCell();
        instrCell.setBackgroundColor(SUCCESS_BG);
        instrCell.setBorderColor(SUCCESS_BORDER);
        instrCell.setBorderWidth(1);
        instrCell.setPadding(12);

        Font instrFont = FontFactory.getFont(FontFactory.HELVETICA, 9, SUCCESS_TEXT);

        String[] instructions = {
            "1. This is a computer-generated registration confirmation receipt.",
            "2. Keep your Registration Number and Tracking Number safe for future reference.",
            "3. Use your login credentials (email and password) to access the student portal.",
            "4. Download your admit card from the portal after your application is approved.",
            "5. Bring this receipt and a valid photo ID to the examination center.",
            "6. Report to the examination center at least 30 minutes before the exam.",
            "7. Electronic devices (mobile phones, calculators) are strictly prohibited during the exam.",
            "8. Contact the admission office for any queries regarding your application."
        };

        for (String instruction : instructions) {
            Paragraph p = new Paragraph(instruction, instrFont);
            p.setSpacingAfter(3);
            instrCell.addElement(p);
        }

        instrTable.addCell(instrCell);
        document.add(instrTable);
    }

    private void addFooter(Document document, PreAdmissionRegistration reg) {
        document.add(Chunk.NEWLINE);

        PdfPTable sigTable = new PdfPTable(2);
        sigTable.setWidthPercentage(100);
        sigTable.setWidths(new float[]{50, 50});
        sigTable.setSpacingAfter(12);

        PdfPCell leftSig = new PdfPCell();
        leftSig.setBorder(Rectangle.NO_BORDER);
        leftSig.setPadding(8);

        Font sigLabelFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_MUTED);
        Font sigLineFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_DARK);

        Paragraph leftLine = new Paragraph("_________________________", sigLineFont);
        leftLine.setAlignment(Element.ALIGN_CENTER);
        leftSig.addElement(leftLine);

        Paragraph leftLabel = new Paragraph("Authorized Signature & Seal", sigLabelFont);
        leftLabel.setAlignment(Element.ALIGN_CENTER);
        leftLabel.setSpacingBefore(4);
        leftSig.addElement(leftLabel);

        Paragraph leftDate = new Paragraph("Date: " + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")), sigLabelFont);
        leftDate.setAlignment(Element.ALIGN_CENTER);
        leftDate.setSpacingBefore(2);
        leftSig.addElement(leftDate);

        sigTable.addCell(leftSig);

        PdfPCell rightSig = new PdfPCell();
        rightSig.setBorder(Rectangle.NO_BORDER);
        rightSig.setPadding(8);

        Paragraph rightLine = new Paragraph("_________________________", sigLineFont);
        rightLine.setAlignment(Element.ALIGN_CENTER);
        rightSig.addElement(rightLine);

        Paragraph rightLabel = new Paragraph("Applicant Signature", sigLabelFont);
        rightLabel.setAlignment(Element.ALIGN_CENTER);
        rightLabel.setSpacingBefore(4);
        rightSig.addElement(rightLabel);

        Paragraph rightDate = new Paragraph("Date: " + java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")), sigLabelFont);
        rightDate.setAlignment(Element.ALIGN_CENTER);
        rightDate.setSpacingBefore(2);
        rightSig.addElement(rightDate);

        sigTable.addCell(rightSig);
        document.add(sigTable);

        PdfPTable footerTable = new PdfPTable(1);
        footerTable.setWidthPercentage(100);

        PdfPCell footerCell = new PdfPCell();
        footerCell.setBorder(Rectangle.NO_BORDER);
        footerCell.setBorderColorTop(BORDER);
        footerCell.setBorderWidthTop(1);
        footerCell.setPadding(8);
        footerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, TEXT_MUTED);
        Paragraph footerLine = new Paragraph("This is a computer-generated document. No physical signature is required.", footerFont);
        footerLine.setAlignment(Element.ALIGN_CENTER);
        footerCell.addElement(footerLine);

        Paragraph printDate = new Paragraph("Print Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a")), footerFont);
        printDate.setAlignment(Element.ALIGN_CENTER);
        printDate.setSpacingBefore(2);
        footerCell.addElement(printDate);

        Paragraph refLine = new Paragraph("Ref: " + reg.getRegistrationNumber() + " | " + (reg.getTrackingNumber() != null ? reg.getTrackingNumber() : ""), footerFont);
        refLine.setAlignment(Element.ALIGN_CENTER);
        refLine.setSpacingBefore(2);
        footerCell.addElement(refLine);

        footerTable.addCell(footerCell);
        document.add(footerTable);
    }

    private void addSectionHeader(Document document, String title) {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, PRIMARY);
        Paragraph section = new Paragraph(title, sectionFont);
        section.setSpacingAfter(2);
        document.add(section);

        LineSeparator ls = new LineSeparator();
        ls.setLineColor(PRIMARY);
        ls.setLineWidth(0.5f);
        document.add(new Chunk(ls));
        document.add(Chunk.NEWLINE);
    }

    private PdfPCell createBorderedCell() {
        PdfPCell cell = new PdfPCell();
        cell.setBorderColor(BORDER);
        cell.setBorderWidth(1);
        cell.setPadding(8);
        cell.setFixedHeight(140);
        return cell;
    }

    private void addInfoCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(LIGHT_BG);
        labelCell.setPadding(7);
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setBorderColorBottom(BORDER);
        labelCell.setBorderWidthBottom(0.5f);
        labelCell.setBorderColorRight(BORDER);
        labelCell.setBorderWidthRight(0.5f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(7);
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setBorderColorBottom(BORDER);
        valueCell.setBorderWidthBottom(0.5f);
        table.addCell(valueCell);
    }

    private void addCompactCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(3);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(3);
        table.addCell(valueCell);
    }

    private void addPreferenceCell(PdfPTable table, String rank, String program) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(ACCENT_BG);
        cell.setBorderColor(PRIMARY);
        cell.setBorderWidth(0.5f);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font rankFont = FontFactory.getFont(FontFactory.HELVETICA, 8, PRIMARY);
        Font progFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, PRIMARY);

        Paragraph p = new Paragraph();
        p.add(new Chunk(rank + "\n", rankFont));
        p.add(new Chunk(program != null ? program : "N/A", progFont));
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);

        table.addCell(cell);
    }

    private static class WatermarkPageEvent extends PdfPageEventHelper {
        private final String watermarkText;
        private final BaseFont baseFont;

        public WatermarkPageEvent(String watermarkText) {
            this.watermarkText = watermarkText;
            try {
                this.baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false);
            } catch (Exception e) {
                throw new BusinessException("Failed to load watermark font: " + e.getMessage());
            }
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.saveState();
            canvas.setColorFill(WATERMARK_COLOR);
            canvas.setFontAndSize(baseFont, 60);

            float x = document.getPageSize().getWidth() / 2;
            float y = document.getPageSize().getHeight() / 2;

            canvas.beginText();
            canvas.showTextAligned(Element.ALIGN_CENTER, watermarkText, x, y, 45);
            canvas.endText();
            canvas.restoreState();
        }
    }
}