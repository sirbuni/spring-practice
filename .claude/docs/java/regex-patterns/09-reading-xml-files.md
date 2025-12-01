# Reading XML Files in Java and Spring Boot

## Table of Contents
1. [Introduction](#introduction)
2. [Understanding the XML Structure](#understanding-the-xml-structure)
3. [Reading XML with Java DOM](#reading-xml-with-java-dom)
4. [Reading XML with Java SAX](#reading-xml-with-java-sax)
5. [Reading XML with Java StAX](#reading-xml-with-java-stax)
6. [Reading XML with JAXB](#reading-xml-with-jaxb)
7. [Reading XML in Spring Boot](#reading-xml-in-spring-boot)
8. [Combining XML Parsing with Regex](#combining-xml-parsing-with-regex)
9. [Best Practices](#best-practices)
10. [Practice Examples](#practice-examples)

## Introduction

XML (eXtensible Markup Language) is a common format for storing and transporting structured data. In this guide, we'll explore various methods to read and parse XML files in Java, with specific focus on the M-Koba and M-Pesa SMS backup files.

### When to Use Each Method

| Method | Best For | Memory Usage | Performance | Complexity |
|--------|----------|--------------|-------------|------------|
| **DOM** | Small files, random access | High (loads entire tree) | Slower | Simple |
| **SAX** | Large files, sequential | Low (event-based) | Fast | Moderate |
| **StAX** | Large files, pull parsing | Low (streaming) | Fast | Moderate |
| **JAXB** | Object mapping | Medium | Medium | Simple |
| **Spring** | Spring Boot apps | Configurable | Good | Very Simple |

## Understanding the XML Structure

### SMS Backup XML Structure

Both M-Koba and M-Pesa XML files follow the same structure from "SMS Backup & Restore Pro":

```xml
<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
<smses count="333" backup_set="..." backup_date="..." type="full">
  <sms
    protocol="0"
    address="M-Koba"
    date="1753982587317"
    type="1"
    body="255765616494(CHARLES KINABO) has purchased shares..."
    read="1"
    status="-1"
    locked="0"
    date_sent="1753982584000"
    readable_date="31 Jul 2025 20:23:07"
    contact_name="(Unknown)" />
  <!-- More <sms> elements -->
</smses>
```

### Key Attributes

- **Root Element (`<smses>`):**
  - `count`: Total number of SMS messages
  - `backup_set`: Unique identifier for the backup
  - `backup_date`: Timestamp of backup creation
  - `type`: Backup type (usually "full")

- **SMS Element (`<sms>`):**
  - `address`: Sender/recipient (e.g., "M-Koba", "MPESA")
  - `date`: Unix timestamp in milliseconds
  - `body`: The actual SMS message text
  - `readable_date`: Human-readable date
  - `type`: "1" for received, "2" for sent
  - `read`: "1" if read, "0" if unread

## Reading XML with Java DOM

DOM (Document Object Model) loads the entire XML into memory as a tree structure.

### Basic DOM Parser

```java
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DOMXmlReader {

    public static class SmsMessage {
        String address;
        String body;
        long date;
        String readableDate;
        String type;

        @Override
        public String toString() {
            return String.format("SMS{from='%s', date='%s', body='%s'}",
                address, readableDate,
                body.length() > 50 ? body.substring(0, 50) + "..." : body);
        }
    }

    public static List<SmsMessage> readSmsBackup(String xmlFilePath) throws Exception {
        List<SmsMessage> messages = new ArrayList<>();

        // Create DocumentBuilderFactory
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Optional: Enable features for security
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        // Create DocumentBuilder
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parse the XML file
        Document document = builder.parse(new File(xmlFilePath));

        // Normalize the document
        document.getDocumentElement().normalize();

        // Get root element
        Element root = document.getDocumentElement();
        System.out.println("Root element: " + root.getNodeName());
        System.out.println("Total SMS count: " + root.getAttribute("count"));

        // Get all <sms> elements
        NodeList smsNodes = document.getElementsByTagName("sms");

        for (int i = 0; i < smsNodes.getLength(); i++) {
            Node node = smsNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element smsElement = (Element) node;

                SmsMessage sms = new SmsMessage();
                sms.address = smsElement.getAttribute("address");
                sms.body = smsElement.getAttribute("body");
                sms.date = Long.parseLong(smsElement.getAttribute("date"));
                sms.readableDate = smsElement.getAttribute("readable_date");
                sms.type = smsElement.getAttribute("type");

                messages.add(sms);
            }
        }

        return messages;
    }

    public static void main(String[] args) {
        try {
            String filePath = "src/main/resources/sample-data/sms-20251128101700.xml";
            List<SmsMessage> messages = readSmsBackup(filePath);

            System.out.println("\nFirst 5 messages:");
            messages.stream()
                .limit(5)
                .forEach(System.out::println);

            System.out.println("\nTotal messages read: " + messages.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### Filtering Messages with DOM

```java
public class DOMXmlFilter {

    /**
     * Filter messages by sender address (e.g., "M-Koba", "MPESA")
     */
    public static List<SmsMessage> filterByAddress(
            String xmlFilePath,
            String address) throws Exception {

        List<SmsMessage> filtered = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(xmlFilePath));

        NodeList smsNodes = document.getElementsByTagName("sms");

        for (int i = 0; i < smsNodes.getLength(); i++) {
            Element smsElement = (Element) smsNodes.item(i);

            if (address.equals(smsElement.getAttribute("address"))) {
                SmsMessage sms = new SmsMessage();
                sms.address = smsElement.getAttribute("address");
                sms.body = smsElement.getAttribute("body");
                sms.readableDate = smsElement.getAttribute("readable_date");

                filtered.add(sms);
            }
        }

        return filtered;
    }

    /**
     * Filter messages by date range
     */
    public static List<SmsMessage> filterByDateRange(
            String xmlFilePath,
            long startDate,
            long endDate) throws Exception {

        List<SmsMessage> filtered = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(xmlFilePath));

        NodeList smsNodes = document.getElementsByTagName("sms");

        for (int i = 0; i < smsNodes.getLength(); i++) {
            Element smsElement = (Element) smsNodes.item(i);
            long date = Long.parseLong(smsElement.getAttribute("date"));

            if (date >= startDate && date <= endDate) {
                SmsMessage sms = new SmsMessage();
                sms.address = smsElement.getAttribute("address");
                sms.body = smsElement.getAttribute("body");
                sms.date = date;
                sms.readableDate = smsElement.getAttribute("readable_date");

                filtered.add(sms);
            }
        }

        return filtered;
    }

    public static void main(String[] args) {
        try {
            String filePath = "src/main/resources/sample-data/sms-20251128101700.xml";

            // Filter M-Koba messages only
            List<SmsMessage> mKobaMessages = filterByAddress(filePath, "M-Koba");
            System.out.println("M-Koba messages: " + mKobaMessages.size());

            // Show first 3
            mKobaMessages.stream()
                .limit(3)
                .forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Reading XML with Java SAX

SAX (Simple API for XML) is event-driven and memory-efficient for large files.

### SAX Parser Implementation

```java
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SAXXmlReader {

    public static class SmsHandler extends DefaultHandler {
        private List<SmsMessage> messages = new ArrayList<>();
        private int totalCount = 0;

        @Override
        public void startElement(String uri, String localName,
                               String qName, Attributes attributes) {

            if ("smses".equals(qName)) {
                // Read root element attributes
                String count = attributes.getValue("count");
                if (count != null) {
                    totalCount = Integer.parseInt(count);
                }
                System.out.println("Expected total messages: " + totalCount);
            }
            else if ("sms".equals(qName)) {
                // Create SMS message from attributes
                SmsMessage sms = new SmsMessage();
                sms.address = attributes.getValue("address");
                sms.body = attributes.getValue("body");

                String dateStr = attributes.getValue("date");
                if (dateStr != null && !dateStr.isEmpty()) {
                    sms.date = Long.parseLong(dateStr);
                }

                sms.readableDate = attributes.getValue("readable_date");
                sms.type = attributes.getValue("type");

                messages.add(sms);
            }
        }

        public List<SmsMessage> getMessages() {
            return messages;
        }

        public int getTotalCount() {
            return totalCount;
        }
    }

    public static List<SmsMessage> readSmsBackup(String xmlFilePath) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        SmsHandler handler = new SmsHandler();
        saxParser.parse(new File(xmlFilePath), handler);

        return handler.getMessages();
    }

    public static void main(String[] args) {
        try {
            String filePath = "src/main/resources/sample-data/sms-20251128101700.xml";

            long startTime = System.currentTimeMillis();
            List<SmsMessage> messages = readSmsBackup(filePath);
            long endTime = System.currentTimeMillis();

            System.out.println("\nMessages read: " + messages.size());
            System.out.println("Time taken: " + (endTime - startTime) + "ms");

            System.out.println("\nFirst 3 messages:");
            messages.stream()
                .limit(3)
                .forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### SAX with Filtering

```java
public class SAXXmlFilter {

    /**
     * Handler that filters messages by address while parsing
     */
    public static class FilteredSmsHandler extends DefaultHandler {
        private List<SmsMessage> messages = new ArrayList<>();
        private String filterAddress;

        public FilteredSmsHandler(String filterAddress) {
            this.filterAddress = filterAddress;
        }

        @Override
        public void startElement(String uri, String localName,
                               String qName, Attributes attributes) {

            if ("sms".equals(qName)) {
                String address = attributes.getValue("address");

                // Only process if address matches filter
                if (filterAddress == null || filterAddress.equals(address)) {
                    SmsMessage sms = new SmsMessage();
                    sms.address = address;
                    sms.body = attributes.getValue("body");
                    sms.readableDate = attributes.getValue("readable_date");

                    messages.add(sms);
                }
            }
        }

        public List<SmsMessage> getMessages() {
            return messages;
        }
    }

    public static List<SmsMessage> filterByAddress(
            String xmlFilePath,
            String address) throws Exception {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        FilteredSmsHandler handler = new FilteredSmsHandler(address);
        saxParser.parse(new File(xmlFilePath), handler);

        return handler.getMessages();
    }

    public static void main(String[] args) {
        try {
            String filePath = "src/main/resources/sample-data/sms-20251128101700.xml";

            // Filter only M-Koba messages
            List<SmsMessage> mKobaMessages = filterByAddress(filePath, "M-Koba");

            System.out.println("M-Koba messages: " + mKobaMessages.size());
            mKobaMessages.stream()
                .limit(3)
                .forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Reading XML with Java StAX

StAX (Streaming API for XML) provides pull-parsing, giving you more control than SAX.

### StAX Parser Implementation

```java
import javax.xml.stream.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StAXXmlReader {

    public static List<SmsMessage> readSmsBackup(String xmlFilePath) throws Exception {
        List<SmsMessage> messages = new ArrayList<>();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(
            new FileInputStream(xmlFilePath)
        );

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                String elementName = reader.getLocalName();

                if ("smses".equals(elementName)) {
                    int count = reader.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        if ("count".equals(reader.getAttributeLocalName(i))) {
                            System.out.println("Total messages: " +
                                reader.getAttributeValue(i));
                        }
                    }
                }
                else if ("sms".equals(elementName)) {
                    SmsMessage sms = new SmsMessage();

                    // Read all attributes
                    int attrCount = reader.getAttributeCount();
                    for (int i = 0; i < attrCount; i++) {
                        String attrName = reader.getAttributeLocalName(i);
                        String attrValue = reader.getAttributeValue(i);

                        switch (attrName) {
                            case "address" -> sms.address = attrValue;
                            case "body" -> sms.body = attrValue;
                            case "date" -> {
                                if (attrValue != null && !attrValue.isEmpty()) {
                                    sms.date = Long.parseLong(attrValue);
                                }
                            }
                            case "readable_date" -> sms.readableDate = attrValue;
                            case "type" -> sms.type = attrValue;
                        }
                    }

                    messages.add(sms);
                }
            }
        }

        reader.close();
        return messages;
    }

    public static void main(String[] args) {
        try {
            String filePath = "src/main/resources/sample-data/sms-20251128101700.xml";
            List<SmsMessage> messages = readSmsBackup(filePath);

            System.out.println("Messages read: " + messages.size());

            System.out.println("\nFirst 3 messages:");
            messages.stream()
                .limit(3)
                .forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Reading XML with JAXB

JAXB (Java Architecture for XML Binding) maps XML to Java objects automatically.

### Define JAXB Models

```java
import jakarta.xml.bind.annotation.*;
import java.util.List;

/**
 * Root element representing the SMS backup file
 */
@XmlRootElement(name = "smses")
@XmlAccessorType(XmlAccessType.FIELD)
public class SmsBackup {

    @XmlAttribute
    private int count;

    @XmlAttribute(name = "backup_set")
    private String backupSet;

    @XmlAttribute(name = "backup_date")
    private long backupDate;

    @XmlAttribute
    private String type;

    @XmlElement(name = "sms")
    private List<SmsMessage> messages;

    // Getters and setters
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public String getBackupSet() { return backupSet; }
    public void setBackupSet(String backupSet) { this.backupSet = backupSet; }

    public long getBackupDate() { return backupDate; }
    public void setBackupDate(long backupDate) { this.backupDate = backupDate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<SmsMessage> getMessages() { return messages; }
    public void setMessages(List<SmsMessage> messages) { this.messages = messages; }
}

/**
 * Individual SMS message
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SmsMessage {

    @XmlAttribute
    private int protocol;

    @XmlAttribute
    private String address;

    @XmlAttribute
    private long date;

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String body;

    @XmlAttribute
    private int read;

    @XmlAttribute
    private int status;

    @XmlAttribute
    private int locked;

    @XmlAttribute(name = "date_sent")
    private long dateSent;

    @XmlAttribute(name = "readable_date")
    private String readableDate;

    @XmlAttribute(name = "contact_name")
    private String contactName;

    // Getters and setters
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }

    public String getReadableDate() { return readableDate; }
    public void setReadableDate(String readableDate) {
        this.readableDate = readableDate;
    }

    @Override
    public String toString() {
        return String.format("SMS{from='%s', date='%s', body='%s'}",
            address, readableDate,
            body != null && body.length() > 50 ?
                body.substring(0, 50) + "..." : body);
    }
}
```

### JAXB Parser Implementation

```java
import jakarta.xml.bind.*;
import java.io.File;

public class JAXBXmlReader {

    public static SmsBackup readSmsBackup(String xmlFilePath) throws Exception {
        JAXBContext context = JAXBContext.newInstance(SmsBackup.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        File xmlFile = new File(xmlFilePath);
        SmsBackup backup = (SmsBackup) unmarshaller.unmarshal(xmlFile);

        return backup;
    }

    public static void main(String[] args) {
        try {
            String filePath = "src/main/resources/sample-data/sms-20251128101700.xml";
            SmsBackup backup = readSmsBackup(filePath);

            System.out.println("Backup Information:");
            System.out.println("  Total count: " + backup.getCount());
            System.out.println("  Backup type: " + backup.getType());
            System.out.println("  Messages loaded: " + backup.getMessages().size());

            System.out.println("\nFirst 3 messages:");
            backup.getMessages().stream()
                .limit(3)
                .forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Reading XML in Spring Boot

Spring Boot provides convenient ways to read XML files using Resource abstraction and auto-configuration.

### Add Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>

    <!-- JAXB for XML binding (if using JAXB) -->
    <dependency>
        <groupId>jakarta.xml.bind</groupId>
        <artifactId>jakarta.xml.bind-api</artifactId>
    </dependency>
    <dependency>
        <groupId>org.glassfish.jaxb</groupId>
        <artifactId>jaxb-runtime</artifactId>
    </dependency>
</dependencies>
```

### Spring Boot Service for XML Reading

```java
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class XmlReaderService {

    private final ResourceLoader resourceLoader;

    public XmlReaderService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Read XML file from classpath using Spring's ResourceLoader
     */
    public List<SmsMessage> readSmsBackup(String resourcePath) throws Exception {
        // Load resource from classpath
        Resource resource = resourceLoader.getResource("classpath:" + resourcePath);

        List<SmsMessage> messages = new ArrayList<>();

        try (InputStream inputStream = resource.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Security features
            factory.setFeature(
                "http://apache.org/xml/features/disallow-doctype-decl",
                true
            );

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            NodeList smsNodes = document.getElementsByTagName("sms");

            for (int i = 0; i < smsNodes.getLength(); i++) {
                Element smsElement = (Element) smsNodes.item(i);

                SmsMessage sms = new SmsMessage();
                sms.setAddress(smsElement.getAttribute("address"));
                sms.setBody(smsElement.getAttribute("body"));
                sms.setReadableDate(smsElement.getAttribute("readable_date"));

                String dateStr = smsElement.getAttribute("date");
                if (dateStr != null && !dateStr.isEmpty()) {
                    sms.setDate(Long.parseLong(dateStr));
                }

                messages.add(sms);
            }
        }

        return messages;
    }

    /**
     * Filter messages by address
     */
    public List<SmsMessage> filterByAddress(
            String resourcePath,
            String address) throws Exception {

        List<SmsMessage> allMessages = readSmsBackup(resourcePath);

        return allMessages.stream()
            .filter(sms -> address.equals(sms.getAddress()))
            .toList();
    }
}
```

### Spring Boot Command Line Runner

```java
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class XmlReaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(XmlReaderApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(XmlReaderService xmlReaderService) {
        return args -> {
            System.out.println("Reading M-Koba SMS messages...");

            // Read M-Koba messages
            List<SmsMessage> mKobaMessages = xmlReaderService.filterByAddress(
                "sample-data/sms-20251128101700.xml",
                "M-Koba"
            );

            System.out.println("M-Koba messages found: " + mKobaMessages.size());

            System.out.println("\nFirst 3 messages:");
            mKobaMessages.stream()
                .limit(3)
                .forEach(System.out::println);

            // Read M-Pesa messages
            List<SmsMessage> mPesaMessages = xmlReaderService.filterByAddress(
                "sample-data/sms-m-pesa-20251128102028.xml",
                "MPESA"
            );

            System.out.println("\nM-Pesa messages found: " + mPesaMessages.size());
        };
    }
}
```

### Spring Boot REST Controller

```java
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    private final XmlReaderService xmlReaderService;

    public SmsController(XmlReaderService xmlReaderService) {
        this.xmlReaderService = xmlReaderService;
    }

    @GetMapping("/mkoba")
    public List<SmsMessage> getMKobaMessages() throws Exception {
        return xmlReaderService.filterByAddress(
            "sample-data/sms-20251128101700.xml",
            "M-Koba"
        );
    }

    @GetMapping("/mpesa")
    public List<SmsMessage> getMPesaMessages() throws Exception {
        return xmlReaderService.filterByAddress(
            "sample-data/sms-m-pesa-20251128102028.xml",
            "MPESA"
        );
    }

    @GetMapping("/mkoba/search")
    public List<SmsMessage> searchMKoba(@RequestParam String keyword) throws Exception {
        List<SmsMessage> messages = xmlReaderService.filterByAddress(
            "sample-data/sms-20251128101700.xml",
            "M-Koba"
        );

        return messages.stream()
            .filter(sms -> sms.getBody().contains(keyword))
            .toList();
    }
}
```

## Combining XML Parsing with Regex

Once you've read the XML, you can apply regex patterns to extract structured data from message bodies.

### Combined Parser Example

```java
import java.util.*;
import java.util.regex.*;

public class XmlRegexCombined {

    /**
     * Read XML and extract transaction details using regex
     */
    public static class TransactionExtractor {

        // Regex pattern for M-Koba transactions
        private static final Pattern TRANSACTION_PATTERN = Pattern.compile(
            "(?<phone>\\d+)\\((?<name>[^)]+)\\)\\s+has\\s+" +
            "(?<action>purchased|paid).*?" +
            "TZS\\.(?<amount>[0-9,]+\\.?[0-9]*).*?" +
            "on\\s+(?<date>\\d{2}/\\d{2}/\\d{4})"
        );

        public static class Transaction {
            String phone;
            String name;
            String action;
            String amount;
            String date;
            String originalMessage;

            @Override
            public String toString() {
                return String.format(
                    "Transaction{phone='%s', name='%s', action='%s', " +
                    "amount='TZS.%s', date='%s'}",
                    phone, name, action, amount, date
                );
            }
        }

        public static List<Transaction> extractTransactions(
                String xmlFilePath) throws Exception {

            List<Transaction> transactions = new ArrayList<>();

            // Read XML file using any method (DOM, SAX, StAX, JAXB)
            List<SmsMessage> messages = DOMXmlReader.readSmsBackup(xmlFilePath);

            // Filter M-Koba messages
            List<SmsMessage> mKobaMessages = messages.stream()
                .filter(sms -> "M-Koba".equals(sms.address))
                .toList();

            // Apply regex to extract transaction details
            for (SmsMessage sms : mKobaMessages) {
                Matcher matcher = TRANSACTION_PATTERN.matcher(sms.body);

                if (matcher.find()) {
                    Transaction transaction = new Transaction();
                    transaction.phone = matcher.group("phone");
                    transaction.name = matcher.group("name");
                    transaction.action = matcher.group("action");
                    transaction.amount = matcher.group("amount");
                    transaction.date = matcher.group("date");
                    transaction.originalMessage = sms.body;

                    transactions.add(transaction);
                }
            }

            return transactions;
        }
    }

    public static void main(String[] args) {
        try {
            String filePath = "src/main/resources/sample-data/sms-20251128101700.xml";

            List<TransactionExtractor.Transaction> transactions =
                TransactionExtractor.extractTransactions(filePath);

            System.out.println("Transactions extracted: " + transactions.size());

            System.out.println("\nFirst 5 transactions:");
            transactions.stream()
                .limit(5)
                .forEach(System.out::println);

            // Group by action
            Map<String, Long> byAction = transactions.stream()
                .collect(Collectors.groupingBy(
                    t -> t.action,
                    Collectors.counting()
                ));

            System.out.println("\nTransactions by action:");
            byAction.forEach((action, count) ->
                System.out.println("  " + action + ": " + count));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Best Practices

### 1. Security Considerations

```java
// Disable external entity processing to prevent XXE attacks
DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
```

### 2. Resource Management

```java
// Always use try-with-resources for automatic cleanup
try (InputStream inputStream = new FileInputStream(xmlFile)) {
    // Parse XML
} catch (Exception e) {
    // Handle exception
}
```

### 3. Error Handling

```java
public List<SmsMessage> readSmsBackup(String xmlFilePath) {
    try {
        // XML parsing code
        return messages;
    } catch (ParserConfigurationException e) {
        throw new RuntimeException("XML parser configuration error", e);
    } catch (SAXException e) {
        throw new RuntimeException("XML parsing error", e);
    } catch (IOException e) {
        throw new RuntimeException("File reading error", e);
    }
}
```

### 4. Performance Tips

```java
// For large files (> 100MB), use SAX or StAX
// For small files (< 10MB), DOM or JAXB is fine
// For medium files, consider StAX for good balance

// Cache compiled patterns
private static final Pattern PATTERN = Pattern.compile(...);

// Use parallel streams for processing after reading
messages.parallelStream()
    .filter(...)
    .map(...)
    .collect(Collectors.toList());
```

## Practice Examples

### Example 1: Statistics Generator

```java
public class SmsStatistics {

    public static void generateStatistics(String xmlFilePath) throws Exception {
        List<SmsMessage> messages = DOMXmlReader.readSmsBackup(xmlFilePath);

        System.out.println("=== SMS Statistics ===");
        System.out.println("Total messages: " + messages.size());

        // Group by sender
        Map<String, Long> bySender = messages.stream()
            .collect(Collectors.groupingBy(
                SmsMessage::getAddress,
                Collectors.counting()
            ));

        System.out.println("\nMessages by sender:");
        bySender.forEach((sender, count) ->
            System.out.println("  " + sender + ": " + count));

        // Average message length
        double avgLength = messages.stream()
            .mapToInt(sms -> sms.getBody() != null ? sms.getBody().length() : 0)
            .average()
            .orElse(0.0);

        System.out.println("\nAverage message length: " + avgLength + " characters");
    }

    public static void main(String[] args) {
        try {
            generateStatistics("src/main/resources/sample-data/sms-20251128101700.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### Example 2: Export to CSV

```java
import java.io.*;

public class XmlToCsvConverter {

    public static void convertToCSV(
            String xmlFilePath,
            String csvFilePath) throws Exception {

        List<SmsMessage> messages = DOMXmlReader.readSmsBackup(xmlFilePath);

        try (PrintWriter writer = new PrintWriter(new FileWriter(csvFilePath))) {
            // Write header
            writer.println("Address,Date,Readable Date,Message");

            // Write data
            for (SmsMessage sms : messages) {
                writer.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n",
                    sms.getAddress(),
                    sms.getDate(),
                    sms.getReadableDate(),
                    sms.getBody() != null ?
                        sms.getBody().replace("\"", "\"\"") : "");
            }
        }

        System.out.println("CSV file created: " + csvFilePath);
    }

    public static void main(String[] args) {
        try {
            convertToCSV(
                "src/main/resources/sample-data/sms-20251128101700.xml",
                "output/mkoba-messages.csv"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## Summary

In this guide, you learned:

1. **DOM Parser**: Best for small files, loads entire XML into memory
2. **SAX Parser**: Event-driven, memory-efficient for large files
3. **StAX Parser**: Pull-parsing, good balance of control and efficiency
4. **JAXB**: Automatic object mapping, clean and type-safe
5. **Spring Boot**: Convenient Resource loading and dependency injection
6. **Combined Approach**: XML parsing + Regex for structured data extraction
7. **Best Practices**: Security, resource management, error handling

### When to Use Each

- **DOM**: Small files, need random access, simple queries
- **SAX**: Very large files, sequential processing, memory constrained
- **StAX**: Large files, need control over parsing, want pull model
- **JAXB**: Type-safe object mapping, clean code, moderate size files
- **Spring Boot**: Already using Spring, want dependency injection, REST APIs

Continue to: [README.md](./README.md) for the complete documentation index
