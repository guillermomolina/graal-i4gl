package i4gl.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import i4gl.common.NumberFormatter;
import i4gl.exceptions.NumberFormatterOverflowException;

public class I4GLNumberFormatterTest {

    @Test
    public void test1() throws Exception {
        check("#####", 0, "     ");
        check("&&&&&", 0, "00000");
        check("$$$$$", 0, "    $");
        check("*****", 0, "*****");
        check("<<<<<", 0, null);
    }

    @Test
    public void test2() throws Exception {
        check("<<<,<<<", 12345, "12,345");
        check("<<<,<<<", 1234, "1,234");
        check("<<<,<<<", 123, "123");
        check("<<<,<<<", 12, "12");
    }

    @Test
    public void test3() throws Exception {
        check("##,###", 12345, "12,345");
        check("##,###", 1234, " 1,234");
        check("##,###", 123, "   123");
        check("##,###", 12, "    12");
        check("##,###", 1, "     1");
        check("##,###", -1, "     1");
        check("##,###", 0, "      ");
    }

    @Test
    public void test4() throws Exception {
        check("&&,&&&", 12345, "12,345");
        check("&&,&&&", 1234, "01,234");
        check("&&,&&&", 123, "000123");
        check("&&,&&&", 12, "000012");
        check("&&,&&&", 1, "000001");
        check("&&,&&&", -1, "000001");
        check("&&,&&&", 0, "000000");
    }

    @Test
    public void test5() throws Exception {
        check("&&,&&&.&&", 12345.67, "12,345.67");
        check("&&,&&&.&&", 1234.56, "01,234.56");
        check("&&,&&&.&&", 123.45, "000123.45");
        check("&&,&&&.&&", 0.01, "000000.01");
    }

    @Test
    public void test6() throws Exception {
        check("$$,$$$", 12345, "******");
        check("$$,$$$", 1234, "$1,234");
        check("$$,$$$", 123, "  $123");
        check("$$,$$$", 12, "   $12");
        check("$$,$$$", 1, "    $1");
        check("$$,$$$", 0, "     $");
    }

    @Test
    public void test7() throws Exception {
        check("**,***", 12345, "12,345");
        check("**,***", 1234, "*1,234");
        check("**,***", 123, "***123");
        check("**,***", 12, "****12");
        check("**,***", 1, "*****1");
        check("**,***", 0, "******");
    }

    @Test
    public void test8() throws Exception {
        check("##,###.##", 12345.67, "12,345.67");
        check("##,###.##", 1234.56, " 1,234.56");
        check("##,###.##", 123.45, "   123.45");
        check("##,###.##", 12.34, "    12.34");
        check("##,###.##", 1.23, "     1.23");
        // Different from the manual " 0.12"
        check("##,###.##", 0.12, "      .12");
        check("##,###.##", 0.01, "      .01");
        check("##,###.##", -0.01, "      .01");
        check("##,###.##", -1.0, "     1.00");
    }

    @Test
    public void test9() throws Exception {
        check("$$,$$$.$$", 12345.67, "*********");
        check("$$,$$$.$$", 1234.56, "$1,234.56");
        check("$$,$$$.##", 0.0, "$.00");
        check("$$,$$$.##", 1234.0, "$1,234.00");
        check("$$,$$$.&&", 0.0, "$.00");
        check("$$,$$$.&&", 1234.0, "$1,234.00");
    }

    @Test
    public void test10() throws Exception {
        check("-$$$,$$$.&&", -12345.67, "-$12,345.67");
        check("-$$$,$$$.&&", -1234.56, "- $1,234.56");
        check("-$$$,$$$.&&", -123.45, "-   $123.45");
    }

    @Test
    public void test11() throws Exception {
        check("--$$,$$$.&&", -12345.67, "-$12,345.67");
        check("--$$,$$$.&&", -1234.56, "-$1,234.56");
        check("--$$,$$$.&&", -123.45, "-  $123.45");
        check("--$$,$$$.&&", -12.34, "-   $12.34");
        check("--$$,$$$.&&", -1.23, "-    $1.23");
    }

    @Test
    public void test12() throws Exception {
        check("-##,###.##", -12345.67, "-12,345.67");
        check("-##,###.##", -123.45, "-   123.45");
        check("-##,###.##", -12.34, "-    12.34");

        check("-##,###.##", 12345.67, "12,345.67");
        check("-##,###.##", 1234.56, "1,234.56");
        check("-##,###.##", 123.45, "123.45");
        check("-##,###.##", 12.34, "12.34");
    }

    @Test
    public void test13() throws Exception {
        check("--#,###.##", -12.34, "-   12.34");

        check("--#,###.##", 12.34, "12.34");
    }

    @Test
    public void test14() throws Exception {
        check("---,###.##", -12.34, "-  12.34");

        check("---,###.##", 12.34, "12.34");
    }

    @Test
    public void test15() throws Exception {
        check("---,-##.##", -12.34, "-12.34");

        check("---,-##.##", 12.34, "12.34");
    }

    @Test
    public void test16() throws Exception {
        check("---,--#.##", -1.0, "-1.00");
    }

    @Test
    public void test17() throws Exception {
        check("---,---.##", 1.0, "1.00");

        check("---,---.--", -0.01, "-.01");

        check("---,---.&&", -0.01, "-.01");
    }

    @Test
    public void test18() throws Exception {
        check("----,--$.&&", -12345.67, "-$12,345.67");
        check("----,--$.&&", -1234.56, "-$1,234.56");
        check("----,--$.&&", -123.45, "-$123.45");
        check("----,--$.&&", -12.34, "-$12.34");
        check("----,--$.&&", -1.23, "-$1.23");
        check("----,--$.&&", -.12, "-$.12");
    }

    @Test
    public void test19() throws Exception {
        check("$***,***.&&", 12345.67, "$*12,345.67");
        check("$***,***.&&", 1234.56, "$**1,234.56");
        check("$***,***.&&", 123.45, "$****123.45");
        check("$***,***.&&", 12.34, "$*****12.34");
        check("$***,***.&&", 1.23, "$******1.23");
        check("$***,***.&&", .12, "$*******.12");
    }

    @Test
    public void test20() throws Exception {
        check("($$$,$$$.&&)", -12345.67, "($12,345.67)");
        check("($$$,$$$.&&)", -1234.56, "( $1,234.56)");
        check("($$$,$$$.&&)", -123.45, "(   $123.45)");
    }

    @Test
    public void test21() throws Exception {
        check("(($$,$$$.&&)", -12345.67, "($12,345.67)");
        check("(($$,$$$.&&)", -1234.56, "($1,234.56)");
        check("(($$,$$$.&&)", -123.45, "(  $123.45)");
        check("(($$,$$$.&&)", -12.34, "(   $12.34)");
        check("(($$,$$$.&&)", -1.23, "(    $1.23)");

        check("(($$,$$$.&&)", 12345.67, "$12,345.67");
        check("(($$,$$$.&&)", 1234.56, "$1,234.56");
        check("(($$,$$$.&&)", 123.45, "$123.45");
        check("(($$,$$$.&&)", 12.34, "$12.34");
        check("(($$,$$$.&&)", 1.23, "$1.23");
    }

    @Test
    public void test22() throws Exception {
        check("((((,(($.&&)", -12345.67, "($12,345.67)");
        check("((((,(($.&&)", -1234.56, "($1,234.56)");
        check("((((,(($.&&)", -123.45, "($123.45)");
        check("((((,(($.&&)", -12.34, "($12.34)");
        check("((((,(($.&&)", -1.23, "($1.23)");
        check("((((,(($.&&)", -.12, "($.12)");

        check("((((,(($.&&)", 12345.67, "$12,345.67");
        check("((((,(($.&&)", 1234.56, "$1,234.56");
        check("((((,(($.&&)", 123.45, "$123.45");
        check("((((,(($.&&)", 12.34, "$12.34");
        check("((((,(($.&&)", 1.23, "$1.23");
        check("((((,(($.&&)", .12, "$.12");
    }

    @Test
    public void test23() throws Exception {
        check("($$$,$$$.&&)", 12345.67, "$12,345.67");
        check("($$$,$$$.&&)", 1234.56, "$1,234.56");
        check("($$$,$$$.&&)", 123.45, "$123.45");
    }

    void check(String format, long data, String expected) {
        String output;
        try {
            output = NumberFormatter.Format(format, data);
        } catch (NumberFormatterOverflowException e) {
            output = null;
        }
        assertEquals(expected, output);
    }

    void check(String format, double data, String expected) {
        String output = NumberFormatter.Format(format, data);
        assertEquals(expected, output);
    }
}
