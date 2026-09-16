package com.portshop.order_service.dto.orderShopDTO;

import com.portshop.order_service.model.ItemChangeRecord;
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
public class OrderShopDTO {

    private String orderId;
    private String shopId;

    @Builder.Default
    List<ItemChangeRecordDTO> itemChangeRecords = new ArrayList<>();

}
