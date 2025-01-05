package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.Entity.OrderEntity;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import com.itextpdf.layout.*;

@Service
public class SaleReportService {
    public LocalDate getStartOfReport(String reportType) {
        switch (reportType.toUpperCase()) {
            case "ALL" :
                return LocalDate.MIN;
            case "DAILY":
                return LocalDate.now();
            case "WEEKLY":
                return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case "MONTHLY":
                return LocalDate.now().withDayOfMonth(1);
            case "YEARLY":
                return LocalDate.now().withDayOfYear(1);
            default:
                return null; // No filtering if the report type is invalid or null.
        }
    }

    public LocalDate getEndOfReport(String reportType) {
        switch (reportType.toUpperCase()) {
            case "DAILY":
                return LocalDate.now();
            case "WEEKLY":
                return LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            case "MONTHLY":
                return LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            case "YEARLY":
                return LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
            default:
                return null; // No filtering if the report type is invalid or null.
        }
    }

        public ByteArrayOutputStream generateSalesReportPdf(String filePath, List<OrderEntity> sales) throws Exception {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new com.itextpdf.layout.Document(pdf);

            // Add title
            document.add(new Paragraph("Sales Report").setBold().setFontSize(16));

            // Add table
            Table table = new com.itextpdf.layout.element.Table(6);
            table.addHeaderCell("Order Id");
            table.addHeaderCell("Date");
            table.addHeaderCell("User");
            table.addHeaderCell("Total");
            table.addHeaderCell("Discounts");
            table.addHeaderCell("Status");

            for (OrderEntity sale : sales) {
                table.addCell(String.valueOf(sale.getId()));
                table.addCell(String.valueOf(sale.getOrderDate()));
                table.addCell(sale.getUser().getFullName());
                table.addCell("₹"+String.valueOf(sale.getOrderTotal()));
                table.addCell("₹"+String.valueOf(sale.getDeductedAmount()));
                table.addCell(sale.getStatus());
            }

            // Calculate totals
            int totalOrders = sales.size();
            double totalSales = sales.stream().mapToDouble(OrderEntity::getOrderTotal).sum();
            double totalDiscounts = sales.stream().mapToDouble(OrderEntity::getDeductedAmount).sum();
// Add a row for "Total Orders"
            Cell totalOrdersCell = new Cell(1, 4) // Merge 3 columns
                    .add(new Paragraph("Total Orders"))         // Add the text
                    .setBold();                  // Make it bold
            table.addCell(totalOrdersCell);

// Add the value for total orders, spanning the last 2 columns
            Cell totalOrdersValueCell = new Cell(1, 2)
                    .add(new Paragraph(String.valueOf(totalOrders)))
                    .setBold();
            table.addCell(totalOrdersValueCell);

// Add a row for "Total Sales"
            Cell totalSalesCell = new Cell(1, 4) // Merge 3 columns
                    .add(new Paragraph("Total Sales"))         // Add the text
                    .setBold();
            table.addCell(totalSalesCell);

// Add the value for total sales, spanning the last 2 columns
            Cell totalSalesValueCell = new Cell(1, 2)
                    .add(new Paragraph(("₹" + String.format("%.2f", totalSales)))) // Format as currency
                    .setBold();
            table.addCell(totalSalesValueCell);

            // Add a row for "Total Sales"
            Cell totalDiscountCell = new Cell(1, 4) // Merge 3 columns
                    .add(new Paragraph("Total Discounts"))         // Add the text
                    .setBold();
            table.addCell(totalDiscountCell);

// Add the value for total sales, spanning the last 2 columns
            Cell totalDiscountValueCell = new Cell(1, 2)
                    .add(new Paragraph(("₹" + String.format("%.2f", totalDiscounts)))) // Format as currency
                    .setBold();
            table.addCell(totalDiscountValueCell);


            document.add(table);
            document.close();
            return outputStream;
    }

    public ByteArrayOutputStream generateSalesReportExcel(String filePath, List<OrderEntity> sales) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales Report");

        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Order Id");
        headerRow.createCell(1).setCellValue("Date");
        headerRow.createCell(2).setCellValue("User");
        headerRow.createCell(3).setCellValue("Total");
        headerRow.createCell(4).setCellValue("Discount");
        headerRow.createCell(5).setCellValue("Status");

        // Data rows
        int rowIndex = 1;
        for (OrderEntity sale : sales) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(sale.getId());
            row.createCell(1).setCellValue(sale.getOrderDate());
            row.createCell(2).setCellValue(sale.getUser().getFullName());
            row.createCell(3).setCellValue(("₹"+String.valueOf(sale.getOrderTotal())));
            row.createCell(4).setCellValue(("₹"+String.valueOf(sale.getDeductedAmount())));
            row.createCell(5).setCellValue(sale.getStatus());
        }


        Double totalSales = sales.stream().mapToDouble(OrderEntity::getOrderTotal).sum();
        int totalOrder = sales.size();
        Double totalDiscounts = sales.stream().mapToDouble(OrderEntity::getDeductedAmount).sum();

        // Total Orders Row
        Row totalOrdersRow = sheet.createRow(rowIndex++);
        totalOrdersRow.createCell(0).setCellValue("Total Orders");
        sheet.addMergedRegion(new CellRangeAddress(totalOrdersRow.getRowNum(), totalOrdersRow.getRowNum(), 0, 2));
        totalOrdersRow.createCell(3).setCellValue(totalOrder);
        totalOrdersRow.createCell(4).setCellValue("");

// Total Discounts Row
        Row totalDiscountRow = sheet.createRow(rowIndex++);
        totalDiscountRow.createCell(0).setCellValue("Total Discounts");
        sheet.addMergedRegion(new CellRangeAddress(totalDiscountRow.getRowNum(), totalDiscountRow.getRowNum(), 0, 2));
        totalDiscountRow.createCell(3).setCellValue("₹" + totalDiscounts);
        totalDiscountRow.createCell(4).setCellValue("");

// Total Sales Row
        Row totalSaleRow = sheet.createRow(rowIndex++);
        totalSaleRow.createCell(0).setCellValue("Total Sales");
        sheet.addMergedRegion(new CellRangeAddress(totalSaleRow.getRowNum(), totalSaleRow.getRowNum(), 0, 2));
        totalSaleRow.createCell(3).setCellValue("₹" + totalSales);
        totalSaleRow.createCell(4).setCellValue("");



        // Write to file
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream;
    }

    public void writeFileToResponse(String filePath, HttpServletResponse response) throws IOException {
        File file = new File(filePath);
        try (InputStream inputStream = new FileInputStream(file);
             OutputStream outputStream = response.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
        }
    }

    public void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

}
