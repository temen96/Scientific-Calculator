import java.util.*;

/**
 * A small scientific expression evaluator (no external libs).
 * Supports:
 * - Numbers (e.g., 3, 3.14, .5)
 * - Operators: +, -, *, /, %, ^ (power)
 * - Parentheses: ( )
 * - Unary minus: -5, -(2+3)
 * - Constants: pi, e
 * - Functions:
 *   sin, cos, tan, asin, acos, atan,
 *   sinh, cosh, tanh,
 *   log (base10), ln,
 *   sqrt, cbrt, abs,
 *   exp,
 *   floor, ceil, round,
 *   fact (factorial),
 *   inv (1/x)
 *
 * Trig functions can operate in degrees or radians depending on the evaluator mode.
 */
public class ExpressionEvaluator {

    public enum AngleMode { RAD, DEG }

    private AngleMode angleMode = AngleMode.RAD;

    public void setAngleMode(AngleMode mode) { this.angleMode = mode; }
    public AngleMode getAngleMode() { return angleMode; }

    public double eval(String input) {
        if (input == null) throw new IllegalArgumentException("Expression is null");
        String expr = input.trim();
        if (expr.isEmpty()) throw new IllegalArgumentException("Empty expression");

        List<Token> tokens = tokenize(expr);
        List<Token> rpn = toRPN(tokens);
        return evalRPN(rpn);
    }

    private enum Type { NUMBER, OP, LPAREN, RPAREN, IDENT, COMMA }

    private static class Token {
        final Type type;
        final String text;
        final double number;
        Token(Type type, String text) { this.type = type; this.text = text; this.number = Double.NaN; }
        Token(double number) { this.type = Type.NUMBER; this.text = Double.toString(number); this.number = number; }
        @Override public String toString() { return type + ":" + text; }
    }

    private List<Token> tokenize(String expr) {
        List<Token> out = new ArrayList<>();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) { i++; continue; }

            if (Character.isDigit(c) || c == '.') {
                int start = i;
                boolean dotSeen = (c == '.');
                i++;
                while (i < expr.length()) {
                    char ch = expr.charAt(i);
                    if (Character.isDigit(ch)) i++;
                    else if (ch == '.' && !dotSeen) { dotSeen = true; i++; }
                    else break;
                }
                String numStr = expr.substring(start, i);
                if (numStr.equals(".")) throw new IllegalArgumentException("Invalid number: '.'");
                out.add(new Token(Double.parseDouble(numStr)));
                continue;
            }

            if (Character.isLetter(c)) {
                int start = i;
                i++;
                while (i < expr.length() && (Character.isLetterOrDigit(expr.charAt(i)) || expr.charAt(i) == '_')) i++;
                String ident = expr.substring(start, i).toLowerCase(Locale.ROOT);
                out.add(new Token(Type.IDENT, ident));
                continue;
            }

            if (c == '(') { out.add(new Token(Type.LPAREN, "(")); i++; continue; }
            if (c == ')') { out.add(new Token(Type.RPAREN, ")")); i++; continue; }
            if (c == ',') { out.add(new Token(Type.COMMA, ",")); i++; continue; }

            if ("+-*/%^".indexOf(c) >= 0) { out.add(new Token(Type.OP, Character.toString(c))); i++; continue; }

            throw new IllegalArgumentException("Unexpected character: '" + c + "'");
        }
        return out;
    }

    private static class OpInfo {
        final int prec;
        final boolean rightAssoc;
        final int arity;
        OpInfo(int prec, boolean rightAssoc, int arity) { this.prec = prec; this.rightAssoc = rightAssoc; this.arity = arity; }
    }

    private static final Map<String, OpInfo> OPS = new HashMap<>();
    static {
        OPS.put("+", new OpInfo(1, false, 2));
        OPS.put("-", new OpInfo(1, false, 2));
        OPS.put("*", new OpInfo(2, false, 2));
        OPS.put("/", new OpInfo(2, false, 2));
        OPS.put("%", new OpInfo(2, false, 2));
        OPS.put("^", new OpInfo(4, true, 2));
        OPS.put("u-", new OpInfo(3, true, 1)); // unary minus
    }

    private List<Token> toRPN(List<Token> tokens) {
        List<Token> out = new ArrayList<>();
        Deque<Token> stack = new ArrayDeque<>();

        Token prev = null;
        for (Token t0 : tokens) {
            Token t = t0;

            switch (t.type) {
                case NUMBER:
                    out.add(t);
                    break;

                case IDENT:
                    stack.push(t); // function/constant
                    break;

                case COMMA:
                    while (!stack.isEmpty() && stack.peek().type != Type.LPAREN) out.add(stack.pop());
                    if (stack.isEmpty()) throw new IllegalArgumentException("Misplaced comma or missing '('");
                    break;

                case OP: {
                    String op = t.text;

                    if (op.equals("-")) {
                        if (prev == null || prev.type == Type.OP || prev.type == Type.LPAREN || prev.type == Type.COMMA) {
                            op = "u-";
                            t = new Token(Type.OP, op);
                        }
                    }

                    OpInfo o1 = OPS.get(op);
                    if (o1 == null) throw new IllegalArgumentException("Unknown operator: " + op);

                    while (!stack.isEmpty() && stack.peek().type == Type.OP) {
                        String op2 = stack.peek().text;
                        OpInfo o2 = OPS.get(op2);
                        if (o2 == null) break;

                        boolean pop = (o1.rightAssoc && o1.prec < o2.prec) || (!o1.rightAssoc && o1.prec <= o2.prec);
                        if (pop) out.add(stack.pop()); else break;
                    }
                    stack.push(t);
                    break;
                }

                case LPAREN:
                    stack.push(t);
                    break;

                case RPAREN:
                    while (!stack.isEmpty() && stack.peek().type != Type.LPAREN) out.add(stack.pop());
                    if (stack.isEmpty()) throw new IllegalArgumentException("Mismatched ')'");
                    stack.pop(); // '('
                    if (!stack.isEmpty() && stack.peek().type == Type.IDENT) out.add(stack.pop()); // function call
                    break;

                default:
                    throw new IllegalStateException("Unhandled token: " + t);
            }
            if (t.type != Type.COMMA) prev = t;
        }

        while (!stack.isEmpty()) {
            Token t = stack.pop();
            if (t.type == Type.LPAREN || t.type == Type.RPAREN) throw new IllegalArgumentException("Mismatched parentheses");
            out.add(t);
        }
        return out;
    }

    private double evalRPN(List<Token> rpn) {
        Deque<Double> st = new ArrayDeque<>();
        for (Token t : rpn) {
            if (t.type == Type.NUMBER) { st.push(t.number); continue; }

            if (t.type == Type.OP) {
                OpInfo info = OPS.get(t.text);
                if (info == null) throw new IllegalArgumentException("Unknown operator: " + t.text);
                if (st.size() < info.arity) throw new IllegalArgumentException("Not enough operands for operator " + t.text);

                if (info.arity == 1) {
                    double a = st.pop();
                    st.push(applyUnaryOp(t.text, a));
                } else {
                    double b = st.pop();
                    double a = st.pop();
                    st.push(applyBinaryOp(t.text, a, b));
                }
                continue;
            }

            if (t.type == Type.IDENT) {
                String id = t.text;

                if (id.equals("pi")) { st.push(Math.PI); continue; }
                if (id.equals("e"))  { st.push(Math.E);  continue; }

                if (st.isEmpty()) throw new IllegalArgumentException("Missing argument for function: " + id);
                double a = st.pop();
                st.push(applyFunc(id, a));
                continue;
            }

            throw new IllegalArgumentException("Unexpected token in RPN: " + t);
        }

        if (st.size() != 1) throw new IllegalArgumentException("Invalid expression");
        double result = st.pop();
        if (Double.isNaN(result) || Double.isInfinite(result)) throw new ArithmeticException("Result is not a finite number");
        return result;
    }

    private double applyUnaryOp(String op, double a) {
        if (op.equals("u-")) return -a;
        throw new IllegalArgumentException("Unknown unary operator: " + op);
    }

    private double applyBinaryOp(String op, double a, double b) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0.0) throw new ArithmeticException("Division by zero");
                return a / b;
            case "%":
                if (b == 0.0) throw new ArithmeticException("Modulo by zero");
                return a % b;
            case "^": return Math.pow(a, b);
            default: throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }

    private double applyFunc(String id, double a) {
        switch (id) {
            case "sin": return Math.sin(toRad(a));
            case "cos": return Math.cos(toRad(a));
            case "tan": return Math.tan(toRad(a));

            case "asin": return fromRad(Math.asin(a));
            case "acos": return fromRad(Math.acos(a));
            case "atan": return fromRad(Math.atan(a));

            case "sinh": return Math.sinh(a);
            case "cosh": return Math.cosh(a);
            case "tanh": return Math.tanh(a);

            case "log":
                if (a <= 0) throw new ArithmeticException("log(x) undefined for x<=0");
                return Math.log10(a);
            case "ln":
                if (a <= 0) throw new ArithmeticException("ln(x) undefined for x<=0");
                return Math.log(a);

            case "sqrt":
                if (a < 0) throw new ArithmeticException("sqrt(x) undefined for x<0");
                return Math.sqrt(a);
            case "cbrt": return Math.cbrt(a);

            case "abs": return Math.abs(a);
            case "exp": return Math.exp(a);

            case "floor": return Math.floor(a);
            case "ceil": return Math.ceil(a);
            case "round": return Math.rint(a);

            case "fact": return factorial(a);
            case "inv":
                if (a == 0.0) throw new ArithmeticException("1/x undefined for x=0");
                return 1.0 / a;

            default:
                throw new IllegalArgumentException("Unknown function: " + id);
        }
    }

    private double toRad(double x) { return (angleMode == AngleMode.DEG) ? Math.toRadians(x) : x; }
    private double fromRad(double x) { return (angleMode == AngleMode.DEG) ? Math.toDegrees(x) : x; }

    private double factorial(double x) {
        if (x < 0) throw new ArithmeticException("factorial undefined for x<0");
        double rounded = Math.rint(x);
        if (Math.abs(x - rounded) > 1e-9) throw new ArithmeticException("factorial defined for integers only");
        int n = (int) rounded;
        if (n > 170) throw new ArithmeticException("factorial too large");
        double res = 1.0;
        for (int i = 2; i <= n; i++) res *= i;
        return res;
    }
}
