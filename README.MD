# Simple Payment Service
Transferring money from one account to another account

## Build
####Build & Unit Tests
```
gradle build
```
####Running Integration Tests
```
gradle run
```
```
gradle integrationTests
```

## Running Payment Service
```
gradle run
```

## API Reference
```
POST /payments
GET /accounts/
```
## API Reference Examples
```
GET
curl localhost:8080/accounts

Response
[
  {
    "id": 1,
    "number": "123456789",
    "balance": {
      "value": "100.00",
      "currency": "GBP"
    }
  },
  {
    "id": 2,
    "number": "987654321",
    "balance": {
      "value": "150.00",
      "currency": "GBP"
    }
  }
]

```
```
POST
curl localhost:8080/payments -v --data "{ "remitterAccount": "123456789", "beneficiaryAccount": "987654321", "amount": {"currency": "GBP", "value": "100"} }"

Response
{
  "remitterAccount": "123456789",
  "beneficiaryAccount": "987654321",
  "amount": {
    "value": "100.00",
    "currency": "GBP"
  },
  "status": "SUCCESS"
}
```
