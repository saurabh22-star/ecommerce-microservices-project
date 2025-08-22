package com.ecommerce.ecommercehub.productmodule.models;



import com.ecommerce.ecommercehub.utility.models.BaseModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Category extends BaseModel {

    private String title;
    private String details;

    @JsonIgnore
    @OneToMany(mappedBy = "categoryRef")
    private List<Product> product;

    public Category(String title) {
        this.title = title;
    }


}
