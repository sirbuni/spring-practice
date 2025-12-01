# Character Classes and Quantifiers

## Table of Contents
1. [Introduction](#introduction)
2. [Character Classes](#character-classes)
3. [Predefined Character Classes](#predefined-character-classes)
4. [Negated Character Classes](#negated-character-classes)
5. [Ranges](#ranges)
6. [Quantifiers](#quantifiers)
7. [Greedy vs Lazy Matching](#greedy-vs-lazy-matching)
8. [Possessive Quantifiers](#possessive-quantifiers)
9. [Practice Examples](#practice-examples)

## Introduction

Character classes and quantifiers are fundamental building blocks that make regex powerful. Character classes let you match one character from a set of characters, while quantifiers specify how many times a pattern should match.

## Character Classes

A character class matches any single character from a set of characters enclosed in square brackets `[]`.

### Basic Syntax

```java
// Match any vowel
String regex = "[aeiou]";

// Match any digit
String regex = "[0123456789]";

// Match 'a', 'b', or 'c'
String regex = "[abc]";
```

### Example: Matching Vowels

```java
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CharacterClassExample {
    public static void main(String[] args) {
        String text = "Hello World";
        String regex = "[aeiou]";  // Match any lowercase vowel

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        System.out.println("Vowels found:");
        while (matcher.find()) {
            System.out.println(matcher.group() + " at position " + matcher.start());
        }
    }
}
```

**Output:**
```
Vowels found:
e at position 1
o at position 4
o at position 7
```

**Note:** Only lowercase vowels are matched. To include uppercase, use `[aeiouAEIOU]` or the case-insensitive flag.

### Example: Case-Insensitive Matching

```java
String text = "Hello World";
String regex = "[aeiou]";

// Use CASE_INSENSITIVE flag
Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
Matcher matcher = pattern.matcher(text);

System.out.println("All vowels (case-insensitive):");
while (matcher.find()) {
    System.out.println(matcher.group() + " at position " + matcher.start());
}
```

**Output:**
```
All vowels (case-insensitive):
e at position 1
o at position 4
o at position 7
```

## Predefined Character Classes

Java provides convenient shortcuts for common character classes.

### Common Predefined Classes

| Shorthand | Equivalent | Description |
|-----------|------------|-------------|
| `\d` | `[0-9]` | Any digit |
| `\D` | `[^0-9]` | Any non-digit |
| `\w` | `[a-zA-Z0-9_]` | Word character (letters, digits, underscore) |
| `\W` | `[^a-zA-Z0-9_]` | Non-word character |
| `\s` | `[ \t\n\r\f]` | Whitespace character |
| `\S` | `[^ \t\n\r\f]` | Non-whitespace character |
| `.` | (any char except newline) | Any character except newline |

**Remember:** In Java strings, you must use double backslashes: `\\d`, `\\w`, `\\s`

### Example: Extracting Digits

```java
String text = "Order #12345 total: $250.75";
String regex = "\\d";  // Match any single digit

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Individual digits:");
while (matcher.find()) {
    System.out.print(matcher.group() + " ");
}
```

**Output:**
```
Individual digits:
1 2 3 4 5 2 5 0 7 5
```

### Example: Matching Word Characters

```java
String text = "user_name123 and my-email@example.com";
String regex = "\\w+";  // One or more word characters

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Word character sequences:");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Word character sequences:
user_name123
and
my
email
example
com
```

**Note:** Hyphens and @ symbols are not word characters, so they split the matches.

### Example: Finding Whitespace

```java
String text = "Hello\tWorld\nNew Line";
String regex = "\\s+";  // One or more whitespace characters

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Whitespace found:");
int count = 0;
while (matcher.find()) {
    count++;
    String ws = matcher.group().replace("\t", "\\t").replace("\n", "\\n");
    System.out.println("Match " + count + ": '" + ws + "'");
}
```

**Output:**
```
Whitespace found:
Match 1: '\t'
Match 2: '\n'
Match 3: ' '
```

## Negated Character Classes

The caret `^` inside square brackets negates the character class, matching any character NOT in the set.

### Syntax

```java
// Match any character that is NOT a vowel
String regex = "[^aeiou]";

// Match any character that is NOT a digit
String regex = "[^0-9]";  // Same as \\D

// Match any character that is NOT a letter
String regex = "[^a-zA-Z]";
```

### Example: Matching Non-Vowels

```java
String text = "Hello";
String regex = "[^aeiou]";  // Match any non-vowel

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Non-vowels:");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Non-vowels:
H
l
l
```

### Example: Removing Special Characters

```java
String text = "Hello, World! How are you?";
String regex = "[^a-zA-Z0-9\\s]";  // Match anything that's not alphanumeric or space

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

// Replace all non-alphanumeric characters with empty string
String cleaned = matcher.replaceAll("");
System.out.println("Original: " + text);
System.out.println("Cleaned:  " + cleaned);
```

**Output:**
```
Original: Hello, World! How are you?
Cleaned:  Hello World How are you
```

## Ranges

Ranges allow you to specify a range of characters using a hyphen `-`.

### Common Ranges

```java
[a-z]      // Any lowercase letter
[A-Z]      // Any uppercase letter
[a-zA-Z]   // Any letter
[0-9]      // Any digit (same as \d)
[a-zA-Z0-9] // Any alphanumeric character
[a-f]      // Letters a through f (useful for hexadecimal)
[A-Fa-f0-9] // Hexadecimal digit
```

### Example: Matching Lowercase Letters

```java
String text = "Hello World 123";
String regex = "[a-z]+";  // One or more lowercase letters

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Lowercase sequences:");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Lowercase sequences:
ello
orld
```

### Example: Hexadecimal Color Codes

```java
String text = "Colors: #FF5733 #C70039 #900C3F #581845";
String regex = "#[A-Fa-f0-9]{6}";  // # followed by exactly 6 hex digits

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Color codes found:");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Color codes found:
#FF5733
#C70039
#900C3F
#581845
```

### Combining Ranges and Individual Characters

```java
// Match digits, comma, and decimal point
String regex = "[0-9,.]";

// Match alphanumeric and underscore (same as \w)
String regex = "[a-zA-Z0-9_]";

// Match common punctuation
String regex = "[.,!?;:]";
```

## Quantifiers

Quantifiers specify how many times a pattern should match.

### Basic Quantifiers

| Quantifier | Meaning | Example |
|------------|---------|---------|
| `*` | Zero or more times | `ab*c` matches "ac", "abc", "abbc" |
| `+` | One or more times | `ab+c` matches "abc", "abbc" but not "ac" |
| `?` | Zero or one time (optional) | `colou?r` matches "color" or "colour" |
| `{n}` | Exactly n times | `\d{3}` matches exactly 3 digits |
| `{n,}` | n or more times | `\d{3,}` matches 3 or more digits |
| `{n,m}` | Between n and m times | `\d{3,5}` matches 3, 4, or 5 digits |

### Example: Zero or More (*)

```java
String text = "ac abc abbc abbbc";
String regex = "ab*c";  // 'a', followed by zero or more 'b', then 'c'

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Matches for 'ab*c':");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Matches for 'ab*c':
ac
abc
abbc
abbbc
```

### Example: One or More (+)

```java
String text = "1 12 123 1234 12345";
String regex = "\\d+";  // One or more digits

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Number sequences:");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Number sequences:
1
12
123
1234
12345
```

### Example: Optional (?)

```java
String text = "color colour gray grey";
String regex = "colou?r";  // 'u' is optional

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Matches for 'colou?r':");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Matches for 'colou?r':
color
colour
```

### Example: Exact Count {n}

```java
String text = "Phone: 123-456-7890, ZIP: 12345, SSN: 123-45-6789";
String regex = "\\d{5}";  // Exactly 5 digits

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("5-digit sequences:");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
5-digit sequences:
12345
```

### Example: Range {n,m}

```java
String text = "Call: 555-1234 or 555-123-4567";
String regex = "\\d{3,4}";  // 3 to 4 digits

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Digit groups (3-4 digits):");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Digit groups (3-4 digits):
555
1234
555
123
4567
```

### Example: Minimum Count {n,}

```java
String text = "a12 b456 c78901 d2345678";
String regex = "[a-z]\\d{4,}";  // Letter followed by 4 or more digits

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Matches (letter + 4+ digits):");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Matches (letter + 4+ digits):
c78901
d2345678
```

## Greedy vs Lazy Matching

By default, quantifiers are **greedy** - they match as much text as possible. Adding `?` after a quantifier makes it **lazy** (non-greedy) - matching as little as possible.

### Greedy Quantifiers (Default)

| Greedy | Meaning |
|--------|---------|
| `*` | Match as many times as possible |
| `+` | Match as many times as possible |
| `?` | Match if possible |
| `{n,m}` | Match as many times as possible |

### Lazy Quantifiers

| Lazy | Meaning |
|------|---------|
| `*?` | Match as few times as possible |
| `+?` | Match as few times as possible |
| `??` | Match only if necessary |
| `{n,m}?` | Match as few times as possible |

### Example: Greedy vs Lazy

```java
String text = "<div>First</div><div>Second</div>";

// Greedy matching
String greedyRegex = "<div>.*</div>";
Matcher greedyMatcher = Pattern.compile(greedyRegex).matcher(text);

System.out.println("Greedy matching:");
if (greedyMatcher.find()) {
    System.out.println(greedyMatcher.group());
}

// Lazy matching
String lazyRegex = "<div>.*?</div>";
Matcher lazyMatcher = Pattern.compile(lazyRegex).matcher(text);

System.out.println("\nLazy matching:");
while (lazyMatcher.find()) {
    System.out.println(lazyMatcher.group());
}
```

**Output:**
```
Greedy matching:
<div>First</div><div>Second</div>

Lazy matching:
<div>First</div>
<div>Second</div>
```

**Explanation:**
- **Greedy (`.*`)**: Matches everything from first `<div>` to the last `</div>`
- **Lazy (`.*?`)**: Matches from `<div>` to the nearest `</div>`

### Example: Extracting Quoted Strings

```java
String text = "Name: \"John Doe\" Age: \"30\"";

// Greedy (wrong - captures too much)
String greedyRegex = "\".*\"";
Matcher greedyMatcher = Pattern.compile(greedyRegex).matcher(text);

System.out.println("Greedy matching:");
while (greedyMatcher.find()) {
    System.out.println(greedyMatcher.group());
}

// Lazy (correct - captures individual strings)
String lazyRegex = "\".*?\"";
Matcher lazyMatcher = Pattern.compile(lazyRegex).matcher(text);

System.out.println("\nLazy matching:");
while (lazyMatcher.find()) {
    System.out.println(lazyMatcher.group());
}
```

**Output:**
```
Greedy matching:
"John Doe" Age: "30"

Lazy matching:
"John Doe"
"30"
```

## Possessive Quantifiers

Possessive quantifiers are like greedy quantifiers but never backtrack. They match as much as possible and never give up characters.

### Possessive Syntax

| Possessive | Meaning |
|------------|---------|
| `*+` | Possessive zero or more |
| `++` | Possessive one or more |
| `?+` | Possessive zero or one |
| `{n,m}+` | Possessive range |

### When to Use Possessive Quantifiers

Possessive quantifiers are primarily used for performance optimization when you know backtracking is not needed.

```java
String text = "aaaaaab";

// Greedy (will backtrack and match)
String greedyRegex = "a+ab";
boolean greedyMatch = Pattern.matches(greedyRegex, text);
System.out.println("Greedy matches: " + greedyMatch);

// Possessive (will NOT backtrack, fails to match)
String possessiveRegex = "a++ab";
boolean possessiveMatch = Pattern.matches(possessiveRegex, text);
System.out.println("Possessive matches: " + possessiveMatch);
```

**Output:**
```
Greedy matches: true
Possessive matches: false
```

**Explanation:**
- **Greedy `a+`**: Consumes all 'a's, then backtracks to allow 'ab' to match
- **Possessive `a++`**: Consumes all 'a's and never gives them back, so 'ab' cannot match

## Practice Examples

### Example 1: Extracting Currency Amounts from M-Koba Messages

```java
String message = "has paid TZS.190,000.0 as loan repayment";
String regex = "TZS\\.[0-9,]+\\.?[0-9]*";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(message);

if (matcher.find()) {
    String amount = matcher.group();
    System.out.println("Amount: " + amount);

    // Extract just the numeric value
    String numericOnly = amount.replaceAll("[^0-9.]", "");
    System.out.println("Numeric value: " + numericOnly);
}
```

**Output:**
```
Amount: TZS.190,000.0
Numeric value: 190000.0
```

### Example 2: Validating Email Address (Simple)

```java
String email1 = "user@example.com";
String email2 = "invalid.email";
String regex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";

System.out.println(email1 + " is valid: " + Pattern.matches(regex, email1));
System.out.println(email2 + " is valid: " + Pattern.matches(regex, email2));
```

**Output:**
```
user@example.com is valid: true
invalid.email is valid: false
```

**Pattern Breakdown:**
- `[a-zA-Z0-9._%+-]+`: One or more valid characters before @
- `@`: Literal @ symbol
- `[a-zA-Z0-9.-]+`: Domain name
- `\\.`: Literal dot
- `[a-zA-Z]{2,}`: Top-level domain (2+ letters)

### Example 3: Extracting Phone Numbers with Different Formats

```java
String text = "Contact: 255-755-959291, (255) 765-123456 or 255.712.345678";
String regex = "[0-9]{3}[-.(]?[0-9]{3}[-. )]?[0-9]{6}";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Phone numbers found:");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Phone numbers found:
255-755-959291
255) 765-123456
255.712.345678
```

### Example 4: Parsing Dates from M-Koba Messages

```java
String message = "on 27/11/2025 at 10:27";
String regex = "\\d{2}/\\d{2}/\\d{4}";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(message);

if (matcher.find()) {
    String date = matcher.group();
    System.out.println("Date found: " + date);

    // Extract day, month, year
    String[] parts = date.split("/");
    System.out.println("Day: " + parts[0]);
    System.out.println("Month: " + parts[1]);
    System.out.println("Year: " + parts[2]);
}
```

**Output:**
```
Date found: 27/11/2025
Day: 27
Month: 11
Year: 2025
```

### Example 5: Finding All Words of Specific Length

```java
String text = "The quick brown fox jumps over the lazy dog";
String regex = "\\b\\w{5}\\b";  // Words with exactly 5 letters

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("5-letter words:");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
5-letter words:
quick
brown
jumps
```

## Summary

In this guide, you learned:

1. **Character classes** `[abc]` match one character from a set
2. **Predefined classes**: `\d` (digit), `\w` (word), `\s` (space)
3. **Negated classes** `[^abc]` match anything NOT in the set
4. **Ranges** `[a-z]` specify character ranges
5. **Quantifiers** specify repetition:
   - `*` (zero or more)
   - `+` (one or more)
   - `?` (optional)
   - `{n}` (exactly n)
   - `{n,m}` (between n and m)
6. **Greedy vs Lazy**: Greedy matches maximum, lazy (`*?`, `+?`) matches minimum
7. **Possessive quantifiers** `*+`, `++` never backtrack (performance optimization)

## Next Steps

In the next guide, we'll explore:
- Grouping with parentheses `()`
- Capturing groups for extracting data
- Non-capturing groups `(?:)`
- Backreferences
- Named groups

Continue to: [03-groups-and-capturing.md](./03-groups-and-capturing.md)
