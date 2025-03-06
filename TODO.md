# Task tracking

## todo

* [] - Implement a way to configure routes and rate-limit specification without the need to restart the api-gateway service
* [] - Implement a custom rate-limiter algorithm(in a new module `rate-limiters`)
  * [v] token bucket(see org.isomorphism.util for inspiration)
    * maybe use redis for underlying storage of token
  * leaking bucket
* [] - (api-gateway, application) - See if the field trace can be removed conditionally(debug vs prod build)

## done
