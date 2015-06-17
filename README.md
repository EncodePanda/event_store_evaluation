# Event Store Evaluation

This project is my sandbox for evaluation Event Store (http://geteventstore.com).
To follow this evaluation you will need Docker (https://www.docker.com/tryit/) installed. 

## Running Event Store

To run Event Store we are going to use an existing docker image *adbrowne/eventstore* (thanks @adbrowne). 

```sudo tredocker run -d -p 2113:2113 -p 1113:1113 adbrowne/eventstore```

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

### Inserting first event

Let's create our first stream called users and add new event to it.

    > These are partition points in the system. If you are Event Sourcing a domain model a stream would equate to an aggregate.   

Sample event is described in a file sample_event.txt and looks like this:

```
[
  {
    "eventId": "fbf4a1a1-b4a3-4dfe-a01f-ec52c34e16e4",
    "eventType": "UserInitialized",
    "data": {
      "firstName": "John",
      "lastName" : "Terry"
    }
  }
]
```

Now lets add him to our users stream

```
$ curl -i -d @sample_event.txt "http://172.17.0.3:2113/streams/users" -H "Content-Type:application/json"

HTTP/1.1 400 Must include an event type with the request either in body or as ES-EventType header.
Access-Control-Allow-Methods: POST, DELETE, GET, OPTIONS
Access-Control-Allow-Headers: Content-Type, X-Requested-With, X-PINGOTHER, Authorization, ES-LongPoll, ES-ExpectedVersion, ES-EventId, ES-EventType, ES-RequiresMaster, ES-HardDelete, ES-ResolveLinkTo, ES-ExpectedVersion
Access-Control-Allow-Origin: *
Access-Control-Expose-Headers: Location, ES-Position
Content-Type: 
Server: Mono-HTTPAPI/1.0
Date: Wed, 17 Jun 2015 20:49:11 GMT
Content-Length:
```

Navigate your browser to ```http://172.17.0.3:2113/web/index.html#/streams``` to see the results. In 'Recently Changed Streams' you will notice new stream called 'users'. If you click it, you will be able to browse all the events in it (currently one, the one that we've just added).
