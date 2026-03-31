# Java Shopping Cart with Localization

## Features
- Console-based shopping cart
- Localization from database:
  - English
  - Finnish
  - Swedish
  - Japanese
- UTF-8 support
- Unit tests with JUnit 5
- Code coverage with JaCoCo
- MySQL/MariaDB database integration
- Dockerized app
- Jenkins CI/CD pipeline
- Docker Hub deployment

---

## Project Overview

This application allows users to:
1. Select a language
2. Enter item prices and quantities
3. Calculate total costs
4. Store results in a database

Localization messages are fetched dynamically from a database instead of `.properties` files.

---

## Database Setup

### 1. Create Database and Tables

Run the provided SQL script
