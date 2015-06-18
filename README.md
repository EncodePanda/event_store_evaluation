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

Lets fetch Event Store IP Address

```
$ sudo docker inspect 5520 | grep IPAddress
        "IPAddress": "172.17.0.3",
```

Now we can open admin console with URL ```http://172.17.0.3:2113```. User is admin, password is changeit.

### Inserting first event

Let's create our first stream called users and add new event to it.

    "These are partition points in the system.
    If you are Event Sourcing a domain model a stream would equate to an aggregate."
    -- Event Store Documentation
	
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
$ curl -i -d @sample_event.txt "http://172.17.0.3:2113/streams/users" -H "Content-Type:application/vnd.eventstore.events+json"
HTTP/1.1 201 Created
Access-Control-Allow-Methods: POST, DELETE, GET, OPTIONS
Access-Control-Allow-Headers: Content-Type, X-Requested-With, X-PINGOTHER, Authorization, ES-LongPoll, ES-ExpectedVersion, ES-EventId, ES-EventType, ES-RequiresMaster, ES-HardDelete, ES-ResolveLinkTo, ES-ExpectedVersion
Access-Control-Allow-Origin: *
Access-Control-Expose-Headers: Location, ES-Position
Location: http://172.17.0.3:2113/streams/users/0
Content-Type: text/plain; charset=utf-8
Server: Mono-HTTPAPI/1.0
Date: Thu, 18 Jun 2015 08:29:58 GMT
Content-Length: 0
Keep-Alive: timeout=15,max=100
```

Navigate your browser to ```http://172.17.0.3:2113/web/index.html#/streams``` to see the results. In 'Recently Changed Streams' you will notice new stream called 'users'. If you click it, you will be able to browse all the events in it. Currently we have one event in it, the one that we've just added.

## Reading from stream

Now let's try to read stream of events. Event Store uses Atom as standard way to fetch stream of events. This allows the client to browse the entire history with ease.

```
$ curl -i -H "Accept:application/atom+xml" "http://172.17.0.3:2113/streams/users"
HTTP/1.1 200 OK
Access-Control-Allow-Methods: POST, DELETE, GET, OPTIONS
Access-Control-Allow-Headers: Content-Type, X-Requested-With, X-PINGOTHER, Authorization, ES-LongPoll, ES-ExpectedVersion, ES-EventId, ES-EventType, ES-RequiresMaster, ES-HardDelete, ES-ResolveLinkTo, ES-ExpectedVersion
Access-Control-Allow-Origin: *
Access-Control-Expose-Headers: Location, ES-Position
Cache-Control: max-age=0, no-cache, must-revalidate
Vary: Accept
ETag: "0;-1296467268"
Content-Type: application/atom+xml; charset=utf-8
Server: Mono-HTTPAPI/1.0
Date: Wed, 17 Jun 2015 21:43:06 GMT
Content-Length: 892
Keep-Alive: timeout=15,max=100

<?xml version="1.0" encoding="utf-8"?>
<feed xmlns="http://www.w3.org/2005/Atom">
<title>Event stream 'users'</title>
<id>http://172.17.0.3:2113/streams/users</id>
<updated>2015-06-17T20:51:43.983174Z</updated>
<author><name>EventStore</name></author>
<link href="http://172.17.0.3:2113/streams/users" rel="self" />
<link href="http://172.17.0.3:2113/streams/users/head/backward/20" rel="first" />
<link href="http://172.17.0.3:2113/streams/users/1/forward/20" rel="previous" />
<link href="http://172.17.0.3:2113/streams/users/metadata" rel="metadata" />
<entry><title>0@users</title><id>http://172.17.0.3:2113/streams/users/0</id><updated>2015-06-17T20:51:43.983174Z</updated><author><name>EventStore</name></author><summary>UserInitialized</summary><link href="http://172.17.0.3:2113/streams/users/0" rel="edit" /><link href="http://172.17.0.3:2113/streams/users/0" rel="alternate" /></entry>
</feed>
```

We see as expected that there is one event, accesible under http://172.17.0.3:2113/streams/users/0. Let's try to fetch it

```
$ curl -i http://127.0.0.1:2113/streams/users/0 -H "Accept: application/json"
HTTP/1.1 200 OK
Access-Control-Allow-Methods: GET, OPTIONS
Access-Control-Allow-Headers: Content-Type, X-Requested-With, X-PINGOTHER, Authorization, ES-LongPoll, ES-ExpectedVersion, ES-EventId, ES-EventType, ES-RequiresMaster, ES-HardDelete, ES-ResolveLinkTo, ES-ExpectedVersion
Access-Control-Allow-Origin: *
Access-Control-Expose-Headers: Location, ES-Position
Cache-Control: max-age=31536000, public
Vary: Accept
Content-Type: application/json; charset=utf-8
Server: Mono-HTTPAPI/1.0
Date: Wed, 17 Jun 2015 21:46:29 GMT
Content-Length: 48
Keep-Alive: timeout=15,max=100

{
  "firstName": "John",
  "lastName": "Terry"
}
```
