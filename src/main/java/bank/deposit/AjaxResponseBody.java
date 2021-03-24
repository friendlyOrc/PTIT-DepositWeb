package bank.deposit;


import java.util.ArrayList;

import lombok.Data;

@Data
public class AjaxResponseBody {

    String msg;
    ArrayList<Account> result;

    //getters and setters

}