package com.example.rental.utils;

import com.example.rental.entity.Contract;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

@Component
public class ContractPdfGenerator {

    // ✅ Tách riêng thư mục sinh hợp đồng
    private static final String OUTPUT_DIR = "uploads/generated_contracts/";

    public String generateContractFile(Contract contract) {
        try {
            Path outputDir = Paths.get(System.getProperty("user.dir"), OUTPUT_DIR);
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }

            String outputFileName = "contract_" + contract.getId() + ".pdf";
            Path outputPath = outputDir.resolve(outputFileName);

            // Tạo file PDF hợp đồng
            PdfWriter writer = new PdfWriter(outputPath.toFile());
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);

            document.add(new Paragraph("HỢP ĐỒNG THUÊ PHÒNG TRỌ")
                    .setBold()
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("BÊN CHO THUÊ: " + contract.getRoom().getBranch().getBranchName()));
            document.add(new Paragraph("Địa chỉ: " + contract.getRoom().getBranch().getAddress()));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("BÊN THUÊ: " + contract.getTenant().getFullName()));
            document.add(new Paragraph("Số CCCD: " + contract.getTenant().getCccd()));
            document.add(new Paragraph("Trường: " + contract.getTenant().getUniversity()
                    + " - MSSV: " + contract.getTenant().getStudentId()));
            document.add(new Paragraph("Phòng số: " + contract.getRoomNumber() + " (" + contract.getBranchCode() + ")"));
            document.add(new Paragraph("Ngày bắt đầu thuê: "
                    + contract.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            document.add(new Paragraph("Tiền cọc: " + contract.getDeposit() + " VND"));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Hai bên cam kết thực hiện đúng các điều khoản trong hợp đồng.")
                    .setFontSize(12));

            document.close();
            pdfDoc.close();

            System.out.println("✅ File hợp đồng đã được tạo tại: " + outputPath.toAbsolutePath());

            return "/uploads/generated_contracts/" + outputFileName;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file hợp đồng PDF: " + e.getMessage(), e);
        }
    }
}
