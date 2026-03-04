package com.AirlineBooking.AirlineBookig.service;

import com.AirlineBooking.AirlineBookig.model.Reservation;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfTicketService {

        public byte[] generateTicketPdf(Reservation reservation) {
                Document document = new Document(PageSize.A4, 36, 36, 54, 36);
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                try {
                        PdfWriter.getInstance(document, out);
                        document.open();

                        // Fonts
                        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Color.BLUE);
                        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.WHITE);
                        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
                        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

                        // Header section
                        Paragraph title = new Paragraph("E-TICKET / ITINERARY", titleFont);
                        title.setAlignment(Element.ALIGN_CENTER);
                        title.setSpacingAfter(20);
                        document.add(title);

                        // Information Table (Passenger & Booking Info)
                        PdfPTable infoTable = new PdfPTable(2);
                        infoTable.setWidthPercentage(100);
                        infoTable.setSpacingBefore(10f);
                        infoTable.setSpacingAfter(20f);

                        // Row 1
                        infoTable.addCell(createCell("Passenger Name:", labelFont, Element.ALIGN_LEFT,
                                        Rectangle.NO_BORDER));
                        infoTable.addCell(createCell(reservation.getUser().getName().toUpperCase(), valueFont,
                                        Element.ALIGN_RIGHT, Rectangle.NO_BORDER));

                        // Row 2
                        infoTable.addCell(createCell("Reservation ID:", labelFont, Element.ALIGN_LEFT,
                                        Rectangle.NO_BORDER));
                        infoTable.addCell(createCell(reservation.getReservationId().toString(), valueFont,
                                        Element.ALIGN_RIGHT, Rectangle.NO_BORDER));

                        // Row 3
                        infoTable.addCell(createCell("Booking Date:", labelFont, Element.ALIGN_LEFT,
                                        Rectangle.NO_BORDER));
                        infoTable.addCell(createCell(reservation.getReservationDate().format(formatter), valueFont,
                                        Element.ALIGN_RIGHT, Rectangle.NO_BORDER));

                        // Row 4
                        infoTable.addCell(createCell("Status:", labelFont, Element.ALIGN_LEFT, Rectangle.NO_BORDER));
                        infoTable.addCell(createCell(reservation.getStatus().name(), valueFont, Element.ALIGN_RIGHT,
                                        Rectangle.NO_BORDER));

                        document.add(infoTable);

                        // Flight Details Table (The Main Ticket Part)
                        PdfPTable flightTable = new PdfPTable(4);
                        flightTable.setWidthPercentage(100);
                        flightTable.setWidths(new float[] { 1.5f, 2.5f, 2.5f, 1.5f });
                        flightTable.setSpacingBefore(10f);

                        // Table Header
                        PdfPCell hCell1 = new PdfPCell(new Phrase("FLIGHT", headerFont));
                        hCell1.setBackgroundColor(Color.BLUE);
                        hCell1.setPadding(8);
                        flightTable.addCell(hCell1);

                        PdfPCell hCell2 = new PdfPCell(new Phrase("DEPARTURE", headerFont));
                        hCell2.setBackgroundColor(Color.BLUE);
                        hCell2.setPadding(8);
                        flightTable.addCell(hCell2);

                        PdfPCell hCell3 = new PdfPCell(new Phrase("ARRIVAL", headerFont));
                        hCell3.setBackgroundColor(Color.BLUE);
                        hCell3.setPadding(8);
                        flightTable.addCell(hCell3);

                        PdfPCell hCell4 = new PdfPCell(new Phrase("CLASS", headerFont));
                        hCell4.setBackgroundColor(Color.BLUE);
                        hCell4.setPadding(8);
                        flightTable.addCell(hCell4);

                        // Table Body
                        PdfPCell bCell1 = new PdfPCell(
                                        new Phrase(reservation.getFlight().getFlightNumber(), valueFont));
                        bCell1.setPadding(8);
                        flightTable.addCell(bCell1);

                        String depText = reservation.getFlight().getOrigin() + "\n"
                                        + reservation.getFlight().getDepartureTime().format(formatter);
                        PdfPCell bCell2 = new PdfPCell(new Phrase(depText, valueFont));
                        bCell2.setPadding(8);
                        flightTable.addCell(bCell2);

                        String arrText = reservation.getFlight().getDestination() + "\n"
                                        + reservation.getFlight().getArrivalTime().format(formatter);
                        PdfPCell bCell3 = new PdfPCell(new Phrase(arrText, valueFont));
                        bCell3.setPadding(8);
                        flightTable.addCell(bCell3);

                        PdfPCell bCell4 = new PdfPCell(
                                        new Phrase(reservation.getSeat().getCabinClass().name(), valueFont));
                        bCell4.setPadding(8);
                        flightTable.addCell(bCell4);

                        document.add(flightTable);

                        // Seat & Payment Info
                        PdfPTable summaryTable = new PdfPTable(2);
                        summaryTable.setWidthPercentage(100);
                        summaryTable.setSpacingBefore(30f);

                        summaryTable.addCell(createCell("Seat Number: ", labelFont, Element.ALIGN_LEFT,
                                        Rectangle.NO_BORDER));
                        summaryTable.addCell(createCell(reservation.getSeat().getSeatNumber(), valueFont,
                                        Element.ALIGN_RIGHT, Rectangle.NO_BORDER));

                        summaryTable.addCell(createCell("Total Amount (KES): ", labelFont, Element.ALIGN_LEFT,
                                        Rectangle.NO_BORDER));
                        String price = reservation.getSeat().getPrice() != null
                                        ? String.format("%.2f", reservation.getSeat().getPrice())
                                        : "N/A";
                        summaryTable.addCell(createCell(price, valueFont, Element.ALIGN_RIGHT, Rectangle.NO_BORDER));

                        document.add(summaryTable);

                        // Footer Note
                        Paragraph footer = new Paragraph(
                                        "\nPlease carry a valid ID matching the passenger name. Have a safe flight!",
                                        FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.GRAY));
                        footer.setAlignment(Element.ALIGN_CENTER);
                        footer.setSpacingBefore(40);
                        document.add(footer);

                        document.close();

                } catch (DocumentException e) {
                        e.printStackTrace();
                }

                return out.toByteArray();
        }

        private PdfPCell createCell(String text, Font font, int alignment, int border) {
                PdfPCell cell = new PdfPCell(new Phrase(text, font));
                cell.setHorizontalAlignment(alignment);
                cell.setBorder(border);
                cell.setPadding(5);
                return cell;
        }
}
