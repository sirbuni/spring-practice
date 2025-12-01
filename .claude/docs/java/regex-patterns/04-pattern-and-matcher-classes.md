# Pattern and Matcher Classes

## Table of Contents
1. [Introduction](#introduction)
2. [The Pattern Class](#the-pattern-class)
3. [Pattern Flags](#pattern-flags)
4. [The Matcher Class](#the-matcher-class)
5. [Matcher Methods](#matcher-methods)
6. [Finding and Matching](#finding-and-matching)
7. [Replacing Text](#replacing-text)
8. [Region Operations](#region-operations)
9. [Performance Considerations](#performance-considerations)
10. [Practice Examples](#practice-examples)

## Introduction

Java's regex functionality is primarily provided through two classes in the `java.util.regex` package:
- **Pattern**: Compiled representation of a regular expression
- **Matcher**: Engine that performs match operations on a character sequence

Understanding these classes is essential for effective regex usage in Java.

## The Pattern Class

The `Pattern` class is a compiled representation of a regular expression. You don't instantiate it directly; instead, you use the static `compile()` method.

### Creating Pattern Objects

```java
import java.util.regex.Pattern;

// Basic pattern compilation
Pattern pattern1 = Pattern.compile("\\d+");

// Pattern with flags
Pattern pattern2 = Pattern.compile("[a-z]+", Pattern.CASE_INSENSITIVE);

// Multiple flags using bitwise OR
Pattern pattern3 = Pattern.compile("^hello",
    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
```

### Pattern Methods

#### compile()

```java
// Static method to create a Pattern
public static Pattern compile(String regex)
public static Pattern compile(String regex, int flags)

// Example
Pattern pattern = Pattern.compile("\\w+@\\w+\\.\\w+");
```

#### matches()

```java
// Static utility method - quick matching without creating Pattern object
public static boolean matches(String regex, CharSequence input)

// Example
boolean isEmail = Pattern.matches("\\w+@\\w+\\.\\w+", "user@example.com");
System.out.println("Is email: " + isEmail);
```

**Output:**
```
Is email: true
```

**Warning:** `Pattern.matches()` requires the entire string to match. For partial matching, use `Matcher.find()`.

#### split()

```java
// Split input based on pattern
public String[] split(CharSequence input)
public String[] split(CharSequence input, int limit)

// Example
Pattern pattern = Pattern.compile("[,\\s]+");
String[] parts = pattern.split("apple, banana  cherry,  date");

for (String part : parts) {
    System.out.println(part);
}
```

**Output:**
```
apple
banana
cherry
date
```

#### pattern()

```java
// Get the regex string used to compile the Pattern
public String pattern()

// Example
Pattern pattern = Pattern.compile("\\d{3}-\\d{4}");
System.out.println("Pattern string: " + pattern.pattern());
```

**Output:**
```
Pattern string: \d{3}-\d{4}
```

#### flags()

```java
// Get the flags used to compile the Pattern
public int flags()

// Example
Pattern pattern = Pattern.compile("test", Pattern.CASE_INSENSITIVE);
System.out.println("Flags: " + pattern.flags());
```

## Pattern Flags

Flags modify how the pattern matching behaves. They can be set during compilation.

### Available Flags

| Flag | Constant | Description |
|------|----------|-------------|
| `CASE_INSENSITIVE` | `(?i)` | Makes matching case-insensitive |
| `MULTILINE` | `(?m)` | `^` and `$` match line boundaries |
| `DOTALL` | `(?s)` | `.` matches any character including newline |
| `UNICODE_CASE` | `(?u)` | Case-insensitive matching for Unicode |
| `COMMENTS` | `(?x)` | Allows whitespace and comments in pattern |
| `LITERAL` | - | Treats pattern as literal string |
| `UNICODE_CHARACTER_CLASS` | `(?U)` | Enables Unicode character classes |

### CASE_INSENSITIVE Flag

```java
String text = "Hello World HELLO hello";

// Without flag - case sensitive
Pattern pattern1 = Pattern.compile("hello");
Matcher matcher1 = pattern1.matcher(text);
System.out.println("Case-sensitive matches:");
while (matcher1.find()) {
    System.out.println("  " + matcher1.group());
}

// With flag - case insensitive
Pattern pattern2 = Pattern.compile("hello", Pattern.CASE_INSENSITIVE);
Matcher matcher2 = pattern2.matcher(text);
System.out.println("\nCase-insensitive matches:");
while (matcher2.find()) {
    System.out.println("  " + matcher2.group());
}
```

**Output:**
```
Case-sensitive matches:
  hello

Case-insensitive matches:
  Hello
  HELLO
  hello
```

### MULTILINE Flag

```java
String text = "First line\nSecond line\nThird line";

// Without MULTILINE - ^ only matches start of entire string
Pattern pattern1 = Pattern.compile("^\\w+");
Matcher matcher1 = pattern1.matcher(text);
System.out.println("Without MULTILINE:");
while (matcher1.find()) {
    System.out.println("  " + matcher1.group());
}

// With MULTILINE - ^ matches start of each line
Pattern pattern2 = Pattern.compile("^\\w+", Pattern.MULTILINE);
Matcher matcher2 = pattern2.matcher(text);
System.out.println("\nWith MULTILINE:");
while (matcher2.find()) {
    System.out.println("  " + matcher2.group());
}
```

**Output:**
```
Without MULTILINE:
  First

With MULTILINE:
  First
  Second
  Third
```

### DOTALL Flag

```java
String text = "First line\nSecond line";

// Without DOTALL - . doesn't match newline
Pattern pattern1 = Pattern.compile("First.*line");
System.out.println("Without DOTALL: " + pattern1.matcher(text).find());

// With DOTALL - . matches newline
Pattern pattern2 = Pattern.compile("First.*line", Pattern.DOTALL);
System.out.println("With DOTALL: " + pattern2.matcher(text).find());
```

**Output:**
```
Without DOTALL: false
With DOTALL: true
```

### COMMENTS Flag

```java
// Allows whitespace and comments in pattern for readability
String regex = """
    (?x)           # Enable comments mode
    (\\d{3})       # Area code
    -              # Separator
    (\\d{3})       # Exchange
    -              # Separator
    (\\d{4})       # Number
    """;

Pattern pattern = Pattern.compile(regex, Pattern.COMMENTS);
Matcher matcher = pattern.matcher("Call 555-123-4567");

if (matcher.find()) {
    System.out.println("Phone: " + matcher.group());
}
```

**Output:**
```
Phone: 555-123-4567
```

### Embedded Flag Expressions

You can embed flags directly in the regex pattern:

```java
// Embedded CASE_INSENSITIVE flag
String regex = "(?i)hello";  // Same as Pattern.CASE_INSENSITIVE

// Embedded MULTILINE flag
String regex = "(?m)^test";  // Same as Pattern.MULTILINE

// Multiple embedded flags
String regex = "(?im)^hello";  // CASE_INSENSITIVE | MULTILINE

// Example
Pattern pattern = Pattern.compile("(?i)hello");
System.out.println("Matches 'HELLO': " + pattern.matcher("HELLO").matches());
```

**Output:**
```
Matches 'HELLO': true
```

## The Matcher Class

The `Matcher` class performs match operations on a character sequence using a compiled Pattern.

### Creating Matcher Objects

```java
Pattern pattern = Pattern.compile("\\d+");
Matcher matcher = pattern.matcher("abc 123 def 456");

// You can also reset the matcher with new input
matcher.reset("new input text 789");
```

### Matcher State

A Matcher maintains state about the current match:
- Current position in the input
- Last match found
- Captured groups from the last match

## Matcher Methods

### Finding Matches

#### find()

```java
// Find the next match in the input
public boolean find()
public boolean find(int start)

// Example
String text = "The numbers are 10, 20, and 30";
Pattern pattern = Pattern.compile("\\d+");
Matcher matcher = pattern.matcher(text);

while (matcher.find()) {
    System.out.println("Found: " + matcher.group() +
                      " at position " + matcher.start());
}
```

**Output:**
```
Found: 10 at position 16
Found: 20 at position 20
Found: 30 at position 28
```

#### matches()

```java
// Attempts to match the ENTIRE input sequence
public boolean matches()

// Example
Pattern pattern = Pattern.compile("\\d{3}-\\d{4}");

System.out.println("Matches '555-1234': " +
    pattern.matcher("555-1234").matches());

System.out.println("Matches 'Call 555-1234': " +
    pattern.matcher("Call 555-1234").matches());
```

**Output:**
```
Matches '555-1234': true
Matches 'Call 555-1234': false
```

#### lookingAt()

```java
// Attempts to match from the beginning of the input
public boolean lookingAt()

// Example
Pattern pattern = Pattern.compile("\\d{3}-\\d{4}");

System.out.println("LookingAt '555-1234 home': " +
    pattern.matcher("555-1234 home").lookingAt());

System.out.println("LookingAt 'Call 555-1234': " +
    pattern.matcher("Call 555-1234").lookingAt());
```

**Output:**
```
LookingAt '555-1234 home': true
LookingAt 'Call 555-1234': false
```

### Extracting Information

#### group()

```java
// Get matched text
public String group()           // Entire match (group 0)
public String group(int group)  // Specific group
public String group(String name) // Named group

// Example
String text = "Contact: John Doe (john@example.com)";
Pattern pattern = Pattern.compile("(\\w+)@(\\w+\\.\\w+)");
Matcher matcher = pattern.matcher(text);

if (matcher.find()) {
    System.out.println("Full email: " + matcher.group(0));
    System.out.println("Username: " + matcher.group(1));
    System.out.println("Domain: " + matcher.group(2));
}
```

**Output:**
```
Full email: john@example.com
Username: john
Domain: example.com
```

#### start() and end()

```java
// Get match positions
public int start()              // Start of entire match
public int start(int group)     // Start of specific group
public int end()                // End of entire match
public int end(int group)       // End of specific group

// Example
String text = "Price: $99.99";
Pattern pattern = Pattern.compile("\\$(\\d+)\\.(\\d{2})");
Matcher matcher = pattern.matcher(text);

if (matcher.find()) {
    System.out.println("Match: " + matcher.group());
    System.out.println("Position: " + matcher.start() + "-" + matcher.end());
    System.out.println("Dollars at: " + matcher.start(1) + "-" + matcher.end(1));
    System.out.println("Cents at: " + matcher.start(2) + "-" + matcher.end(2));
}
```

**Output:**
```
Match: $99.99
Position: 7-13
Dollars at: 8-10
Cents at: 11-13
```

#### groupCount()

```java
// Get number of capturing groups
public int groupCount()

// Example
Pattern pattern = Pattern.compile("(\\d{3})-(\\d{3})-(\\d{4})");
Matcher matcher = pattern.matcher("555-123-4567");

System.out.println("Number of groups: " + matcher.groupCount());
```

**Output:**
```
Number of groups: 3
```

### Resetting and Reusing

#### reset()

```java
// Reset matcher with same or new input
public Matcher reset()
public Matcher reset(CharSequence input)

// Example
Pattern pattern = Pattern.compile("\\d+");
Matcher matcher = pattern.matcher("abc 123");

if (matcher.find()) {
    System.out.println("First text: " + matcher.group());
}

// Reset with new input
matcher.reset("def 456");
if (matcher.find()) {
    System.out.println("Second text: " + matcher.group());
}
```

**Output:**
```
First text: 123
Second text: 456
```

## Finding and Matching

### Difference Between find(), matches(), and lookingAt()

```java
String text = "Hello World";
Pattern pattern = Pattern.compile("Hello");

Matcher matcher = pattern.matcher(text);

System.out.println("find(): " + matcher.find());        // true - found anywhere
matcher.reset();
System.out.println("matches(): " + matcher.matches());  // false - doesn't match entire string
matcher.reset();
System.out.println("lookingAt(): " + matcher.lookingAt()); // true - matches from start
```

**Output:**
```
find(): true
matches(): false
lookingAt(): true
```

### Iterating Over All Matches

```java
String text = "Amounts: TZS.100.00, TZS.250.50, TZS.1,000.00";
Pattern pattern = Pattern.compile("TZS\\.([0-9,]+\\.[0-9]{2})");
Matcher matcher = pattern.matcher(text);

System.out.println("All amounts:");
while (matcher.find()) {
    System.out.println("  " + matcher.group(1));
}
```

**Output:**
```
All amounts:
  100.00
  250.50
  1,000.00
```

## Replacing Text

### replaceAll()

```java
// Replace all matches
public String replaceAll(String replacement)

// Example
String text = "Price: $99.99, Tax: $10.00";
Pattern pattern = Pattern.compile("\\$");
String result = pattern.matcher(text).replaceAll("USD ");

System.out.println("Original: " + text);
System.out.println("Result: " + result);
```

**Output:**
```
Original: Price: $99.99, Tax: $10.00
Result: Price: USD 99.99, Tax: USD 10.00
```

### replaceFirst()

```java
// Replace only the first match
public String replaceFirst(String replacement)

// Example
String text = "Price: $99.99, Tax: $10.00";
Pattern pattern = Pattern.compile("\\$");
String result = pattern.matcher(text).replaceFirst("USD ");

System.out.println("Original: " + text);
System.out.println("Result: " + result);
```

**Output:**
```
Original: Price: $99.99, Tax: $10.00
Result: Price: USD 99.99, Tax: $10.00
```

### Using Group References in Replacement

```java
// Use $1, $2, etc. to reference groups in replacement
String text = "John Doe, Jane Smith";
Pattern pattern = Pattern.compile("(\\w+)\\s+(\\w+)");
String result = pattern.matcher(text).replaceAll("$2, $1");

System.out.println("Original: " + text);
System.out.println("Result: " + result);
```

**Output:**
```
Original: John Doe, Jane Smith
Result: Doe, John, Smith, Jane
```

### appendReplacement() and appendTail()

For complex replacements, use `appendReplacement()` and `appendTail()`:

```java
String text = "Prices: $10, $20, $30";
Pattern pattern = Pattern.compile("\\$(\\d+)");
Matcher matcher = pattern.matcher(text);
StringBuffer result = new StringBuffer();

while (matcher.find()) {
    int price = Integer.parseInt(matcher.group(1));
    int newPrice = price * 2;  // Double the price
    matcher.appendReplacement(result, "\\$" + newPrice);
}
matcher.appendTail(result);

System.out.println("Original: " + text);
System.out.println("Result: " + result);
```

**Output:**
```
Original: Prices: $10, $20, $30
Result: Prices: $20, $40, $60
```

### replaceAll() with Lambda (Java 9+)

```java
String text = "Prices: $10, $20, $30";
Pattern pattern = Pattern.compile("\\$(\\d+)");
Matcher matcher = pattern.matcher(text);

String result = matcher.replaceAll(match -> {
    int price = Integer.parseInt(match.group(1));
    return "$" + (price * 2);
});

System.out.println("Original: " + text);
System.out.println("Result: " + result);
```

**Output:**
```
Original: Prices: $10, $20, $30
Result: Prices: $20, $40, $60
```

## Region Operations

Regions allow you to limit matching to a specific part of the input.

### region()

```java
String text = "Ignore this 123 use this 456 ignore this";
Pattern pattern = Pattern.compile("\\d+");
Matcher matcher = pattern.matcher(text);

// Set region to only search "use this 456"
matcher.region(13, 28);

System.out.println("Matches in region:");
while (matcher.find()) {
    System.out.println("  " + matcher.group() + " at position " + matcher.start());
}
```

**Output:**
```
Matches in region:
  456 at position 22
```

### regionStart() and regionEnd()

```java
Matcher matcher = pattern.matcher(text);
matcher.region(10, 20);

System.out.println("Region start: " + matcher.regionStart());
System.out.println("Region end: " + matcher.regionEnd());
```

## Performance Considerations

### Compile Once, Use Many Times

**Bad Practice:**
```java
// Compiling pattern every time (slow)
for (String text : texts) {
    if (Pattern.matches("\\d+", text)) {
        // process
    }
}
```

**Good Practice:**
```java
// Compile once, reuse (fast)
Pattern pattern = Pattern.compile("\\d+");
for (String text : texts) {
    if (pattern.matcher(text).matches()) {
        // process
    }
}
```

### Reuse Matcher Objects

**Better Performance:**
```java
Pattern pattern = Pattern.compile("\\d+");
Matcher matcher = pattern.matcher("");

for (String text : texts) {
    matcher.reset(text);
    if (matcher.matches()) {
        // process
    }
}
```

### Use String Methods When Appropriate

For simple operations, String methods may be faster:

```java
// For simple literal replacement, String.replace() is faster
String result1 = text.replace("old", "new");

// Pattern is overkill for simple contains check
boolean contains = text.contains("substring");
```

## Practice Examples

### Example 1: Extracting All Phone Numbers from Text

```java
String text = """
    Contact us:
    Main office: 255-755-959291
    Support: (255) 765-123456
    Emergency: 255.712.345678
    """;

Pattern pattern = Pattern.compile("\\d{3}[-.() ]*\\d{3}[-.() ]*\\d{6}");
Matcher matcher = pattern.matcher(text);

System.out.println("Phone numbers found:");
while (matcher.find()) {
    System.out.println("  " + matcher.group());
}
```

**Output:**
```
Phone numbers found:
  255-755-959291
  255) 765-123456
  255.712.345678
```

### Example 2: Validating and Extracting Email Components

```java
String email = "user.name+tag@example.co.uk";
Pattern pattern = Pattern.compile(
    "(?<local>[a-zA-Z0-9._%+-]+)@(?<domain>[a-zA-Z0-9.-]+)\\.(?<tld>[a-zA-Z]{2,})"
);
Matcher matcher = pattern.matcher(email);

if (matcher.matches()) {
    System.out.println("Valid email!");
    System.out.println("Local part: " + matcher.group("local"));
    System.out.println("Domain: " + matcher.group("domain"));
    System.out.println("TLD: " + matcher.group("tld"));
} else {
    System.out.println("Invalid email");
}
```

**Output:**
```
Valid email!
Local part: user.name+tag
Domain: example.co
TLD: uk
```

### Example 3: Parsing and Reformatting Dates

```java
String text = "Important dates: 27/11/2025, 15-12-2025, 01.02.2026";
Pattern pattern = Pattern.compile("(\\d{2})[/.-](\\d{2})[/.-](\\d{4})");
Matcher matcher = pattern.matcher(text);

System.out.println("Dates in ISO format (YYYY-MM-DD):");
while (matcher.find()) {
    String day = matcher.group(1);
    String month = matcher.group(2);
    String year = matcher.group(3);
    System.out.println("  " + year + "-" + month + "-" + day);
}
```

**Output:**
```
Dates in ISO format (YYYY-MM-DD):
  2025-11-27
  2025-12-15
  2026-02-01
```

### Example 4: Cleaning M-Koba Transaction Messages

```java
String message = "CKR4LDS51MY Confirmed.You successfully paid TZS.1,000.0";

// Remove transaction reference
Pattern pattern = Pattern.compile("^[A-Z0-9]+\\s+Confirmed\\.");
Matcher matcher = pattern.matcher(message);
String cleaned = matcher.replaceFirst("");

System.out.println("Original: " + message);
System.out.println("Cleaned: " + cleaned);
```

**Output:**
```
Original: CKR4LDS51MY Confirmed.You successfully paid TZS.1,000.0
Cleaned: You successfully paid TZS.1,000.0
```

### Example 5: Counting Transaction Types

```java
String messages = """
    has paid as loan repayment
    purchased shares worth
    paid for social fund
    loan repayment for KIZPART
    purchased shares worth
    """;

Pattern pattern = Pattern.compile("(?i)(loan repayment|purchased shares|social fund)");
Matcher matcher = pattern.matcher(messages);

int count = 0;
while (matcher.find()) {
    count++;
}

System.out.println("Total transactions found: " + count);

// Reset and show details
matcher.reset();
while (matcher.find()) {
    System.out.println("  Type: " + matcher.group(1));
}
```

**Output:**
```
Total transactions found: 5
  Type: loan repayment
  Type: purchased shares
  Type: social fund
  Type: loan repayment
  Type: purchased shares
```

## Summary

In this guide, you learned:

1. **Pattern class** compiles regex strings into reusable patterns
2. **Pattern.compile()** creates Pattern objects with optional flags
3. **Pattern flags** modify matching behavior (CASE_INSENSITIVE, MULTILINE, DOTALL, etc.)
4. **Matcher class** performs match operations on character sequences
5. **Finding methods**: `find()`, `matches()`, `lookingAt()`
6. **Extracting data**: `group()`, `start()`, `end()`, `groupCount()`
7. **Replacing text**: `replaceAll()`, `replaceFirst()`, `appendReplacement()`
8. **Performance**: Compile patterns once, reuse Matcher objects
9. **Regions**: Limit matching to specific parts of input

## Next Steps

In the next guide, we'll explore:
- Practical text extraction techniques
- Input validation patterns
- Data parsing strategies
- Building robust parsers

Continue to: [05-text-extraction-and-validation.md](./05-text-extraction-and-validation.md)
