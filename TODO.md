# Task tracking

## todo

* [] - Implement a way to configure routes and rate-limit specification without the need to restart the api-gateway service
* [] - Implement a custom rate-limiter algorithm(in a new module `rate-limiters`)
  * [v] token bucket(see org.isomorphism.util for inspiration)
    * maybe use redis for underlying storage of token
  * leaking bucket
    * [v - kind of] queue based
    * no-queue based https://www.rdiachenko.com/posts/arch/rate-limiting/leaky-bucket-algorithm/
  * compare leaking bucket implementations
    * see if queue-based should be improved 
* [] - (api-gateway, application) - See if the field trace can be removed conditionally(debug vs prod build)

## done
