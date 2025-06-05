package com.ecommerce.ecommercehub.productmodule.models;


import com.ecommerce.ecommercehub.utility.models.BaseModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Blob;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Image extends BaseModel {

    private String name;
    private String type;

    @Lob
    private Blob data;
    private String url;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Product product;
}