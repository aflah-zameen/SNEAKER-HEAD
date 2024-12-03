package com.e_commerce.SNEAKERHEAD.Entity;

import com.e_commerce.SNEAKERHEAD.Enums.StockStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.attoparser.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

@Setter
@Getter
@ToString
@Entity
@Table(name = "product_variant")
public class ProductVariant {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "article_code", nullable = false)
    private String articleCode;

    @Column(name = "size")
    private List<String> size;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "color")
    private String color;

    @Column(name = "price")
    private Double price;

    @Column(name = "images" , columnDefinition = "JSONB")
    private List<String> images;

    @Column(name = "max_quantity")
    private Integer maxQuantity;

    @Column(name="stock_status")
    private String stockStatus;

    @OneToMany(mappedBy = "productVariant",cascade = CascadeType.ALL)
    private List<Cart> carts;

    @OneToMany(mappedBy = "productVariant",cascade = CascadeType.ALL)
    private List<OrderItems> orderItems;

    public String getFormattedPrice(){
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(price);
    }

}
