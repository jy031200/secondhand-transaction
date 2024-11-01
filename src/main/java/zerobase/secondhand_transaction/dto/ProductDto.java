package zerobase.secondhand_transaction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import zerobase.secondhand_transaction.entity.constant.Category;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    @NotBlank
    private String pd_name; // 상품명

    @NotBlank
    private String pd_img; // 상품 이미지

    @NotNull
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")  // 최소값 검사
    private Integer pd_price; // 가격

    private String pd_detail;

    private String location;

    private Double latitude;  // 위도 (선택)

    private Double longitude; // 경도 (선택)

    @NotBlank
    private String place;

    @NotEmpty
    private List<String> time;

    private Category category;

}