# Kafka Integration for Library Management System

This document describes the Kafka integration that has been added to the Library Management System.

## Overview

The system now includes event-driven architecture using Apache Kafka for:
- **Book Events**: CREATE, UPDATE, DELETE, BORROW, RETURN operations
- **Author Events**: CREATE, UPDATE, DELETE operations  
- **Audit Events**: All operations for compliance and monitoring

## Architecture

### Event Flow
1. **Service Layer** → Publishes events to Kafka topics
2. **Kafka Topics** → Store events for processing
3. **Event Consumers** → Process events for various business logic

### Topics
- `book-events-topic`: Book-related operations
- `author-events-topic`: Author-related operations
- `audit-events-topic`: Audit trail for all operations

## Setup

### 1. Start Kafka Infrastructure
```bash
docker-compose up -d zookeeper kafka
```

### 2. Verify Kafka is Running
```bash
docker-compose ps
```

### 3. Start the Application
```bash
mvn spring-boot:run
```

## Event Types

### Book Events
- **CREATE**: New book added to library
- **UPDATE**: Book information modified
- **DELETE**: Book removed from library
- **BORROW**: Book availability changed to BORROWED
- **RETURN**: Book availability changed to AVAILABLE

### Author Events
- **CREATE**: New author added
- **UPDATE**: Author information modified
- **DELETE**: Author removed

### Audit Events
- All operations are logged with:
  - User ID
  - Action performed
  - Resource type and ID
  - Timestamp
  - Additional details

## Configuration

### Application Properties
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: library-management-group
    producer:
      acks: all
      retries: 3

library:
  kafka:
    topics:
      book-events: book-events-topic
      author-events: author-events-topic
      audit-events: audit-events-topic
```

## Usage Examples

### Creating a Book (with Events)
```java
// When a book is created, the following events are automatically published:
// 1. BookEvent with operation "CREATE"
// 2. AuditEvent with action "CREATE_BOOK"
```

### Updating Book Availability (with Events)
```java
// When book availability is updated:
// 1. BookEvent with operation "BORROW" or "RETURN"
// 2. AuditEvent with action "UPDATE_BOOK_AVAILABILITY"
```

## Event Processing

The `EventConsumer` class processes all incoming events and can be extended to:
- Update search indexes
- Send notifications
- Update analytics
- Cache invalidation
- Integration with external systems

## Monitoring

### Kafka Topics
```bash
# List topics
docker exec -it library-kafka kafka-topics --bootstrap-server localhost:9092 --list

# View topic details
docker exec -it library-kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic book-events-topic
```

### Consumer Groups
```bash
# List consumer groups
docker exec -it library-kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list

# View consumer group details
docker exec -it library-kafka kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group library-management-group
```

## Error Handling

- **Producer**: Events are published asynchronously with callback handling
- **Consumer**: Manual acknowledgment with error logging
- **Retry**: Configured for 3 retries on producer side
- **Dead Letter Queue**: Can be implemented for failed message processing

## Benefits

1. **Decoupling**: Services are decoupled through event-driven architecture
2. **Scalability**: Kafka provides high throughput and scalability
3. **Reliability**: Events are persisted and can be replayed
4. **Audit Trail**: Complete audit trail of all operations
5. **Extensibility**: Easy to add new event consumers for additional functionality

## Future Enhancements

- Dead Letter Queue for failed messages
- Event sourcing for complete state reconstruction
- Integration with external systems (notifications, analytics)
- Real-time dashboards using Kafka Streams
- Schema registry for event versioning
