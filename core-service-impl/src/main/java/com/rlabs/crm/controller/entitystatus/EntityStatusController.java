package com.rlabs.crm.controller.entitystatus;

import com.rlabs.crm.api.controller.EntityStatusApi;
import com.rlabs.crm.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class EntityStatusController implements EntityStatusApi {

    @Override
    public ResponseEntity<Object> getEntityStatus() {
        log.info("Request to get status of various entities");
        Map<String, Object> status = new HashMap<>();
        status.put("customer", Arrays.asList(Constants.STATUS_ACTIVE, Constants.STATUS_ARCHIVE));
        status.put("quotation", Arrays.asList(Constants.STATUS_DRAFT, Constants.STATUS_PENDING_FOR_REVIEW,
            Constants.STATUS_REVIEW_COMPLETED, Constants.STATUS_CORRECTIONS_REQUIRED,
            Constants.STATUS_READY_FOR_AMENDMENTS, Constants.STATUS_AMENDMENTS_COMPLETED,
            Constants.STATUS_APPROVED, Constants.STATUS_REJECTED,
            Constants.STATUS_READY_FOR_SENT_TO_CUSTOMER_APPROVAL, Constants.STATUS_SENT_TO_CUSTOMER_APPROVAL,
            Constants.STATUS_APPROVED_BY_CUSTOMER,Constants.STATUS_REJECTED_BY_CUSTOMER,
            Constants.STATUS_READY_TO_CONVERT_TO_ORDER, Constants.STATUS_CONVERTED_TO_ORDER));

        return ResponseEntity.status(HttpStatus.OK).body(status);
    }

}
