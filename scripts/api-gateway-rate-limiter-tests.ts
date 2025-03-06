function callRateLimitedEndpointThroughApiGateway() {
    return fetch(
        "http://localhost:6060/application/api/v1/ratelimited/configuration2?delay=30s"
    );
}

console.log("api-gateway custom rate limiter tests")

for (let i = 0; i < 4; i++) {
    callRateLimitedEndpointThroughApiGateway()
        .then((res) => console.log(`Response of request ${i}: ${res.statusText}`))
        .catch((err) => console.log(`Error response of request ${i}: ${err}`));
}
