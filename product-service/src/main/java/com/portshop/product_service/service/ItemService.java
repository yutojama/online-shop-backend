package com.portshop.product_service.service;

import com.portshop.product_service.exception.ItemNotFoundException;
import com.portshop.product_service.model.Item;
import com.portshop.product_service.model.Product;
import com.portshop.product_service.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ItemService {

    Logger logger = LoggerFactory.getLogger(ItemService.class);
    private final ProductService productService;
    private final ItemRepository itemRepository;

    public ItemService(ProductService productService, ItemRepository itemRepository) {
        this.productService = productService;
        this.itemRepository = itemRepository;
    }

    /* add item */
    public Item addItem(Item item){

        // find out the existing item
        Product product = item.getProduct();
        product.addItem(item);
        Product savedProduct = productService.updateProduct(product);
        Optional<Item> savedItem = savedProduct.getItems().stream().filter(it -> it.getSize().equals(item.getSize())).findFirst();
        if(savedItem.isPresent()){
            logger.info("Item added with id: " + savedItem.get().getId());
            return savedItem.get();
        }
        throw new ItemNotFoundException("Item not found");
    }

    public void addItems(List<Item> items) {
        items.forEach(this::addItem);
    }

    /* get item */
    public Item getItem(UUID id){
        Optional<Item> item = itemRepository.findById(id);
        if(item.isPresent()){
            return item.get();
        } else {
            throw new ItemNotFoundException("Item not found: "+id);
        }
    }

    public void deleteAll(){
        itemRepository.deleteAll();
    }

}
