# Bank implementation

### Development description

I used *spring initializr* for creation of the project. For developing of the bank I used those libraries:
- Spring Web framework
- Spring Data JPA
- H2 database
- HATEOAS for linking responses with related resources

I have decided for the H2 database because it is uncomplicated to implement into the code and easy to use for unit testing.

The content will be returned in JSON which is by default used by the web framework. Also any updates through POST and PUT are passed as a JSON body in the request.

I designed the REST interface as follows:
- `/accounts` for accessing and manipulating with client accounts
- `/transfers` for creating and executing the transfers

## Accounts

First I implemented basic obtaining and changing of the accounts. The basic requests are using the different HTTP methods accordingly:
- **GET** to obtain all accounts/one account
- **POST** to create a new account
- **PUT** to create or update (if already exists) an account
- **DELETE** to delete an account

Each account is tied with a specific id, which is used to obtain, update or delete it.

### Examples of requests
Obtain all accounts:
```
curl -X GET localhost:8080/accounts
```
Create a new account:
```
curl -X POST localhost:8080/accounts -H 'Content-type:application/json' -d '{"name": "new", "treasury": true, "balance": {"currency": "USD", "amount": 10}}'
```

### Comments
- Currency cannot be changed with already created account. This is because it would not make sense to change it on an account which has some nominal value of money assigned.
- It would be useful to implement an error message for reassigning the treasury field. I did not do it because it would require a custom input binding to be able to determine whether the value was specified by client (and not only default as `false` was set).

## Transfers

Transfers are done in a two-state way to avoid problems with repeating the same transfers (because of caching for example).

Firstly, client creates a transfer through POST method, where he/she specifies id of the source and destination account and amount.

Then transaction is executed through PUT method during which all the requirements are checked (this is because accounts could be changed between creating and executing the transaction).
    It can return three different error HTTP statuses:
- `400` when transaction has already been executed
- `403` when non-treasury account would have a negative value
- `405` when currency of account does not match
        
### Comments
- I have added synchronisation to the PUT method in order to avoid race conditions with insufficient funds on an account. Controller classes are created as singleton so using `synchronise` prevents it from accessing critical code at the same time.

# Hypermedia
Then I added hypermedia to the application to make it RESTful. The returned body contains link which points to itself and other related endpoints.

# Unit tests
Unit tests are created to test each feature from the requirements. Therefore, each condition should have been fulfilled.

# Time comment
I could not develop the solution continuously so I made a break for 30 minutes around 12 and for about one hour from 15:30. With this in consideration I should have not exceeded the 4 hours limit.