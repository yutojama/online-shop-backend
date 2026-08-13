package com.portshop.product_service.dto;

import com.portshop.product_service.productEnum.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemImportRequestDTO {

    @NotBlank(message = "product_id is required")
    private Integer productId;
    @NotNull(message = "Quantity is required")
    @Min(1)
    private Integer quantity;
    @NotNull(message = "Size is required")
    private Size size;

}
