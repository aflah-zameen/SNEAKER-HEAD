package com.e_commerce.SNEAKERHEAD.Service;

import com.e_commerce.SNEAKERHEAD.Entity.OrderEntity;
import com.e_commerce.SNEAKERHEAD.Entity.OrderItems;
import com.e_commerce.SNEAKERHEAD.Repository.OrderRepository;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class InvoiceService {

    @Autowired
    OrderRepository orderRepository;

    public ByteArrayOutputStream generateInvoicePdf(Long orderId) throws Exception {
            OrderEntity order = orderRepository.findById(orderId).orElse(new OrderEntity());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Header Section
            document.add(new Paragraph("INVOICE")
                    .setFont(boldFont)
                    .setFontSize(24)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            // Business Details Section
            document.add(new Paragraph("SNEAKERHEAD")
                    .setFont(boldFont)
                    .setFontSize(14));
            document.add(new Paragraph("123 Vally Street\nKochi, Kerala, 678768\nPhone: (123) 456-7890\nEmail: sneaker@gmail.com")
                    .setFont(regularFont)
                    .setFontSize(12)
                    .setMarginBottom(20));

            // Customer Details Section
            document.add(new Paragraph("Bill To:")
                    .setFont(boldFont)
                    .setFontSize(14));
            document.add(new Paragraph(order.getAddress().getName()+"\n"+order.getAddress().getBuilding()+"\n"+order.getAddress().getCity()+
                    ", "+order.getAddress().getState()+", "+order.getAddress().getCountry()+", "+order.getAddress().getZipCode())
                    .setFont(regularFont)
                    .setFontSize(12)
                    .setMarginBottom(20));

            Table itemsTable = new Table(new float[]{4, 1, 1, 1});
            itemsTable.setWidth(520);
            itemsTable.addHeaderCell(new Cell().add(new Paragraph("Item").setFont(boldFont)));
            itemsTable.addHeaderCell(new Cell().add(new Paragraph("Qty").setFont(boldFont)));
            itemsTable.addHeaderCell(new Cell().add(new Paragraph("Price").setFont(boldFont)));
            itemsTable.addHeaderCell(new Cell().add(new Paragraph("Total").setFont(boldFont)));


            for (OrderItems item : order.getOrderItems()) {
                    itemsTable.addCell(new Cell().add(new Paragraph(item.getProductVariant().getProduct().getName()).setFont(regularFont)));
                    itemsTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity())).setFont(regularFont)));
                    itemsTable.addCell(new Cell().add(new Paragraph("INR "+ (item.getProductVariant().getOfferPrice() != null ?
                            item.getProductVariant().getOfferPrice() : item.getProductVariant().getPrice())).setFont(regularFont)));
                    itemsTable.addCell(new Cell().add(new Paragraph("INR "+ item.getQuantity()*(item.getProductVariant().getOfferPrice() != null ?
                            item.getProductVariant().getOfferPrice() : item.getProductVariant().getPrice())).setFont(regularFont)));
            }

            document.add(itemsTable.setMarginBottom(20));

            // Footer Section
            Table totalsTable = new Table(2);
            totalsTable.setWidth(200);
            totalsTable.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);
            totalsTable.addCell(new Cell().add(new Paragraph("Subtotal:").setFont(boldFont)));
            totalsTable.addCell(new Cell().add(new Paragraph("INR "+(order.getOrderTotal()+order.getDeductedAmount()))).setFont(regularFont));
//            totalsTable.addCell(new Cell().add(new Paragraph("Tax (10%):").setFont(boldFont)));
//            totalsTable.addCell(new Cell().add(new Paragraph("$14").setFont(regularFont)));
            totalsTable.addCell(new Cell().add(new Paragraph("Discount:").setFont(boldFont)));
            totalsTable.addCell(new Cell().add(new Paragraph("INR "+order.getDeductedAmount())).setFont(regularFont));
            totalsTable.addCell(new Cell().add(new Paragraph("Shipping Fee:").setFont(boldFont)));
            totalsTable.addCell(new Cell().add(new Paragraph("Free").setFont(regularFont)));
            totalsTable.addCell(new Cell().add(new Paragraph("Total:").setFont(boldFont)));
            totalsTable.addCell(new Cell().add(new Paragraph("INR "+order.getOrderTotal())).setFont(regularFont));

            document.add(totalsTable);

            // Close document
            document.close();
            System.out.println("Invoice PDF created successfully!");
            return outputStream;
    }
}
