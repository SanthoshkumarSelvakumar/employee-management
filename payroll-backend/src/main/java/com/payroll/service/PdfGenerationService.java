package com.payroll.service;

import com.payroll.entity.Employee;
import com.payroll.entity.Payslip;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class PdfGenerationService {

    private static final float MARGIN = 50;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();

    public byte[] generatePayslipPdf(Payslip payslip) throws IOException {
        Employee employee = payslip.getEmployee();
        String monthName = Month.of(payslip.getPayPeriodMonth())
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float y = PDRectangle.A4.getHeight() - MARGIN;

                // Company Header
                y = drawCenteredText(content, "PAYROLL SYSTEM", fontBold, 18, y); //ignorei18n_start //ignorei18n_end
                y -= 5;
                y = drawCenteredText(content, "Payslip", fontRegular, 12, y); //ignorei18n_start //ignorei18n_end
                y -= 20;

                // Separator line
                content.setLineWidth(1f);
                content.moveTo(MARGIN, y);
                content.lineTo(PAGE_WIDTH - MARGIN, y);
                content.stroke();
                y -= 25;

                // Pay Period
                y = drawText(content, "Pay Period: " + monthName + " " + payslip.getPayPeriodYear(), //ignorei18n_start //ignorei18n_end
                        fontBold, 11, MARGIN, y);
                y -= 20;

                // Employee Details Section
                y = drawText(content, "EMPLOYEE DETAILS", fontBold, 11, MARGIN, y); //ignorei18n_start //ignorei18n_end
                y -= 5;
                content.moveTo(MARGIN, y);
                content.lineTo(PAGE_WIDTH - MARGIN, y);
                content.stroke();
                y -= 18;

                y = drawLabelValue(content, fontRegular, "Employee Code:", //ignorei18n_start //ignorei18n_end
                        employee.getEmployeeCode(), MARGIN, y);
                y = drawLabelValue(content, fontRegular, "Name:", //ignorei18n_start //ignorei18n_end
                        employee.getFirstName() + " " + employee.getLastName(), MARGIN, y); //ignorei18n_start //ignorei18n_end
                y = drawLabelValue(content, fontRegular, "Department:", //ignorei18n_start //ignorei18n_end
                        employee.getDepartment() != null ? employee.getDepartment().getName() : "N/A", MARGIN, y); //ignorei18n_start //ignorei18n_end
                y = drawLabelValue(content, fontRegular, "Designation:", //ignorei18n_start //ignorei18n_end
                        employee.getDesignation() != null ? employee.getDesignation() : "N/A", MARGIN, y); //ignorei18n_start //ignorei18n_end
                y -= 20;

                // Earnings Section
                y = drawText(content, "EARNINGS", fontBold, 11, MARGIN, y); //ignorei18n_start //ignorei18n_end
                y -= 5;
                content.moveTo(MARGIN, y);
                content.lineTo(PAGE_WIDTH - MARGIN, y);
                content.stroke();
                y -= 18;

                y = drawAmountRow(content, fontRegular, "Basic Salary", //ignorei18n_start //ignorei18n_end
                        payslip.getBasicSalary(), MARGIN, y);
                y = drawAmountRow(content, fontRegular, "House Rent Allowance (HRA)", //ignorei18n_start //ignorei18n_end
                        payslip.getHra(), MARGIN, y);
                y = drawAmountRow(content, fontRegular, "Other Allowances", //ignorei18n_start //ignorei18n_end
                        payslip.getAllowances(), MARGIN, y);
                y -= 5;
                y = drawAmountRow(content, fontBold, "Total Earnings", //ignorei18n_start //ignorei18n_end
                        payslip.getTotalEarnings(), MARGIN, y);
                y -= 20;

                // Deductions Section
                y = drawText(content, "DEDUCTIONS", fontBold, 11, MARGIN, y); //ignorei18n_start //ignorei18n_end
                y -= 5;
                content.moveTo(MARGIN, y);
                content.lineTo(PAGE_WIDTH - MARGIN, y);
                content.stroke();
                y -= 18;

                y = drawAmountRow(content, fontRegular, "Provident Fund (PF)", //ignorei18n_start //ignorei18n_end
                        payslip.getPfDeduction(), MARGIN, y);
                y = drawAmountRow(content, fontRegular, "Tax Deduction", //ignorei18n_start //ignorei18n_end
                        payslip.getTaxDeduction(), MARGIN, y);
                y = drawAmountRow(content, fontRegular, "Insurance", //ignorei18n_start //ignorei18n_end
                        payslip.getInsuranceDeduction(), MARGIN, y);
                y -= 5;
                y = drawAmountRow(content, fontBold, "Total Deductions", //ignorei18n_start //ignorei18n_end
                        payslip.getTotalDeductions(), MARGIN, y);
                y -= 25;

                // Net Pay
                content.setLineWidth(2f);
                content.moveTo(MARGIN, y);
                content.lineTo(PAGE_WIDTH - MARGIN, y);
                content.stroke();
                y -= 20;
                y = drawAmountRow(content, fontBold, "NET PAY", //ignorei18n_start //ignorei18n_end
                        payslip.getNetPay(), MARGIN, y);
                y -= 5;
                content.setLineWidth(2f);
                content.moveTo(MARGIN, y);
                content.lineTo(PAGE_WIDTH - MARGIN, y);
                content.stroke();
                y -= 40;

                // Footer
                drawText(content, "This is a system-generated document.", //ignorei18n_start //ignorei18n_end
                        fontRegular, 9, MARGIN, y);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    private float drawCenteredText(PDPageContentStream content, String text,
                                   PDType1Font font, float fontSize, float y) throws IOException {
        float textWidth = font.getStringWidth(text) / 1000 * fontSize;
        float x = (PAGE_WIDTH - textWidth) / 2;
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
        return y - fontSize - 4;
    }

    private float drawText(PDPageContentStream content, String text,
                           PDType1Font font, float fontSize, float x, float y) throws IOException {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
        return y - fontSize - 4;
    }

    private float drawLabelValue(PDPageContentStream content, PDType1Font font,
                                 String label, String value, float x, float y) throws IOException {
        content.beginText();
        content.setFont(font, 10);
        content.newLineAtOffset(x, y);
        content.showText(label);
        content.endText();

        content.beginText();
        content.setFont(font, 10);
        content.newLineAtOffset(x + 120, y);
        content.showText(value);
        content.endText();

        return y - 16;
    }

    private float drawAmountRow(PDPageContentStream content, PDType1Font font,
                                String label, BigDecimal amount, float x, float y) throws IOException {
        content.beginText();
        content.setFont(font, 10);
        content.newLineAtOffset(x, y);
        content.showText(label);
        content.endText();

        String amountStr = String.format("₹ %,.2f", amount); //ignorei18n_start //ignorei18n_end
        float amountWidth = font.getStringWidth(amountStr) / 1000 * 10;
        content.beginText();
        content.setFont(font, 10);
        content.newLineAtOffset(PAGE_WIDTH - MARGIN - amountWidth, y);
        content.showText(amountStr);
        content.endText();

        return y - 16;
    }
}
