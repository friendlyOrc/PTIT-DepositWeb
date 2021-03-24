package bank.deposit;

import java.io.Serializable;
import java.sql.Date;
import java.util.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "saving")
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
    private float interest;
    @NotNull
    private int time;
    @NotNull
    @Column(name = "createtime")
    private Date createTime;
    

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "accountid")
    private Account account;
}