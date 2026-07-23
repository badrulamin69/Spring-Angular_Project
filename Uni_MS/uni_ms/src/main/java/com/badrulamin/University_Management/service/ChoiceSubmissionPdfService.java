package com.badrulamin.University_Management.service;

import com.badrulamin.University_Management.exception.BusinessException;
import com.badrulamin.University_Management.entity.ApplicantChoice;
import com.badrulamin.University_Management.entity.ApplicantChoiceSubmission;
import com.badrulamin.University_Management.entity.ChoiceFillingConfig;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChoiceSubmissionPdfService {

    private static final Color PRIMARY = new Color(0, 102, 153);
    private static final Color HEADER_BG = new Color(0, 102, 153);
    private static final Color LIGHT_BG = new Color(240, 248, 255);

    public byte[] generateAdminSubmissionsReport(List<ApplicantChoiceSubmission> submissions,
                                                  ChoiceFillingConfig config) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY);
            Paragraph title = new Paragraph("Choice Submissions Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Paragraph subtitle = new Paragraph("Session: " + config.getSession().getName() +
                    " | Deadline: " + config.getChoiceEndDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")), subFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(15);
            document.add(subtitle);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{5, 15, 20, 15, 15, 10, 20});

            addTableHeader(table);
            addTableRows(table, submissions);

            document.add(table);

            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
            Paragraph footer = new Paragraph("Generated: " + java.time.LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")), footerFont);
            footer.setSpacingBefore(10);
            document.add(footer);

            document.close();
        } catch (DocumentException e) {
            throw new BusinessException("Error generating PDF report: " + e.getMessage());
        }
        return baos.toByteArray();
    }

    public byte[] generateApplicantChoicesPdf(ApplicantChoiceSubmission submission,
                                               List<ApplicantChoice> choices) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 30, 30, 30, 30);

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, PRIMARY);
            Paragraph title = new Paragraph("My Choice List", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5);
            document.add(title);

            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
            Paragraph subtitle = new Paragraph("Submission ID: " + submission.getSubmissionId(), subFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(15);
            document.add(subtitle);

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(80);
            infoTable.setHorizontalAlignment(Element.ALIGN_CENTER);
            infoTable.setWidths(new float[]{35, 65});

            addInfoRow(infoTable, "Applicant Name", submission.getApplicantName());
            addInfoRow(infoTable, "Status", submission.getStatus());
            if (submission.getMeritRank() != null) {
                addInfoRow(infoTable, "Merit Rank", String.valueOf(submission.getMeritRank()));
            }
            if (submission.getMeritScore() != null) {
                addInfoRow(infoTable, "Merit Score", String.format("%.2f", submission.getMeritScore()));
            }
            addInfoRow(infoTable, "Total Choices", String.valueOf(submission.getTotalChoices()));
            if (submission.getSubmittedAt() != null) {
                addInfoRow(infoTable, "Submitted At",
                        submission.getSubmittedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
            }

            Paragraph spacer = new Paragraph();
            spacer.setSpacingAfter(10);
            document.add(spacer);
            document.add(infoTable);

            Paragraph choiceHeader = new Paragraph("Selected Programs", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, PRIMARY));
            choiceHeader.setSpacingBefore(15);
            choiceHeader.setSpacingAfter(10);
            document.add(choiceHeader);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{8, 25, 30, 20, 17});

            addChoiceTableHeader(table);
            addChoiceTableRows(table, choices);

            document.add(table);

            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
            Paragraph footer = new Paragraph("Generated: " + java.time.LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")), footerFont);
            footer.setSpacingBefore(10);
            document.add(footer);

            document.close();
        } catch (DocumentException e) {
            throw new BusinessException("Error generating applicant choices PDF: " + e.getMessage());
        }
        return baos.toByteArray();
    }

    private void addTableHeader(PdfPTable table) {
        String[] headers = {"#", "Submission ID", "Applicant", "Merit Rank", "Score", "Status", "Submitted At"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
            cell.setBackgroundColor(HEADER_BG);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    private void addTableRows(PdfPTable table, List<ApplicantChoiceSubmission> submissions) {
        int rowNum = 1;
        for (ApplicantChoiceSubmission sub : submissions) {
            Color bg = rowNum % 2 == 0 ? LIGHT_BG : Color.WHITE;
            addCell(table, String.valueOf(rowNum), bg);
            addCell(table, sub.getSubmissionId() != null ? sub.getSubmissionId() : "-", bg);
            addCell(table, sub.getApplicantName() != null ? sub.getApplicantName() : "-", bg);
            addCell(table, sub.getMeritRank() != null ? String.valueOf(sub.getMeritRank()) : "-", bg);
            addCell(table, sub.getMeritScore() != null ? String.format("%.2f", sub.getMeritScore()) : "-", bg);
            addCell(table, sub.getStatus(), bg);
            addCell(table, sub.getSubmittedAt() != null ?
                    sub.getSubmittedAt().format(DateTimeFormatter.ofPattern("dd MMM HH:mm")) : "-", bg);
            rowNum++;
        }
    }

    private void addChoiceTableHeader(PdfPTable table) {
        String[] headers = {"Priority", "Program", "Faculty", "Department", "Status"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE)));
            cell.setBackgroundColor(HEADER_BG);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    private void addChoiceTableRows(PdfPTable table, List<ApplicantChoice> choices) {
        for (ApplicantChoice choice : choices) {
            Color bg = choices.indexOf(choice) % 2 == 0 ? LIGHT_BG : Color.WHITE;
            addCell(table, String.valueOf(choice.getPriority()), bg);
            addCell(table, choice.getProgramName() != null ? choice.getProgramName() : "-", bg);
            addCell(table, choice.getFacultyName() != null ? choice.getFacultyName() : "-", bg);
            addCell(table, choice.getDepartmentName() != null ? choice.getDepartmentName() : "-", bg);
            addCell(table, choice.getStatus(), bg);
        }
    }

    private void addInfoRow(PdfPTable table, String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setPadding(5);
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addCell(PdfPTable table, String text, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        table.addCell(cell);
    }
}
