# Event Store Evaluation

This project is my sandbox for evaluation of Event Store (http://geteventstore.com).
The objective is to familirize myself with the tool to an extent where I can
write event sourced applications in Scala based on Event Store.

To follow this evaluation step-by-step you will need to have Docker (https://www.docker.com/tryit/) installed. 

## 1. Running Event Store

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

## 2. Reads and writes using HTTP API

### Inserting first two events

Let's create our first stream called users and add new event to it.

> "These are partition points in the system.
> If you are Event Sourcing a domain model a stream would equate to an aggregate."
> -- Event Store Documentation
	
Sample event is are available in a files [john_event.txt](john_event.txt) and [jane_event.txt](jane_event.txt). Now lets add John & Jane events to our store. Since Event Store can handle hundred of millions of streams, we will create stream per each user: user_1 for John and user_2 for Jane.

```
$ curl -i -d @john_event.txt "http://172.17.0.3:2113/streams/user_1" -H "Content-Type:application/vnd.eventstore.events+json"
HTTP/1.1 201 Created
(...)
```

```
$ curl -i -d @jane_event.txt "http://172.17.0.3:2113/streams/user_2" -H "Content-Type:application/vnd.eventstore.events+json"
HTTP/1.1 201 Created
(...)
```

Navigate your browser to http://172.17.0.3:2113/web/index.html#/streams to see the results. In 'Recently Changed Streams' you will notice two new stream created called "user_1" and "user_2". If you click it, you will be able to browse all the events in it. Currently we have one event per each stream.

### Reading from the stream

Now let's try to read stream of events. Event Store uses Atom as standard way to fetch stream of events. This allows the client to browse the entire history with ease.

```	
$ curl -i -H "Accept:application/atom+xml" "http://172.17.0.3:2113/streams/user_1"
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
Date: Thu, 18 Jun 2015 15:21:49 GMT
Content-Length: 902
Keep-Alive: timeout=15,max=100

<?xml version="1.0" encoding="utf-8"?>
<feed xmlns="http://www.w3.org/2005/Atom">
<title>Event stream 'user_1'</title>
<id>http://172.17.0.3:2113/streams/user_1</id>
<updated>2015-06-18T15:20:24.685335Z</updated>
<author><name>EventStore</name></author>
<link href="http://172.17.0.3:2113/streams/user_1" rel="self" />
<link href="http://172.17.0.3:2113/streams/user_1/head/backward/20" rel="first" />
<link href="http://172.17.0.3:2113/streams/user_1/1/forward/20" rel="previous" />
<link href="http://172.17.0.3:2113/streams/user_1/metadata" rel="metadata" />
<entry>
<title>0@user_1</title>
<id>http://172.17.0.3:2113/streams/user_1/0</id>
<updated>2015-06-18T15:20:24.685335Z</updated>
<author><name>EventStore</name></author>
<summary>UserInitialized</summary>
<link href="http://172.17.0.3:2113/streams/user_1/0" rel="edit" />
<link href="http://172.17.0.3:2113/streams/user_1/0" rel="alternate" /></entry>
</feed>
```

Atom feed will only give you stream of links to events. This is fully browsable, since you can you provided links like 'first' or 'previous'.

To fetch event details we must create separate call, using link listed in the feed. Let's try that now:

```
$ curl -i http://127.0.0.1:2113/streams/users_1/0 -H "Accept: application/json"
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
  "lastName": "Smith"
}
```

## 3. Reads and writes using JVM API

JVM API is hosted on github under https://github.com/EventStore/EventStore.JVM

### Writing events programmatically

There are two ways to write events to stream:

1. with Actors 
2. with Future-like API

Both ways are shown in [WriteEventExample.scala](/src/main/scala/ese/WriteEventExample.scala)

### Reading events programmatically

As with writes, there are two ways to read events: with Actors and with Future-like API. There are 3 types of commands that you can use to read events:

1. *ReadEvent* - will fetch details of one particular event, examples in [ReadParticularStreamEvent.scala](/src/main/scala/ese/ReadParticularStreamEvent.scala)
2. *ReadStreamEvents* - will fetach list of events from given stream, examples in  [ReadStreamEvents.scala](/src/main/scala/ese/ReadStreamEvents.scala)
3. *ReadAllEvents* - will fetchh ALL events in the system, examples in[ReadAllEvents.scala](/src/main/scala/es/ReadAllEvents.scala)

