import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BigInteger
{
    public static final String QUIT_COMMAND = "quit";
    public static final String MSG_INVALID_INPUT = "Wrong Input";

    // implement this
    public static final String REGEX =
            "\\s*([\\+\\-\\*]?)\\s*(\\d+)\\s*([\\+\\-\\*])\\s*([\\+\\-\\*]?)\\s*(\\d+)\\s*";
    public static final Pattern EXPRESSION_PATTERN = Pattern.compile(
            REGEX);

    public int len = 0;

    public int sign = 1;

    public byte[] abs;

    public BigInteger()
    {
        this.sign = 0;
    }

    public BigInteger(int i)
    {
        this.len = i;
        this.abs = new byte[i];
        initArray(this.abs);
    }

    public BigInteger(byte[] num1) {
        this.len = num1.length;
        this.abs = num1;
    }

    public BigInteger(byte[] num1, int sign) {
        this.len = num1.length;
        this.sign = sign;
        this.abs = num1;
    }

    public BigInteger(String s, int sign) {
        int strlen = s.length();
        if (strlen==1 && s.equals("0")) {
            this.sign = 0;
        }

        else {
            this.abs = new byte[strlen];
            for (int i = 0; i < strlen; i++) {
                abs[strlen - i - 1] = (byte) (s.charAt(i) - '0');
            }
            this.len = strlen;
            this.sign = sign;
        }
    }

    private int compareAbs(BigInteger num) {
        int cmp;

        cmp = this.len - num.len;
        if (cmp>0) return 1;
        if (cmp<0) return -1;

        for (int i=len-1; i>=0; i--) {
            cmp = this.abs[i] - num.abs[i];
            if (cmp>0) return 1;
            if (cmp<0) return -1;
        }

        return 0;
    }

    private void initArray(byte[] arr) {
        Arrays.fill(arr, (byte) 0);
    }

    public BigInteger add(BigInteger big) {
        if (this.sign==0) return big;
        if (big.sign==0) return this;

        if (this.sign == big.sign) {
            return addBigOfSameSign(big);
        }
        return addBigOfDifferentSign(big);
    }

    private BigInteger addBigOfSameSign(BigInteger big) {
        byte carry = 0;
        byte digit1;
        byte digit2;
        int maxLen = Math.max(this.len, big.len);
        byte[] resultArr = new byte[maxLen+1];

        initArray(resultArr);

        for (int i=0; i<maxLen; i++) {
            digit1 = (i < this.len)? this.abs[i]:0;
            digit2 = (i < big.len)? big.abs[i]:0;
            byte tmp = (byte) (digit1 + digit2 + carry);
            resultArr[i] = (byte) (tmp % 10);
            carry = (byte) (tmp / 10);
        }
        if (carry > 0) {
            resultArr[maxLen] = carry;
        }

        return new BigInteger(resultArr, sign);
    }

    private BigInteger addBigOfDifferentSign(BigInteger big) {
        return subtractBigOfSameSign(big);
    }

    public BigInteger subtract(BigInteger big)
    {
        if (big.sign == 0) return this;
        if (this.sign == 0) return new BigInteger(big.abs, -1*big.sign);

        if (this.sign == big.sign) {
            return subtractBigOfSameSign(big);
        }

        return subtractBigOfDifferentSign(big);

    }

    private BigInteger subtractBigOfSameSign(BigInteger big) {
        int cmp = compareAbs(big);
        if (cmp==0) return new BigInteger();

        int maxLen = Math.max(this.len, big.len);
        byte digit1;
        byte digit2;
        byte borrow=0;
        byte[] resultArr = new byte[maxLen];
        initArray(resultArr);

        BigInteger num1 = (cmp > 0)? this : big;
        BigInteger num2 = (cmp > 0)? big : this;
        for (int i=0; i<maxLen; i++) {
            digit1 = num1.abs[i];
            digit2 = (i < num2.len)? num2.abs[i]:0;
            byte tmp = (byte) (digit1 - digit2 - borrow);
            resultArr[i] = (tmp >= 0)? tmp: (byte) (tmp + 10);
            borrow = (byte) ((tmp>=0)? 0:1);
        }

        int resultSign = (sign == cmp)? 1:-1;

        return new BigInteger(resultArr, resultSign);
    }

    private BigInteger subtractBigOfDifferentSign(BigInteger big) {
        return addBigOfSameSign(big);
    }


    public BigInteger multiply(BigInteger big) {
        if (this.sign==0) return this;
        if (big.sign==0) return big;

        BigInteger result = new BigInteger(this.len + 1);
        for (int i=0; i<big.len; i++) {
            result = result.add(multiplyByEachDigit(big, i));
        }

        result.sign = determineSignOfMul(big);

        return result;
    }

    private BigInteger multiplyByEachDigit(BigInteger big, int digitNum) {
        byte carry = 0;
        byte digitOfBig = big.abs[digitNum];
        byte[] resultArr = new byte[this.len+digitNum+1];

        if (digitOfBig==0) return new BigInteger();

        initArray(resultArr);
        for (int i=0; i<this.len; i++) {
            byte tmp = (byte) (this.abs[i] * digitOfBig + carry);
            resultArr[i+digitNum] = (byte) (tmp % 10);
            carry = (byte) (tmp / 10);
        }
        if (carry > 0) resultArr[this.len+digitNum] = carry;

        return new BigInteger(resultArr);
    }

    private int determineSignOfMul(BigInteger big) {
        return (this.sign == big.sign)? 1:-1;
    }

    @Override
    public String toString() {
        if (sign==0) return "0";

        StringBuilder sb = new StringBuilder();
        if (sign==-1) sb.append("-");

        while (abs[len-1] == 0) {
            len--;
        }
        for (int i=len-1; i>=0; i--) {
            sb.append(abs[i]);
        }

        return sb.toString();
    }

    static BigInteger evaluate(String input) throws IllegalArgumentException {
        // implement here
        // parse input
        // using regex is allowed
        Matcher matcher = EXPRESSION_PATTERN.matcher(input);
        String signOfNum1InString;
        String arg1;
        String operator;
        String signOfNum2InString;
        String arg2;
        int sign1;
        int sign2;

        if (matcher.find()) {
            signOfNum1InString = matcher.group(1);
            arg1 = matcher.group(2);
            operator = matcher.group(3);
            signOfNum2InString = matcher.group(4);
            arg2 = matcher.group(5);
        } else {
            throw new IllegalArgumentException();
        }

        sign1 = convertSignToInt(signOfNum1InString);
        sign2 = convertSignToInt(signOfNum2InString);

        // One possible implementation
        BigInteger num1 = new BigInteger(arg1, sign1);
        BigInteger num2 = new BigInteger(arg2, sign2);

        if (operator.equals("+")) {
            return num1.add(num2);
        }
        else if (operator.equals("-")) {
            return num1.subtract(num2);
        }
        else if (operator.equals("*")) {
            return num1.multiply(num2);
        }
        else throw new IllegalArgumentException();
    }

    private static int convertSignToInt(String signInString) {
        if (signInString.equals("-")) {
            return -1;
        }
        return 1;
    }

    public static void main(String[] args) throws Exception
    {
        try (InputStreamReader isr = new InputStreamReader(System.in))
        {
            try (BufferedReader reader = new BufferedReader(isr))
            {
                boolean done = false;
                while (!done)
                {
                    String input = reader.readLine();

                    try
                    {
                        done = processInput(input);
                    }
                    catch (IllegalArgumentException e)
                    {
                        System.err.println(MSG_INVALID_INPUT);
                    }
                }
            }
        }
    }

    static boolean processInput(String input) throws IllegalArgumentException
    {
        boolean quit = isQuitCmd(input);

        if (quit)
        {
            return true;
        }
        else
        {
            BigInteger result = evaluate(input);
            System.out.println(result.toString());

            return false;
        }
    }

    static boolean isQuitCmd(String input)
    {
        return input.equalsIgnoreCase(QUIT_COMMAND);
    }
}