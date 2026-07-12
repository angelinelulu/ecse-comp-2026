package com.kirbken.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;

public class PdfTextExtractor {
    public static String extractText(File pdfFile) throws Exception {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            return new PDFTextStripper().getText(document);
        }
    }
}