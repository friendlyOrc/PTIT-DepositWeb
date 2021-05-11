package bank.deposit;

import java.io.Serializable;
import java.sql.Date;
import java.util.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "saving")
@NoArgsConstructor
@AllArgsConstructor
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class Saving implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private float balance;
    @NotNull
    private int status;
    @NotNull
    private int type;
    @NotNull
    private float interest;
    @NotNull
    private int time;
    @NotNull
    @Column(name = "createtime")
    private Date createTime;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "accountid")
    private Account account;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "staffid")
    private Account staff;
}