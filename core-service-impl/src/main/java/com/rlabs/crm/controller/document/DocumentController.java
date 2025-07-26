package com.rlabs.crm.controller.document;

import com.rlabs.crm.api.controller.DocumentApi;
import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.Document;
import com.rlabs.crm.payload.response.document.DocumentResponse;
import com.rlabs.crm.repository.DocumentRepository;
import com.rlabs.crm.repository.QuotationRepository;
import com.rlabs.crm.service.document.DocumentService;
import com.rlabs.crm.service.quotation.QuotationService;
import com.rlabs.crm.util.DateTimeUtil;
import com.rlabs.crm.util.MapperUtil;
import com.rlabs.crm.web.rest.errors.MandatoryFieldMissingException;
import com.rlabs.crm.web.rest.errors.document.DocumentNotFoundException;
import com.rlabs.crm.web.rest.errors.file.FileDownFailedException;
import com.rlabs.crm.web.rest.errors.file.FileUploadFailedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api")
public class DocumentController implements DocumentApi {
//
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
    private MapperUtil mapperUtil;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public ResponseEntity<Object> addDocument(MultipartFile file, Object body) {
        log.info("Request to add new document");
        if(body == null){
            log.error("Source missing");
            throw new MandatoryFieldMissingException("Source missing","source");
        }
        LinkedHashMap source = mapperUtil.toObject((String) body, LinkedHashMap.class);

        if (source.isEmpty()) {
            log.error("Source is empty");
            throw new MandatoryFieldMissingException("Source is empty","source");
        }

        if(source.get("sourceType") == null){
            log.error("Source type missing");
            throw new MandatoryFieldMissingException("Source type missing","source");
        }
        if(source.get("sourceId") == null){
            log.error("Source id missing");
            throw new MandatoryFieldMissingException("Source id missing","source");
        }

        Document document = null;
        if(Constants.QUOTATION.equalsIgnoreCase((String)source.get("sourceType"))){
            /**
             * source json eg.
             * {
             * 	"sourceType":"Quotation",
             * 	"sourceId":{
             * 		"customerId": "1",
             * 		"quotationId": "2",
             * 		"productId": "2",
             * 		"version": "1"
             * 	    }
             * }
             */
            document = documentService.uploadQuotationDocument(file, source, (String) body);
        }
        if(document == null){
            log.error("File upload failed");
            throw new FileUploadFailedException();
        }
        return ResponseEntity.ok(DocumentResponse.buildDocumentResponse(document));
    }

    @Override
    public ResponseEntity<Object> deleteDocument(Long id) {
        log.info("Request to delete document. Document id: {}",id);
        if (Objects.isNull(id)) {
            log.error("Document id missing");
            throw new MandatoryFieldMissingException("Document", "id");
        }
        if(!documentRepository.existsById(id)){
            log.error("Document not found");
            throw new DocumentNotFoundException();
        }
        Document document = null;
        try {
            document = documentService.deleteDocument(id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(HttpStatus.OK).body(DocumentResponse.buildDocumentResponse(document));
    }

    @Override
    public ResponseEntity<Object> downloadDocument(Long id) {
        log.info("Request to download document. Document id: {}",id);
        if (Objects.isNull(id)) {
            log.error("Document id missing");
            throw new MandatoryFieldMissingException("Document", "id");
        }
        if(!documentRepository.existsById(id)){
            log.error("Document not found");
            throw new DocumentNotFoundException();
        }
        Document document = documentService.findById(id);

        try {
            byte[] file  = getFile(document);
            return ResponseEntity.ok(Base64.encodeBase64String(file));
        } catch (Exception e) {
            log.error("Failed to retrieve file. ", e);
            throw new FileDownFailedException();
        }
    }

    public byte[] getFile(Document document) throws IOException {
        Path path = Paths.get(document.getLocalFilePath()+ File.separatorChar + document.getFileName());
        return Files.readAllBytes(path);
    }
}
