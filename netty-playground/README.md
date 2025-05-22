# Netty playground

# Notes on netty

* https://www.youtube.com/watch?v=NvnOg6g4114 Netty, the IO framework that propels them all By Stephane LANDELLE

* netty is a network framework

* used by spring webflux(project reactor), vert.x, micronaut, etc

* low level api

* supports http/2, tls, 

* goal is to have performance

* regular java api for socket and inputstream is blocking and does not scale well with native threads
  * virtual threads help with this and non-blocking io is being used

* The NIO api For Java 4 and onwards:
  * ServerSocketChannel
  * Selector(essentially a wrapper of linux's epoll)

* The NIO2 api for Java 7 and onwards:
  * AsynchronousServerSocketChannel
  * Future

* The event handlers for a channel should not block since multiple channels are associated with one event loop
  * blocking or computationally-intensive code should be delegated to some thread pool