package com.portshop.order_service.dto.orderShopDTO;

import com.portshop.order_service.model.ItemHistory;
import com.portshop.order_service.orderEnum.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemChangeRecordDTO {

    private String productId;
    private String itemId;
    private String productName;
    private Integer quantity;
    private Size size;
    private String image;

    @Builder.Default
    List<ItemHistory> itemHistories = new ArrayList<>();

}
