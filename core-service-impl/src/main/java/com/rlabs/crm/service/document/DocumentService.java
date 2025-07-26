package com.rlabs.crm.service.document;

import com.rlabs.crm.config.Constants;
import com.rlabs.crm.domain.Document;
import com.rlabs.crm.repository.DocumentRepository;
import com.rlabs.crm.util.DateTimeUtil;
import com.rlabs.crm.util.MapperUtil;
import com.rlabs.crm.web.rest.errors.MandatoryFieldMissingException;
import com.rlabs.crm.web.rest.errors.file.FileUploadFailedException;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
public class DocumentService {

    @Value("${file-path.base-path}")
    private String filePathQuotation;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MapperUtil mapperUtil;

    public List<Document> findAll(){
        return documentRepository.findAll();
    }

    public List<Document> findAllById(List<Long> ids){
        return documentRepository.findAllById(ids);
    }

    public Document findById(Long id){
        return documentRepository.findById(id).orElse(null);
    }

    @Transactional
    public Document uploadDocument(Path newDir, String  source, MultipartFile file) throws IOException {
        log.debug("Adding new document");
        String extension= file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        Path path = Paths.get(newDir.toString() + File.separatorChar + file.getOriginalFilename());
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        Document document = Document.builder()
            .uid(Constants.THREE_CHAR_DOCUMENT +dateTimeUtil.convertLocalDateTimeToString(LocalDateTime.now(), Constants.DATE_TIME_FORMAT_yyyyMMddHHmmss))
            .source(source)
            .localFilePath(newDir.toString())
            .fileName(file.getOriginalFilename())
            .fileType(extension)
            .build();
        document = documentRepository.save(document);
        log.debug("New document added");
        return document;
    }

    @Transactional
    public Document deleteDocument(Long id) throws IOException {
        log.debug("Deleting document. Document id: {}",id);
        Document document = documentRepository.findById(id).get();
        Path path = Paths.get(document.getLocalFilePath() + File.separatorChar + document.getFileName());
        Files.delete(path);
        documentRepository.deleteById(id);
        log.debug("Document deleted");
        return document;
    }

    public Document uploadQuotationDocument(MultipartFile file, LinkedHashMap source, String body){
        LinkedHashMap sourceIdMap = (LinkedHashMap) source.get("sourceId");
//        if (sourceIdMap.get("customerId") == null) {
//            log.error("Customer id is missing in source json");
//            throw new MandatoryFieldMissingException("Customer id missing in source json","source");
//        }
        if (sourceIdMap.get("quotationId") == null) {
            log.error("Quotation id is missing in source json");
            throw new MandatoryFieldMissingException("Quotation id missing in source json","source");
        }
//        if (sourceIdMap.get("productId") == null) {
//            log.error("Product id is missing in source json");
//            throw new MandatoryFieldMissingException("Product id missing in source json","source");
//        }
//        if (sourceIdMap.get("version") == null) {
//            log.error("Quotation version no is missing in source json");
//            throw new MandatoryFieldMissingException("Quotation version no missing in source json","source");
//        }

        String filePath = filePathQuotation;
        if(sourceIdMap.get("customerId") != null && !StringUtils.isBlank((String)sourceIdMap.get("customerId"))){
            filePath = filePathQuotation + "/customers/"+(String)sourceIdMap.get("customerId");
        }
        if(sourceIdMap.get("quotationId") != null && !StringUtils.isBlank((String)sourceIdMap.get("quotationId"))){
            filePath = filePathQuotation + "/quotations/"+(String)sourceIdMap.get("quotationId");
        }
        if(sourceIdMap.get("version") != null && !StringUtils.isBlank((String)sourceIdMap.get("version"))){
            filePath = filePathQuotation + "/version/"+(String)sourceIdMap.get("version");
        }

//        String filePath = filePathQuotation.replace("#cusId#", (String)sourceIdMap.get("customerId"))
//            .replace("#qteId#", (String)sourceIdMap.get("quotationId"))
//            .replace("#verNo#", (String)sourceIdMap.get("version"));
        try {
            Path projectDir = Paths.get(System.getProperty("user.dir"));
            Path newDir = projectDir.resolve(filePath);
            if (!Files.exists(newDir)) {
                Files.createDirectories(newDir);
            }
            return uploadDocument(newDir, body, file);
        } catch (Exception e) {
            log.error("Failed to upload file/document", e);
            throw new FileUploadFailedException();
        }
    }


}
