# NetworkingParent
Parent project for set of projects aimed at studying network interactions using java.

## Version 
1.0-SNAPSHOT

## Description
##### At this moment implemented 3 client\server pairs:
- Socket. java.net used. Implements simple echo pair allows to operate with lines and echo pair to operate with messages. Simple protocol was used (message consist of it's size and the actual message body).
- Nio. java.nio used. Implements echo pair allows to operate with messages. CompletionHandlers approach used.
- Nio (Future approach) The same as Nio but Future approach used for read\write opperations with buffers.

### Server accept parameters:
- port -p (9000 by default)
- threads -t (5 by default) - specify allowable number of threads
- server -s (socket by default) - specify server type (socket, nio, fnio) 

### Client accept parameters:
- attempt -a (10 by defult) specify number of request attemts
- client -c (socket by default) specify client type (socket, nio, fnio)
- host -h (127.0.0.1 by default) specify server host to connect
- port -p (9000 by default) specify server port to connect
- request -r ('default request' by default) specify request message

## Installation
To build project move to Server or Client folder and use maven
```sh
mvn clean install
```