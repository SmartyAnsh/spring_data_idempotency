## Spring Data Idempotency

### An example implementation of using Redis as a cache along with the H2 database using Spring Data Redis and Spring Data JPA.

We've added the `Customer` entity which is stored in the H2 in-memory database using Spring Data JPA and POST API endpoint `/customer`.

* Example POST curl: `curl -X POST "http://localhost:8080/customer" -d '{"firstName":"anshul","lastName":"bansal","email":"smartdiscover17@gmail.com"}' -H "Accept: application/json" \
-H "Content-Type:application/json"`

However, when the `Customer` record is fetched using the GET API, we require to pass the `id` of the `Customer` object and the `idempotencyKey` in the request header.
The application will check if the `idempotencyKey` exist in the Redis cache and tries to fetch the `Customer` object from the cache else it will search the H2 database.

* Example GET curl: `curl "http://localhost:8080/customer?id=281407da-5265-468f-b65b-d98d2268b122" -H "idempotencyKey: 0f3123e5-de97-4d4b-b2d5-e59e8ccfd442"`

Notes:
* We've used the `UUID` for both `id` and `idempotencyKey`.
* Use the `id` of the `Customer` object received in the response of the POST API.
