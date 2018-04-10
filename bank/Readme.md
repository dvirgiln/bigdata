# Introduction
This project provides an example of API using Akka Http and Akka Actors.

The API contains basic calls for making transactions between users. For achieving that there are 3 different endpoints:

* Users: contains basic operations to add, get and list users. It is not possible to delete users, as we want to keep track of all the history of operations.
* Transactions: basic endpoint for transactions as adding, listing and getting by user id. It communicate with the Users service to validate the users involved in the transactions.
* Clients: this is a mix between the previous 2. It allows to create a user with an initial deposit. It communicate between the previous 2 services. It is possible to get basic and detailed information.

# Usage

        sbt docker:publishLocal
        docker run --rm -p8080:8080 bank-api:0.0.1-SNAPSHOT

# Endpoints
The endpoints can be seen in postman. Just import the file bank_endpoints.json

Here it is the complete list of endpoints:

## Users
1. GET  /users -> retrieves a list with all the users. As you see does not contain any information about the balance.
        Response:
```json
        {
            "users": [
                {
                    "id": 0,
                    "name": "Main Bank",
                    "age": 100,
                    "countryOfResidence": "UK"
                },
                {
                    "id": 1,
                    "name": "Juan Virgil",
                    "age": 30,
                    "countryOfResidence": "UK"
                },
                {
                    "id": 2,
                    "name": "David Virgil",
                    "age": 30,
                    "countryOfResidence": "UK"
                }
            ]
        }
```
2. GET  /users/{userId} -> Gives the information about an specific userId.
        Response:
```json
        {
            "id": 1,
            "name": "Juan Virgil",
            "age": 30,
            "countryOfResidence": "UK"
        }
```
3. POST /users
        Request:
```json
        {
          "id" : "54321",
          "name" : "Pepe Naranjo",
          "age" : 33,
          "countryOfResidence" : "UK"
        }
```
        Response:
```json
        {
            "description": "User Pepe Naranjo created."
        }
```
## Transactions

1. GET  /transactions -> Get all the transactions. There is an initial transaction when the app is started from the MAIN_BANK (main entity that creates money), that is used to make initial deposits from the users.
```json
        {
            "transactions": [
                {
                    "receiverId": 1,
                    "amount": 8000,
                    "senderId": 0,
                    "id": 1,
                    "status": "PENDING",
                    "transactionDate": "2018-04-10T10:04:52.995+0100"
                },
                {
                    "receiverId": 2,
                    "amount": 1000,
                    "senderId": 1,
                    "id": 3,
                    "status": "PENDING",
                    "transactionDate": "2018-04-10T10:07:05.642+0100"
                }
            ]
        }
```
2. GET  /transactions/{userID} -> Get the transaction by the userID. Makes a validation using the UserActor to check that the user exists.
        Response:
```json
        {
            "transactions": [
                {
                    "receiverId": 1,
                    "amount": 8000,
                    "senderId": 0,
                    "id": 1,
                    "status": "PENDING",
                    "transactionDate": "2018-04-10T10:04:52.995+0100"
                },
                {
                    "receiverId": 2,
                    "amount": 1000,
                    "senderId": 1,
                    "id": 3,
                    "status": "PENDING",
                    "transactionDate": "2018-04-10T10:07:05.642+0100"
                }
            ]
        }
```
3. POST /transactions -> Creates a new transaction.
     * Validates that the sender and receiver are users of the system.
     * Validates if it is possible to make the transaction with the current money in the sender account.
        Request:
```json
        {
        	"senderId" : 1,
        	"receiverId" : 2,
        	"amount" : 1000
        }
```
        Response:
```json
        {
            "receiverId": 2,
            "amount": 1000,
            "senderId": 1,
            "id": 3,
            "status": "PENDING",
            "transactionDate": "2018-04-10T10:07:05.642+0100"
        }
```

## Clients
1. GET  /clients  -> retrieves a list with all the users basic information.
2. GET  /clients/{userId} -> retrienves the client basic information.
        Response:
```json
        {
            "user": {
                "id": 1,
                "name": "Juan Virgil",
                "age": 30,
                "countryOfResidence": "UK"
            },
            "balance": 8000
        }
```
3. GET  /clients/{userId}?detailed=true  -> Contains the client detailed information.
        Response:
```json
        {
            "user": {
                "id": 1,
                "name": "Juan Virgil",
                "age": 30,
                "countryOfResidence": "UK"
            },
            "balance": 7000,
            "transactions": [
                {
                    "receiverId": 1,
                    "amount": 8000,
                    "senderId": 0,
                    "id": 1,
                    "status": "PENDING",
                    "transactionDate": "2018-04-10T10:04:52.995+0100"
                },
                {
                    "receiverId": 2,
                    "amount": 1000,
                    "senderId": 1,
                    "id": 3,
                    "status": "PENDING",
                    "transactionDate": "2018-04-10T10:07:05.642+0100"
                }
            ]
        }
```
4. POST /clients
        Request:
```json
        {
            "user" : {
                "name" : "Juan Virgil",
                "age" : 30,
                "countryOfResidence" : "UK"
            },
            "balance" : 8000
        }
```
        Response:
```json
        {
            "message": "Created user with userId 1  with an initial deposit of 8000.0"
        }
```
```
4. POST /clients/{user_id}/deposit  -> A deposit is done from the MAIN_BANK
        Request:
```json
        {
            "deposit" : 5000
        }
```
        Response:
```json
        {
            "message": "Deposit of 5000 has been done to userId 1"
        }
```
