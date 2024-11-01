package zerobase.secondhand_transaction.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import zerobase.secondhand_transaction.dto.ProductDto;
import zerobase.secondhand_transaction.entity.constant.Category;
import zerobase.secondhand_transaction.entity.constant.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity(name = "Product")
@Getter
@ToString
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer id;

    @Column(name = "pd_name")
    private String pd_name; // 상품명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 상품 등록한 유저 (1:N)

    @Column(nullable = false)
    private String pd_img; // 상품 이미지

    @Column(nullable = false)
    private Integer pd_price; // 가격

    private String pd_detail; // 상세 설명

    private String location; // 위치 (선택)

    private Double latitude;  // 위도 (선택)

    private Double longitude; // 경도 (선택)

    @Column(nullable = false)
    private String place; // 거래 장소

    @Column(nullable = false)
    @ElementCollection
    private List<String> time;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private LocalDateTime createdDt;
    // 등록일 한달이내에 판매 x 상태이면 삭제하도록
    // => 이건 한 3주 후에 알림 주도록

    @Column(nullable = false)
    private int request_buy;
    // 거래 요청 상태 (0: 기본, 1: 요청됨, 2: 승인됨, -1: 거절됨)

    @Column(nullable = false)
    private String pd_num; // 상품 등록 번호 (중복 없음) (이 번호로 상품 구별)

    @PrePersist
    public void prePersist(){
        this.createdDt = LocalDateTime.now(); // DB 저장 직전? 시간 설정
    }

    // 상품 등록번호 (중복x)
    public String generateProductNumber() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    // 생성자
    public Product(ProductDto productDto, User user) {
        this.pd_name = productDto.getPd_name();
        this.user = user;
        this.pd_img = productDto.getPd_img();
        this.pd_price = productDto.getPd_price();
        this.pd_detail = productDto.getPd_detail();
        this.location = productDto.getLocation();
        this.latitude = productDto.getLatitude();
        this.longitude = productDto.getLongitude();
        this.place = productDto.getPlace();
        this.time = productDto.getTime();
        this.status = Status.AVAILABLE; // 상태 초기값 : 구매 가능
        this.category = productDto.getCategory();
        this.request_buy = 0; // 거래 요청 상태 기본값 : 0
        this.pd_num = generateProductNumber();
    }

}