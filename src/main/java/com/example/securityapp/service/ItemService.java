package com.example.securityapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.securityapp.domain.Item;
import com.example.securityapp.dto.ItemDto;
import com.example.securityapp.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;


    public List<ItemDto> retrieveItems () {

        List<Item> items = itemRepository.findAll();
        
        return items.stream().map(item -> entityToDto(item)).collect(Collectors.toList());

    }



    public ItemDto entityToDto(Item item) {
        return ItemDto.builder()
            .id(item.getId())
            .name(item.getName())
            .build();
    }

}
