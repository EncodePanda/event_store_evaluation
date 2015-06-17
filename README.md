# Event Store Evaluation

This project is my sandbox for evaluation Event Store (http://geteventstore.com).
To follow this evaluation you will need Docker (https://www.docker.com/tryit/) installed. 

## Running Event Store

To run Event Store we are going to use an existing docker image *adbrowne/eventstore* (thanks @adbrowne). 

```sudo docker run -d -p 2113:2113 -p 1113:1113 adbrowne/eventstore```

We can see that Event Store is running

```
$ sudo docker ps
CONTAINER ID        IMAGE                        CREATED             NAMES
5520c023be13        adbrowne/eventstore:latest   6 minutes ago       trusting_mestorf
```

Fetch its IP Address

```
$ sudo docker inspect 5520 | grep IPAddress
        "IPAddress": "172.17.0.3",
```

Now we can open admin console with URL ```http://172.17.0.3:2113```. User is admin, password is changeit.