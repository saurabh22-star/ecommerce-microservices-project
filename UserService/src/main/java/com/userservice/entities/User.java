package com.userservice.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Size(min = 2, max = 25, message = "First name must be 2-25 characters")
    @Pattern(regexp = "^[\\p{L}]+$", message = "First name may contain only letters")
    @Column(name = "first_name", length = 25, nullable = false)
    private String firstName;

    @Size(min = 2, max = 30, message = "Last name must be 2-30 characters")
    @Pattern(regexp = "^[\\p{L}]+$", message = "Last name may contain only letters")
    @Column(name = "last_name", length = 30, nullable = false)
    private String lastName;

    @Size(min = 10, max = 10, message = "Mobile number must be exactly 10 digits")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must contain only digits")
    @Column(name = "mobile_number", length = 10)
    private String mobileNumber;

    @Email(message = "Provide a valid email address")
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "user_address",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    private List<Address> addresses = new ArrayList<>();

    @Version
    private Long version;

    public User(Long id, String givenName, String familyName, String mail, String pwd, String mobile, Set<Role> roleSet, List<Address> addressList) {
        this.userId = id;
        this.firstName = givenName;
        this.lastName = familyName;
        this.mobileNumber = mobile;
        this.email = mail;
        this.password = pwd;
        this.roles = (roleSet != null) ? roleSet : new HashSet<>();
        this.addresses = (addressList != null) ? addressList : new ArrayList<>();
    }
}