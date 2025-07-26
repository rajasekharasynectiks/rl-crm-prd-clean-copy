package com.rlabs.crm.controller.quotation;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.rlabs.crm.api.controller.QuotationApi;
import com.rlabs.crm.api.model.SearchQuotationRequest;
import com.rlabs.crm.domain.Customer;
import com.rlabs.crm.domain.Quotation;
import com.rlabs.crm.helper.PdfHelper;
import com.rlabs.crm.payload.request.quotation.AddQuotationRequest;
import com.rlabs.crm.payload.request.quotation.QuotationRequest;
import com.rlabs.crm.payload.request.quotation.ReviewQuotationRequest;
import com.rlabs.crm.payload.response.quotation.QuotationAuditHistoryResponse;
import com.rlabs.crm.payload.response.quotation.QuotationResponse;
import com.rlabs.crm.payload.response.quotation.QuotationResponseV3;
import com.rlabs.crm.repository.DocumentRepository;
import com.rlabs.crm.repository.QuotationRepository;
import com.rlabs.crm.service.document.DocumentService;
import com.rlabs.crm.service.quotation.QuotationAuditHistoryService;
import com.rlabs.crm.service.quotation.QuotationService;
import com.rlabs.crm.util.DateTimeUtil;
import com.rlabs.crm.util.MapperUtil;
import com.rlabs.crm.web.rest.errors.MandatoryFieldMissingException;
import com.rlabs.crm.web.rest.errors.file.FileUploadFailedException;
import com.rlabs.crm.web.rest.errors.quotation.QuotationNotFoundException;
import com.rlabs.crm.web.rest.errors.security.UserNotFoundException;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class QuotationController implements QuotationApi {

//    @Value("${file-path.quotation}")
//    private String filePathQuotation;

    @Autowired
    private QuotationRepository quotationRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private QuotationService quotationService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private QuotationAuditHistoryService quotationAuditHistoryService;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private PdfHelper pdfHelper;

    @Override
    public ResponseEntity<Object> addQuotation(Object body) {
        log.info("Request to create new quotation");
        AddQuotationRequest addQuotationRequest = mapperUtil.convertObject(body, AddQuotationRequest.class);
        if (StringUtils.isBlank(addQuotationRequest.getQtNumber())) {
            log.error("Quotation number missing");
            throw new MandatoryFieldMissingException("Quotation","number");
        }
        Quotation quotation = quotationService.addQuotation(addQuotationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(QuotationResponse.buildQuotationResponse(quotation, dateTimeUtil));
    }

    @Override
    public ResponseEntity<Object> editQuotation(Object body) {
        log.info("Request to edit quotation");
        QuotationRequest quotationRequest = mapperUtil.convertObject(body, QuotationRequest.class);
        if (Objects.isNull(quotationRequest.getId())) {
            log.error("Quotation id missing");
            throw new MandatoryFieldMissingException("Quotation", "id");
        }
        if(!quotationRepository.existsById(quotationRequest.getId())){
            log.error("Quotation not found");
            throw new QuotationNotFoundException();
        }
        Quotation quotation = quotationService.editQuotation(quotationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(QuotationResponse.buildQuotationResponse(quotation, dateTimeUtil));
    }

    @Override
    public ResponseEntity<Object> deleteQuotation(Long id) {
        log.info("Request to delete quotation");
        if (Objects.isNull(id)) {
            log.error("Quotation id missing");
            throw new MandatoryFieldMissingException("Quotation", "id");
        }
        if(!quotationRepository.existsById(id)){
            log.error("Quotation not found");
            throw new QuotationNotFoundException();
        }
        Quotation quotation = quotationService.deleteQuotation(id);
        return ResponseEntity.status(HttpStatus.OK).body(QuotationResponse.buildQuotationResponse(quotation, dateTimeUtil));
    }

    @Override
    public ResponseEntity<Object> searchQuotations(SearchQuotationRequest searchRequest) {
        log.info("Request to search quotations");
        List<Quotation> quotationList = null;
        if(searchRequest != null){
            quotationList = quotationService.searchQuotation(searchRequest);
        }else{
            quotationList = quotationService.findAll();
        }
        List<QuotationResponseV3> responses =  quotationList.stream().map(obj -> QuotationResponseV3.buildQuotationResponseV3(obj, dateTimeUtil, quotationService)).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @Override
    public ResponseEntity<Object> reviewQuotation(Object body) {
        log.info("Request to review quotation");
        ReviewQuotationRequest quotationRequest = mapperUtil.convertObject(body, ReviewQuotationRequest.class);
        if (Objects.isNull(quotationRequest.getId())) {
            log.error("Quotation id missing");
            throw new MandatoryFieldMissingException("Quotation", "id");
        }
        if (StringUtils.isBlank(quotationRequest.getStatus())) {
            log.error("Quotation status missing");
            throw new MandatoryFieldMissingException("Quotation", "status");
        }
        if (StringUtils.isBlank(quotationRequest.getComments())) {
            log.error("Quotation comments missing");
            throw new MandatoryFieldMissingException("Quotation", "comments");
        }
        if(!quotationRepository.existsById(quotationRequest.getId())){
            log.error("Quotation not found");
            throw new QuotationNotFoundException();
        }
        Quotation quotation = quotationService.reviewQuotation(quotationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(QuotationResponse.buildQuotationResponse(quotation, dateTimeUtil));
    }

    @Override
    public ResponseEntity<Object> getQuotationAuditHistory(Long id) {
        log.info("Request to get quotation history");
        if (Objects.isNull(id)) {
            log.error("Quotation id missing");
            throw new MandatoryFieldMissingException("Quotation", "id");
        }
        if(!quotationRepository.existsById(id)){
            log.error("Quotation not found");
            throw new QuotationNotFoundException();
        }
        List<QuotationAuditHistoryResponse> responseList = quotationAuditHistoryService.findByQuotationId(id).stream().map(obj -> QuotationAuditHistoryResponse.buildQuotationAuditHistoryResponse(obj, dateTimeUtil)).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @Override
    public ResponseEntity<Object> downdloadQuotation(Long id) {
        log.info("Request to download quotation pdf");
        if (Objects.isNull(id)) {
            log.error("Quotation id missing");
            throw new MandatoryFieldMissingException("Quotation", "id");
        }
        if(!quotationRepository.existsById(id)){
            log.error("Quotation not found");
            throw new QuotationNotFoundException();
        }
        Quotation quotation = quotationService.findById(id);
        byte[]  b = genPdf();
        Context context = createContext(quotation);
//        String htmlContent = templateEngine.process("quotation/index", context);
        String pdfFileName = "quotation" + (quotation.getQtNumber() != null ? "_"+quotation.getQtNumber() : "") + ".pdf";

//        ByteArrayOutputStream outputStream = pdfHelper.getPdf(htmlContent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", pdfFileName);
        return new ResponseEntity<>(b, headers, HttpStatus.OK);
    }

    public byte[] genPdf() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Create a PDF writer and PDF document
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Title
//            document.add(new Paragraph("PRODUCTION PRICE PROPOSAL")
//                .setBold()
//                .setFontSize(14)
//                .setTextAlignment(TextAlignment.CENTER));

            // Header

            // Add a cell with an image
            String imgPath = "D:\\mycode\\rasi-labs\\rl-crm\\core-service-impl\\src\\main\\resources\\logo\\logo_addr.png";
            ImageData imageData = ImageDataFactory.create(imgPath);
            Image image = new Image(imageData);

            Cell imageCell = new Cell();
            imageCell.add(image.setAutoScale(true));

            float[] hederTableColumns = {1, 2};
            Table headerTable = new Table(hederTableColumns);
            headerTable.addCell(imageCell);
            headerTable.addCell("Rasi Laboratories, Inc.");

//            headerTable.addCell("DATE: 03/10/2023");
//            headerTable.addCell("QUOTE NUMBER: DRB-031023-03");
            document.add(headerTable);

            // Product Details
//            document.add(new Paragraph("PRODUCT NAME: DURABLE BLOOD PRESSURE").setBold());
//            document.add(new Paragraph("CUSTOMER PRODUCT CODE: DURABLE").setBold());

            // Table for Supplement Facts
//            float[] columnWidths = {1, 2, 2, 2};
//            Table table = new Table(columnWidths);
//            table.addCell("Serving size:");
//            table.addCell("2 vegetable capsules");
//            table.addCell("Amount per serving");
//            table.addCell("% Daily Value");
//            table.addCell("MegaNatural®-BP (Grape Seed Extract) (Std. to 90-95% polyphenols)");
//            table.addCell("150 mg");
//            table.addCell("*");
//            document.add(table);

            // Footer
//            document.add(new Paragraph("PRICE VALIDITY - 30 days from the date of this quote")
//                .setFontColor(new DeviceRgb(128, 0, 0))
//                .setBold());

            document.close();

            // Convert the PDF to a byte array
            byte[] pdfBytes = baos.toByteArray();
            return pdfBytes;
//            // Set HTTP response headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Price_Quotation.pdf");
//            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

//            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    private Context createContext(Quotation quotation){
        Context context = new Context();
        context.setVariable("quotationNo", quotation.getQtNumber());
        return context;
    }

    @RequestMapping(
        method = {RequestMethod.POST},
        value = {"/quotation/test-html-to-pdf"},
        produces = {"application/json"},
        consumes = {"multipart/form-data"}
    )
    public ResponseEntity<Object> testQtPdf(@Parameter(name = "file",description = "") @RequestPart(value = "file",required = false) MultipartFile file) {
        log.info("Request to test quotation pdf from html file");
        try {
            byte[] fileBytes = file.getBytes();
            String htmlContent = new String(fileBytes, StandardCharsets.UTF_8);
            ByteArrayOutputStream outputStream = pdfHelper.getPdf(htmlContent);
            String pdfFileName = "quotation.pdf";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", pdfFileName);
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to upload file. ", e);
            throw new FileUploadFailedException();
        }
    }
}
