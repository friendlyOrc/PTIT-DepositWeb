package bank.deposit;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SquaresLoopRangeJUnit {

    SquaresLoopRangeDemo demo = new SquaresLoopRangeDemo();

    // Input hợp lệ
    @Test
    void valid1() {
        assertEquals("1 4 9", demo.SquaresLoopRange(1, 3), "Kết quả không mong muốn");

    }

    // Input hợp lệ, 2 số bằng nhau
    @Test
    void valid2() {
        assertEquals("1", demo.SquaresLoopRange(1, 1), "Kết quả không mong muốn");

    }

    // Input hợp lệ, số bắt đầu là số âm
    @Test
    void valid3() {
        assertEquals("1 0 1", demo.SquaresLoopRange(-1, 1), "Kết quả không mong muốn");

    }

    // Input hợp lệ, cả 2 số là số âm
    @Test
    void valid4() {
        assertEquals("4 1", demo.SquaresLoopRange(-2, -1), "Kết quả không mong muốn");

    }

    // Input không hợp lệ, số thứ 2 lớn hơn số thứ nhất
    @Test
    void invalid1() {
        assertEquals("Start-limit greater than stop-limit!", demo.SquaresLoopRange(3, 1), "Kết quả không mong muốn");

    }

}
