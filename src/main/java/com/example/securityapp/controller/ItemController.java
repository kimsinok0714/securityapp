package com.example.securityapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.securityapp.dto.ItemDto;
import com.example.securityapp.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;



@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")    
    @GetMapping("/items")
    public ResponseEntity<List<ItemDto>> getItems() {

        List<ItemDto> items = itemService.retrieveItems();
        
        return new ResponseEntity<>(items, HttpStatus.OK);

    }   
    

}
