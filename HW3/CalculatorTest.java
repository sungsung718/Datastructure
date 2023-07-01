import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class CalculatorTest
{
    private static boolean isLastTokenSpace;

    // ignore space when considering the last token
    private static boolean isLastTokenDigit;
    private static boolean isLastTokenOperator;
    private static boolean isLastTokenOpeningBracket;

    private static StringBuilder numBuilder = new StringBuilder();
    private static Stack<Character> operatorStack = new Stack<>();
    private static ArrayList<String> postfix = new ArrayList<>();
    private static Stack<Long> numStack = new Stack<>();
    private static final HashMap<Character, Integer> PRECEDENCE = new HashMap<>();
    static {
        PRECEDENCE.put(',', 0);
        PRECEDENCE.put('+', 1);
        PRECEDENCE.put('-', 1);
        PRECEDENCE.put('*', 2);
        PRECEDENCE.put('/', 2);
        PRECEDENCE.put('%', 2);
        PRECEDENCE.put('~', 3);
        PRECEDENCE.put('^', 4);
    }

    public static void main(String args[])
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true)
        {
            try
            {
                String input = br.readLine();
                if (input.compareTo("q") == 0)
                    break;

                command(input);
            }
            catch (Exception e)
            {
                System.out.println("ERROR");
            }
        }
    }

    private static void command(String input)
    {
        long result = evaluate(input);
        printPostfix();
        System.out.println(result);
    }

    private static long evaluate(String input) {
        convertInfixToPostfix(input);
        long result = evaluatePostfix();
        return result;
    }

    private static void printPostfix() {
        StringBuilder sb = new StringBuilder();
        for (String token: postfix) {
            sb.append(token);
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        System.out.println(sb);
    }

    private static void convertInfixToPostfix(String input) {
        isLastTokenDigit = false;
        isLastTokenSpace = false;
        isLastTokenOperator = false;
        isLastTokenOpeningBracket = false;

        // initializing numBuilder, operatorStack, postfix
        numBuilder.setLength(0);
        operatorStack.clear();
        postfix.clear();

        input = input.trim();

        for (int i=0; i<input.length(); i++) {
            char token = input.charAt(i);

            if (Character.isWhitespace(token)) {
                processSpaceInInfix();
            } else if (Character.isDigit(token)) {
                processDigitInInfix(i, input);
            } else {
                processNonDigitInInfix(i, input);
            }
        }

        if (isLastTokenDigit) {
            postfix.add(numBuilder.toString());
        }

        while (!operatorStack.isEmpty()) {
            postfix.add(String.valueOf(operatorStack.pop()));
        }
    }

    private static void processSpaceInInfix() {
        if (!isLastTokenSpace && isLastTokenDigit && !numBuilder.toString().equals("")) {
            postfix.add(numBuilder.toString());
        }
        isLastTokenSpace = true;
    }

    private static void processDigitInInfix(int index, String infix) {
        char token = infix.charAt(index);
        if (isLastTokenSpace || !isLastTokenDigit) {
            numBuilder.setLength(0); // initialize numBuilder
        }
        numBuilder.append(token);

        isLastTokenSpace = false;
        isLastTokenDigit = true;
        isLastTokenOpeningBracket = false;
        isLastTokenOperator = false;
    }

    private static void processNonDigitInInfix(int index, String infix) {
        char token = infix.charAt(index);
        if (!isLastTokenSpace && isLastTokenDigit && !numBuilder.toString().equals("")) {
            postfix.add(numBuilder.toString());
        }

        if (token == '(') {
            processOpeningBracketInInfix(index);
        } else if (token == ')') {
            processClosingBracketInInfix();
        } else if (isOperator(token)) {
            processOperatorInInfix(index, infix);
        } else if (token == ','){
            processCommaInInfix();
        } else {
            throw new IllegalArgumentException();
        }

        isLastTokenSpace = false;
        isLastTokenDigit = false;
    }

    private static void processOpeningBracketInInfix(int index) {
        if (index==0 || isLastTokenOpeningBracket || isLastTokenOperator) {
            operatorStack.push('(');
            isLastTokenOpeningBracket = true;
            isLastTokenOperator = false;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void processClosingBracketInInfix() {
        boolean isOpeningBracket = false;
        boolean isAvgCalculation = false;
        int lenOfAvg = 0;

        while (!operatorStack.isEmpty()) {
            char operator = operatorStack.pop();
            if (operator == ',') {
                isAvgCalculation = true;
                lenOfAvg++;
            }
            else {
                if (operator == '(') {
                    isOpeningBracket = true;
                    lenOfAvg++;
                    break;
                }
                postfix.add(String.valueOf(operator));
            }
        }

        if (!isOpeningBracket) {
            throw new IllegalArgumentException();
        }

        if (isAvgCalculation) {
            postfix.add(Integer.toString(lenOfAvg));
            postfix.add("avg");
        }

        isLastTokenOpeningBracket = false;
        isLastTokenOperator = false;
    }

    private static void processOperatorInInfix(int index, String infix) {
        char token = infix.charAt(index);

        if (index == 0 || isLastTokenOpeningBracket || isLastTokenOperator) {
            if (token != '-') {
                throw new IllegalArgumentException();
            }
            token = '~';
        }

        if (operatorStack.isEmpty()) {
            operatorStack.push(token);
        }
        else {
            while (!operatorStack.isEmpty() &&
                    compareOperators(operatorStack.peek(), token) > 0) {
                postfix.add(String.valueOf(operatorStack.pop()));
            }
            operatorStack.push(token);
        }

        isLastTokenOpeningBracket = false;
        isLastTokenOperator = true;
    }

    private static void processCommaInInfix() {
        while (operatorStack.peek() != ',' && operatorStack.peek() != '(') {
            postfix.add(String.valueOf(operatorStack.pop()));
        }
        operatorStack.push(',');
        isLastTokenOpeningBracket = false;
        isLastTokenOperator = true;
    }

    private static boolean isOperator(char token) {
        return (token == '+' || token == '-' || token == '*' || token == '/' || token == '%' ||
                token == '^');
    }

    private static int compareOperators(char operator1, char operator2) {
        if (operator1 == '(')
            return 0;

        int val1 = PRECEDENCE.get(operator1);
        int val2 = PRECEDENCE.get(operator2);

        if (val1 > val2)
            return 1;
        if (val1 == val2 && (!isRightAssociative(operator1))) {
            return 1;
        }
        return 0;
    }

    private static boolean isRightAssociative(char operator) {
        return (operator == '~' || operator == '^');
    }

    private static long evaluatePostfix() {
        // initialize numStack
        numStack.clear();

        for (String token: postfix) {
            if (Character.isDigit(token.charAt(0))) {
                processDigitInPostfix(token);
            }
            else if (token.equals("~")) {
                processUnaryMinusInPostfix();
            } else if (token.equals("avg")) {
                processAvgInPostfix();
            } else {
                processOperatorInPostfix(token);
            }
        }

        long result = numStack.pop();
        if (!numStack.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return result;
    }

    private static void processDigitInPostfix(String token) {
        numStack.push(Long.parseLong(token));
    }

    private static void processUnaryMinusInPostfix() {
        numStack.push(-1*numStack.pop());
    }

    private static void processAvgInPostfix() {
        long total = 0;
        long len = numStack.pop();

        for (int i=0; i<len; i++) {
            total += numStack.pop();
        }

        numStack.add(total / len);
    }

    private static void processOperatorInPostfix(String token) {
        long num1 = numStack.pop();
        long num2 = numStack.pop();
        switch (token) {
            case "+":
                numStack.push(num1+num2);
                break;
            case "-":
                numStack.push(num2-num1);
                break;
            case "*":
                numStack.push(num1*num2);
                break;
            case "/":
                numStack.push(num2/num1);
                break;
            case "%":
                numStack.push(num2%num1);
                break;
            case "^":
                if (num2==0 && num1<0) {
                    throw new ArithmeticException();
                }
                numStack.push((long) Math.pow(num2, num1));

        }
    }
}
