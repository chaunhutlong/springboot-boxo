package com.springboot.boxo.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCartRequest {
    @NotNull(message = "BookId should not be null")
    private Long bookId;

    @NotNull(message = "Quantity should not be null")
    @Min(value = 1, message = "Quantity should not be less than 1")
    private Integer quantity;
}

