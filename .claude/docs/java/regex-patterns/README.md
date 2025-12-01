# Java Regex Patterns - Learning Guide

This directory contains comprehensive documentation for learning Java regular expressions (regex) through practical examples, with a focus on parsing M-Koba SACCOS transaction messages.

## 📚 Documentation Series

### Beginner Level

**[01 - Regex Fundamentals](./01-regex-fundamentals.md)**
- Introduction to regular expressions
- Basic pattern matching
- Literal characters and metacharacters
- The dot metacharacter
- Anchors (^, $, \b)
- Escape sequences (\d, \w, \s)

**[02 - Character Classes and Quantifiers](./02-character-classes-and-quantifiers.md)**
- Character classes [abc]
- Predefined character classes
- Negated character classes [^abc]
- Ranges [a-z]
- Quantifiers (*, +, ?, {n}, {n,m})
- Greedy vs lazy matching

**[03 - Groups and Capturing](./03-groups-and-capturing.md)**
- Grouping with parentheses ()
- Capturing groups and group numbering
- Non-capturing groups (?:...)
- Named groups (?<name>...)
- Backreferences (\1, \2, \k<name>)
- Alternation with groups (|)
- Nested groups

### Intermediate Level

**[04 - Pattern and Matcher Classes](./04-pattern-and-matcher-classes.md)**
- The Pattern class
- Pattern flags (CASE_INSENSITIVE, MULTILINE, DOTALL, etc.)
- The Matcher class
- Finding and matching methods
- Extracting data with groups
- Replacing text
- Region operations
- Performance considerations

**[05 - Text Extraction and Validation](./05-text-extraction-and-validation.md)**
- Common validation patterns (email, phone, date, currency)
- Data extraction strategies
- Building robust validators
- Parsing structured text (logs, CSV)
- Handling edge cases
- Creating utility classes

### Advanced Level

**[06 - Real-World Examples: M-Koba](./06-real-world-examples-mkoba.md)**
- Understanding M-Koba message types
- Parsing member transaction messages
- Parsing loan disbursement messages
- Parsing personal confirmation messages
- Building a unified message parser
- Handling message variations
- Batch processing

**[07 - Advanced Techniques](./07-advanced-techniques.md)**
- Lookahead assertions (?=..., ?!...)
- Lookbehind assertions (?<=..., ?<!...)
- Atomic groups (?>...)
- Conditional patterns
- Unicode and character properties (\p{L}, \p{N})
- Performance optimization
- Complex pattern composition

**[08 - Best Practices](./08-best-practices.md)**
- Code organization
- Documentation and comments
- Common pitfalls and solutions
- Testing strategies
- Maintainability guidelines
- Security considerations (ReDoS)
- When NOT to use regex
- Debugging techniques

### Specialized Topics

**[09 - Reading XML Files](./09-reading-xml-files.md)**
- Understanding XML structure (SMS Backup format)
- Reading XML with Java DOM
- Reading XML with Java SAX (event-driven)
- Reading XML with Java StAX (streaming)
- Reading XML with JAXB (object mapping)
- Reading XML in Spring Boot
- Combining XML parsing with regex
- Best practices for XML processing
- Converting XML to other formats

## 🎯 Learning Path

### For Complete Beginners
Start here and follow in order:
1. 01-regex-fundamentals.md
2. 02-character-classes-and-quantifiers.md
3. 03-groups-and-capturing.md
4. 04-pattern-and-matcher-classes.md

### For Practical Application
Focus on these for real-world usage:
1. 05-text-extraction-and-validation.md
2. 06-real-world-examples-mkoba.md
3. 08-best-practices.md

### For Advanced Users
Master these advanced concepts:
1. 07-advanced-techniques.md
2. 08-best-practices.md (security and performance sections)

### For XML Processing
Work with XML data files:
1. 09-reading-xml-files.md (XML parsing fundamentals)
2. Combine with 06-real-world-examples-mkoba.md (apply regex to extracted XML data)

## 🔍 Quick Reference

### Common Patterns

**Phone Numbers (Tanzania)**
```java
Pattern.compile("^255[67]\\d{8}$")  // Full format: 255712345678
Pattern.compile("^0[67]\\d{8}$")    // Short format: 0712345678
```

**Email**
```java
Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
```

**Date (DD/MM/YYYY)**
```java
Pattern.compile("^(0[1-9]|[12]\\d|3[01])/(0[1-9]|1[012])/(19|20)\\d{2}$")
```

**Currency (TZS)**
```java
Pattern.compile("TZS\\.([0-9,]+\\.?[0-9]*)")
```

## 📖 M-Koba Use Case

This documentation series uses M-Koba SACCOS transaction messages as a practical use case. Sample data is available in multiple formats:

### Text Format
- `src/main/resources/sample-data/m-koba-sample-texts.txt`

### XML Format (SMS Backup)
- `src/main/resources/sample-data/sms-20251128101700.xml` (M-Koba messages)
- `src/main/resources/sample-data/sms-m-pesa-20251128102028.xml` (M-Pesa messages)

### Message Types Covered
1. **Member Transactions**: Share purchases, loan repayments, social fund contributions
2. **Loan Disbursements**: Development loans, education loans, emergency loans
3. **Confirmation Messages**: Personal transaction confirmations

## 💡 Tips for Learning

1. **Read sequentially** - Each guide builds on previous concepts
2. **Run the examples** - All code examples are complete and runnable
3. **Experiment** - Modify patterns and test with different inputs
4. **Use online testers** - Tools like regex101.com help visualize patterns
5. **Start simple** - Begin with basic patterns and add complexity gradually
6. **Test thoroughly** - Always test edge cases and invalid inputs

## 🛠️ Practice Projects

After completing the documentation, try these projects:

1. **SMS XML Parser**: Read SMS backup XML files and extract transaction data
2. **M-Koba Transaction Analyzer**: Parse M-Koba messages and generate statistics
3. **Log Analyzer**: Extract information from application logs
4. **Data Validator**: Build validators for various data formats
5. **CSV Parser**: Parse CSV files with complex quoting rules
6. **XML to CSV Converter**: Convert SMS backup XML to CSV format
7. **Configuration Reader**: Parse custom configuration file formats

## 📝 Next Steps

1. Work through guides 01-09 in order
2. Practice with the M-Koba sample data (text and XML formats)
3. Create your own text parsing utility class
4. Combine XML parsing with regex for structured data extraction
5. Build a complete SMS message analyzer
6. Apply regex to real problems in your projects
7. Review best practices before production use

## 🎓 Learning Outcomes

After completing this series, you will be able to:

✅ Understand regex syntax and metacharacters
✅ Write patterns for common validation tasks
✅ Extract structured data from text
✅ Use Java's Pattern and Matcher classes effectively
✅ Read and parse XML files using multiple methods (DOM, SAX, StAX, JAXB)
✅ Process XML in Spring Boot applications
✅ Combine XML parsing with regex for advanced data extraction
✅ Debug and test regex patterns
✅ Apply best practices for maintainable code
✅ Avoid common pitfalls and security issues
✅ Optimize regex performance
✅ Handle Unicode and international text

## 📚 Additional Resources

- [Java Pattern Documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/regex/Pattern.html)
- [Regex101](https://regex101.com/) - Interactive regex tester
- [Regular-Expressions.info](https://www.regular-expressions.info/) - Comprehensive tutorial

---

**Note**: This is a learning-focused documentation series. No actual implementations are provided - only comprehensive guides with complete examples for learning purposes.

**Branch**: `feature/regex-patterns`
**Created**: November 2025
**Purpose**: Java regex pattern learning reference