package bank.deposit;

public class SquaresLoopRangeDemo {

    public String SquaresLoopRange(int first, int second) {
        String rs = "";
        if (first > second) {
            rs = "Start-limit greater than stop-limit!";
        } else {
            for (int i = first; i < second; i++) {
                rs += String.valueOf(i * i) + " ";
            }
            rs += second * second;
        }
        return rs;
    }

    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println(SquaresLoopRange(1, 3));
    }

}
