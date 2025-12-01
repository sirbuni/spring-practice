# Regex Best Practices and Common Pitfalls

## Table of Contents
1. [Introduction](#introduction)
2. [Code Organization](#code-organization)
3. [Documentation and Comments](#documentation-and-comments)
4. [Common Pitfalls](#common-pitfalls)
5. [Testing Strategies](#testing-strategies)
6. [Maintainability Guidelines](#maintainability-guidelines)
7. [Security Considerations](#security-considerations)
8. [When NOT to Use Regex](#when-not-to-use-regex)
9. [Debugging Regex](#debugging-regex)
10. [Summary Checklist](#summary-checklist)

## Introduction

Writing effective regex is not just about creating patterns that work—it's about writing patterns that are:
- **Readable**: Others (including future you) can understand them
- **Maintainable**: Easy to modify when requirements change
- **Performant**: Don't cause performance issues
- **Secure**: Don't create vulnerabilities
- **Testable**: Can be validated with comprehensive tests

## Code Organization

### Use Named Constants for Patterns

**Bad Practice:**
```java
public class BadExample {
    public void validateEmail(String email) {
        if (email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")) {
            // process
        }
    }

    public void extractEmail(String text) {
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$");
        // ... duplicate pattern!
    }
}
```

**Good Practice:**
```java
public class GoodExample {
    // Define pattern once as a constant
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$");

    public void validateEmail(String email) {
        if (EMAIL_PATTERN.matcher(email).matches()) {
            // process
        }
    }

    public void extractEmail(String text) {
        Matcher matcher = EMAIL_PATTERN.matcher(text);
        // ... reuse pattern
    }
}
```

### Group Related Patterns

```java
public class TextValidator {
    // Email patterns
    private static final Pattern EMAIL_SIMPLE =
        Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern EMAIL_STRICT =
        Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    // Phone patterns
    private static final Pattern PHONE_TZ_FULL =
        Pattern.compile("^255[67]\\d{8}$");

    private static final Pattern PHONE_TZ_SHORT =
        Pattern.compile("^0[67]\\d{8}$");

    // Date patterns
    private static final Pattern DATE_DDMMYYYY =
        Pattern.compile("^(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[012])/(19|20)\\d{2}$");

    private static final Pattern DATE_YYYYMMDD =
        Pattern.compile("^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12]\\d|3[01])$");
}
```

### Create Utility Classes

```java
public final class RegexPatterns {
    // Prevent instantiation
    private RegexPatterns() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    // Email patterns
    public static final class Email {
        public static final Pattern SIMPLE =
            Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

        public static final Pattern STRICT =
            Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    // Phone patterns
    public static final class Phone {
        public static final Pattern TZ_MOBILE =
            Pattern.compile("^(?:255|0)[67]\\d{8}$");

        public static final Pattern INTERNATIONAL =
            Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    }

    // Usage
    public static void main(String[] args) {
        boolean isValid = RegexPatterns.Email.SIMPLE
            .matcher("user@example.com")
            .matches();
    }
}
```

## Documentation and Comments

### Document Complex Patterns

**Bad Practice:**
```java
// No explanation
Pattern pattern = Pattern.compile(
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
);
```

**Good Practice:**
```java
/**
 * Strong password validation pattern.
 * Requirements:
 * - At least one lowercase letter (?=.*[a-z])
 * - At least one uppercase letter (?=.*[A-Z])
 * - At least one digit (?=.*\\d)
 * - At least one special character (?=.*[@$!%*?&])
 * - Minimum 8 characters total
 * - Only allows alphanumeric and specified special characters
 */
private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
    "^" +
    "(?=.*[a-z])" +           // Lowercase letter
    "(?=.*[A-Z])" +           // Uppercase letter
    "(?=.*\\d)" +             // Digit
    "(?=.*[@$!%*?&])" +       // Special character
    "[A-Za-z\\d@$!%*?&]{8,}" + // Allowed characters, min 8
    "$"
);
```

### Use the COMMENTS Flag for Complex Patterns

```java
private static final Pattern PHONE_PATTERN = Pattern.compile(
    """
    ^                   # Start of string
    (?<country>         # Country code group
        \\+?            # Optional plus sign
        \\d{1,3}        # 1-3 digits
    )?                  # Country code is optional
    [-\\s]?             # Optional separator
    (?<area>            # Area code group
        \\d{3}          # Exactly 3 digits
    )
    [-\\s]?             # Optional separator
    (?<number>          # Phone number group
        \\d{3}          # First 3 digits
        [-\\s]?         # Optional separator
        \\d{4}          # Last 4 digits
    )
    $                   # End of string
    """,
    Pattern.COMMENTS | Pattern.MULTILINE
);
```

### Document Named Groups

```java
/**
 * Parses M-Koba transaction messages.
 *
 * Named groups:
 * - phone: Member's phone number (digits only)
 * - name: Member's full name
 * - action: Transaction action (paid, purchased)
 * - amount: Transaction amount (without currency symbol)
 * - date: Transaction date in DD/MM/YYYY format
 * - time: Transaction time in HH:MM format
 */
private static final Pattern TRANSACTION_PATTERN = Pattern.compile(
    "(?<phone>\\d+)\\((?<name>[^)]+)\\).*?" +
    "TZS\\.(?<amount>[0-9,]+\\.?[0-9]*).*?" +
    "on\\s+(?<date>\\d{2}/\\d{2}/\\d{4})\\s+" +
    "at\\s+(?<time>\\d{2}:\\d{2})"
);
```

## Common Pitfalls

### Pitfall 1: Not Escaping Special Characters

**Problem:**
```java
// WRONG: . matches any character, not a literal dot
String regex = "file.txt";  // Matches "file-txt", "file@txt", etc.
```

**Solution:**
```java
// CORRECT: Escape the dot
String regex = "file\\.txt";  // Only matches "file.txt"

// Or use Pattern.quote() for user input
String userInput = "file.txt";
String regex = Pattern.quote(userInput);
```

### Pitfall 2: Forgetting matches() vs find()

**Problem:**
```java
String text = "My phone is 123-456-7890";
String regex = "\\d{3}-\\d{3}-\\d{4}";

// WRONG: matches() requires entire string to match
boolean matches = Pattern.matches(regex, text);  // false!
```

**Solution:**
```java
// CORRECT: Use find() to search within the string
Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);
boolean found = matcher.find();  // true
```

### Pitfall 3: Catastrophic Backtracking

**Problem:**
```java
// DANGEROUS: Can cause exponential time complexity
String regex = "(a+)+b";
String text = "aaaaaaaaaaaaaaaaaaaaac";  // Takes VERY long!
Pattern.compile(regex).matcher(text).matches();
```

**Solution:**
```java
// Use atomic groups or possessive quantifiers
String regex1 = "(?>a+)+b";  // Atomic group
String regex2 = "a++b";      // Possessive quantifier

// Or redesign the pattern
String regex3 = "a+b";       // Simpler is often better
```

### Pitfall 4: Not Handling Null Input

**Problem:**
```java
public boolean isValid(String input) {
    // WRONG: NullPointerException if input is null
    return Pattern.matches("\\d+", input);
}
```

**Solution:**
```java
public boolean isValid(String input) {
    // CORRECT: Check for null first
    if (input == null) {
        return false;
    }
    return Pattern.matches("\\d+", input);
}
```

### Pitfall 5: Over-Matching with Greedy Quantifiers

**Problem:**
```java
String html = "<div>First</div><div>Second</div>";
String regex = "<div>.*</div>";  // Greedy!

// WRONG: Matches entire string
Matcher matcher = Pattern.compile(regex).matcher(html);
matcher.find();
System.out.println(matcher.group());  // "<div>First</div><div>Second</div>"
```

**Solution:**
```java
// CORRECT: Use lazy quantifier
String regex = "<div>.*?</div>";  // Lazy

Matcher matcher = Pattern.compile(regex).matcher(html);
while (matcher.find()) {
    System.out.println(matcher.group());
}
// Output:
// <div>First</div>
// <div>Second</div>
```

### Pitfall 6: Not Considering Unicode

**Problem:**
```java
// WRONG: Only matches ASCII letters
String regex = "[a-zA-Z]+";
String text = "Hello 世界";  // Won't match Chinese characters
```

**Solution:**
```java
// CORRECT: Use Unicode letter class
String regex = "\\p{L}+";
String text = "Hello 世界";  // Matches both
```

### Pitfall 7: Modifying Matcher State Incorrectly

**Problem:**
```java
Pattern pattern = Pattern.compile("\\d+");
Matcher matcher = pattern.matcher("123 456");

if (matcher.find()) {
    System.out.println(matcher.group());  // "123"
}

// WRONG: Forgot to reset or continue from current position
if (matcher.matches()) {  // false! Already at position 3
    System.out.println("Matches");
}
```

**Solution:**
```java
Pattern pattern = Pattern.compile("\\d+");
Matcher matcher = pattern.matcher("123 456");

if (matcher.find()) {
    System.out.println(matcher.group());  // "123"
}

// CORRECT: Reset matcher for new operation
matcher.reset();
if (matcher.find()) {  // Start from beginning again
    System.out.println("Found: " + matcher.group());
}
```

## Testing Strategies

### Unit Test Regex Patterns

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmailValidatorTest {
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    @Test
    public void testValidEmails() {
        String[] validEmails = {
            "user@example.com",
            "first.last@example.com",
            "user+tag@example.com",
            "user123@test-domain.co.uk"
        };

        for (String email : validEmails) {
            assertTrue(EMAIL_PATTERN.matcher(email).matches(),
                "Should accept valid email: " + email);
        }
    }

    @Test
    public void testInvalidEmails() {
        String[] invalidEmails = {
            "invalid.email",
            "@example.com",
            "user@",
            "user@domain",
            "user name@example.com"
        };

        for (String email : invalidEmails) {
            assertFalse(EMAIL_PATTERN.matcher(email).matches(),
                "Should reject invalid email: " + email);
        }
    }

    @Test
    public void testEdgeCases() {
        assertFalse(EMAIL_PATTERN.matcher(null).matches(),
            "Should handle null");
        assertFalse(EMAIL_PATTERN.matcher("").matches(),
            "Should reject empty string");
        assertFalse(EMAIL_PATTERN.matcher("   ").matches(),
            "Should reject whitespace");
    }
}
```

### Test with Realistic Data

```java
@Test
public void testWithRealData() {
    // Test with actual M-Koba messages from sample data
    String message = "255755959291(OBED SANGA) has purchased shares " +
                    "worth of TZS.20,000.00 from KIZPART SACCOS group " +
                    "on 27/11/2025 at 10:27";

    Matcher matcher = TRANSACTION_PATTERN.matcher(message);
    assertTrue(matcher.find(), "Should parse real message");

    assertEquals("255755959291", matcher.group("phone"));
    assertEquals("OBED SANGA", matcher.group("name"));
    assertEquals("20,000.00", matcher.group("amount"));
    assertEquals("27/11/2025", matcher.group("date"));
    assertEquals("10:27", matcher.group("time"));
}
```

### Property-Based Testing

```java
import org.junit.jupiter.api.Test;
import java.util.Random;

public class PropertyBasedTest {
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^255[67]\\d{8}$");

    @Test
    public void testPhonePatternProperties() {
        Random random = new Random();

        // Generate 1000 valid phone numbers
        for (int i = 0; i < 1000; i++) {
            String phone = "255" +
                          (random.nextBoolean() ? "6" : "7") +
                          String.format("%08d", random.nextInt(100000000));

            assertTrue(PHONE_PATTERN.matcher(phone).matches(),
                "Generated phone should be valid: " + phone);
        }
    }
}
```

## Maintainability Guidelines

### Keep Patterns Simple

**Bad Practice:**
```java
// Overly complex pattern trying to do everything
String regex = "^(?:(?:(?:(?:[01]?\\d|2[0-3]):)?(?:[0-5]?\\d):)?(?:[0-5]?\\d))?$";
```

**Good Practice:**
```java
// Break into logical components
String hours = "(?:[01]?\\d|2[0-3])";
String minutes = "(?:[0-5]?\\d)";
String seconds = "(?:[0-5]?\\d)";

// Compose from components
String time24h = hours + ":" + minutes + "(?::" + seconds + ")?";
Pattern pattern = Pattern.compile("^" + time24h + "$");
```

### Version Your Patterns

```java
public class PatternVersions {
    /**
     * Email validation - Version 1.0
     * Simple validation for common cases
     * @deprecated Use EMAIL_V2 for better validation
     */
    @Deprecated
    public static final Pattern EMAIL_V1 =
        Pattern.compile("^[\\w.]+@[\\w.]+\\.[a-z]{2,}$");

    /**
     * Email validation - Version 2.0
     * Added support for special characters and longer TLDs
     * @since 2024-11-27
     */
    public static final Pattern EMAIL_V2 =
        Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
}
```

### Extract Magic Numbers

**Bad Practice:**
```java
Pattern pattern = Pattern.compile("\\d{12}");  // What is 12?
```

**Good Practice:**
```java
// Define meaningful constants
private static final int TZ_PHONE_LENGTH = 12;  // 255 + 9 digits

Pattern pattern = Pattern.compile("\\d{" + TZ_PHONE_LENGTH + "}");
```

## Security Considerations

### Avoid ReDoS (Regular Expression Denial of Service)

**Vulnerable Pattern:**
```java
// DANGEROUS: Can be exploited for DoS attack
Pattern vulnerable = Pattern.compile("(a+)+b");

// Attack: String with many 'a's but no 'b'
String malicious = "a".repeat(30);  // Takes exponential time!
```

**Safe Pattern:**
```java
// SAFE: Use atomic groups or possessive quantifiers
Pattern safe1 = Pattern.compile("(?>a+)+b");
Pattern safe2 = Pattern.compile("a++b");
Pattern safe3 = Pattern.compile("a+b");  // Simpler is safer
```

### Set Timeout for Matching

```java
import java.util.concurrent.*;
import java.util.regex.*;

public class SafeRegexMatcher {
    private static final long TIMEOUT_MS = 1000;  // 1 second timeout

    public static boolean matchesWithTimeout(Pattern pattern, String input) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<Boolean> future = executor.submit(() -> {
            return pattern.matcher(input).matches();
        });

        try {
            return future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new RuntimeException("Regex matching timeout", e);
        } catch (Exception e) {
            throw new RuntimeException("Regex matching error", e);
        } finally {
            executor.shutdown();
        }
    }
}
```

### Sanitize User Input

```java
public class SafeInputHandler {
    /**
     * Escape special regex characters in user input
     */
    public static String escapeRegex(String userInput) {
        return Pattern.quote(userInput);
    }

    /**
     * Safe search in text using user input
     */
    public static boolean safeSearch(String text, String userInput) {
        if (userInput == null || text == null) {
            return false;
        }

        // Escape user input to treat it as literal
        String escaped = Pattern.quote(userInput);
        Pattern pattern = Pattern.compile(escaped);

        return pattern.matcher(text).find();
    }

    public static void main(String[] args) {
        String text = "Price is $99.99";
        String userInput = "$99.99";  // Contains special characters

        // This is safe - $ and . are escaped
        boolean found = safeSearch(text, userInput);
        System.out.println("Found: " + found);  // true
    }
}
```

### Validate Input Length

```java
public class InputValidator {
    private static final int MAX_INPUT_LENGTH = 10000;
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");

    public static boolean isValidEmail(String email) {
        // Check length first to prevent DoS
        if (email == null || email.length() > MAX_INPUT_LENGTH) {
            return false;
        }

        return EMAIL_PATTERN.matcher(email).matches();
    }
}
```

## When NOT to Use Regex

### Use String Methods for Simple Tasks

```java
// DON'T use regex for simple contains check
boolean bad = Pattern.compile("hello").matcher(text).find();

// DO use String methods
boolean good = text.contains("hello");

// DON'T use regex for simple starts/ends check
boolean bad2 = Pattern.matches("^hello.*", text);

// DO use String methods
boolean good2 = text.startsWith("hello");

// DON'T use regex for simple replacement
String bad3 = text.replaceAll("old", "new");  // Treats "old" as regex!

// DO use String methods
String good3 = text.replace("old", "new");  // Literal replacement
```

### Don't Parse HTML/XML with Regex

```java
// DON'T do this - HTML is not regular!
String badRegex = "<div.*?>.*?</div>";  // Won't handle nested divs

// DO use proper parsers
// Jsoup for HTML
Document doc = Jsoup.parse(html);
Elements divs = doc.select("div");

// DOM parser for XML
DocumentBuilder builder = DocumentBuilderFactory
    .newInstance()
    .newDocumentBuilder();
Document xmlDoc = builder.parse(new InputSource(new StringReader(xml)));
```

### Use Dedicated Libraries for Complex Formats

```java
// DON'T write complex regex for email validation
// RFC 5322 email regex is 6000+ characters!

// DO use libraries like Apache Commons Validator
import org.apache.commons.validator.routines.EmailValidator;

boolean isValid = EmailValidator.getInstance().isValid(email);

// DON'T write complex regex for URL validation
// DO use java.net.URL or Apache Commons Validator
import org.apache.commons.validator.routines.UrlValidator;

UrlValidator urlValidator = new UrlValidator();
boolean isValidUrl = urlValidator.isValid(url);
```

## Debugging Regex

### Use Online Regex Testers

Popular tools:
- regex101.com
- regexr.com
- regexpal.com

These provide:
- Real-time testing
- Explanation of pattern
- Match highlighting
- Debugger

### Print Intermediate Results

```java
Pattern pattern = Pattern.compile("(\\d{3})-(\\d{3})-(\\d{4})");
Matcher matcher = pattern.matcher("Call 555-123-4567");

if (matcher.find()) {
    System.out.println("Full match: " + matcher.group(0));
    System.out.println("Group 1: " + matcher.group(1));
    System.out.println("Group 2: " + matcher.group(2));
    System.out.println("Group 3: " + matcher.group(3));
    System.out.println("Start: " + matcher.start());
    System.out.println("End: " + matcher.end());
}
```

### Build Incrementally

```java
// Start simple
Pattern p1 = Pattern.compile("\\d+");
test(p1, "123");  // Does basic part work?

// Add complexity step by step
Pattern p2 = Pattern.compile("\\d{3}");
test(p2, "123");  // Does exact count work?

Pattern p3 = Pattern.compile("\\d{3}-\\d{4}");
test(p3, "123-4567");  // Does separator work?

Pattern p4 = Pattern.compile("^\\d{3}-\\d{4}$");
test(p4, "123-4567");  // Do anchors work?
```

## Summary Checklist

### Before Writing Regex

- [ ] Is the task simple enough for String methods?
- [ ] Is there a library that handles this better?
- [ ] Do I understand the input format completely?
- [ ] Have I considered all edge cases?

### While Writing Regex

- [ ] Is the pattern as simple as possible?
- [ ] Are all special characters properly escaped?
- [ ] Am I using named groups for readability?
- [ ] Have I avoided catastrophic backtracking?
- [ ] Is the pattern documented?

### After Writing Regex

- [ ] Does it have comprehensive unit tests?
- [ ] Have I tested with realistic data?
- [ ] Have I tested edge cases (null, empty, very long)?
- [ ] Is it stored as a compiled Pattern constant?
- [ ] Is there inline documentation?
- [ ] Have I considered security implications?

### Performance Checklist

- [ ] Pattern compiled once and reused?
- [ ] Avoided excessive backtracking?
- [ ] Used specific patterns instead of overly broad ones?
- [ ] Considered using possessive or atomic groups?
- [ ] Set timeout for untrusted input?

### Maintenance Checklist

- [ ] Is the pattern easy to understand?
- [ ] Would a junior developer understand it?
- [ ] Is it properly commented?
- [ ] Can it be broken into smaller components?
- [ ] Is there documentation on what it matches?

## Final Thoughts

Regular expressions are powerful but can be dangerous if misused. Always remember:

1. **Simplicity is better than cleverness**
2. **Readability is better than brevity**
3. **Test thoroughly with realistic data**
4. **Document your intentions**
5. **Consider alternatives before reaching for regex**
6. **Security matters - validate inputs and set timeouts**
7. **Performance matters - avoid backtracking**

With proper practices, regex becomes a reliable tool in your programming arsenal rather than a source of bugs and confusion.

---

## Congratulations!

You've completed the Java Regex Patterns documentation series. You now have:

1. **Fundamentals**: Basic patterns, metacharacters, and escaping
2. **Character Classes**: Matching character sets and ranges
3. **Groups**: Capturing, non-capturing, and named groups
4. **Pattern/Matcher**: Java's regex API
5. **Extraction/Validation**: Practical applications
6. **Real-World Examples**: M-Koba message parsing
7. **Advanced Techniques**: Lookahead, lookbehind, Unicode
8. **Best Practices**: This guide

You're now equipped to handle complex text processing tasks with confidence!

## Additional Resources

- [Java Pattern Documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html)
- [Java Matcher Documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Matcher.html)
- [Regular-Expressions.info](https://www.regular-expressions.info/)
- [Regex101](https://regex101.com/) - Online regex tester
