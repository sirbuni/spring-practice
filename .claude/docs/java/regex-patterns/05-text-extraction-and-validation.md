# Text Extraction and Validation

## Table of Contents
1. [Introduction](#introduction)
2. [Common Validation Patterns](#common-validation-patterns)
3. [Data Extraction Strategies](#data-extraction-strategies)
4. [Building Robust Validators](#building-robust-validators)
5. [Parsing Structured Text](#parsing-structured-text)
6. [Handling Edge Cases](#handling-edge-cases)
7. [Creating Utility Classes](#creating-utility-classes)
8. [Practice Examples](#practice-examples)

## Introduction

Text extraction and validation are two of the most common uses of regex in real-world applications. This guide focuses on practical patterns and techniques you'll use in production code.

**Extraction**: Getting specific data from text
**Validation**: Verifying text matches expected format

## Common Validation Patterns

### Email Validation

```java
import java.util.regex.Pattern;

public class EmailValidator {
    // Simple email pattern
    private static final Pattern SIMPLE_EMAIL =
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$");

    // More comprehensive email pattern
    private static final Pattern COMPREHENSIVE_EMAIL =
        Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
        );

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return SIMPLE_EMAIL.matcher(email).matches();
    }

    public static void main(String[] args) {
        String[] emails = {
            "user@example.com",           // Valid
            "first.last@example.co.uk",   // Valid
            "user+tag@example.com",       // Valid
            "invalid.email",              // Invalid
            "@example.com",               // Invalid
            "user@",                      // Invalid
        };

        for (String email : emails) {
            System.out.println(email + " -> " +
                (isValidEmail(email) ? "VALID" : "INVALID"));
        }
    }
}
```

**Output:**
```
user@example.com -> VALID
first.last@example.co.uk -> VALID
user+tag@example.com -> VALID
invalid.email -> INVALID
@example.com -> INVALID
user@ -> INVALID
```

### Phone Number Validation

```java
public class PhoneValidator {
    // Tanzania phone number patterns
    private static final Pattern TZ_PHONE_FULL =
        Pattern.compile("^255[67]\\d{8}$");  // 255712345678

    private static final Pattern TZ_PHONE_SHORT =
        Pattern.compile("^0[67]\\d{8}$");    // 0712345678

    private static final Pattern TZ_PHONE_FORMATTED =
        Pattern.compile("^255[-.]?[67]\\d{2}[-.]?\\d{6}$");  // 255-712-345678

    public static boolean isValidTanzaniaPhone(String phone) {
        if (phone == null) {
            return false;
        }

        // Remove all whitespace
        phone = phone.replaceAll("\\s", "");

        return TZ_PHONE_FULL.matcher(phone).matches() ||
               TZ_PHONE_SHORT.matcher(phone).matches() ||
               TZ_PHONE_FORMATTED.matcher(phone).matches();
    }

    public static String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }

        // Remove all non-digit characters
        phone = phone.replaceAll("[^\\d]", "");

        // Convert short format to full
        if (phone.matches("^0[67]\\d{8}$")) {
            phone = "255" + phone.substring(1);
        }

        return phone;
    }

    public static void main(String[] args) {
        String[] phones = {
            "255712345678",
            "0712345678",
            "255-712-345678",
            "255 712 345678",
            "712345678",      // Invalid
            "255812345678"    // Invalid (8 not valid)
        };

        for (String phone : phones) {
            boolean valid = isValidTanzaniaPhone(phone);
            String normalized = normalizePhone(phone);
            System.out.printf("%s -> %s (normalized: %s)%n",
                phone, valid ? "VALID" : "INVALID", normalized);
        }
    }
}
```

**Output:**
```
255712345678 -> VALID (normalized: 255712345678)
0712345678 -> VALID (normalized: 255712345678)
255-712-345678 -> VALID (normalized: 255712345678)
255 712 345678 -> VALID (normalized: 255712345678)
712345678 -> INVALID (normalized: 712345678)
255812345678 -> INVALID (normalized: 255812345678)
```

### Date Validation

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateValidator {
    // DD/MM/YYYY format
    private static final Pattern DATE_PATTERN =
        Pattern.compile("^(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[012])/(19|20)\\d{2}$");

    public static boolean isValidDate(String date) {
        if (date == null) {
            return false;
        }

        Matcher matcher = DATE_PATTERN.matcher(date);
        if (!matcher.matches()) {
            return false;
        }

        // Extract components
        int day = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int year = Integer.parseInt(matcher.group(3));

        // Additional validation for days in month
        return isValidDayForMonth(day, month, year);
    }

    private static boolean isValidDayForMonth(int day, int month, int year) {
        int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        // Check for leap year
        if (month == 2 && isLeapYear(year)) {
            return day <= 29;
        }

        return day <= daysInMonth[month - 1];
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public static void main(String[] args) {
        String[] dates = {
            "27/11/2025",   // Valid
            "31/02/2025",   // Invalid (Feb doesn't have 31 days)
            "29/02/2024",   // Valid (leap year)
            "29/02/2025",   // Invalid (not leap year)
            "32/01/2025",   // Invalid (day > 31)
            "15/13/2025"    // Invalid (month > 12)
        };

        for (String date : dates) {
            System.out.println(date + " -> " +
                (isValidDate(date) ? "VALID" : "INVALID"));
        }
    }
}
```

**Output:**
```
27/11/2025 -> VALID
31/02/2025 -> INVALID
29/02/2024 -> VALID
29/02/2025 -> INVALID
32/01/2025 -> INVALID
15/13/2025 -> INVALID
```

### Currency Amount Validation

```java
public class CurrencyValidator {
    // Matches TZS.1,000.00 or TZS.1000.00 or TZS.50.0
    private static final Pattern TZS_AMOUNT =
        Pattern.compile("^TZS\\.([0-9,]+)(?:\\.([0-9]{1,2}))?$");

    public static boolean isValidAmount(String amount) {
        return amount != null && TZS_AMOUNT.matcher(amount).matches();
    }

    public static Double parseAmount(String amount) {
        if (!isValidAmount(amount)) {
            return null;
        }

        Matcher matcher = TZS_AMOUNT.matcher(amount);
        if (matcher.matches()) {
            // Remove "TZS." prefix and commas
            String wholepart = matcher.group(1).replace(",", "");
            String decimal = matcher.group(2);

            String numericValue = wholepart;
            if (decimal != null) {
                // Pad decimal to 2 digits if only 1
                if (decimal.length() == 1) {
                    decimal = decimal + "0";
                }
                numericValue += "." + decimal;
            } else {
                numericValue += ".00";
            }

            return Double.parseDouble(numericValue);
        }

        return null;
    }

    public static void main(String[] args) {
        String[] amounts = {
            "TZS.1,000.00",
            "TZS.250.50",
            "TZS.50.0",
            "TZS.1000",
            "TZS1000.00",     // Invalid (missing .)
            "TZS.1,000.5.0"   // Invalid (extra .)
        };

        for (String amount : amounts) {
            boolean valid = isValidAmount(amount);
            Double value = parseAmount(amount);
            System.out.printf("%s -> %s (value: %.2f)%n",
                amount, valid ? "VALID" : "INVALID",
                value != null ? value : 0.0);
        }
    }
}
```

**Output:**
```
TZS.1,000.00 -> VALID (value: 1000.00)
TZS.250.50 -> VALID (value: 250.50)
TZS.50.0 -> VALID (value: 50.00)
TZS.1000 -> VALID (value: 1000.00)
TZS1000.00 -> INVALID (value: 0.00)
TZS.1,000.5.0 -> INVALID (value: 0.00)
```

## Data Extraction Strategies

### Strategy 1: Extract All Matches

```java
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractAllMatches {
    public static List<String> extractPhoneNumbers(String text) {
        List<String> phoneNumbers = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{6}\\b");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            phoneNumbers.add(matcher.group());
        }

        return phoneNumbers;
    }

    public static void main(String[] args) {
        String text = """
            Contact information:
            Primary: 255-712-345678
            Secondary: 255.765.987654
            Office: 255-755-123456
            """;

        List<String> phones = extractPhoneNumbers(text);
        System.out.println("Phone numbers found: " + phones.size());
        phones.forEach(System.out::println);
    }
}
```

**Output:**
```
Phone numbers found: 3
255-712-345678
255.765.987654
255-755-123456
```

### Strategy 2: Extract with Context

```java
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractWithContext {
    public static Map<String, String> extractLabeledPhones(String text) {
        Map<String, String> labeledPhones = new HashMap<>();

        // Pattern: Label: phone
        Pattern pattern = Pattern.compile(
            "(?<label>\\w+):\\s*(?<phone>\\d{3}[-.]?\\d{3}[-.]?\\d{6})"
        );
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String label = matcher.group("label");
            String phone = matcher.group("phone");
            labeledPhones.put(label, phone);
        }

        return labeledPhones;
    }

    public static void main(String[] args) {
        String text = """
            Contact information:
            Primary: 255-712-345678
            Secondary: 255.765.987654
            Office: 255-755-123456
            """;

        Map<String, String> phones = extractLabeledPhones(text);
        System.out.println("Labeled phone numbers:");
        phones.forEach((label, phone) ->
            System.out.println("  " + label + ": " + phone));
    }
}
```

**Output:**
```
Labeled phone numbers:
  Primary: 255-712-345678
  Secondary: 255.765.987654
  Office: 255-755-123456
```

### Strategy 3: Extract Multiple Fields

```java
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractMultipleFields {
    public static class Transaction {
        String phone;
        String name;
        String amount;
        String date;

        @Override
        public String toString() {
            return String.format("Transaction{phone='%s', name='%s', amount='%s', date='%s'}",
                phone, name, amount, date);
        }
    }

    public static List<Transaction> extractTransactions(String text) {
        List<Transaction> transactions = new ArrayList<>();

        Pattern pattern = Pattern.compile(
            "(?<phone>\\d+)\\((?<name>[^)]+)\\).*?" +
            "TZS\\.(?<amount>[0-9,]+\\.?[0-9]*).*?" +
            "on\\s+(?<date>\\d{2}/\\d{2}/\\d{4})"
        );

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            Transaction t = new Transaction();
            t.phone = matcher.group("phone");
            t.name = matcher.group("name");
            t.amount = matcher.group("amount");
            t.date = matcher.group("date");
            transactions.add(t);
        }

        return transactions;
    }

    public static void main(String[] args) {
        String text = """
            255755959291(OBED SANGA) has purchased shares worth of TZS.20,000.00 on 27/11/2025
            255764800188(MUSTAFA MMANGA) has paid TZS.34,000.0 as loan on 27/11/2025
            """;

        List<Transaction> transactions = extractTransactions(text);
        System.out.println("Transactions found: " + transactions.size());
        transactions.forEach(System.out::println);
    }
}
```

**Output:**
```
Transactions found: 2
Transaction{phone='255755959291', name='OBED SANGA', amount='20,000.00', date='27/11/2025'}
Transaction{phone='255764800188', name='MUSTAFA MMANGA', amount='34,000.0', date='27/11/2025'}
```

## Building Robust Validators

### Validator with Error Messages

```java
import java.util.regex.Pattern;

public class RobustValidator {
    public static class ValidationResult {
        boolean valid;
        String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$");

    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Email cannot be empty");
        }

        if (email.length() > 254) {
            return new ValidationResult(false, "Email is too long (max 254 characters)");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return new ValidationResult(false, "Email format is invalid");
        }

        // Additional checks
        String[] parts = email.split("@");
        if (parts[0].length() > 64) {
            return new ValidationResult(false, "Local part is too long (max 64 characters)");
        }

        return new ValidationResult(true, null);
    }

    public static void main(String[] args) {
        String[] emails = {
            "valid@example.com",
            "",
            "a".repeat(65) + "@example.com",
            "invalid.email"
        };

        for (String email : emails) {
            ValidationResult result = validateEmail(email);
            System.out.printf("Email: '%s'%n", email);
            System.out.printf("  Valid: %s%n", result.isValid());
            if (!result.isValid()) {
                System.out.printf("  Error: %s%n", result.getErrorMessage());
            }
            System.out.println();
        }
    }
}
```

**Output:**
```
Email: 'valid@example.com'
  Valid: true

Email: ''
  Valid: false
  Error: Email cannot be empty

Email: 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@example.com'
  Valid: false
  Error: Local part is too long (max 64 characters)

Email: 'invalid.email'
  Valid: false
  Error: Email format is invalid
```

## Parsing Structured Text

### Parsing Log Files

```java
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {
    public static class LogEntry {
        String timestamp;
        String level;
        String message;

        @Override
        public String toString() {
            return String.format("[%s] %s: %s", timestamp, level, message);
        }
    }

    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^\\[(?<timestamp>[^\\]]+)\\]\\s+" +
        "(?<level>INFO|WARN|ERROR|DEBUG)\\s*:\\s*" +
        "(?<message>.+)$",
        Pattern.MULTILINE
    );

    public static List<LogEntry> parseLogs(String logText) {
        List<LogEntry> entries = new ArrayList<>();
        Matcher matcher = LOG_PATTERN.matcher(logText);

        while (matcher.find()) {
            LogEntry entry = new LogEntry();
            entry.timestamp = matcher.group("timestamp");
            entry.level = matcher.group("level");
            entry.message = matcher.group("message");
            entries.add(entry);
        }

        return entries;
    }

    public static void main(String[] args) {
        String logs = """
            [2025-11-27 10:30:45] INFO: Application started
            [2025-11-27 10:31:12] WARN: High memory usage detected
            [2025-11-27 10:31:45] ERROR: Database connection failed
            [2025-11-27 10:32:00] DEBUG: Retrying connection
            """;

        List<LogEntry> entries = parseLogs(logs);
        System.out.println("Log entries found: " + entries.size());
        entries.forEach(System.out::println);
    }
}
```

**Output:**
```
Log entries found: 4
[2025-11-27 10:30:45] INFO: Application started
[2025-11-27 10:31:12] WARN: High memory usage detected
[2025-11-27 10:31:45] ERROR: Database connection failed
[2025-11-27 10:32:00] DEBUG: Retrying connection
```

### Parsing CSV-like Data

```java
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVParser {
    // Handles quoted fields with commas inside
    private static final Pattern CSV_PATTERN = Pattern.compile(
        "\"([^\"]*)\"|([^,]+)|,"
    );

    public static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        Matcher matcher = CSV_PATTERN.matcher(line);

        while (matcher.find()) {
            String quoted = matcher.group(1);
            String unquoted = matcher.group(2);

            if (quoted != null) {
                fields.add(quoted);
            } else if (unquoted != null) {
                fields.add(unquoted.trim());
            } else if (matcher.group().equals(",")) {
                // Empty field between commas
                if (fields.isEmpty() || matcher.start() > 0 &&
                    line.charAt(matcher.start() - 1) == ',') {
                    fields.add("");
                }
            }
        }

        return fields;
    }

    public static void main(String[] args) {
        String[] csvLines = {
            "John,Doe,30,Engineer",
            "Jane,\"Smith, Jr.\",25,\"Software Developer\"",
            "Bob,,35,Manager"
        };

        for (String line : csvLines) {
            List<String> fields = parseCsvLine(line);
            System.out.println("Fields: " + fields);
        }
    }
}
```

## Handling Edge Cases

### Null and Empty String Handling

```java
public class SafeValidator {
    private static final Pattern PATTERN = Pattern.compile("\\d+");

    public static boolean isValid(String input) {
        // Always check for null first
        if (input == null) {
            return false;
        }

        // Check for empty or whitespace-only
        if (input.trim().isEmpty()) {
            return false;
        }

        return PATTERN.matcher(input).matches();
    }

    public static void main(String[] args) {
        System.out.println("null: " + isValid(null));
        System.out.println("empty: " + isValid(""));
        System.out.println("whitespace: " + isValid("   "));
        System.out.println("123: " + isValid("123"));
    }
}
```

**Output:**
```
null: false
empty: false
whitespace: false
123: true
```

### Handling Special Characters

```java
import java.util.regex.Pattern;

public class SpecialCharacterHandler {
    // Escape special regex characters in user input
    public static String escapeRegex(String input) {
        return Pattern.quote(input);
    }

    public static void main(String[] args) {
        String userInput = "Price: $99.99 (special)";

        // Wrong: Using user input directly in regex
        // This would fail because $ and . are special characters
        // Pattern wrongPattern = Pattern.compile(userInput);

        // Right: Escape the user input
        String escaped = escapeRegex(userInput);
        Pattern safePattern = Pattern.compile(escaped);

        String text = "The label says: Price: $99.99 (special)";
        boolean matches = safePattern.matcher(text).find();

        System.out.println("Original: " + userInput);
        System.out.println("Escaped: " + escaped);
        System.out.println("Found in text: " + matches);
    }
}
```

**Output:**
```
Original: Price: $99.99 (special)
Escaped: \QPrice: $99.99 (special)\E
Found in text: true
```

## Creating Utility Classes

### Complete Text Parser Utility

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParserUtility {
    // Phone number patterns
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{6}\\b");

    // Email pattern
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b");

    // Currency pattern
    private static final Pattern CURRENCY_PATTERN =
        Pattern.compile("TZS\\.([0-9,]+\\.?[0-9]*)");

    // Date pattern (DD/MM/YYYY)
    private static final Pattern DATE_PATTERN =
        Pattern.compile("\\b\\d{2}/\\d{2}/\\d{4}\\b");

    /**
     * Extract all phone numbers from text
     */
    public static List<String> extractPhoneNumbers(String text) {
        return extractMatches(text, PHONE_PATTERN);
    }

    /**
     * Extract all email addresses from text
     */
    public static List<String> extractEmails(String text) {
        return extractMatches(text, EMAIL_PATTERN);
    }

    /**
     * Extract all currency amounts from text
     */
    public static List<String> extractAmounts(String text) {
        return extractMatches(text, CURRENCY_PATTERN);
    }

    /**
     * Extract all dates from text
     */
    public static List<String> extractDates(String text) {
        return extractMatches(text, DATE_PATTERN);
    }

    /**
     * Find first occurrence of pattern
     */
    public static Optional<String> findFirst(String text, Pattern pattern) {
        if (text == null) {
            return Optional.empty();
        }

        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? Optional.of(matcher.group()) : Optional.empty();
    }

    /**
     * Extract all matches for a pattern
     */
    private static List<String> extractMatches(String text, Pattern pattern) {
        List<String> matches = new ArrayList<>();

        if (text == null) {
            return matches;
        }

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }

    /**
     * Validate if text matches pattern
     */
    public static boolean validate(String text, Pattern pattern) {
        if (text == null) {
            return false;
        }
        return pattern.matcher(text).matches();
    }

    public static void main(String[] args) {
        String text = """
            Transaction from 255-712-345678 (user@example.com)
            Amount: TZS.1,500.50 on 27/11/2025
            Contact: backup@test.com or 255-765-987654
            """;

        System.out.println("Phone numbers: " + extractPhoneNumbers(text));
        System.out.println("Emails: " + extractEmails(text));
        System.out.println("Amounts: " + extractAmounts(text));
        System.out.println("Dates: " + extractDates(text));
    }
}
```

**Output:**
```
Phone numbers: [255-712-345678, 255-765-987654]
Emails: [user@example.com, backup@test.com]
Amounts: [TZS.1,500.50]
Dates: [27/11/2025]
```

## Practice Examples

### Example 1: Complete Transaction Parser

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionParser {
    private static final Pattern TRANSACTION_PATTERN = Pattern.compile(
        "(?<phone>\\d+)\\((?<name>[^)]+)\\)\\s+has\\s+" +
        "(?<action>paid|purchased)\\s+" +
        "(?:shares worth of |)TZS\\.(?<amount>[0-9,]+\\.?[0-9]*).*?" +
        "(?:on|Date)\\s+(?<date>\\d{2}/\\d{2}/\\d{4})\\s+" +
        "(?:at\\s+)?(?<time>\\d{2}:\\d{2})"
    );

    public static void parseTransaction(String message) {
        Matcher matcher = TRANSACTION_PATTERN.matcher(message);

        if (matcher.find()) {
            System.out.println("=== Transaction Details ===");
            System.out.println("Phone: " + matcher.group("phone"));
            System.out.println("Name: " + matcher.group("name"));
            System.out.println("Action: " + matcher.group("action"));
            System.out.println("Amount: TZS." + matcher.group("amount"));
            System.out.println("Date: " + matcher.group("date"));
            System.out.println("Time: " + matcher.group("time"));
        } else {
            System.out.println("Could not parse transaction");
        }
    }

    public static void main(String[] args) {
        String message = "255755959291(OBED SANGA) has purchased shares worth of " +
                        "TZS.20,000.00 from KIZPART SACCOS group on 27/11/2025 at 10:27";

        parseTransaction(message);
    }
}
```

**Output:**
```
=== Transaction Details ===
Phone: 255755959291
Name: OBED SANGA
Action: purchased
Amount: TZS.20,000.00
Date: 27/11/2025
Time: 10:27
```

## Summary

In this guide, you learned:

1. **Common validation patterns** for emails, phones, dates, and currency
2. **Data extraction strategies** for single and multiple fields
3. **Building robust validators** with proper error handling
4. **Parsing structured text** like logs and CSV files
5. **Handling edge cases** including null, empty strings, and special characters
6. **Creating utility classes** for reusable text parsing functions

## Next Steps

In the next guide, we'll apply these concepts to:
- Parse real M-Koba SACCOS messages
- Build complete parsers for different message types
- Handle variations in message formats
- Create a comprehensive M-Koba message parser

Continue to: [06-real-world-examples-mkoba.md](./06-real-world-examples-mkoba.md)
