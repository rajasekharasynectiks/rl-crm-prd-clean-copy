
/**
 * @author Manoj Sharma
 */

package com.rlabs.crm.helper;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.rlabs.crm.web.rest.errors.file.FileDownFailedException;
import com.rlabs.crm.web.rest.errors.template.HtmlToPdfConversionFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Component
public class PdfHelper {

    public ByteArrayOutputStream getPdf(String htmlContent){
        PdfDocument pdfDocument = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            pdfDocument = new PdfDocument(writer);
            pdfDocument.setDefaultPageSize(PageSize.A4);
            HtmlConverter.convertToPdf(new ByteArrayInputStream(htmlContent.getBytes()), pdfDocument);
            pdfDocument.close();
            return outputStream;
        } catch (IOException e) {
            log.error("IOException while converting html into pdf. Exception: ",e);
            throw new HtmlToPdfConversionFailedException();
        }finally {
            log.debug("closing pdf document");
            if(pdfDocument != null){
                pdfDocument.close();
            }
        }
    }

}
