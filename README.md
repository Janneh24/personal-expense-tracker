# Personal Expense Tracker

[![Java CI with Maven](https://github.com/Janneh24/personal-expense-tracker/actions/workflows/maven.yml/badge.svg)](https://github.com/Janneh24/personal-expense-tracker/actions/workflows/maven.yml)
[![Coverage Status](https://coveralls.io/repos/github/Janneh24/personal-expense-tracker/badge.svg?branch=main)](https://coveralls.io/github/Janneh24/personal-expense-tracker?branch=main)
[![Build Status](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_personal-expense-tracker&metric=alert_status)](https://sonarcloud.io/dashboard?id=Janneh24_personal-expense-tracker)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_personal-expense-tracker&metric=coverage)](https://sonarcloud.io/dashboard?id=Janneh24_personal-expense-tracker)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=Janneh24_personal-expense-tracker&metric=sqale_index)](https://sonarcloud.io/dashboard?id=Janneh24_personal-expense-tracker)

A simple Java application to manage personal expenses, developed as a project for the Automated Software Testing course at UNIFI.

## Features
- User registration and login (secured with jBCrypt).
- Category management.
- Expense tracking with descriptions, dates, and amounts.
- Monthly report generation and PDF export.
- Graphical User Interface (Swing).

## Technical Stack
- **Java 17**
- **Maven** for build automation.
- **Hibernate / JPA** with **MySQL** (using Testcontainers for integration testing).
- **Google Guice** for Dependency Injection.
- **JUnit 5**, **Mockito**, and **AssertJ Swing** for unit and integration testing.
- **Cucumber** for BDD / End-to-End testing.
- **JaCoCo** for code coverage (100% requirement).
- **PITest** for mutation testing.
- **SonarCloud** for code quality analysis.

## How to Build
To build the project and run all tests (including integration and E2E):
```bash
mvn clean verify
```

## How to Run
To run the application locally:
```bash
mvn exec:java
```

## CI/CD
The project uses GitHub Actions for Continuous Integration. On every push to `main` or `master`, the workflow:
1. Builds the project.
2. Runs all tests.
3. Performs mutation testing.
4. Generates Jacoco reports.
5. Uploads coverage to Coveralls.
6. Performs SonarCloud analysis.
