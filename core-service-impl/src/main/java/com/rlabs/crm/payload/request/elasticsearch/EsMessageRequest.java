package com.rlabs.crm.payload.request.elasticsearch;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "crm-message-index")
public class EsMessageRequest<T> implements Serializable {

    @Id
    private String dbId;

//    @Field(type = FieldType.Text, name = "sourceData")
    private T sourceData;

//    @Field(type = FieldType.Text, name = "sourceType")
    private String sourceType;
}
