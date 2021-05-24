package bank.deposit.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import bank.deposit.web.ValidateFunction;

import java.sql.Date;
import java.util.Calendar;

@SpringBootTest
@AutoConfigureMockMvc
public class ValidateFunctionTest {
    private final ValidateFunction val;

    @Autowired
    public ValidateFunctionTest(ValidateFunction val) {
        this.val = val;
    }

    @Test
    /**
     * Input have some character
     */
    void isAllNumberInputWrong() {
        String s = "123123aaa";
        assertEquals(val.isAllNumber(s), false);
    }

    @Test
    /**
     * Input have some speacial character
     */
    void isAllNumberInputWrong2() {
        String s = "123123$%#";
        assertEquals(val.isAllNumber(s), false);
    }

    @Test
    /**
     * Input is null
     */
    void isAllNumberInputWrong3() {
        String s = "";
        assertEquals(val.isAllNumber(s), true);
    }

    @Test
    /**
     * Input have negative number
     */
    void isAllNumberInputWrong4() {
        String s = "-1231231231";
        assertEquals(val.isAllNumber(s), false);
    }

    @Test
    /**
     * Input is all digit
     */
    void isAllNumberInputRight() {
        String s = "12312311";
        assertEquals(val.isAllNumber(s), true);
    }

    @Test
    /**
     * Input with not all digit string
     */
    void verifyIdInputWrong() {
        String s = "1232aaa";
        assertEquals(val.verifyId(s), false);
    }

    @Test
    /**
     * Input with size is 9
     */
    void verifyIdInputSizeWrong() {
        String s = "123123123";
        assertEquals(val.verifyId(s), false);
    }

    @Test
    /**
     * Input is null
     */
    void verifyIdInputWrong2() {
        String s = "";
        assertEquals(val.verifyId(s), false);
    }

    @Test
    /**
     * Input is negative number
     */
    void verifyIdInputNegativeNumber() {
        String s = "-123123123";
        assertEquals(val.verifyId(s), false);
    }

    @Test
    /**
     * Input is all digit and size = 10
     */
    void verifyIdInputRight() {
        String s = "1231231234";
        assertEquals(val.verifyId(s), true);
    }

    @Test
    /**
     * Input is all digit and size > 10
     */
    void verifyIdInputWrongSize() {
        String s = "123123123412312";
        assertEquals(val.verifyId(s), false);
    }

    @Test
    /**
     * Input have some speacial character
     */
    void speacialKeyExistSpeacial() {
        String s = "123123###";
        assertEquals(val.specialKey(s), false);
    }

    @Test
    /**
     * Input don't have speacial character
     */
    void speacialKeyWithNoSpeacial() {
        String s = "123123";
        assertEquals(val.specialKey(s), true);
    }

    @Test
    /**
     * Input is null
     */
    void speacialKeyWithNullInput() {
        String s = "";
        assertEquals(val.specialKey(s), true);
    }

    @Test
    /**
     * Input year is 2010 which make different year lower than 18
     */
    void validDOBBelow() {
        java.util.Date now = new java.util.Date();
        now.setYear(2010);
        Date date = new Date(now.getTime());
        assertEquals(val.validDOB(date), false);
    }

    @Test
    /**
     * Input year is 2003 which make different year same as 18
     */
    void validDOBSame() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2003);
        Date date = new Date(cal.getTime().getTime());
        assertEquals(val.validDOB(date), true);
    }

    @Test
    /**
     * Input year is 2023 which make different year is a negative number
     */
    void validDOBInvalid() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2023);
        Date date = new Date(cal.getTime().getTime());
        assertEquals(val.validDOB(date), false);
    }

    @Test
    /**
     * Input year is 1999 which make different year higher than 18
     */
    void validDOBHigh() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 1999);
        Date date = new Date(cal.getTime().getTime());
        assertEquals(val.validDOB(date), true);
    }

    @Test
    /**
     * Valid Email
     */
    void validEmail() {
        String email = "kienpt@gmail.com";
        assertEquals(val.validateEmail(email), true);
    }

    @Test
    /**
     * Invalid Email - No @
     */
    void invalidEmail() {
        String email = "kienpt";
        assertEquals(val.validateEmail(email), false);
    }

    @Test
    /**
     * Invalid Email - Only @gmail.com
     */
    void invalidEmail2() {
        String email = "@gmail.com";
        assertEquals(val.validateEmail(email), false);
    }

    @Test
    /**
     * Invalid Email - No .com
     */
    void invalidEmail3() {
        String email = "kienpt@gmail";
        assertEquals(val.validateEmail(email), false);
    }

    @Test
    /**
     * Invalid Email - Special Character
     */
    void invalidEmail4() {
        String email = "ki!#!#enpt@gmail";
        assertEquals(val.validateEmail(email), false);
    }

    @Test
    /**
     * Name contain number check - no number
     */
    void nameNoNumber() {
        String name = "kien";
        assertEquals(val.isContainsNumber(name), false);
    }

    @Test
    /**
     * Name contain number check - number at first index
     */
    void nameHasNumberFirst() {
        String name = "0kien";
        assertEquals(val.isContainsNumber(name), true);
    }

    @Test
    /**
     * Name contain number check - number at last index
     */
    void nameHasNumberLast() {
        String name = "kien0";
        assertEquals(val.isContainsNumber(name), true);
    }

    @Test
    /**
     * Name contain number check - number at middle index
     */
    void nameHasNumberMiddle() {
        String name = "ki0en";
        assertEquals(val.isContainsNumber(name), true);
    }

    @Test
    /**
     * Name contain number check - all number
     */
    void nameAllNumber() {
        String name = "00000000";
        assertEquals(val.isContainsNumber(name), true);
    }

}
