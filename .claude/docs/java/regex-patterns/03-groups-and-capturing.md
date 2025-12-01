# Groups and Capturing

## Table of Contents
1. [Introduction](#introduction)
2. [Grouping with Parentheses](#grouping-with-parentheses)
3. [Capturing Groups](#capturing-groups)
4. [Non-Capturing Groups](#non-capturing-groups)
5. [Named Groups](#named-groups)
6. [Backreferences](#backreferences)
7. [Alternation with Groups](#alternation-with-groups)
8. [Nested Groups](#nested-groups)
9. [Practice Examples](#practice-examples)

## Introduction

Groups are one of the most powerful features in regex. They allow you to:
- Extract specific parts of matched text
- Apply quantifiers to multiple characters
- Create subpatterns with alternation
- Reference previously matched text (backreferences)
- Organize complex patterns

## Grouping with Parentheses

Parentheses `()` create groups in regex patterns. Groups serve multiple purposes:
1. Group characters together for quantifiers
2. Capture matched text for extraction
3. Create subpatterns
4. Enable backreferences

### Basic Grouping Syntax

```java
// Without grouping - only 'c' is optional
"abc?" // Matches: "ab" or "abc"

// With grouping - entire "abc" is optional
"(abc)?" // Matches: "" or "abc"

// Without grouping - only last 'o' is repeated
"ho+" // Matches: "ho", "hoo", "hooo"

// With grouping - "ho" is repeated
"(ho)+" // Matches: "ho", "hoho", "hohoho"
```

### Example: Grouping for Quantifiers

```java
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class GroupingExample {
    public static void main(String[] args) {
        String text1 = "ha haha hahaha";
        String regex = "(ha)+";  // One or more "ha"

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text1);

        System.out.println("Matches for '(ha)+':");
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
```

**Output:**
```
Matches for '(ha)+':
ha
haha
hahaha
```

## Capturing Groups

Capturing groups not only group patterns but also save the matched text for later retrieval. Each group is numbered starting from 1.

### Group Numbering

```java
// Group 0: Entire match (always available)
// Group 1: First set of parentheses
// Group 2: Second set of parentheses
// And so on...

String regex = "(\\d{3})-(\\d{3})-(\\d{4})";
// Group 0: Entire match (e.g., "123-456-7890")
// Group 1: First 3 digits (e.g., "123")
// Group 2: Second 3 digits (e.g., "456")
// Group 3: Last 4 digits (e.g., "7890")
```

### Example: Capturing Phone Number Parts

```java
String phoneNumber = "Call me at 255-755-959291";
String regex = "(\\d{3})-(\\d{3})-(\\d{6})";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(phoneNumber);

if (matcher.find()) {
    System.out.println("Full match: " + matcher.group(0));  // or just matcher.group()
    System.out.println("Country code: " + matcher.group(1));
    System.out.println("Area code: " + matcher.group(2));
    System.out.println("Number: " + matcher.group(3));
}
```

**Output:**
```
Full match: 255-755-959291
Country code: 255
Area code: 755
Number: 959291
```

### Example: Parsing Names

```java
String text = "John Doe, Jane Smith, Bob Johnson";
String regex = "(\\w+)\\s+(\\w+)";  // First name, space, last name

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Names found:");
while (matcher.find()) {
    String firstName = matcher.group(1);
    String lastName = matcher.group(2);
    System.out.println("First: " + firstName + ", Last: " + lastName);
}
```

**Output:**
```
Names found:
First: John, Last: Doe
First: Jane, Last: Smith
First: Bob, Last: Johnson
```

### Example: Extracting Date Components

```java
String text = "Transaction date: 27/11/2025";
String regex = "(\\d{2})/(\\d{2})/(\\d{4})";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

if (matcher.find()) {
    String day = matcher.group(1);
    String month = matcher.group(2);
    String year = matcher.group(3);

    System.out.println("Date: " + matcher.group());
    System.out.println("Day: " + day);
    System.out.println("Month: " + month);
    System.out.println("Year: " + year);
}
```

**Output:**
```
Date: 27/11/2025
Day: 27
Month: 11
Year: 2025
```

### Counting Groups

```java
String regex = "(\\d{3})-(\\d{3})-(\\d{4})";
Pattern pattern = Pattern.compile(regex);

System.out.println("Number of capturing groups: " + pattern.matcher("").groupCount());
```

**Output:**
```
Number of capturing groups: 3
```

**Note:** `groupCount()` returns the number of capturing groups, not including group 0 (the entire match).

## Non-Capturing Groups

Sometimes you want to group for quantifiers or alternation but don't need to capture the text. Use `(?:...)` for non-capturing groups.

### Syntax

```java
// Capturing group (saves matched text)
"(abc)+"

// Non-capturing group (doesn't save matched text)
"(?:abc)+"
```

### Why Use Non-Capturing Groups?

1. **Performance**: Slightly faster (no need to save matched text)
2. **Memory**: Uses less memory
3. **Simplicity**: Keeps group numbering simpler when you don't need to extract the text

### Example: Capturing vs Non-Capturing

```java
String text = "hohoho";

// With capturing groups
String capturingRegex = "(ho)+";
Matcher capturingMatcher = Pattern.compile(capturingRegex).matcher(text);

if (capturingMatcher.find()) {
    System.out.println("Capturing groups:");
    System.out.println("Full match: " + capturingMatcher.group(0));
    System.out.println("Group 1: " + capturingMatcher.group(1));
    System.out.println("Group count: " + capturingMatcher.groupCount());
}

// With non-capturing groups
String nonCapturingRegex = "(?:ho)+";
Matcher nonCapturingMatcher = Pattern.compile(nonCapturingRegex).matcher(text);

if (nonCapturingMatcher.find()) {
    System.out.println("\nNon-capturing groups:");
    System.out.println("Full match: " + nonCapturingMatcher.group(0));
    System.out.println("Group count: " + nonCapturingMatcher.groupCount());
    // nonCapturingMatcher.group(1) would throw an exception!
}
```

**Output:**
```
Capturing groups:
Full match: hohoho
Group 1: ho
Group count: 1

Non-capturing groups:
Full match: hohoho
Group count: 0
```

### Example: Practical Use Case

```java
String text = "Price: $99.99 or €85.50";
// We want to capture the amount but not the currency symbol
String regex = "(?:[$€])(\\d+\\.\\d{2})";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Amounts found:");
while (matcher.find()) {
    // Group 1 contains just the number (currency is not captured)
    System.out.println(matcher.group(1));
}
```

**Output:**
```
Amounts found:
99.99
85.50
```

## Named Groups

Named groups allow you to assign names to capturing groups, making your code more readable and maintainable.

### Syntax

```java
// Named group syntax: (?<name>pattern)
String regex = "(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})";
```

### Example: Named Groups for Dates

```java
String text = "Date: 2025-11-27";
String regex = "(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

if (matcher.find()) {
    System.out.println("Full date: " + matcher.group());
    System.out.println("Year: " + matcher.group("year"));
    System.out.println("Month: " + matcher.group("month"));
    System.out.println("Day: " + matcher.group("day"));

    // You can also access by index
    System.out.println("\nUsing indices:");
    System.out.println("Group 1 (year): " + matcher.group(1));
    System.out.println("Group 2 (month): " + matcher.group(2));
    System.out.println("Group 3 (day): " + matcher.group(3));
}
```

**Output:**
```
Full date: 2025-11-27
Year: 2025
Month: 11
Day: 27

Using indices:
Group 1 (year): 2025
Group 2 (month): 11
Group 3 (day): 27
```

### Example: Parsing M-Koba Transaction Messages

```java
String message = "255755959291(OBED SANGA) has purchased shares worth of TZS.20,000.00";
String regex = "(?<phone>\\d+)\\((?<name>[^)]+)\\).*?TZS\\.(?<amount>[0-9,]+\\.?[0-9]*)";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(message);

if (matcher.find()) {
    System.out.println("Phone: " + matcher.group("phone"));
    System.out.println("Name: " + matcher.group("name"));
    System.out.println("Amount: TZS." + matcher.group("amount"));
}
```

**Output:**
```
Phone: 255755959291
Name: OBED SANGA
Amount: TZS.20,000.00
```

## Backreferences

Backreferences allow you to match the same text that was previously matched by a capturing group.

### Syntax

```java
// \1 refers to group 1, \2 to group 2, etc.
// In Java strings: \\1, \\2, etc.

// Match repeated words
String regex = "\\b(\\w+)\\s+\\1\\b";  // Word followed by the same word
```

### Example: Finding Duplicate Words

```java
String text = "This is is a test test of duplicate words";
String regex = "\\b(\\w+)\\s+\\1\\b";  // \\1 refers to whatever group 1 matched

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Duplicate words:");
while (matcher.find()) {
    System.out.println("'" + matcher.group(1) + "' appears twice");
}
```

**Output:**
```
Duplicate words:
'is' appears twice
'test' appears twice
```

### Example: Matching Paired Tags

```java
String html = "<div>Content</div> <span>Text</span> <p>Paragraph</p>";
String regex = "<(\\w+)>.*?</\\1>";  // Opening tag, content, matching closing tag

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(html);

System.out.println("Valid HTML tags:");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Valid HTML tags:
<div>Content</div>
<span>Text</span>
<p>Paragraph</p>
```

**Explanation:** `\\1` matches whatever the first group `(\\w+)` captured, ensuring the closing tag matches the opening tag.

### Example: Finding Repeated Patterns

```java
String text = "abcabc xyzxyz 123123";
String regex = "(\\w{3})\\1";  // Any 3 characters repeated

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Repeated patterns:");
while (matcher.find()) {
    System.out.println(matcher.group() + " (repeating: " + matcher.group(1) + ")");
}
```

**Output:**
```
Repeated patterns:
abcabc (repeating: abc)
xyzxyz (repeating: xyz)
123123 (repeating: 123)
```

### Named Backreferences

You can also use backreferences with named groups using `\k<name>`:

```java
String text = "The word word appears twice";
String regex = "\\b(?<word>\\w+)\\s+\\k<word>\\b";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

if (matcher.find()) {
    System.out.println("Duplicate: " + matcher.group("word"));
}
```

**Output:**
```
Duplicate: word
```

## Alternation with Groups

The pipe `|` creates alternation (OR) within groups.

### Syntax

```java
// Match either "cat" or "dog"
"(cat|dog)"

// Match "gray" or "grey"
"gr(a|e)y"

// Match different date formats
"\\d{2}(/|-)\\d{2}\\1\\d{4}"  // Using backreference to match same separator
```

### Example: Multiple Options

```java
String text = "I have a cat, a dog, and a bird";
String regex = "(cat|dog|bird)";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Animals found:");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Animals found:
cat
dog
bird
```

### Example: Matching Different Transaction Types

```java
String text = "has paid as loan repayment, purchased shares, paid for social fund";
String regex = "(loan repayment|purchased shares|social fund)";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Transaction types:");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Transaction types:
loan repayment
purchased shares
social fund
```

### Example: Consistent Separators with Backreferences

```java
String text = "Dates: 27/11/2025, 15-12-2025, 01/02/2026";
// Match date where all separators are the same
String regex = "\\d{2}([/-])\\d{2}\\1\\d{4}";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

System.out.println("Consistent date formats:");
while (matcher.find()) {
    System.out.println(matcher.group());
}
```

**Output:**
```
Consistent date formats:
27/11/2025
15-12-2025
01/02/2026
```

## Nested Groups

Groups can be nested inside other groups. Numbering proceeds left to right based on opening parentheses.

### Numbering Nested Groups

```java
String regex = "((\\d{3})-(\\d{4}))";
// Group 0: Entire match
// Group 1: (\\d{3})-(\\d{4}) - outer group
// Group 2: (\\d{3}) - first inner group
// Group 3: (\\d{4}) - second inner group
```

### Example: Nested Groups

```java
String text = "Phone: 555-1234";
String regex = "((\\d{3})-(\\d{4}))";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

if (matcher.find()) {
    System.out.println("Group 0 (full): " + matcher.group(0));
    System.out.println("Group 1 (outer): " + matcher.group(1));
    System.out.println("Group 2 (first inner): " + matcher.group(2));
    System.out.println("Group 3 (second inner): " + matcher.group(3));
}
```

**Output:**
```
Group 0 (full): 555-1234
Group 1 (outer): 555-1234
Group 2 (first inner): 555
Group 3 (second inner): 1234
```

### Example: Complex Nested Pattern

```java
String email = "user.name@example.com";
String regex = "(([a-z]+)\\.([a-z]+))@(([a-z]+)\\.([a-z]+))";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(email);

if (matcher.find()) {
    System.out.println("Full email: " + matcher.group(0));
    System.out.println("Local part: " + matcher.group(1));
    System.out.println("  First name: " + matcher.group(2));
    System.out.println("  Last name: " + matcher.group(3));
    System.out.println("Domain: " + matcher.group(4));
    System.out.println("  Domain name: " + matcher.group(5));
    System.out.println("  TLD: " + matcher.group(6));
}
```

**Output:**
```
Full email: user.name@example.com
Local part: user.name
  First name: user
  Last name: name
Domain: example.com
  Domain name: example
  TLD: com
```

## Practice Examples

### Example 1: Parsing M-Koba Confirmation Messages

```java
String message = "CKR4LDS51MY Confirmed.You successfully paid TZS.1,000.0 for a social of KIZPART SACCOS group on 27/11/2025 at 07:30";

String regex = "(?<ref>\\w+)\\s+Confirmed\\..*?TZS\\.(?<amount>[0-9,]+\\.?[0-9]*).*?on\\s+(?<date>\\d{2}/\\d{2}/\\d{4})\\s+at\\s+(?<time>\\d{2}:\\d{2})";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(message);

if (matcher.find()) {
    System.out.println("Reference: " + matcher.group("ref"));
    System.out.println("Amount: TZS." + matcher.group("amount"));
    System.out.println("Date: " + matcher.group("date"));
    System.out.println("Time: " + matcher.group("time"));
}
```

**Output:**
```
Reference: CKR4LDS51MY
Amount: TZS.1,000.0
Date: 27/11/2025
Time: 07:30
```

### Example 2: Extracting URL Components

```java
String url = "https://www.example.com:8080/path/to/resource?query=value";
String regex = "(?<protocol>https?)://(?<domain>[^:/]+)(?::(?<port>\\d+))?(?<path>/[^?]*)?(?:\\?(?<query>.*))?";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(url);

if (matcher.find()) {
    System.out.println("Protocol: " + matcher.group("protocol"));
    System.out.println("Domain: " + matcher.group("domain"));
    System.out.println("Port: " + matcher.group("port"));
    System.out.println("Path: " + matcher.group("path"));
    System.out.println("Query: " + matcher.group("query"));
}
```

**Output:**
```
Protocol: https
Domain: www.example.com
Port: 8080
Path: /path/to/resource
Query: query=value
```

### Example 3: Reformatting Phone Numbers

```java
String text = "Call 255-755-959291 or 255-765-123456";
String regex = "(\\d{3})-(\\d{3})-(\\d{6})";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(text);

// Reformat to (XXX) XXX-XXXXXX
String result = matcher.replaceAll("($1) $2-$3");
System.out.println("Original: " + text);
System.out.println("Formatted: " + result);
```

**Output:**
```
Original: Call 255-755-959291 or 255-765-123456
Formatted: Call (255) 755-959291 or (255) 765-123456
```

### Example 4: Validating and Parsing Time Stamps

```java
String[] times = {"10:27", "23:59", "25:00", "12:75"};
String regex = "(?<hour>[01]?\\d|2[0-3]):(?<minute>[0-5]\\d)";

Pattern pattern = Pattern.compile(regex);

for (String time : times) {
    Matcher matcher = pattern.matcher(time);
    if (matcher.matches()) {
        System.out.println(time + " is valid - Hour: " + matcher.group("hour") +
                         ", Minute: " + matcher.group("minute"));
    } else {
        System.out.println(time + " is INVALID");
    }
}
```

**Output:**
```
10:27 is valid - Hour: 10, Minute: 27
23:59 is valid - Hour: 23, Minute: 59
25:00 is INVALID
12:75 is INVALID
```

### Example 5: Extracting Member Information from M-Koba

```java
String message = "255755959291(OBED SANGA) has purchased shares worth of TZS.20,000.00 from KIZPART SACCOS group";

String regex = "(?<phone>\\d+)\\((?<name>[^)]+)\\)\\s+has\\s+(?<action>\\w+\\s+\\w+).*?TZS\\.(?<amount>[0-9,]+\\.?[0-9]*).*?(?<group>[A-Z]+\\s+SACCOS\\s+group)";

Pattern pattern = Pattern.compile(regex);
Matcher matcher = pattern.matcher(message);

if (matcher.find()) {
    System.out.println("Member Details:");
    System.out.println("  Phone: " + matcher.group("phone"));
    System.out.println("  Name: " + matcher.group("name"));
    System.out.println("Transaction:");
    System.out.println("  Action: " + matcher.group("action"));
    System.out.println("  Amount: TZS." + matcher.group("amount"));
    System.out.println("  Group: " + matcher.group("group"));
}
```

**Output:**
```
Member Details:
  Phone: 255755959291
  Name: OBED SANGA
Transaction:
  Action: purchased shares
  Amount: TZS.20,000.00
  Group: KIZPART SACCOS group
```

## Summary

In this guide, you learned:

1. **Grouping** with `()` groups characters for quantifiers and creates subpatterns
2. **Capturing groups** save matched text for extraction using `group(n)` or `group("name")`
3. **Group numbering** starts at 1 (group 0 is always the full match)
4. **Non-capturing groups** `(?:...)` group without capturing (better performance)
5. **Named groups** `(?<name>...)` make code more readable
6. **Backreferences** `\\1`, `\\2` or `\\k<name>` match previously captured text
7. **Alternation** with `|` creates OR patterns within groups
8. **Nested groups** are numbered left-to-right by opening parenthesis

## Next Steps

In the next guide, we'll explore:
- The Pattern class in detail
- The Matcher class methods
- Flags and modifiers (CASE_INSENSITIVE, MULTILINE, etc.)
- Compiling and reusing patterns
- Performance considerations

Continue to: [04-pattern-and-matcher-classes.md](./04-pattern-and-matcher-classes.md)
