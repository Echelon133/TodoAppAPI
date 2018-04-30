# TodoAppAPI

## About
This todo app functionalities:
* user registration
* token authentication using JSON Web Token
* management of lists and tasks (create, read, update, delete)

## About - Technical Details

### Custom Authentication

My main purpose when writing this app was to learn about the internals of Spring.
That's why instead of using something like OAuth2 I implemented my own system based on JSON Web Tokens.

When user generates his token, the app puts it in Redis for a time specified
in the **TokenService** as *hoursTimeToLive*.

Each token holds inside its owner username. It makes API endpoints neater,
because user can **GET** */api/todo-lists* and the app simply extracts username from the token, 
checks in Redis whether specific user holds a token that is identical to the token from which username was extracted, and
when that condition is true, it takes user lists from the database and returns a response. 

There are two authentication systems in this application:
* Basic Auth - for **POST** requests at */users/token* that provide *Username* and *Password* encoded in the header.
* JWT Auth - for requests that start with */api/* and have *Authorization* header with *Bearer* token

### Redis use

JWT library has token claims verification built-in, but I did not use it because I wanted to try out Redis in a real application.

In this app tokens are stored in Redis and they are verified by comparison between the token sent by user and the token that is stored 
in Redis under the username key. If the keys match then the verification is successful.


## Documentation

### Registration

* **POST** /users/register - register a new user

Send with request: 
```Json
{
    "username":"my_new_user",
    "password":"userpassword",
    "passwordConfirm":"userpassword"
}
```

### Token generation

* **POST** /users/token - get currently valid token

Send with request: header with basic auth data

### General todo lists operations

* **GET** /api/todo-lists - get all todo lists owned by the user

Send with request: header with bearer token

* **POST** /api/todo-lists - create a new todo list

Send with request: header with bearer token and JSON as below:

```Json
{
    "name" : "My new list name"
}
```

### Specific todo list operations

* **GET** /api/todo-lists/{listId} - get a list with specified id

Send with request: header with bearer token

* **PUT** /api/todo-lists/{listId} - rename a list with specified id

Send with request: header with bearer token and JSON as below:

```Json
{
    "name" : "New name for my already existing list"
}
```

* **DELETE** /api/todo-lists/{listId} - remove a list with specified id

Send with request: header with bearer token

