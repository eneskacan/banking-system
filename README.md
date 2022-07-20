# Banking System

![Publish Docker Image](https://github.com/eneskacan/banking-system/actions/workflows/publish-docker-image.yml/badge.svg)

Banking System is a simple web service for simulating simple operations including creating accounts, depositing, and transferring assets. To run the app, it is required to generate a [CollectAPI](https://collectapi.com/api/imdb/imdb-api) access token, and [Kafka](https://kafka.apache.org/documentation/) must be up and running. To download and install Kafka, please refer to the official guide [here](https://kafka.apache.org/quickstart), or [here](https://www.baeldung.com/ops/kafka-docker-setup) for setup with Docker.

## Docker

The easiest way to run this application is via Docker. To install Docker, see [here](https://docs.docker.com/). Once you have Docker installed and [working](https://docs.docker.com/get-started/#test-docker-installation), you can easily pull and start the Docker image.

```bash
  docker run -e collect.api.token="apikey <your-token-here>" -e kafka.bootstrap.address="localhost:9092" -p 8080:8080 eneskacan/banking-system
```

## Installation

Clone the project

```bash
  git clone https://github.com/eneskacan/banking-system.git
```

Update access token in the [application.properties](src/main/resources/application.properties) file

```bash
  collect.api.token=apikey <your-token-here>
```

Go to the project directory

```bash
  cd banking-system
```

Install and start

```bash
  mvn spring-boot:run
```

## API Reference

#### Create account

```http
POST /api/accounts
```

```json
{
  "name" : "Abdullah",
  "surname" : "Yavuz",
  "email" : "abdullah@bootcamp.com",
  "idNumber" : "12121212121",
  "type" : "TRY"
}
```

| Parameter | Type | Description |
| :--- | :--- | :--- |
| `body` | `json` | **Required**. Account details |

If account is successfully created, returns a JSON response in the following format:

```json
{
  "message" : "Account successfully created",
  "id" : "9758876081"
}
```

If account type is invalid, returns a JSON response in the following format:

```json
{
  "message" : "Invalid account type: Expected TRY, USD or XAU but got EUR"
}
```

#### Get account

```http
GET /api/accounts/${id}
```

| Parameter | Type  | Description |
|:----------|:------| :--- |
| `id`      | `int` | **Required**. Account number |

If account number is valid, returns a JSON response in the following format:

```json
{
  "id" : "8793740856",
  "name" : "Abdullah",
  "surname" : "Yavuz",
  "email" : "abdullah@bootcamp.com",
  "idNumber" : "12121212121",
  "accountType" : "TRY",
  "balance" : 0.0,
  "lastUpdated" : 1657928659221
}
```

#### Make deposit

```http
PATCH /api/accounts/${id}/deposits
```

```json
{
  "amount" : 100
}
```

| Parameter | Type   | Description |
|:----------|:-------| :--- |
| `body`    | `json` | **Required**. Deposit details |
| `id`      | `int`  | **Required**. Account number |

#### Make transfer

```http
PATCH /api/accounts/${id}/transfers
```

```json
{
  "receiverId" : "3006665471",
  "amount" : 100
}
```

| Parameter | Type   | Description |
| :--- |:-------| :--- |
| `body` | `json` | **Required**. Transfer details |
| `receiverId` | `int`  | **Required**. Account number |

#### Get logs

```http
GET /api/logs
```

If request is successful, returns a JSON array in the following format:

```json
[
  {
    "message": "Account 9758876081 deposited 100.000 XAU on 2022-07-16 02:26:50.065"
  },
  {
    "message": "Account 8793740856 deposited 100.000 TRY on 2022-07-16 02:46:56.308"
  }
]
```

## Acknowledgements

- [Spring Initializr](https://start.spring.io/)
- [White House Web API Standards](https://github.com/WhiteHouse/api-standards)
- [Exception Handling for REST API](https://medium.com/@sampathsl/exception-handling-for-rest-api-with-spring-boot-c5d5ba928f5b) 
- [Intro to Apache Kafka with Spring](https://www.baeldung.com/spring-kafka)
- [Spring Boot In Memory Caching](https://medium.com/tech-it-out/spring-boot-in-memory-caching-f2327f9bfdd6)
- [Patika.dev](https://www.patika.dev/tr)