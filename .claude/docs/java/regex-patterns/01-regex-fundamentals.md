# Java Regex Fundamentals

## Table of Contents
1. [Introduction](#introduction)
2. [What is Regular Expression?](#what-is-regular-expression)
3. [Basic Pattern Matching](#basic-pattern-matching)
4. [Literal Characters](#literal-characters)
5. [Metacharacters](#metacharacters)
6. [The Dot Metacharacter](#the-dot-metacharacter)
7. [Anchors](#anchors)
8. [Escape Sequences](#escape-sequences)
9. [Practice Examples](#practice-examples)

## Introduction

Regular expressions (regex) are powerful patterns used for matching, searching, and manipulating text. In Java, regex functionality is provided through the `java.util.regex` package, primarily using the `Pattern` and `Matcher` classes.

## What is Regular Expression?

A regular expression is a sequence of characters that defines a search pattern. Think of it as a sophisticated "find" operation that can:
- Match specific text patterns
- Validate input formats (emails, phone numbers, dates)
- Extract data from text
- Replace or transform text
- Split strings based on complex delimiters

### Real-World Use Cases
1. **Form Validation**: Email addresses, phone numbers, postal codes
2. **Data Extraction**: Parsing log files, extracting transaction details
3. **Text Processing**: Finding and replacing patterns in documents
4. **Data Cleaning**: Removing unwanted characters or formatting
5. **URL Routing**: Matching URL patterns in web applications

## Basic Pattern Matching

### Simple Example in Java

```java
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class BasicRegexExample {
    public static void main(String[] args) {
        // The text to search in
        String text = "The price is 100 dollars";

        // The pattern to search for
        String regex = "price";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(text);

        // Check if pattern is found
        if (matcher.find()) {
            System.out.println("Pattern found!");
            System.out.println("Found at index: " + matcher.start());
        }
    }
}
```

**Output:**
```
Pattern found!
Found at index: 4
```

**Explanation:**
- `Pattern.compile(regex)`: Compiles the regex string into a Pattern object
- `pattern.matcher(text)`: Creates a Matcher to search in the given text
- `matcher.find()`: Searches for the pattern in the text
- `matcher.start()`: Returns the starting index where the match was found

## Literal Characters

Literal characters match themselves exactly. Most letters and numbers are literal characters.

### Example: Matching Exact Text

```java
String text = "I have 3 cats and 2 dogs";
String regex = "cats";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

if (matcher.find()) {
    System.out.println("Match found: " + matcher.group());
    System.out.println("Position: " + matcher.start() + "-" + matcher.end());
}
```

**Output:**
```
Match found: cats
Position: 9-13
```

**Note:** The match is case-sensitive by default. "Cats" would not match "cats".

## Metacharacters

Metacharacters are special characters with specific meanings in regex. They need to be escaped with a backslash `\` if you want to match them literally.

### Common Metacharacters

| Character | Meaning | Example |
|-----------|---------|---------|
| `.` | Any single character (except newline) | `c.t` matches "cat", "cot", "cut" |
| `^` | Start of line | `^Hello` matches "Hello" at line start |
| `$` | End of line | `bye$` matches "bye" at line end |
| `*` | Zero or more times | `ab*c` matches "ac", "abc", "abbc" |
| `+` | One or more times | `ab+c` matches "abc", "abbc" but not "ac" |
| `?` | Zero or one time | `colou?r` matches "color" or "colour" |
| `\|` | Alternation (OR) | `cat\|dog` matches "cat" or "dog" |
| `[]` | Character class | `[aeiou]` matches any vowel |
| `()` | Grouping | `(ab)+` matches "ab", "abab", "ababab" |
| `{}` | Quantifier | `a{3}` matches "aaa" exactly |

### Example: Using Metacharacters

```java
String text = "My email is user@example.com and my phone is 123-456-7890";

// Match the @ symbol (literal, no escape needed)
String regex1 = "@";

// Match a dot (needs escaping because . is a metacharacter)
String regex2 = "\\.";  // Note: In Java strings, we need double backslash

// Match a hyphen in phone number (literal in this context)
String regex3 = "-";

Pattern pattern = Pattern.compile(regex2);
Matcher matcher = pattern.matcher(text);

System.out.println("Dots found:");
while (matcher.find()) {
    System.out.println("At position: " + matcher.start());
}
```

**Output:**
```
Dots found:
At position: 23
```

## The Dot Metacharacter

The dot `.` is one of the most commonly used metacharacters. It matches any single character except newline.

### Example: Dot Metacharacter

```java
String text = "cat cot cut c@t c1t";
String regex = "c.t";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Matches found:");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Matches found:
cat
cot
cut
c@t
c1t
```

**Explanation:** The pattern `c.t` matches 'c', followed by ANY single character, followed by 't'.

### Example: Matching Literal Dot

```java
String text = "File names: report.pdf document.docx image.jpg";
String regex = "\\.pdf";  // Escape the dot to match it literally

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

if (matcher.find()) {
    System.out.println("PDF file found!");
}
```

**Output:**
```
PDF file found!
```

## Anchors

Anchors don't match characters; they match positions in the text.

### Start of Line: `^`

```java
String text = "Hello World\nHello Universe";
String regex = "^Hello";

Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
Matcher matcher = pattern.matcher(text);

System.out.println("Matches at line start:");
while (matcher.find()) {
    System.out.println("Found at position: " + matcher.start());
}
```

**Output:**
```
Matches at line start:
Found at position: 0
Found at position: 12
```

**Note:** `Pattern.MULTILINE` flag makes `^` match at the start of each line, not just the start of the entire string.

### End of Line: `$`

```java
String text = "Hello World\nGoodbye World";
String regex = "World$";

Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
Matcher matcher = pattern.matcher(text);

System.out.println("Matches at line end:");
while (matcher.find()) {
    System.out.println("Line ending with 'World': " + matcher.group());
}
```

**Output:**
```
Matches at line end:
Line ending with 'World': World
Line ending with 'World': World
```

### Word Boundaries: `\b`

Word boundaries match the position between a word character and a non-word character.

```java
String text = "The cat scattered the seeds";
String regex = "\\bcat\\b";  // Match 'cat' as a whole word

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Whole word 'cat' found: " + matcher.find());

// Now try matching 'cat' anywhere
String regex2 = "cat";
Matcher matcher2 = Pattern.compile(regex2).matcher(text);
System.out.println("\nAll 'cat' occurrences:");
while (matcher2.find()) {
    System.out.println("Found at: " + matcher2.start());
}
```

**Output:**
```
Whole word 'cat' found: true

All 'cat' occurrences:
Found at: 4
Found at: 8
```

**Explanation:**
- `\bcat\b` matches "cat" only as a complete word (position 4)
- `cat` matches "cat" anywhere, including inside "scattered" (positions 4 and 8)

## Escape Sequences

In Java, you need to escape backslashes because they're special in Java strings.

### Common Escape Sequences

| Regex | Java String | Meaning |
|-------|-------------|---------|
| `\d` | `"\\d"` | Any digit (0-9) |
| `\D` | `"\\D"` | Any non-digit |
| `\w` | `"\\w"` | Word character (a-z, A-Z, 0-9, _) |
| `\W` | `"\\W"` | Non-word character |
| `\s` | `"\\s"` | Whitespace (space, tab, newline) |
| `\S` | `"\\S"` | Non-whitespace |
| `\b` | `"\\b"` | Word boundary |
| `\B` | `"\\B"` | Non-word boundary |
| `\.` | `"\\."` | Literal dot |
| `\\` | `"\\\\"` | Literal backslash |

### Example: Using Escape Sequences

```java
String text = "Order #12345 costs $99.99 and ships on 2025-11-27";

// Match all digits
String digitRegex = "\\d+";
Pattern digitPattern = Pattern.compile(digitRegex);
Matcher digitMatcher = digitPattern.matcher(text);

System.out.println("Numbers found:");
while (digitMatcher.find()) {
    System.out.println(digitMatcher.group());
}

// Match all word characters (alphanumeric sequences)
String wordRegex = "\\w+";
Pattern wordPattern = Pattern.compile(wordRegex);
Matcher wordMatcher = wordPattern.matcher(text);

System.out.println("\nWords found:");
while (wordMatcher.find()) {
    System.out.println(wordMatcher.group());
}
```

**Output:**
```
Numbers found:
12345
99
99
2025
11
27

Words found:
Order
12345
costs
99
99
and
ships
on
2025
11
27
```

## Practice Examples

### Example 1: Finding Phone Numbers (Simple Pattern)

```java
String text = "Call me at 123-456-7890 or 987-654-3210";
String regex = "\\d{3}-\\d{3}-\\d{4}";

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
123-456-7890
987-654-3210
```

**Explanation:**
- `\d{3}`: Exactly 3 digits
- `-`: Literal hyphen
- `\d{3}`: Exactly 3 digits
- `-`: Literal hyphen
- `\d{4}`: Exactly 4 digits

### Example 2: Checking if String Starts/Ends with Pattern

```java
String text = "HelloWorld";

// Check if starts with "Hello"
boolean startsWithHello = Pattern.matches("^Hello.*", text);
System.out.println("Starts with 'Hello': " + startsWithHello);

// Check if ends with "World"
boolean endsWithWorld = Pattern.matches(".*World$", text);
System.out.println("Ends with 'World': " + endsWithWorld);

// Note: Pattern.matches() requires the entire string to match
boolean exactMatch = Pattern.matches("HelloWorld", text);
System.out.println("Exact match: " + exactMatch);
```

**Output:**
```
Starts with 'Hello': true
Ends with 'World': true
Exact match: true
```

### Example 3: Extracting Transaction Amounts

```java
// Based on M-Koba messages
String message = "has paid TZS.190,000.0 as loan repayment";
String regex = "TZS\\.[0-9,]+\\.?[0-9]*";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(message);

if (matcher.find()) {
    System.out.println("Amount found: " + matcher.group());
}
```

**Output:**
```
Amount found: TZS.190,000.0
```

**Explanation:**
- `TZS\\.`: Literal "TZS."
- `[0-9,]+`: One or more digits or commas
- `\\.?`: Optional decimal point
- `[0-9]*`: Zero or more digits after decimal

## Summary

In this guide, you learned:

1. **Regular expressions** are patterns for matching and manipulating text
2. **Pattern and Matcher** are the main Java classes for regex operations
3. **Literal characters** match themselves exactly
4. **Metacharacters** have special meanings (`. ^ $ * + ? | [] () {}`)
5. **The dot (.)** matches any single character
6. **Anchors** match positions: `^` (start), `$` (end), `\b` (word boundary)
7. **Escape sequences** represent character classes: `\d` (digit), `\w` (word), `\s` (space)
8. **Java requires double backslashes** in regex strings: `\\d`, `\\.`, `\\w`

## Next Steps

In the next guide, we'll explore:
- Character classes in depth `[abc]`, `[^abc]`, `[a-z]`
- Quantifiers: `*`, `+`, `?`, `{n}`, `{n,}`, `{n,m}`
- Greedy vs lazy matching
- Practical examples for data validation

Continue to: [02-character-classes-and-quantifiers.md](./02-character-classes-and-quantifiers.md)
