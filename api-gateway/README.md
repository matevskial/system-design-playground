# Api Gateway

This module contains implementation of api-gateway for various purposes(such as rate-limiter)

The README will contain notes abotu api-gateway and other specific topic that are related to api-gateway

API gateway is a fully managed
service that supports rate limiting, SSL termination, authentication, IP whitelisting, servicing
static content, etc.

When we deploy an api-gateway as a service, it is a server-side api-gateway since its not a physical component or a third party component.


## Rate limiter

## Some implementation details of org.isomorphism.util token bucket rate limiter

* uses synchronized methods
* uses google's common library ticker
* the timestamps are with nano precision
* each operation(consume, tryConsume, readavailable tokens give chance to refill by calling refill)
* the method `consume` waits to consume until a token is available, 
  * the waiting is either sleep uninteruptly for one nanosecond
  * or busy waiting(not sleeping) in order to not yeld the cpu
    * this might be more performant if the refill is frequent
