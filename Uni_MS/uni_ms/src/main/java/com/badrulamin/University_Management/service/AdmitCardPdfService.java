package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.entity.PreAdmissionRegistration;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

@Service
public class AdmitCardPdfService {

    private static final Color PRIMARY = new Color(30, 64, 175);
    private static final Color LIGHT_BG = new Color(248, 250, 252);
    private static final Color TEXT_DARK = new Color(30, 41, 59);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color ACCENT_BG = new Color(239, 246, 255);
    private static final Color WARNING_BG = new Color(254, 243, 199);
    private static final Color WARNING_BORDER = new Color(252, 211, 77);
    private static final Color WARNING_TEXT = new Color(120, 53, 15);

    public byte[] generateAdmitCardPdf(PreAdmissionRegistration reg) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 30, 30);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        String seatNo = "SEAT-" + reg.getRegistrationNumber().replace("PRE-ADM-", "");

        addHeader(document, reg.getRegistrationNumber());
        addBarcode(document, reg.getRegistrationNumber());
        addPersonalInfo(document, reg, seatNo);
        addProgramPreferences(document, reg);
        addInstructions(document);
        addFooter(document);

        document.close();
        return baos.toByteArray();
    }

    private void addHeader(Document document, String registrationNumber) {
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(PRIMARY);
        cell.setPadding(20);
        cell.setBorderColor(PRIMARY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.WHITE);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, new Color(255, 255, 255));

        Paragraph title = new Paragraph("UNIVERSITY ADMISSION", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(4);
        cell.addElement(title);

        Paragraph subtitle = new Paragraph("Admit Card - Entrance Examination", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(subtitle);

        headerTable.addCell(cell);
        document.add(headerTable);
        document.add(Chunk.NEWLINE);
    }

    private void addBarcode(Document document, String registrationNumber) {
        PdfPTable barcodeTable = new PdfPTable(1);
        barcodeTable.setWidthPercentage(100);

        Font barcodeFont = FontFactory.getFont(FontFactory.COURIER, 22, TEXT_DARK);
        Font barcodeLabelFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED);

        PdfPCell codeCell = new PdfPCell(new Phrase("||| " + registrationNumber + " |||", barcodeFont));
        codeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        codeCell.setBorder(Rectangle.NO_BORDER);
        codeCell.setBackgroundColor(LIGHT_BG);
        codeCell.setPadding(12);
        barcodeTable.addCell(codeCell);

        PdfPCell labelCell = new PdfPCell(new Phrase("REGISTRATION NUMBER", barcodeLabelFont));
        labelCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setBackgroundColor(LIGHT_BG);
        labelCell.setPaddingBottom(8);
        barcodeTable.addCell(labelCell);

        document.add(barcodeTable);

        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BORDER);
        separator.setLineWidth(0.5f);
        document.add(new Chunk(separator));
        document.add(Chunk.NEWLINE);
    }

    private void addPersonalInfo(Document document, PreAdmissionRegistration reg, String seatNo) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, TEXT_DARK);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{50, 50});
        table.setSpacingAfter(12);

        addInfoCell(table, "Full Name", reg.getFirstName() + " " + reg.getLastName(), labelFont, valueFont);
        addInfoCell(table, "Registration No", reg.getRegistrationNumber(), labelFont, valueFont);
        addInfoCell(table, "Email", reg.getEmail() != null ? reg.getEmail() : "N/A", labelFont, valueFont);
        addInfoCell(table, "Seat Number", seatNo, labelFont, valueFont);
        addInfoCell(table, "Date of Birth", reg.getDateOfBirth() != null ? reg.getDateOfBirth().toString() : "N/A", labelFont, valueFont);
        addInfoCell(table, "Gender", reg.getGender() != null ? reg.getGender() : "N/A", labelFont, valueFont);
        addInfoCell(table, "SSC GPA", reg.getSscGpa() != null ? String.valueOf(reg.getSscGpa()) : "N/A", labelFont, valueFont);
        addInfoCell(table, "HSC GPA", reg.getHscGpa() != null ? String.valueOf(reg.getHscGpa()) : "N/A", labelFont, valueFont);

        document.add(table);
    }

    private void addInfoCell(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(LIGHT_BG);
        labelCell.setPadding(8);
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setBorderColorBottom(BORDER);
        labelCell.setBorderWidthBottom(0.5f);
        labelCell.setBorderColorRight(BORDER);
        labelCell.setBorderWidthRight(0.5f);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(8);
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setBorderColorBottom(BORDER);
        valueCell.setBorderWidthBottom(0.5f);
        table.addCell(valueCell);
    }

    private void addProgramPreferences(Document document, PreAdmissionRegistration reg) {
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, TEXT_MUTED);
        document.add(new Paragraph("PROGRAM PREFERENCES", sectionFont));

        LineSeparator ls = new LineSeparator();
        ls.setLineColor(BORDER);
        document.add(new Chunk(ls));
        document.add(Chunk.NEWLINE);

        PdfPTable prefTable = new PdfPTable(3);
        prefTable.setWidthPercentage(100);
        prefTable.setWidths(new float[]{33, 33, 34});
        prefTable.setSpacingAfter(12);

        addPreferenceCell(prefTable, "1st Preference", reg.getProgramPreference1());
        addPreferenceCell(prefTable, "2nd Preference", reg.getProgramPreference2());
        addPreferenceCell(prefTable, "3rd Preference", reg.getProgramPreference3());

        document.add(prefTable);
    }

    private void addPreferenceCell(PdfPTable table, String rank, String program) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(ACCENT_BG);
        cell.setBorderColor(PRIMARY);
        cell.setBorderWidth(0.5f);
        cell.setPadding(10);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font rankFont = FontFactory.getFont(FontFactory.HELVETICA, 8, PRIMARY);
        Font progFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, PRIMARY);

        Paragraph p = new Paragraph();
        p.add(new Chunk(rank + "\n", rankFont));
        p.add(new Chunk(program != null ? program : "N/A", progFont));
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);

        table.addCell(cell);
    }

    private void addInstructions(Document document) {
        PdfPTable instrTable = new PdfPTable(1);
        instrTable.setWidthPercentage(100);
        instrTable.setSpacingAfter(12);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(WARNING_BG);
        cell.setBorderColor(WARNING_BORDER);
        cell.setBorderWidth(1);
        cell.setPadding(14);

        Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(146, 64, 14));
        Font itemFont = FontFactory.getFont(FontFactory.HELVETICA, 9, WARNING_TEXT);

        cell.addElement(new Paragraph("Important Instructions", headingFont));
        cell.addElement(Chunk.NEWLINE);

        String[] instructions = {
            "1. Bring this admit card and a valid photo ID to the examination center.",
            "2. Report to the examination center at least 30 minutes before the exam.",
            "3. Electronic devices (mobile phones, calculators) are strictly prohibited.",
            "4. Writing or marking on this admit card will render it invalid.",
            "5. Contact the admission office for any queries."
        };

        for (String instr : instructions) {
            Paragraph p = new Paragraph(instr, itemFont);
            p.setSpacingAfter(4);
            cell.addElement(p);
        }

        instrTable.addCell(cell);
        document.add(instrTable);
    }

    private void addFooter(Document document) {
        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setWidthPercentage(100);
        footerTable.setWidths(new float[]{50, 50});

        Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED);
        Font sigFont = FontFactory.getFont(FontFactory.HELVETICA, 9, TEXT_MUTED);

        PdfPCell dateCell = new PdfPCell(new Phrase("Generated: " + java.time.LocalDate.now(), dateFont));
        dateCell.setBorder(Rectangle.NO_BORDER);
        dateCell.setBorderColorTop(BORDER);
        dateCell.setBorderWidthTop(1);
        dateCell.setPadding(10);
        footerTable.addCell(dateCell);

        PdfPCell sigCell = new PdfPCell();
        sigCell.setBorder(Rectangle.NO_BORDER);
        sigCell.setBorderColorTop(BORDER);
        sigCell.setBorderWidthTop(1);
        sigCell.setPadding(10);
        sigCell.addElement(Chunk.NEWLINE);
        sigCell.addElement(Chunk.NEWLINE);
        sigCell.addElement(new Chunk(new LineSeparator()));
        Paragraph sigLabel = new Paragraph("Authorized Signature & Seal", sigFont);
        sigLabel.setAlignment(Element.ALIGN_CENTER);
        sigCell.addElement(sigLabel);
        footerTable.addCell(sigCell);

        document.add(footerTable);
    }
}
