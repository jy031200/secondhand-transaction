package zerobase.secondhand_transaction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private boolean payment; // 결제여부

    private boolean success; // 거래성사여부

    private Integer request_cancel; // 취소요청

    private boolean money_delivery; // 대금전달여부


}