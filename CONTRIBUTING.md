# Contributing to techbulls-secure-logging

Thank you for your interest in contributing! We welcome contributions from the community.

## How to Contribute

### Reporting Bugs

- Open a [GitHub issue](https://github.com/techbulls/techbulls-secure-logging/issues) with a clear description of the problem.
- Include steps to reproduce, expected behavior, and actual behavior.
- Include the library version and Java version you are using.

### Suggesting Features

- Open a [GitHub issue](https://github.com/techbulls/techbulls-secure-logging/issues) describing the feature and its use case.

### Submitting Changes

1. **Fork** the repository and create a branch from `main`.
2. **Make your changes** and ensure all existing tests pass:
   ```bash
   mvn clean test
   ```
3. **Add tests** for any new functionality.
4. **Commit** your changes with a clear, descriptive commit message.
5. **Open a Pull Request** against the `main` branch.

## Development Setup

### Prerequisites

- Java 11 or higher
- Maven 3.x

### Building

```bash
mvn clean package
```

### Running Tests

```bash
mvn test
```

## Code Guidelines

- Follow existing code style and conventions in the project.
- Keep changes focused — one feature or fix per pull request.
- Ensure backward compatibility.
- Add Javadoc for public API changes.

## Pull Request Process

1. Ensure your PR description clearly explains the change and links any relevant issues.
2. All tests must pass.
3. A maintainer will review your PR and may request changes.
4. Once approved, a maintainer will merge your contribution.

## License

By contributing, you agree that your contributions will be licensed under the [Apache License 2.0](LICENSE).
