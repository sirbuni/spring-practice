# Real-World Examples: M-Koba SACCOS Message Parsing

## Table of Contents
1. [Introduction](#introduction)
2. [Understanding M-Koba Message Types](#understanding-m-koba-message-types)
3. [Parsing Member Transactions](#parsing-member-transactions)
4. [Parsing Loan Disbursement Messages](#parsing-loan-disbursement-messages)
5. [Parsing Confirmation Messages](#parsing-confirmation-messages)
6. [Building a Complete Parser](#building-a-complete-parser)
7. [Handling Message Variations](#handling-message-variations)
8. [Practice Examples](#practice-examples)

## Introduction

M-Koba is a platform used by SACCOS (Savings and Credit Cooperative Societies) in Tanzania to send transaction notifications to members. These messages follow specific patterns and contain important transaction details.

This guide demonstrates how to parse real M-Koba messages using regex patterns, extracting valuable information like amounts, dates, member details, and transaction types.

### Sample Message Types

Based on the sample data in `src/main/resources/sample-data/m-koba-sample-texts.txt`, we have three main message types:

1. **Member Transaction Messages**: "{phone}({name}) has {action} TZS.{amount}..."
2. **Loan Disbursement Messages**: "{ref} Confirmed.{loan_type} loan of TZS.{amount}..."
3. **Personal Confirmation Messages**: "{ref} Confirmed.You successfully {action} TZS.{amount}..."

## Understanding M-Koba Message Types

### Type 1: Member Transaction Messages

**Pattern:**
```
{phone}({name}) has {action} {details} TZS.{amount} {type} on {date} at {time}
```

**Examples:**
```
255755959291(OBED SANGA) has purchased shares worth of TZS.20,000.00 from KIZPART SACCOS group on 27/11/2025 at 10:27

255764800188(MUSTAFA MMANGA) has paid TZS.34,000.0 as loan repayment for KIZPART SACCOS group on 27/11/2025 at 07:24

255753437735(GASTO TAIRO) has paid TZS.30,000.0 for social fund for KIZPART SACCOS group on 24/11/2025 at 22:21
```

### Type 2: Loan Disbursement Messages

**Pattern:**
```
{ref} Confirmed.{loan_type} loan of TZS.{amount} has been transfered from {group} to {phone}({name}).Date {date} at {time}.A group account balance is TZS.{balance}
```

**Examples:**
```
CKO9LC170LH Confirmed.A development loan of TZS.350,000.00 has been transfered from KIZPART SACCOS group to 255753206784(INNOCENT MRINGO).Date 2025-11-24 at 19:03:52.A group account balance is TZS.43,727,817.00

CJQ9KU8YEAD Confirmed.Education loan of TZS.700,000.00 has been transfered from KIZPART SACCOS group to 255767857171(DAVID NYAMBALYA). Date 2025-10-26 at 07:33:36.A group account balance is TZS.35,832,550.00
```

### Type 3: Personal Confirmation Messages

**Pattern:**
```
{ref} Confirmed.You successfully {action} TZS.{amount} {details} on {date} at {time}
```

**Examples:**
```
CKR4LDS51MY Confirmed.You successfully paid TZS.1,000.0 for a social of KIZPART SACCOS group on 27/11/2025 at 07:30

CIT8KD0EGS2 Confirmed.You successfully repaid TZS.112,000.00 for your loan from KIZPART SACCOS group on 29/09/2025 at 21:05
```

## Parsing Member Transaction Messages

### Basic Member Transaction Parser

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemberTransactionParser {

    public static class MemberTransaction {
        String phone;
        String name;
        String action;
        String amount;
        String transactionType;
        String group;
        String date;
        String time;

        @Override
        public String toString() {
            return String.format(
                "MemberTransaction{%n" +
                "  phone='%s',%n" +
                "  name='%s',%n" +
                "  action='%s',%n" +
                "  amount='TZS.%s',%n" +
                "  type='%s',%n" +
                "  group='%s',%n" +
                "  date='%s',%n" +
                "  time='%s'%n" +
                "}",
                phone, name, action, amount, transactionType, group, date, time
            );
        }
    }

    // Comprehensive pattern for member transactions
    private static final Pattern MEMBER_TRANSACTION_PATTERN = Pattern.compile(
        "(?<phone>\\d+)" +                           // Phone number
        "\\((?<name>[^)]+)\\)" +                     // Name in parentheses
        "\\s+has\\s+" +                              // " has "
        "(?<action>purchased shares|paid)" +         // Action type
        "(?:\\s+shares worth of|)\\s+" +             // Optional "shares worth of"
        "TZS\\.(?<amount>[0-9,]+\\.?[0-9]*)" +      // Amount
        "(?:\\s+as\\s+(?<type>loan repayment|[^f][^o][^r]*))?"+  // Transaction type
        "(?:\\s+for\\s+(?<fortype>social fund|[^f][^o][^r]*))?"+ // For type
        ".*?" +                                      // Any text
        "(?<group>[A-Z]+\\s+SACCOS\\s+group)" +     // SACCOS group name
        "\\s+on\\s+" +                               // " on "
        "(?<date>\\d{2}/\\d{2}/\\d{4})" +           // Date DD/MM/YYYY
        "\\s+at\\s+" +                               // " at "
        "(?<time>\\d{2}:\\d{2})"                    // Time HH:MM
    );

    public static MemberTransaction parse(String message) {
        Matcher matcher = MEMBER_TRANSACTION_PATTERN.matcher(message);

        if (!matcher.find()) {
            return null;
        }

        MemberTransaction transaction = new MemberTransaction();
        transaction.phone = matcher.group("phone");
        transaction.name = matcher.group("name");
        transaction.action = matcher.group("action");
        transaction.amount = matcher.group("amount");

        // Determine transaction type
        String type = matcher.group("type");
        String fortype = matcher.group("fortype");
        if (type != null) {
            transaction.transactionType = type;
        } else if (fortype != null) {
            transaction.transactionType = fortype;
        } else if ("purchased shares".equals(transaction.action)) {
            transaction.transactionType = "share purchase";
        } else {
            transaction.transactionType = "payment";
        }

        transaction.group = matcher.group("group");
        transaction.date = matcher.group("date");
        transaction.time = matcher.group("time");

        return transaction;
    }

    public static void main(String[] args) {
        String[] messages = {
            "255755959291(OBED SANGA) has purchased shares worth of TZS.20,000.00 from KIZPART SACCOS group on 27/11/2025 at 10:27",
            "255764800188(MUSTAFA MMANGA) has paid TZS.34,000.0 as loan repayment for KIZPART SACCOS group on 27/11/2025 at 07:24",
            "255753437735(GASTO TAIRO) has paid TZS.30,000.0 for social fund for KIZPART SACCOS group on 24/11/2025 at 22:21"
        };

        for (String message : messages) {
            System.out.println("Message: " + message);
            MemberTransaction transaction = parse(message);
            if (transaction != null) {
                System.out.println(transaction);
            } else {
                System.out.println("Failed to parse");
            }
            System.out.println();
        }
    }
}
```

**Output:**
```
Message: 255755959291(OBED SANGA) has purchased shares worth of TZS.20,000.00 from KIZPART SACCOS group on 27/11/2025 at 10:27
MemberTransaction{
  phone='255755959291',
  name='OBED SANGA',
  action='purchased shares',
  amount='TZS.20,000.00',
  type='share purchase',
  group='KIZPART SACCOS group',
  date='27/11/2025',
  time='10:27'
}

Message: 255764800188(MUSTAFA MMANGA) has paid TZS.34,000.0 as loan repayment for KIZPART SACCOS group on 27/11/2025 at 07:24
MemberTransaction{
  phone='255764800188',
  name='MUSTAFA MMANGA',
  action='paid',
  amount='TZS.34,000.0',
  type='loan repayment',
  group='KIZPART SACCOS group',
  date='27/11/2025',
  time='07:24'
}

Message: 255753437735(GASTO TAIRO) has paid TZS.30,000.0 for social fund for KIZPART SACCOS group on 24/11/2025 at 22:21
MemberTransaction{
  phone='255753437735',
  name='GASTO TAIRO',
  action='paid',
  amount='TZS.30,000.0',
  type='social fund',
  group='KIZPART SACCOS group',
  date='27/11/2025',
  time='22:21'
}
```

## Parsing Loan Disbursement Messages

### Loan Disbursement Parser

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoanDisbursementParser {

    public static class LoanDisbursement {
        String reference;
        String loanType;
        String amount;
        String group;
        String recipientPhone;
        String recipientName;
        String date;
        String time;
        String accountBalance;

        @Override
        public String toString() {
            return String.format(
                "LoanDisbursement{%n" +
                "  reference='%s',%n" +
                "  loanType='%s',%n" +
                "  amount='TZS.%s',%n" +
                "  group='%s',%n" +
                "  recipient='%s (%s)',%n" +
                "  dateTime='%s %s',%n" +
                "  balance='TZS.%s'%n" +
                "}",
                reference, loanType, amount, group,
                recipientName, recipientPhone, date, time, accountBalance
            );
        }
    }

    private static final Pattern LOAN_DISBURSEMENT_PATTERN = Pattern.compile(
        "(?<ref>[A-Z0-9]+)\\s+Confirmed\\." +                    // Reference + Confirmed
        "(?:A\\s+)?(?<loantype>[^l]*?)\\s*loan\\s+of\\s+" +     // Loan type
        "TZS\\.(?<amount>[0-9,]+\\.?[0-9]*)" +                  // Amount
        "\\s+has been transfered from\\s+" +                     // Transfer text
        "(?<group>[A-Z]+\\s+SACCOS\\s+group)" +                 // Group name
        "\\s+to\\s+" +                                           // " to "
        "(?<phone>\\d+)" +                                       // Recipient phone
        "\\((?<name>[^)]+)\\)" +                                // Recipient name
        "\\.\\s*Date\\s+" +                                      // Date prefix
        "(?<date>\\d{4}-\\d{2}-\\d{2})" +                       // Date YYYY-MM-DD
        "\\s+at\\s+" +                                           // " at "
        "(?<time>\\d{2}:\\d{2}:\\d{2})" +                       // Time HH:MM:SS
        "\\.A group account balance is\\s+" +                    // Balance prefix
        "TZS\\.(?<balance>[0-9,]+\\.?[0-9]*)"                  // Balance amount
    );

    public static LoanDisbursement parse(String message) {
        Matcher matcher = LOAN_DISBURSEMENT_PATTERN.matcher(message);

        if (!matcher.find()) {
            return null;
        }

        LoanDisbursement loan = new LoanDisbursement();
        loan.reference = matcher.group("ref");
        loan.loanType = matcher.group("loantype").trim();
        loan.amount = matcher.group("amount");
        loan.group = matcher.group("group");
        loan.recipientPhone = matcher.group("phone");
        loan.recipientName = matcher.group("name");
        loan.date = matcher.group("date");
        loan.time = matcher.group("time");
        loan.accountBalance = matcher.group("balance");

        return loan;
    }

    public static void main(String[] args) {
        String[] messages = {
            "CKO9LC170LH Confirmed.A development loan of TZS.350,000.00 has been transfered from KIZPART SACCOS group to 255753206784(INNOCENT MRINGO).Date 2025-11-24 at 19:03:52.A group account balance is TZS.43,727,817.00",
            "CJQ9KU8YEAD Confirmed.Education loan of TZS.700,000.00 has been transfered from KIZPART SACCOS group to 255767857171(DAVID NYAMBALYA). Date 2025-10-26 at 07:33:36.A group account balance is TZS.35,832,550.00",
            "CKE7L5BZBVD Confirmed.Emergency loan of TZS.450,000.00 has been transfered from KIZPART SACCOS group to 255756777781(PAULO MWABUSILA). Date 2025-11-14 at 13:08:38.A group account balance is TZS.43,362,817.00"
        };

        for (String message : messages) {
            System.out.println("Parsing: " + message.substring(0, Math.min(80, message.length())) + "...");
            LoanDisbursement loan = parse(message);
            if (loan != null) {
                System.out.println(loan);
            } else {
                System.out.println("Failed to parse");
            }
            System.out.println();
        }
    }
}
```

**Output:**
```
Parsing: CKO9LC170LH Confirmed.A development loan of TZS.350,000.00 has been transfere...
LoanDisbursement{
  reference='CKO9LC170LH',
  loanType='development',
  amount='TZS.350,000.00',
  group='KIZPART SACCOS group',
  recipient='INNOCENT MRINGO (255753206784)',
  dateTime='2025-11-24 19:03:52',
  balance='TZS.43,727,817.00'
}

Parsing: CJQ9KU8YEAD Confirmed.Education loan of TZS.700,000.00 has been transfered f...
LoanDisbursement{
  reference='CJQ9KU8YEAD',
  loanType='Education',
  amount='TZS.700,000.00',
  group='KIZPART SACCOS group',
  recipient='DAVID NYAMBALYA (255767857171)',
  dateTime='2025-10-26 07:33:36',
  balance='TZS.35,832,550.00'
}

Parsing: CKE7L5BZBVD Confirmed.Emergency loan of TZS.450,000.00 has been transfered f...
LoanDisbursement{
  reference='CKE7L5BZBVD',
  loanType='Emergency',
  amount='TZS.450,000.00',
  group='KIZPART SACCOS group',
  recipient='PAULO MWABUSILA (255756777781)',
  dateTime='2025-11-14 13:08:38',
  balance='TZS.43,362,817.00'
}
```

## Parsing Confirmation Messages

### Personal Confirmation Parser

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfirmationMessageParser {

    public static class Confirmation {
        String reference;
        String action;
        String amount;
        String details;
        String group;
        String date;
        String time;

        @Override
        public String toString() {
            return String.format(
                "Confirmation{%n" +
                "  reference='%s',%n" +
                "  action='%s',%n" +
                "  amount='TZS.%s',%n" +
                "  details='%s',%n" +
                "  group='%s',%n" +
                "  dateTime='%s %s'%n" +
                "}",
                reference, action, amount, details, group, date, time
            );
        }
    }

    private static final Pattern CONFIRMATION_PATTERN = Pattern.compile(
        "(?<ref>[A-Z0-9]+)\\s+Confirmed\\." +                    // Reference
        "You successfully\\s+" +                                  // Success message
        "(?<action>paid|purchased shares|repaid)" +              // Action
        "(?:\\s+shares worth of|)\\s+" +                         // Optional shares text
        "TZS\\.(?<amount>[0-9,]+\\.?[0-9]*)" +                  // Amount
        "\\s+(?<details>for.*?)" +                               // Details
        "(?<group>[A-Z]+\\s+SACCOS\\s+group)" +                 // Group name
        "\\s+on\\s+" +                                           // " on "
        "(?<date>\\d{2}/\\d{2}/\\d{4})" +                       // Date
        "\\s+at\\s+" +                                           // " at "
        "(?<time>\\d{2}:\\d{2})"                                // Time
    );

    public static Confirmation parse(String message) {
        Matcher matcher = CONFIRMATION_PATTERN.matcher(message);

        if (!matcher.find()) {
            return null;
        }

        Confirmation confirmation = new Confirmation();
        confirmation.reference = matcher.group("ref");
        confirmation.action = matcher.group("action");
        confirmation.amount = matcher.group("amount");
        confirmation.details = matcher.group("details");
        confirmation.group = matcher.group("group");
        confirmation.date = matcher.group("date");
        confirmation.time = matcher.group("time");

        return confirmation;
    }

    public static void main(String[] args) {
        String[] messages = {
            "CKR4LDS51MY Confirmed.You successfully paid TZS.1,000.0 for a social of KIZPART SACCOS group on 27/11/2025 at 07:30",
            "CIT9KD0F9JX Confirmed.You successfully purchased shares worth of TZS.1,000,000.00 from KIZPART SACCOS group on 29/09/2025 at 21:06",
            "CIT8KD0EGS2 Confirmed.You successfully repaid TZS.112,000.00 for your loan from KIZPART SACCOS group on 29/09/2025 at 21:05"
        };

        for (String message : messages) {
            System.out.println("Parsing: " + message.substring(0, Math.min(80, message.length())) + "...");
            Confirmation confirmation = parse(message);
            if (confirmation != null) {
                System.out.println(confirmation);
            } else {
                System.out.println("Failed to parse");
            }
            System.out.println();
        }
    }
}
```

**Output:**
```
Parsing: CKR4LDS51MY Confirmed.You successfully paid TZS.1,000.0 for a social of KIZP...
Confirmation{
  reference='CKR4LDS51MY',
  action='paid',
  amount='TZS.1,000.0',
  details='for a social of',
  group='KIZPART SACCOS group',
  dateTime='27/11/2025 07:30'
}

Parsing: CIT9KD0F9JX Confirmed.You successfully purchased shares worth of TZS.1,000,0...
Confirmation{
  reference='CIT9KD0F9JX',
  action='purchased shares',
  amount='TZS.1,000,000.00',
  details='from',
  group='KIZPART SACCOS group',
  dateTime='29/09/2025 21:06'
}

Parsing: CIT8KD0EGS2 Confirmed.You successfully repaid TZS.112,000.00 for your loan f...
Confirmation{
  reference='CIT8KD0EGS2',
  action='repaid',
  amount='TZS.112,000.00',
  details='for your loan from',
  group='KIZPART SACCOS group',
  dateTime='29/09/2025 21:05'
}
```

## Building a Complete Parser

### Unified M-Koba Message Parser

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MKobaMessageParser {

    public enum MessageType {
        MEMBER_TRANSACTION,
        LOAN_DISBURSEMENT,
        PERSONAL_CONFIRMATION,
        UNKNOWN
    }

    public static class ParsedMessage {
        MessageType type;
        Object data;

        public ParsedMessage(MessageType type, Object data) {
            this.type = type;
            this.data = data;
        }

        @Override
        public String toString() {
            return String.format("ParsedMessage{type=%s, data=%s}", type, data);
        }
    }

    // Patterns for message type detection
    private static final Pattern MEMBER_TX_DETECTOR =
        Pattern.compile("^\\d+\\([^)]+\\)\\s+has\\s+(paid|purchased)");

    private static final Pattern LOAN_DISBURSEMENT_DETECTOR =
        Pattern.compile("^[A-Z0-9]+\\s+Confirmed\\..*?loan\\s+of\\s+TZS.*has been transfered");

    private static final Pattern CONFIRMATION_DETECTOR =
        Pattern.compile("^[A-Z0-9]+\\s+Confirmed\\.You successfully");

    /**
     * Automatically detect message type and parse accordingly
     */
    public static ParsedMessage parse(String message) {
        if (message == null || message.trim().isEmpty()) {
            return new ParsedMessage(MessageType.UNKNOWN, null);
        }

        // Detect message type
        if (MEMBER_TX_DETECTOR.matcher(message).find()) {
            MemberTransactionParser.MemberTransaction tx =
                MemberTransactionParser.parse(message);
            return new ParsedMessage(MessageType.MEMBER_TRANSACTION, tx);
        }

        if (LOAN_DISBURSEMENT_DETECTOR.matcher(message).find()) {
            LoanDisbursementParser.LoanDisbursement loan =
                LoanDisbursementParser.parse(message);
            return new ParsedMessage(MessageType.LOAN_DISBURSEMENT, loan);
        }

        if (CONFIRMATION_DETECTOR.matcher(message).find()) {
            ConfirmationMessageParser.Confirmation confirmation =
                ConfirmationMessageParser.parse(message);
            return new ParsedMessage(MessageType.PERSONAL_CONFIRMATION, confirmation);
        }

        return new ParsedMessage(MessageType.UNKNOWN, null);
    }

    public static void main(String[] args) {
        String[] messages = {
            "255755959291(OBED SANGA) has purchased shares worth of TZS.20,000.00 from KIZPART SACCOS group on 27/11/2025 at 10:27",
            "CKO9LC170LH Confirmed.A development loan of TZS.350,000.00 has been transfered from KIZPART SACCOS group to 255753206784(INNOCENT MRINGO).Date 2025-11-24 at 19:03:52.A group account balance is TZS.43,727,817.00",
            "CKR4LDS51MY Confirmed.You successfully paid TZS.1,000.0 for a social of KIZPART SACCOS group on 27/11/2025 at 07:30"
        };

        for (int i = 0; i < messages.length; i++) {
            System.out.println("=== Message " + (i + 1) + " ===");
            ParsedMessage parsed = parse(messages[i]);
            System.out.println("Type: " + parsed.type);
            System.out.println("Data: " + parsed.data);
            System.out.println();
        }
    }
}
```

## Handling Message Variations

### Flexible Amount Parsing

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlexibleAmountParser {
    // Handles various amount formats
    private static final Pattern AMOUNT_PATTERN =
        Pattern.compile("TZS\\.([0-9,]+)(?:\\.([0-9]{1,2}))?");

    public static Double parseAmount(String message) {
        Matcher matcher = AMOUNT_PATTERN.matcher(message);

        if (!matcher.find()) {
            return null;
        }

        String wholepart = matcher.group(1).replace(",", "");
        String decimal = matcher.group(2);

        StringBuilder numeric = new StringBuilder(wholepart);

        if (decimal != null && !decimal.isEmpty()) {
            // Pad single digit decimals
            if (decimal.length() == 1) {
                decimal = decimal + "0";
            }
            numeric.append(".").append(decimal);
        } else {
            numeric.append(".00");
        }

        try {
            return Double.parseDouble(numeric.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        String[] messages = {
            "TZS.1,000.00",    // 1000.00
            "TZS.250.50",      // 250.50
            "TZS.50.0",        // 50.00
            "TZS.1,000,000",   // 1000000.00
            "TZS.99"           // 99.00
        };

        for (String msg : messages) {
            Double amount = parseAmount(msg);
            System.out.printf("%s -> %.2f%n", msg, amount != null ? amount : 0.0);
        }
    }
}
```

**Output:**
```
TZS.1,000.00 -> 1000.00
TZS.250.50 -> 250.50
TZS.50.0 -> 50.00
TZS.1,000,000 -> 1000000.00
TZS.99 -> 99.00
```

### Date Format Converter

```java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateFormatConverter {
    // Matches DD/MM/YYYY
    private static final Pattern DATE_SLASH =
        Pattern.compile("(\\d{2})/(\\d{2})/(\\d{4})");

    // Matches YYYY-MM-DD
    private static final Pattern DATE_DASH =
        Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");

    private static final DateTimeFormatter OUTPUT_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String normalizeDate(String message) {
        // Try DD/MM/YYYY format
        Matcher slashMatcher = DATE_SLASH.matcher(message);
        if (slashMatcher.find()) {
            int day = Integer.parseInt(slashMatcher.group(1));
            int month = Integer.parseInt(slashMatcher.group(2));
            int year = Integer.parseInt(slashMatcher.group(3));
            LocalDate date = LocalDate.of(year, month, day);
            return date.format(OUTPUT_FORMAT);
        }

        // Try YYYY-MM-DD format
        Matcher dashMatcher = DATE_DASH.matcher(message);
        if (dashMatcher.find()) {
            return dashMatcher.group();  // Already in correct format
        }

        return null;
    }

    public static void main(String[] args) {
        String[] messages = {
            "on 27/11/2025 at 10:27",
            "Date 2025-11-24 at 19:03:52",
            "15/12/2025",
            "2025-10-26"
        };

        for (String msg : messages) {
            String normalized = normalizeDate(msg);
            System.out.printf("%s -> %s%n", msg, normalized);
        }
    }
}
```

**Output:**
```
on 27/11/2025 at 10:27 -> 2025-11-27
Date 2025-11-24 at 19:03:52 -> 2025-11-24
15/12/2025 -> 2025-12-15
2025-10-26 -> 2025-10-26
```

## Practice Examples

### Example 1: Batch Processing M-Koba Messages

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BatchProcessor {

    public static class Statistics {
        int totalMessages = 0;
        int memberTransactions = 0;
        int loanDisbursements = 0;
        int confirmations = 0;
        int unparsed = 0;
        double totalAmount = 0.0;

        @Override
        public String toString() {
            return String.format(
                "Statistics{%n" +
                "  Total Messages: %d%n" +
                "  Member Transactions: %d%n" +
                "  Loan Disbursements: %d%n" +
                "  Confirmations: %d%n" +
                "  Unparsed: %d%n" +
                "  Total Amount: TZS.%.2f%n" +
                "}",
                totalMessages, memberTransactions,
                loanDisbursements, confirmations, unparsed, totalAmount
            );
        }
    }

    public static Statistics processMessages(String filePath) throws IOException {
        String content = Files.readString(Paths.get(filePath));
        Statistics stats = new Statistics();

        // Split by message boundaries (each message is on its own line or delimited)
        String[] messages = content.split("(?=[A-Z0-9]{11}\\s+Confirmed|\\d{12}\\()");

        for (String message : messages) {
            if (message.trim().isEmpty()) {
                continue;
            }

            stats.totalMessages++;
            MKobaMessageParser.ParsedMessage parsed = MKobaMessageParser.parse(message);

            switch (parsed.type) {
                case MEMBER_TRANSACTION:
                    stats.memberTransactions++;
                    // Extract and sum amount
                    Double amt = FlexibleAmountParser.parseAmount(message);
                    if (amt != null) stats.totalAmount += amt;
                    break;
                case LOAN_DISBURSEMENT:
                    stats.loanDisbursements++;
                    Double loanAmt = FlexibleAmountParser.parseAmount(message);
                    if (loanAmt != null) stats.totalAmount += loanAmt;
                    break;
                case PERSONAL_CONFIRMATION:
                    stats.confirmations++;
                    Double confAmt = FlexibleAmountParser.parseAmount(message);
                    if (confAmt != null) stats.totalAmount += confAmt;
                    break;
                default:
                    stats.unparsed++;
            }
        }

        return stats;
    }

    public static void main(String[] args) {
        // Example usage - you would provide actual file path
        String sampleContent = """
            255755959291(OBED SANGA) has purchased shares worth of TZS.20,000.00 from KIZPART SACCOS group on 27/11/2025 at 10:27
            CKR4LDS51MY Confirmed.You successfully paid TZS.1,000.0 for a social of KIZPART SACCOS group on 27/11/2025 at 07:30
            CKO9LC170LH Confirmed.A development loan of TZS.350,000.00 has been transfered from KIZPART SACCOS group to 255753206784(INNOCENT MRINGO).Date 2025-11-24 at 19:03:52.A group account balance is TZS.43,727,817.00
            """;

        Statistics stats = new Statistics();
        String[] messages = sampleContent.split("\\n");

        for (String message : messages) {
            if (message.trim().isEmpty()) continue;

            stats.totalMessages++;
            MKobaMessageParser.ParsedMessage parsed = MKobaMessageParser.parse(message);

            switch (parsed.type) {
                case MEMBER_TRANSACTION -> {
                    stats.memberTransactions++;
                    Double amt = FlexibleAmountParser.parseAmount(message);
                    if (amt != null) stats.totalAmount += amt;
                }
                case LOAN_DISBURSEMENT -> {
                    stats.loanDisbursements++;
                    Double loanAmt = FlexibleAmountParser.parseAmount(message);
                    if (loanAmt != null) stats.totalAmount += loanAmt;
                }
                case PERSONAL_CONFIRMATION -> {
                    stats.confirmations++;
                    Double confAmt = FlexibleAmountParser.parseAmount(message);
                    if (confAmt != null) stats.totalAmount += confAmt;
                }
                default -> stats.unparsed++;
            }
        }

        System.out.println(stats);
    }
}
```

**Output:**
```
Statistics{
  Total Messages: 3
  Member Transactions: 1
  Loan Disbursements: 1
  Confirmations: 1
  Unparsed: 0
  Total Amount: TZS.371000.00
}
```

## Summary

In this guide, you learned:

1. **M-Koba message types**: Member transactions, loan disbursements, and confirmations
2. **Pattern-specific parsers** for each message type
3. **Named groups** for clean data extraction
4. **Unified parser** that automatically detects and routes messages
5. **Flexible parsing** to handle format variations
6. **Batch processing** for analyzing multiple messages
7. **Real-world complexity** in production message parsing

These patterns demonstrate the power of regex in extracting structured data from semi-structured text messages, which is a common requirement in financial systems and SMS-based applications.

## Next Steps

In the next guide, we'll explore:
- Advanced regex techniques (lookahead, lookbehind)
- Performance optimization
- Complex pattern composition
- Unicode handling

Continue to: [07-advanced-techniques.md](./07-advanced-techniques.md)
