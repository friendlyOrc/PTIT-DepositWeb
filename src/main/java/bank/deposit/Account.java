package bank.deposit;

import java.io.Serializable;
import java.sql.Date;
import java.util.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "account")
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class Account implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private String name;
    @NotNull
    private Date dob;
    @NotNull
    private int sex;
    @NotNull
    private String address;
    @NotNull
    private String idcard;
    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private int role;
    
    @JsonIgnore
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Saving> savings;
}