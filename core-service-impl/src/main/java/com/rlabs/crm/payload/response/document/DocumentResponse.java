package com.rlabs.crm.payload.response.document;

import com.rlabs.crm.domain.Document;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private Long id;
    private String uid;
    private String source;
    private String localFilePath;
    private String fileName;
    private String fileType;
    private String templateId;
    private String backupType;
    private String backupLocation;
    private String backupAccessUrl;
    private LocalDate backupOn;

    public static DocumentResponse buildDocumentResponse(Document document){
        if(document == null){
            return DocumentResponse.builder().build();
        }
        return DocumentResponse.builder()
            .id(document.getId())
            .uid(document.getUid())
            .source(document.getSource())
            .localFilePath(document.getLocalFilePath())
            .fileName(document.getFileName())
            .fileType(document.getFileType())
            .templateId(document.getTemplateId())
            .backupType(document.getBackupType())
            .build();
    }
}
