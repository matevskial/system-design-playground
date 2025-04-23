# URL SHORTENER

This readme will contain information about the module and submodules related to url shortener, 
as well as basic knowledge notes about url shortener

## Module organization

* `url-shortener`
  * `url-shortener-application`
  * `url-shortener-application-reactive` (todo)

The goal is to make some benchmarks between regular and reactive implementation of url shortener with spring boot

## How to run

Before running this application:
 * run the docker compose located in the folder `docker-compose-local` at the root this repository
 * connect to the postgres database and create the database `url_shortener`

Then compile and run the application either from the command line or using IDE such as  IntelliJ.
Note: A run configuration for Intellij is stored in the folder `.run` at the root of this repository.

## url shortener general notes

An example specification and assumptions of url shortener

* can contain only [a-zA-Z0-9]
* average url length 100 bytes
* we can assume that read is more frequent than writes(10:1)

* http status 301 means moved permanently, meaning browser might cache the long url
* http status 302 means  moved temporarily meaning browser will not cache the long url

* in order to determine the length of the hashValue, we need to know how many different url we expect to store: let's assume
we want to store 100 millions urls per day, for 10 years, we get 365 billion urls

* total possible characters for the hashValue are 62, so we need to find smallest n such that 62^n >= 365 billion 
* let n be 7

* hash strategy:
  * sha plus collision resolution strategy
    * resolution is to regenerate sha of currenturl = currenturl + predefined string
    * bloom filter might be used to help in the collision resolution strategy
  * generating unique id(in  distributed manner such as tsid) plus converting to base-62 number

* api design:

POST /api/v1/url/shorten
  parameter: url
  return shortenedUrl
  flow: first checks if url exists in DB(meaning hash is already generated), then generate the hash of the long url and return the short url

GET /api/v1/{urlHashValue}
  respond with 301 or 302 and header: Location: <the-url>
  flow: first check if url exists in cache, then check if url exists in db, then respond accordingly
