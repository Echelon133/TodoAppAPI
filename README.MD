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

In this app tokens are stored in Redis and they are verified by comparing the token sent by the user and the token that is stored 
in Redis under the username key. If the tokens match then the verification is successful.


## Documentation
| Endpoint                                          | Method | Data appended to the request            |      Description                           |
| ------------------------------------------------- | ------ | --------------------------------------- |      ------------                          |
| /users/register                                   | POST   |  [User registration JSON](https://github.com/Echelon133/TodoAppAPI/blob/master/README.MD#user-registration-json)                                     |      Register a new user                   |
| /users/token                                      | POST   |  Header with basic auth set             | Get currently valid token                  |
| /api/todo-lists                                   | GET    |  Header with bearer token               | Get all todo lists owned by user           |
| /api/todo-lists                                   | POST   |  Header with bearer token and [List JSON](https://github.com/Echelon133/TodoAppAPI/blob/master/README.MD#list-json)        | Create a new list with specified name      |
| /api/todo-lists/{listId}                          | GET    |  Header with bearer token               | Get a specific todo list with specified listId |
| /api/todo-lists/{listId}                          | PUT    |  Header with bearer token and [List JSON](https://github.com/Echelon133/TodoAppAPI/blob/master/README.MD#list-json)        | Rename the list with specified listId          |
| /api/todo-lists/{listId}                          | DELETE |  Header with bearer token               | Delete the list with specified listId          |
| /api/todo-lists/{listId}/tasks                    | GET    |  Header with bearer token               | Get all tasks from a list with specified listId|
| /api/todo-lists/{listId}/tasks                    | POST   |  Header with bearer token and [Task JSON](https://github.com/Echelon133/TodoAppAPI/blob/master/README.MD#task-json)        | Create a new task that belongs to a list with specified listId|
| /api/todo-lists/{listId}/tasks/{taskId}           | GET    |  Header with bearer token               | Get a task with taskId that belongs to a list with listId |
| /api/todo-lists/{listId}/tasks/{taskId}           | PUT    |  Header with bearer token and [Task JSON](https://github.com/Echelon133/TodoAppAPI/blob/master/README.MD#task-json)        | Modify a task with specified taskId that belongs to a list with listId |
| /api/todo-lists/{listId}/tasks/{taskId}           | DELETE |  Header with bearer token               | Delete a task with specified taskId that belongs to a list with listId |




## JSON objects

### User registration JSON

```Json
{
    "username" : "my_new_user",
    "password" : "userpassword",
    "passwordConfirm" : "userpassword"
}
```

### List JSON

```Json
{
    "name" : "My list name"
}
```

### Task JSON

```Json
{
    "taskContent" : "Put your task content here",
    "finished" : false
}
```
## Screens
### Token Generation
![GenerateToken](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/1GenerateToken.png)

### List Creation
![CreateList](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/2CreateList.png)

### All Lists 
![GetLists](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/3GetLists.png)

### Specific List
![GetSpecificList](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/4GetSpecificList.png)

### All Tasks In a List
![GetAllTasks](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/5GetAllTasks.png)

### Task Creation
![CreateTask](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/6CreateTask.png)

### Specific List with Tasks
![GetNotEmptyList](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/7GetNotEmptyList.png)

### Specific Task
![GetSpecificTask](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/8GetSpecificTask.png)

### Task Name Updating
![UpdateTaskName](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/9UpdateTaskName.png)

### Task Deletion
![DeleteTask](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/10DeleteTask.png)

### Specific List (Updated)
![GetUpdatedList](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/11GetUpdatedList.png)

### List Name Change
![ChangeListName](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/12ChangeListName.png)

### All Lists (Updated)
![GetAllLists](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/13GetAllLists.png)

### List Deletion
![DeleteList](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/14DeleteList.png)

### All Lists (Updated again)
![GetEmptyLists](https://github.com/Echelon133/TodoAppAPI/blob/master/screens/15GetEmptyLists.png)
