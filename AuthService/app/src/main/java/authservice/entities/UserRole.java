package authservice.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;


@Entity
@Data
@Table(name = "roles")
public class UserRole {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private Long roleId;

    private String name;
}
