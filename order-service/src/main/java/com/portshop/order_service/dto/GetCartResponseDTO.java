package com.portshop.order_service.dto;

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
public class GetCartResponseDTO {

    @Builder.Default
    private List<GetCartItemResponseDTO> cartItemResponseDTOList = new ArrayList<>();

    private CartSumResponseDTO cartSumResponseDTO;

}
