# Contributing to Portfolio Animation System

Thank you for your interest in contributing! This document provides guidelines and instructions for contributing to the project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Making Changes](#making-changes)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Commit Messages](#commit-messages)
- [Documentation](#documentation)
- [Testing](#testing)
- [Performance Guidelines](#performance-guidelines)
- [Reporting Issues](#reporting-issues)

---

## Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inspiring community for all. Please read and abide by our Code of Conduct:

- Be respectful and inclusive
- Welcome differing opinions and experiences
- Focus on constructive criticism
- Respect privacy and confidentiality
- Report unacceptable behavior to maintainers

---

## Getting Started

### Prerequisites

- Node.js 14.0 or higher
- Git
- Code editor (VS Code recommended)
- Modern browser (Chrome, Firefox, Safari, or Edge)

### Fork & Clone

```bash
# 1. Fork the repository on GitHub
# 2. Clone your fork locally
git clone https://github.com/yourusername/portfolio-animation-system.git

# 3. Add upstream remote
git remote add upstream https://github.com/original-owner/portfolio-animation-system.git

# 4. Create a branch for your work
git checkout -b feature/your-feature-name
```

---

## Development Setup

### Install Dependencies

```bash
npm install
```

### Development Commands

```bash
npm run dev        # Start development server
npm test           # Run tests
npm run lint       # Check code style
npm run build      # Build for production
```

---

## Making Changes

### Workflow

1. Create a feature branch from `main`
2. Make focused, clear commits
3. Keep your branch updated with main
4. Test thoroughly before submitting
5. Push to your fork and create a Pull Request

### Branch Naming

```
feature/description     - New feature
fix/description        - Bug fix
perf/description       - Performance improvement
docs/description       - Documentation
test/description       - Tests
```

---

## Pull Request Process

### Before Submitting

- [ ] Code follows style guide
- [ ] All tests pass: `npm test`
- [ ] No linting errors: `npm run lint`
- [ ] Documentation is updated
- [ ] Changes tested on mobile and desktop
- [ ] Branch is up-to-date with main

### Creating a PR

Use clear, descriptive title and description:

```markdown
## Description
Brief description of changes

## Related Issues
Closes #123

## Changes Made
- Change 1
- Change 2

## Testing
How to test the changes
```

---

## Coding Standards

### JavaScript

```javascript
// ✅ GOOD: Clear, documented code with physics explanation
// Physics: Spring pulls toward target position
// Duration determines stiffness (lower = tighter)
function smoothFollower() {
    const xTo = gsap.quickTo(circle, "x", {
        duration: 0.9,    // Spring stiffness
        ease: "power4"    // Damping
    });
    // Implementation...
}

// ❌ AVOID: Unclear, hard to read
function smoothFollower(){const xT=gsap.quickTo(c,"x",{duration:0.9,ease:"power4"});}
```

### Naming Conventions

```javascript
const MAX_VELOCITY = 100;      // Constants: UPPER_SNAKE_CASE
let currentVelocity = 0;       // Variables: camelCase
function calculateVelocity() {} // Functions: camelCase
class AnimationController {}    // Classes: PascalCase
function _privateHelper() {}    // Private: leading underscore
```

### Comments

Always explain **WHY**, not **WHAT**:

```javascript
// ✅ GOOD: Explains physics and reasoning
// Physics: Clamp prevents extreme scale values
// Range: 0.8 (compression) to 1.2 (stretch)
xscale = gsap.utils.clamp(0.8, 1.2, velocity);

// ❌ AVOID: Obvious statements
// Set xscale to clamped velocity
xscale = gsap.utils.clamp(0.8, 1.2, velocity);
```

### CSS

```css
/* ✅ GOOD: Organized with clear sections */
/* =============================== */
/* CURSOR STYLES                   */
/* =============================== */

#minicircle {
    position: fixed;
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background-color: #fff;
    will-change: transform;
    pointer-events: none;
    z-index: 50;
}
```

---

## Commit Messages

### Format

```
<type>(<scope>): <subject>

<body>
```

### Types

- `feat`: New feature
- `fix`: Bug fix
- `perf`: Performance improvement
- `docs`: Documentation
- `test`: Tests
- `refactor`: Code restructure

### Examples

```bash
feat(cursor): add spring physics to smooth follower
fix(scroll): prevent layout thrashing
docs(physics): add detailed spring dynamics explanation
perf(scroll): reduce paint operations

# Good: Small, focused commits
git commit -m "feat(cursor): add spring physics"
git commit -m "test(cursor): add spring physics tests"

# Bad: Too vague
git commit -m "update stuff"
```

---

## Documentation

### Update Documentation When

- ✅ Adding new features
- ✅ Changing behavior
- ✅ Adding examples
- ✅ Fixing bugs
- ✅ Improving performance

### Format

```markdown
## Function Name

### Description
What does it do?

### Signature
```javascript
function name(param1, param2) {}
```

### Parameters
| Name | Type | Default | Description |
|------|------|---------|-------------|

### Example
```javascript
// Working code example
```

### Physics
Mathematical explanation if applicable
```

---

## Testing

### Running Tests

```bash
npm test              # Run all tests
npm test -- file.js  # Run specific test
npm test -- --watch  # Watch mode
```

### Writing Tests

```javascript
describe("smoothFollower", () => {
    it("should move cursor toward target", () => {
        // Test implementation
        expect(result).toBe(expected);
    });
});
```

### Coverage Goals

- Statements: > 80%
- Branches: > 75%
- Functions: > 80%
- Lines: > 80%

---

## Performance Guidelines

### Requirements

- Maintain **60 FPS** on desktop
- Maintain **30+ FPS** on mobile
- Keep **paint time < 16ms**
- Avoid **layout thrashing**
- **Memory usage < 5MB** additional

### Checklist

```
☐ No console errors/warnings
☐ Consistent 60 FPS (Chrome DevTools)
☐ Memory stable (DevTools → Memory)
☐ Works on mobile device
☐ No layout shift during animations
☐ Accessibility features work
```

---

## Reporting Issues

### Bug Report Template

```markdown
### Description
Clear description of the issue

### Steps to Reproduce
1. Step 1
2. Step 2

### Expected Behavior
What should happen

### Actual Behavior
What actually happens

### Environment
- Browser: Chrome 120
- OS: Windows 11
- Device: Desktop
```

### Feature Request Template

```markdown
### Feature Description
What feature do you want?

### Use Case
Why do you need this?

### Proposed Solution
How should it work?
```

---

## Questions?

- Check [README.md](./README.md)
- Check [documentation](./docs)
- Create a GitHub issue
- Email: your.email@example.com

---

Thank you for contributing! 🎉

**Last Updated**: January 2024
