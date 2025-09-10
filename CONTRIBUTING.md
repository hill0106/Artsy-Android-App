# Contributing to Artsy Android App

Thank you for your interest in contributing to the Artsy Android App! This document provides guidelines and information for contributors.

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Git
- Basic knowledge of Kotlin and Jetpack Compose

### Development Setup
1. Fork the repository
2. Clone your fork: `git clone https://github.com/yourusername/artsy-android-app.git`
3. Open the project in Android Studio
4. Sync the project with Gradle files
5. Run the app to ensure everything works

## 📋 How to Contribute

### Reporting Issues
- Use the GitHub issue tracker
- Provide detailed information about the problem
- Include steps to reproduce the issue
- Add screenshots if applicable

### Suggesting Features
- Open an issue with the "enhancement" label
- Describe the feature and its benefits
- Consider the impact on existing functionality

### Code Contributions
1. Create a feature branch: `git checkout -b feature/your-feature-name`
2. Make your changes
3. Write or update tests as needed
4. Ensure all tests pass
5. Commit your changes with clear messages
6. Push to your fork: `git push origin feature/your-feature-name`
7. Open a Pull Request

## 📝 Code Style Guidelines

### Kotlin Style
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused

### Compose Guidelines
- Use `@Composable` functions for UI components
- Follow Material Design principles
- Use proper state management
- Add preview functions for components

### Example Code Style
```kotlin
/**
 * Displays artist information in a card format.
 * 
 * @param artist The artist data to display
 * @param onArtistClick Callback when artist is clicked
 */
@Composable
fun ArtistCard(
    artist: Artist,
    onArtistClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onArtistClick(artist.id) }
    ) {
        // Card content
    }
}
```

## 🧪 Testing

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# All tests
./gradlew check
```

### Test Guidelines
- Write unit tests for business logic
- Add UI tests for critical user flows
- Maintain test coverage above 80%
- Use descriptive test names

## 📦 Pull Request Process

### Before Submitting
- [ ] Code follows the project's style guidelines
- [ ] Self-review of your code has been performed
- [ ] Tests have been added/updated and pass
- [ ] Documentation has been updated if needed
- [ ] No merge conflicts exist

### PR Description Template
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] UI tests pass
- [ ] Manual testing completed

## Screenshots (if applicable)
Add screenshots for UI changes

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
```

## 🏷️ Commit Message Format

Use conventional commit format:
```
type(scope): description

[optional body]

[optional footer]
```

### Types
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

### Examples
```
feat(ui): add artist search functionality
fix(api): resolve authentication timeout issue
docs(readme): update installation instructions
```

## 🐛 Bug Reports

When reporting bugs, please include:

1. **Environment**
   - Android version
   - Device model
   - App version

2. **Steps to Reproduce**
   - Clear, numbered steps
   - Expected vs actual behavior

3. **Additional Context**
   - Screenshots or videos
   - Logcat output
   - Related issues

## 💡 Feature Requests

For feature requests, please provide:

1. **Problem Description**
   - What problem does this solve?
   - Why is this feature needed?

2. **Proposed Solution**
   - How should it work?
   - Any design considerations?

3. **Alternatives Considered**
   - Other approaches you've thought about

## 📞 Getting Help

- **GitHub Discussions**: For questions and general discussion
- **Issues**: For bug reports and feature requests
- **Pull Requests**: For code contributions

## 🎯 Areas for Contribution

### High Priority
- [ ] Performance optimizations
- [ ] Accessibility improvements
- [ ] Test coverage expansion
- [ ] Documentation updates

### Medium Priority
- [ ] UI/UX enhancements
- [ ] New features
- [ ] Code refactoring
- [ ] Dependency updates

### Low Priority
- [ ] Code style improvements
- [ ] Minor bug fixes
- [ ] Documentation typos

## 📄 License

By contributing to this project, you agree that your contributions will be licensed under the MIT License.

## 🙏 Recognition

Contributors will be recognized in:
- README.md contributors section
- Release notes
- Project documentation

Thank you for contributing to the Artsy Android App! 🎨
