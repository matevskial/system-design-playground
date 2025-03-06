# Scripts

Various scripts to automate stuff, such as some tests

To run tests for api-gateway rate limiters:

* make sure both `api-gateway` and `application` are running

* run
    ```bash
    bun run api-gateway-rate-limiter-tests.ts
    ```

This project was created using `bun init` in bun v1.2.4. [Bun](https://bun.sh) is a fast all-in-one JavaScript runtime.
