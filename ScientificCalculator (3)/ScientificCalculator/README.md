# Scientific Calculator with Unit Converter

A comprehensive Java-based scientific calculator featuring a graphical user interface built with Swing, a powerful expression evaluator, unit conversion tools, and multiple themes.

## Features

### 🧮 Scientific Calculator
- **Basic Operations**: Addition, subtraction, multiplication, division
- **Advanced Operations**: Power (^), modulo (%), unary minus
- **Trigonometric Functions**: sin, cos, tan, asin, acos, atan (with RAD/DEG toggle)
- **Hyperbolic Functions**: sinh, cosh, tanh
- **Logarithmic Functions**: log (base 10), ln (natural log)
- **Other Functions**: sqrt, cbrt, abs, exp, floor, ceil, round, factorial (fact), reciprocal (inv)
- **Constants**: π (pi), e

### 🔄 Unit Converter
- **Length**: m, cm, mm, km, inch, ft, yd, mile
- **Weight**: kg, g, mg, lb, oz
- **Temperature**: Celsius, Fahrenheit, Kelvin

### 🎨 Themes
- **Light**: Clean, bright interface
- **Dark**: Eye-friendly dark mode
- **Blue**: Professional blue-themed interface
- **High Contrast**: Accessibility-focused high contrast mode

### 📝 Additional Features
- **Expression History**: Track and reuse previous calculations
- **Memory Functions**: MC, MR, M+, M-
- **Keyboard Support**: Type expressions directly
- **Error Handling**: Clear error messages for invalid expressions
- **Decimal Formatting**: Clean display of results without unnecessary trailing zeros

## Project Structure

```
ScientificCalculator/
├── ExpressionEvaluator.java   # Core expression parsing and evaluation engine
├── ScientificCalculator.java   # Main calculator GUI with history and memory
├── UnitConverterPanel.java     # Unit conversion interface
├── Theme.java                  # Theme definitions and colors
└── README.md                   # This file
```

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Any Java IDE (Eclipse, IntelliJ IDEA, NetBeans) or command-line tools

### Compilation
```bash
javac *.java
```

### Running
```bash
java ScientificCalculator
```

## Usage Guide

### Basic Calculator Operations
1. Click number buttons or type digits
2. Use operator buttons (+, -, ×, ÷) for calculations
3. Press `=` or Enter key to evaluate
4. Use `C` to clear all, `CE` to clear entry
5. `⌫` button or Backspace to delete last character

### Function Usage
- Click function buttons (sin, cos, log, etc.) to insert them
- Functions use parentheses: `sin(30)`
- Trigonometric functions respect DEG/RAD mode
- Examples:
  - `sin(90)` = 1 (in DEG mode)
  - `log(100)` = 2
  - `sqrt(16)` = 4
  - `fact(5)` = 120
  - `inv(4)` = 0.25

### Memory Functions
- **MC**: Clear memory
- **MR**: Recall stored value
- **M+**: Add current value to memory
- **M-**: Subtract current value from memory

### Unit Converter
1. Select category (Length, Weight, Temperature)
2. Choose source and target units
3. Enter value
4. Click "Convert" or press Enter

### History Panel
- Double-click any history entry to reuse the expression
- Use "Copy Selected to Display" button
- Clear history with "Clear" button

### Keyboard Shortcuts
| Key | Action |
|-----|--------|
| 0-9 | Insert digit |
| . | Decimal point |
| + - * / | Basic operators |
| % | Modulo |
| ^ | Power |
| ( ) | Parentheses |
| Enter | Evaluate |
| Backspace | Delete last character |
| Escape | Clear all |

## Expression Evaluator Details

The `ExpressionEvaluator` class implements a full-featured mathematical expression parser using the **Shunting Yard algorithm** to convert infix notation to Reverse Polish Notation (RPN) for evaluation.

### Supported Syntax
- Numbers: `3`, `3.14`, `.5`
- Operators: `+`, `-`, `*`, `/`, `%`, `^`
- Unary minus: `-5`, `-(2+3)`
- Parentheses: `(`, `)`
- Functions: `sin(`, `cos(`, `tan(`, etc.
- Constants: `pi`, `e`
- Comma-separated arguments (for functions that would support multiple args)

### Error Handling
- Division by zero detection
- Domain errors (sqrt of negative, log of non-positive)
- Factorial of non-integers
- Mismatched parentheses
- Invalid characters

## Theme Customization

The application includes four built-in themes defined in `Theme.java`. Each theme specifies:
- `windowBg`: Main window background
- `textFg`: Text color
- `panelBg`: Panel background
- `inputBg`: Input field background
- `accent`: Accent color for selections

To add a new theme, simply add a new enum constant in `Theme.java` with your desired colors.

## Development Notes

### Expression Parsing
The evaluator handles operator precedence and associativity:
- `^` (power) is right-associative with highest precedence
- `*`, `/`, `%` are left-associative with medium precedence
- `+`, `-` are left-associative with lowest precedence
- Unary minus is handled separately with higher precedence

### RPN Evaluation
The Reverse Polish Notation stack-based evaluation ensures correct order of operations without parentheses.

### GUI Components
- `GridBagLayout` for flexible button arrangement
- `JSplitPane` for resizable calculator/history panels
- `JTabbedPane` for calculator/converter tabs
- Custom theme propagation through component hierarchy

## Limitations
- Factorial limited to n ≤ 170 (prevents overflow)
- No implicit multiplication (e.g., `2pi` must be written as `2*pi`)
- Functions require parentheses (e.g., `sin30` invalid, must be `sin(30)`)

## Future Enhancements
- [ ] Add more scientific constants
- [ ] Implement statistical functions
- [ ] Add graphing capabilities
- [ ] Support for user-defined variables
- [ ] Save/load history to file
- [ ] Additional unit categories (area, volume, speed)

## License
This project is open-source and available for educational and personal use.

## Acknowledgments
- Built with Java Swing
- Expression parsing inspired by Edsger Dijkstra's Shunting Yard algorithm
- Unit conversion factors based on standard international definitions