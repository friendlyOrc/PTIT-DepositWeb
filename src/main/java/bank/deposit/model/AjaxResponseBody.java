package bank.deposit.model;


import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class AjaxResponseBody implements Serializable{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String msg;
    private ArrayList<Account> result;
    private ArrayList<Saving> resultSav;

}