package tm.salam.cocaiot.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Column(name = "uuid")
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID uuid;
    @Column(name = "name")
    @NotNull(message = "user name don't be null")
    @NotEmpty(message = "user name don't be empty")
    @Size(min = 1, max = 50, message = "user name length should be long than 0 and less than 51")
    private String name;
    @Column(name = "surname")
    @NotNull(message = "user surname don't be null")
    @NotEmpty(message = "user surname don't be empty")
    @Size(min = 1, max = 65, message = "user surname should be long than 0 and less than 66")
    private String surname;
    @Column(name = "patronomic_name")
    @Size(max = 75, message = "user patronomic name should be less than 76")
    private String patronomicName;
    @Column(name = "email")
//    @Email(message = "user email invalid")
    @Size(min = 1, max = 100, message = "user email length should be long than 0 and less than 101")
    private String email;
    @Column(name = "password")
    @NotNull(message = "user password don't be null")
    @NotEmpty(message = "user password don't be empty")
    @Size(min = 1, max = 250, message = "user password length should be long than 0 and less than 251")
    private String password;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "updated")
    @UpdateTimestamp
    private Date updated;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "role_uuid", referencedColumnName = "uuid")
    private Role role;


}
