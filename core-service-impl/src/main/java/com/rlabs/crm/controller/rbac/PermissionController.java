package com.rlabs.crm.controller.rbac;

import com.rlabs.crm.api.controller.PermissionApi;
import com.rlabs.crm.defaultdata.permissions.PermissionCache;
import com.rlabs.crm.payload.response.rbac.PermissionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class PermissionController implements PermissionApi {

    @Override
    public ResponseEntity<Object> getAllPermissions(){
        log.info("Request to get all permissions");
        List<PermissionResponse> permissionList = new ArrayList<>();

        for(String key: PermissionCache.getKeys()){
            List<String> permissionNames = PermissionCache.get(key);
            for(String permissionName: permissionNames){
                PermissionResponse permission = PermissionResponse.builder().category(key).name(permissionName).build();
                permissionList.add(permission);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(permissionList);
    }

}
