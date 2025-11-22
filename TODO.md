# Task tracking

## todo

* [] - Implement a way to configure routes and rate-limit specification without the need to restart the api-gateway service
* [] - Consider refactoring TokenBucket and LeakingBucket with Clock instance
  * this can help making the implementations testable
  * this also makes the implementation more specific, i.e we explicitly select the Clock, with System.nanoTime we are at the mercy of the JVM
* [] - Consider using redis for storing state of token bucket
* [] - Consider using redis for storing state of leaking bucket
* [] - Consider implementing other rate-limiting algorithms https://www.rdiachenko.com/posts/arch/rate-limiting/leaky-bucket-algorithm/
* [] - Consider rate-limiting per key
* [] - Consider implementing a way of updating of rate-limiting rules dynamically
* [] - Consider implementing a way of updating routing rules dynamically in `api-gateway`
* [] - (api-gateway, application) - See if the field trace can be removed conditionally(debug vs prod build)
* [] - (url-shortener) Implement configuration to set node in a dynamic way in order to prepare the service to be running with multiple instances. 
One example is calculating a node id based on the ip address of the pod where the application is running
* [] - (url-shortener) Implement redis caching
* [] - (url-shortener-netty-application) Implement request body - multipart/form-data; boundary=...

## done

* [v] - Implement a custom rate-limiter algorithm(in a new module `rate-limiters`)
  * [v] token bucket(see org.isomorphism.util for inspiration)
    * maybe use redis for underlying storage of token
  * [v] leaking bucket
    * [v - kind of] queue based
    * [v] no-queue based https://www.rdiachenko.com/posts/arch/rate-limiting/leaky-bucket-algorithm/
  * compare leaking bucket implementations
    * see if queue-based should be improved
* [v] - (url-shortener-netty) make sure exception is handled when mapping netty http request to framework request because request is hanging
  * to reproduce the problem, use POST http://localhost:6063/api/v1/shorten?originalUrl=http://youtube.com where queryParams are part of the path
* [v] - (url-shortener-netty) Configure the ExecutorService to mimic tomcat's behavior: 200 threads maximum, 25 idle
