# Banking Microservices - Saga Orchestration Demo

Four independent Spring Boot applications:
- eureka-server:8761
- account-service:8085 (MySQL: banking_account_db)
- transaction-service:8082 (MySQL: banking_transaction_db; Saga orchestrator)
- banking-api-gateway:9091

## Requirements
- JDK 24 (matching the Spring Boot 4.1.2 setup used in this example)
- Maven 3.9+
- MySQL 8+
- Git (optional)

This project uses Spring Boot 4.1.2 and Spring Cloud Netflix/Gateway 5.0.4.
If your Spring Initializr offers different compatible patch versions, keep the Spring Boot and Spring Cloud release train compatible.

## 1. Create databases
Run in MySQL:
```sql
CREATE DATABASE banking_account_db;
CREATE DATABASE banking_transaction_db;
```
Update the MySQL username/password in each service's `application.properties`.

## 2. Start services (in separate terminals)
Start in this order:
1. `eureka-server`: `mvn spring-boot:run`
2. `account-service`: `mvn spring-boot:run`
3. `transaction-service`: `mvn spring-boot:run`
4. `banking-api-gateway`: `mvn spring-boot:run`

Open Eureka: http://localhost:8761

The Account Service seeds demo accounts if they do not exist:
- Account 101: INR 20,000
- Account 102: INR 10,000

## 3. Test direct APIs
Check balances:
```http
GET http://localhost:8085/accounts/101
GET http://localhost:8085/accounts/102
```

Transfer ₹5,000:
```http
POST http://localhost:9091/api/transactions/transfer
Content-Type: application/json

{
  "fromAccountId": 101,
  "toAccountId": 102,
  "amount": 5000.00
}
```

Check transfer status:
```http
GET http://localhost:9091/api/transactions/{transactionId}
```

For a quick failure-path demonstration, transfer to a non-existent receiver account. The sender debit is compensated by a refund if the refund endpoint succeeds.

## Important design notes
- Transaction Service is the Saga Orchestrator.
- Account Service owns account balances and its own database.
- Each Account Service operation is locally transactional and idempotent using a transaction key.
- The orchestrator records transfer status as PENDING, COMPLETED, FAILED, or COMPENSATION_FAILED.
- This is a learning/demo implementation, not production banking software.
- Production systems need durable workflow recovery, outbox/inbox messaging, retry policies, authentication/authorization, audit trails, limits, fraud checks, reconciliation, concurrency controls, and operational monitoring.
- The synchronous HTTP orchestration has a crash window between remote actions and status persistence. For production, use a durable state machine and/or transactional outbox with messaging.
- The demo uses a simple debit then credit model. Real banks commonly reserve/hold funds first, then confirm or release them.
