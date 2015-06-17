# Event Store Evaluation

This project is my sandbox for evaluation Event Store (http://geteventstore.com).
To follow this evaluation you will need Docker (https://www.docker.com/tryit/) installed. 

## Running Event Store

To run Event Store we are going to use an existing docker image *adbrowne/eventstore* (thanks @adbrowne). 

```sudo docker run -d -p 2113:2113 -p 1113:1113 adbrowne/eventstore```
