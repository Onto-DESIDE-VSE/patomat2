# PatOMat2

PatOMat2 is a tool for automatic processing of ontologies. It is an evolution of the
original [PatOMat](https://patomat.vse.cz/).

## Development

Requirements:

- Java 21 or later
- Apache Maven 3.x
- Node 18 or later

The application is split into `backend` written in Java and using Spring Boot and `frontend` written in TypeScript,
using Vue.js and built by Vite.

The backend can be started by going into the `backend` directory and calling `mvn spring-boot:run`. It starts a web
application
listening on port 8080.

The frontend can be started by going into the `frontend` directory and calling `npm run dev`. This starts the `Vite`
development
server on port 5173.

Cross-Origin Resource Sharing (CORS) is used to ensure frontend can communicate with backend. It is pre-configured to
the
frontend default URL (http://localhost:5173). If you need to change it, do so
in `backend/src/main/resources/application.yml`.

## Deployment

The application can be deployed using Docker Compose. The following parameters can be configured:

| Parameter    | Default value           | Description                                                                            |
|:-------------|:------------------------|:---------------------------------------------------------------------------------------|
| `ROOT`       | `/patomat2`             | Context path at which the application should be available.                             |
| `HOST_PORT`  | `1234`                  | Port at which the application should be available to the host system.                  |
| `PUBLIC_URL` | `http://localhost:1234` | **Public** URL at which the application is running, without the context path (`ROOT`). |

The easiest way to configure them is by creating a `.env` file and setting them there. This file will be automatically
picked
by Docker compose.

Run the application by calling `docker compose up -d`.

