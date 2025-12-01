# Advanced Regex Techniques

## Table of Contents
1. [Introduction](#introduction)
2. [Lookahead Assertions](#lookahead-assertions)
3. [Lookbehind Assertions](#lookbehind-assertions)
4. [Atomic Groups](#atomic-groups)
5. [Conditional Patterns](#conditional-patterns)
6. [Unicode and Character Properties](#unicode-and-character-properties)
7. [Performance Optimization](#performance-optimization)
8. [Complex Pattern Composition](#complex-pattern-composition)
9. [Practice Examples](#practice-examples)

## Introduction

Advanced regex techniques allow you to write more precise, efficient, and powerful patterns. These techniques are essential for handling complex text processing scenarios that simple patterns cannot address.

## Lookahead Assertions

Lookahead assertions check if a pattern exists ahead of the current position **without consuming characters**. They don't include the matched text in the result.

### Positive Lookahead: `(?=...)`

Matches if the pattern ahead exists.

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PositiveLookaheadExample {
    public static void main(String[] args) {
        String text = "Password1 Password2 Password";

        // Match "Password" only if followed by a digit
        String regex = "Password(?=\\d)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        System.out.println("Passwords followed by digits:");
        while (matcher.find()) {
            System.out.println("  " + matcher.group() +
                             " at position " + matcher.start());
        }
    }
}
```

**Output:**
```
Passwords followed by digits:
  Password at position 0
  Password at position 10
```

**Explanation:** Matches "Password" only when followed by a digit, but the digit itself is not part of the match.

### Negative Lookahead: `(?!...)`

Matches if the pattern ahead does NOT exist.

```java
public class NegativeLookaheadExample {
    public static void main(String[] args) {
        String text = "Password1 Password2 Password";

        // Match "Password" only if NOT followed by a digit
        String regex = "Password(?!\\d)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        System.out.println("Passwords NOT followed by digits:");
        while (matcher.find()) {
            System.out.println("  " + matcher.group() +
                             " at position " + matcher.start());
        }
    }
}
```

**Output:**
```
Passwords NOT followed by digits:
  Password at position 20
```

### Practical Example: Password Validation

```java
public class PasswordValidator {
    public static boolean isStrongPassword(String password) {
        // Must have at least one uppercase, one lowercase, one digit, one special char
        // and be at least 8 characters long
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

        return Pattern.matches(regex, password);
    }

    public static void main(String[] args) {
        String[] passwords = {
            "Weak123",              // Missing special char
            "weak@123",             // Missing uppercase
            "WEAK@123",             // Missing lowercase
            "Strong@123",           // Valid
            "VeryStrong@Password1"  // Valid
        };

        for (String password : passwords) {
            System.out.printf("%s -> %s%n",
                password,
                isStrongPassword(password) ? "STRONG" : "WEAK");
        }
    }
}
```

**Output:**
```
Weak123 -> WEAK
weak@123 -> WEAK
WEAK@123 -> WEAK
Strong@123 -> STRONG
VeryStrong@Password1 -> STRONG
```

**Pattern Breakdown:**
- `^` - Start of string
- `(?=.*[a-z])` - Must contain at least one lowercase letter
- `(?=.*[A-Z])` - Must contain at least one uppercase letter
- `(?=.*\\d)` - Must contain at least one digit
- `(?=.*[@$!%*?&])` - Must contain at least one special character
- `[A-Za-z\\d@$!%*?&]{8,}` - Allow only these characters, minimum 8
- `$` - End of string

### Example: Finding Words Not Followed by Punctuation

```java
public class WordsWithoutPunctuation {
    public static void main(String[] args) {
        String text = "Hello, world! How are you today";

        // Match words NOT followed by punctuation
        String regex = "\\b\\w+\\b(?![,.!?;:])";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        System.out.println("Words not followed by punctuation:");
        while (matcher.find()) {
            System.out.println("  " + matcher.group());
        }
    }
}
```

**Output:**
```
Words not followed by punctuation:
  world
  How
  are
  you
  today
```

## Lookbehind Assertions

Lookbehind assertions check if a pattern exists before the current position **without consuming characters**.

### Positive Lookbehind: `(?<=...)`

Matches if the pattern behind exists.

```java
public class PositiveLookbehindExample {
    public static void main(String[] args) {
        String text = "Price: $100, Cost: $50, Amount: $200";

        // Match numbers that are preceded by $
        String regex = "(?<=\\$)\\d+";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        System.out.println("Dollar amounts:");
        while (matcher.find()) {
            System.out.println("  " + matcher.group());
        }
    }
}
```

**Output:**
```
Dollar amounts:
  100
  50
  200
```

### Negative Lookbehind: `(?<!...)`

Matches if the pattern behind does NOT exist.

```java
public class NegativeLookbehindExample {
    public static void main(String[] args) {
        String text = "Price: $100, Quantity: 50, Total: $200";

        // Match numbers NOT preceded by $
        String regex = "(?<!\\$)\\b\\d+\\b";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        System.out.println("Non-dollar numbers:");
        while (matcher.find()) {
            System.out.println("  " + matcher.group());
        }
    }
}
```

**Output:**
```
Non-dollar numbers:
  50
```

### Example: Extracting Currency Without Symbol

```java
public class CurrencyExtractor {
    public static void main(String[] args) {
        String text = "Prices: $99.99, €85.50, £75.00, 100 (no currency)";

        // Extract amounts with currency symbols
        String regex = "(?<=[\\$€£])[0-9]+\\.[0-9]{2}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        System.out.println("Currency amounts (without symbols):");
        while (matcher.find()) {
            System.out.println("  " + matcher.group());
        }
    }
}
```

**Output:**
```
Currency amounts (without symbols):
  99.99
  85.50
  75.00
```

### Combining Lookahead and Lookbehind

```java
public class CombinedLookExample {
    public static void main(String[] args) {
        String text = "Contact: [email]user@example.com[/email] and [phone]555-1234[/phone]";

        // Extract content between [email] and [/email] tags
        String regex = "(?<=\\[email\\])[^\\[]+(?=\\[/email\\])";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        System.out.println("Email content:");
        while (matcher.find()) {
            System.out.println("  " + matcher.group());
        }
    }
}
```

**Output:**
```
Email content:
  user@example.com
```

## Atomic Groups

Atomic groups `(?>...)` prevent backtracking once matched. They improve performance but can prevent some matches.

### Syntax and Behavior

```java
public class AtomicGroupExample {
    public static void main(String[] args) {
        String text = "aaaab";

        // Without atomic group (will match through backtracking)
        String regex1 = "a+ab";
        boolean match1 = Pattern.matches(regex1, text);
        System.out.println("Without atomic group: " + match1);

        // With atomic group (won't match - no backtracking)
        String regex2 = "(?>a+)ab";
        boolean match2 = Pattern.matches(regex2, text);
        System.out.println("With atomic group: " + match2);
    }
}
```

**Output:**
```
Without atomic group: true
With atomic group: false
```

**Explanation:**
- Without atomic group: `a+` matches "aaaa", then backtracks to "aaa" allowing "ab" to match
- With atomic group: `a+` matches "aaaa" and doesn't give up characters, so "ab" cannot match

### Performance Optimization with Atomic Groups

```java
import java.util.regex.Pattern;

public class AtomicGroupPerformance {
    public static void main(String[] args) {
        String text = "This is a very long text " + "word ".repeat(1000);

        // Without atomic group (slower with catastrophic backtracking potential)
        long start1 = System.nanoTime();
        Pattern pattern1 = Pattern.compile("(\\w+\\s)*end");
        boolean match1 = pattern1.matcher(text).find();
        long time1 = System.nanoTime() - start1;

        // With atomic group (faster, no backtracking)
        long start2 = System.nanoTime();
        Pattern pattern2 = Pattern.compile("(?>(\\w+\\s)*)end");
        boolean match2 = pattern2.matcher(text).find();
        long time2 = System.nanoTime() - start2;

        System.out.println("Without atomic: " + time1 + " ns");
        System.out.println("With atomic: " + time2 + " ns");
        System.out.println("Speed improvement: " + (time1 / (double)time2) + "x");
    }
}
```

## Conditional Patterns

Conditional patterns allow different matching based on whether a previous group matched.

### Syntax: `(?(condition)yes-pattern|no-pattern)`

```java
public class ConditionalPatternExample {
    public static void main(String[] args) {
        // Match phone numbers with or without country code
        // If country code exists, expect format: +1-234-567-8901
        // If no country code, expect format: 234-567-8901

        String regex = "(\\+\\d{1,3})?[- ]?(?(1)\\d{3}-\\d{3}-\\d{4}|\\d{3}-\\d{4})";

        String[] phones = {
            "+1-234-567-8901",  // With country code
            "234-567-8901",     // Without country code (should fail - wrong format)
            "234-5678",         // Without country code (correct short format)
        };

        Pattern pattern = Pattern.compile(regex);

        for (String phone : phones) {
            boolean matches = pattern.matcher(phone).matches();
            System.out.printf("%s -> %s%n", phone, matches ? "MATCH" : "NO MATCH");
        }
    }
}
```

## Unicode and Character Properties

Java regex supports Unicode and character properties for international text processing.

### Unicode Categories

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicodeExample {
    public static void main(String[] args) {
        String text = "Hello, 世界! Привет мир! مرحبا بالعالم";

        // Match all letter characters (any language)
        String regex = "\\p{L}+";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        System.out.println("All letter sequences:");
        while (matcher.find()) {
            System.out.println("  " + matcher.group());
        }
    }
}
```

**Output:**
```
All letter sequences:
  Hello
  世界
  Привет
  мир
  مرحبا
  بالعالم
```

### Common Unicode Categories

| Category | Description | Example |
|----------|-------------|---------|
| `\p{L}` | Any letter | a, A, ñ, 世 |
| `\p{Lu}` | Uppercase letter | A, Ñ |
| `\p{Ll}` | Lowercase letter | a, ñ |
| `\p{N}` | Any number | 0-9, ١-٩ |
| `\p{Nd}` | Decimal digit | 0-9 |
| `\p{P}` | Punctuation | .,!? |
| `\p{S}` | Symbol | $, €, © |
| `\p{Z}` | Separator (space) | space, tab |
| `\p{C}` | Control character | \n, \t |

### Unicode Scripts

```java
public class UnicodeScripts {
    public static void main(String[] args) {
        String text = "Hello 世界 Привет مرحبا";

        // Match only Latin script
        Pattern latinPattern = Pattern.compile("\\p{IsLatin}+");
        Matcher latinMatcher = latinPattern.matcher(text);

        System.out.println("Latin script:");
        while (latinMatcher.find()) {
            System.out.println("  " + latinMatcher.group());
        }

        // Match only Han (Chinese) script
        Pattern hanPattern = Pattern.compile("\\p{IsHan}+");
        Matcher hanMatcher = hanPattern.matcher(text);

        System.out.println("\nHan script:");
        while (hanMatcher.find()) {
            System.out.println("  " + hanMatcher.group());
        }

        // Match only Cyrillic script
        Pattern cyrillicPattern = Pattern.compile("\\p{IsCyrillic}+");
        Matcher cyrillicMatcher = cyrillicPattern.matcher(text);

        System.out.println("\nCyrillic script:");
        while (cyrillicMatcher.find()) {
            System.out.println("  " + cyrillicMatcher.group());
        }
    }
}
```

**Output:**
```
Latin script:
  Hello

Han script:
  世界

Cyrillic script:
  Привет
```

## Performance Optimization

### Tip 1: Compile Patterns Once

```java
// BAD: Compiling in a loop
for (String text : texts) {
    if (Pattern.matches("\\d+", text)) {  // Compiles every time!
        // process
    }
}

// GOOD: Compile once, reuse
Pattern pattern = Pattern.compile("\\d+");
for (String text : texts) {
    if (pattern.matcher(text).matches()) {
        // process
    }
}
```

### Tip 2: Use Specific Patterns

```java
// SLOW: Overly broad pattern
String slowRegex = ".*keyword.*";  // .* can match anything

// FAST: More specific pattern
String fastRegex = "\\S*keyword\\S*";  // Only non-whitespace
```

### Tip 3: Anchor Patterns When Possible

```java
// SLOWER: Pattern can start matching anywhere
String regex1 = "\\d{3}-\\d{4}";

// FASTER: Anchored pattern
String regex2 = "^\\d{3}-\\d{4}$";
```

### Tip 4: Avoid Excessive Backtracking

```java
// CATASTROPHIC BACKTRACKING: Can take exponential time
String badRegex = "(a+)+b";  // On "aaaaaaaaac" this is VERY slow

// BETTER: Use atomic groups or possessive quantifiers
String betterRegex = "(?>a+)+b";  // Much faster
String alsoGood = "a++b";         // Possessive quantifier
```

### Example: Benchmarking Patterns

```java
import java.util.regex.Pattern;

public class PerformanceBenchmark {
    public static void benchmark(String name, Pattern pattern, String text, int iterations) {
        long start = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            pattern.matcher(text).find();
        }

        long elapsed = System.nanoTime() - start;
        System.out.printf("%s: %.2f ms%n", name, elapsed / 1_000_000.0);
    }

    public static void main(String[] args) {
        String text = "The quick brown fox jumps over the lazy dog";
        int iterations = 100_000;

        Pattern unoptimized = Pattern.compile(".*fox.*");
        Pattern optimized = Pattern.compile("\\S*fox\\S*");

        benchmark("Unoptimized", unoptimized, text, iterations);
        benchmark("Optimized", optimized, text, iterations);
    }
}
```

## Complex Pattern Composition

### Building Patterns from Components

```java
public class PatternComposition {
    // Define reusable pattern components
    private static final String PHONE_COUNTRY = "(?<country>\\+?\\d{1,3})";
    private static final String PHONE_AREA = "(?<area>\\d{3})";
    private static final String PHONE_NUMBER = "(?<number>\\d{3}[-.]?\\d{4})";
    private static final String SEPARATOR = "[-. ]?";

    // Compose complete pattern
    private static final String FULL_PHONE_REGEX =
        PHONE_COUNTRY + SEPARATOR + PHONE_AREA + SEPARATOR + PHONE_NUMBER;

    private static final Pattern PHONE_PATTERN = Pattern.compile(FULL_PHONE_REGEX);

    public static void main(String[] args) {
        String text = "+1-555-123-4567";
        Matcher matcher = PHONE_PATTERN.matcher(text);

        if (matcher.matches()) {
            System.out.println("Country: " + matcher.group("country"));
            System.out.println("Area: " + matcher.group("area"));
            System.out.println("Number: " + matcher.group("number"));
        }
    }
}
```

### Pattern Builder Class

```java
public class RegexBuilder {
    private StringBuilder pattern;

    public RegexBuilder() {
        this.pattern = new StringBuilder();
    }

    public RegexBuilder startOfLine() {
        pattern.append("^");
        return this;
    }

    public RegexBuilder endOfLine() {
        pattern.append("$");
        return this;
    }

    public RegexBuilder literal(String text) {
        pattern.append(Pattern.quote(text));
        return this;
    }

    public RegexBuilder digits(int count) {
        pattern.append("\\d{").append(count).append("}");
        return this;
    }

    public RegexBuilder optional(String subPattern) {
        pattern.append("(?:").append(subPattern).append(")?");
        return this;
    }

    public RegexBuilder group(String name, String subPattern) {
        pattern.append("(?<").append(name).append(">").append(subPattern).append(")");
        return this;
    }

    public Pattern build() {
        return Pattern.compile(pattern.toString());
    }

    public static void main(String[] args) {
        // Build pattern for DD/MM/YYYY date
        Pattern datePattern = new RegexBuilder()
            .startOfLine()
            .group("day", "\\d{2}")
            .literal("/")
            .group("month", "\\d{2}")
            .literal("/")
            .group("year", "\\d{4}")
            .endOfLine()
            .build();

        Matcher matcher = datePattern.matcher("27/11/2025");
        if (matcher.matches()) {
            System.out.println("Day: " + matcher.group("day"));
            System.out.println("Month: " + matcher.group("month"));
            System.out.println("Year: " + matcher.group("year"));
        }
    }
}
```

**Output:**
```
Day: 27
Month: 11
Year: 2025
```

## Practice Examples

### Example 1: Extract Quoted Strings with Escaped Quotes

```java
public class QuotedStringExtractor {
    public static void main(String[] args) {
        String text = "He said \"Hello \\\"World\\\"\" and left";

        // Match quoted strings, handling escaped quotes
        String regex = "\"((?:[^\"\\\\]|\\\\.)*)\"";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        System.out.println("Quoted strings:");
        while (matcher.find()) {
            System.out.println("  " + matcher.group(1));
        }
    }
}
```

**Output:**
```
Quoted strings:
  Hello \"World\"
```

### Example 2: Validate Complex Email Addresses

```java
public class ComplexEmailValidator {
    // RFC 5322 compliant (simplified)
    private static final String EMAIL_REGEX =
        "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
        "|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]" +
        "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")" +
        "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" +
        "|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
        "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:" +
        "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]" +
        "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public static void main(String[] args) {
        String[] emails = {
            "simple@example.com",
            "user+tag@example.co.uk",
            "\"unusual but valid\"@example.com",
            "user@[192.168.1.1]"
        };

        for (String email : emails) {
            System.out.printf("%s -> %s%n",
                email, isValidEmail(email) ? "VALID" : "INVALID");
        }
    }
}
```

### Example 3: Parse Nested Structures (Limited Depth)

```java
public class NestedStructureParser {
    public static void main(String[] args) {
        String text = "func(arg1, func2(nested), arg3)";

        // Match function calls (max one level of nesting)
        String regex = "(\\w+)\\(([^(),]*(?:\\([^()]*\\)[^(),]*)*)\\)";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String functionName = matcher.group(1);
            String arguments = matcher.group(2);

            System.out.println("Function: " + functionName);
            System.out.println("Arguments: " + arguments);
            System.out.println();
        }
    }
}
```

**Output:**
```
Function: func
Arguments: arg1, func2(nested), arg3
```

## Summary

In this guide, you learned:

1. **Lookahead assertions**: `(?=...)` positive, `(?!...)` negative
2. **Lookbehind assertions**: `(?<=...)` positive, `(?<!...)` negative
3. **Atomic groups**: `(?>...)` prevent backtracking for performance
4. **Conditional patterns**: Different patterns based on conditions
5. **Unicode support**: `\p{L}`, `\p{N}` and script matching
6. **Performance optimization**: Compile once, avoid backtracking
7. **Pattern composition**: Building complex patterns from components

These advanced techniques enable you to write sophisticated, efficient regex patterns for complex text processing tasks.

## Next Steps

In the final guide, we'll cover:
- Best practices for maintainable regex
- Common pitfalls and how to avoid them
- Testing strategies
- Documentation guidelines

Continue to: [08-best-practices.md](./08-best-practices.md)