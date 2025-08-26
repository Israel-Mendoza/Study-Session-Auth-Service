# Study Session Auth Service

A Kotlin-based authentication service built with Ktor framework that provides JWT token-based authentication.

## Features

- User registration and login
- JWT token generation and validation
- Protected routes with authentication middleware
- RESTful API endpoints

## Tech Stack

- **Kotlin** - Primary programming language
- **Ktor** - Web framework
- **Gradle** - Build tool
- **JWT** - Authentication tokens

## Getting Started

### Prerequisites

- JDK 17 or higher
- Gradle 7.0+

### Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd study-session-auth-service
   ```

2. In the application.yaml file, set your environment configuration:

    ```yaml
    jwt:
        realm: "Study Session Auth App"
        domain: "https://jwt-provider-domain/"
        secret: "<here-goes-your-secret-key>"
        issuer: "<here-goes-your-issuer>"
        audience: "<here-goes-your-audience>"
    ```
3. Or you can set up environment variables:

    ```text
    export JWT_SECRET="your-secret-key-here"
    export JWT_ISSUER="study-session-auth-service"
    export JWT_AUDIENCE="study-session-app"
    ```
4. Build the project:

   ```bash
   ./gradlew build
   ```
5. Run the application:

   ```bash
   ./gradlew run
   ```

## API Endpoints

### Authentication Routes

- `POST /register` - Register a new user
- `POST /login` - Login and receive JWT token

### Protected Routes

- `GET /protected` - Access protected resource (requires JWT token)

## Configuration

Update src/main/resources/application.yaml:

```yaml
jwt:
secret: ${JWT_SECRET}
issuer: ${JWT_ISSUER}
audience: ${JWT_AUDIENCE}
```

## Security

Ensure to keep your JWT secret key secure and do not expose it in public repositories.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing
Contributions are welcome! Please open an issue or submit a pull request for any improvements or bug fixes.

## Contact
For any questions or support, please contact [Israel Mendoza](mailto:israel.mendoza9@icloud.com).