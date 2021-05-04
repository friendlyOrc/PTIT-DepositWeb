package bank.deposit;

import java.util.Calendar;

public class temp {
    public static boolean validDOB(java.sql.Date date) {
        java.util.Date date2 = new java.util.Date(date.getTime());
        java.util.Date date1 = new java.util.Date();
        return getDiffYears(date2, date1) >= 18;
    }

    public static int getDiffYears(java.util.Date first, java.util.Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        System.out.print(diff);
        if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH)
                || (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(java.util.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static void main(String[] args) {
        System.out.print(validDOB(java.sql.Date.valueOf("1980-04-09")));
    }
}
