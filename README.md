# Java Shopping Cart with Localization

## Features
- JavaFX GUI-based shopping cart (FXML + Controller)
- Console version (legacy support)
- Localization from database:
  - English
  - Finnish
  - Swedish
  - Japanese
- Dynamic UI language switching
- UTF-8 support
- Real-time cart calculation
- Persistent storage in MySQL/MariaDB
- Unit tests with JUnit 5
- Code coverage with JaCoCo
- Dockerized app
- Jenkins CI/CD pipeline
- Docker Hub deployment

---

## Project Overview

This application allows users to:

1. Select a language
2. Enter item prices and quantities via GUI
3. Automatically calculate item subtotals
4. Calculate total cart cost
5. Store cart data and items in a database

Localization messages are fetched dynamically from a database instead of `.properties` files.

The application now includes a **JavaFX graphical user interface**, while still supporting the original console-based version.

---

## Architecture Overview

The project follows a layered structure:

- **UI Layer**
  - `ShoppingCartFxApp` (JavaFX entry point)
  - `ShoppingCartController` (FXML controller)
  - `shopping-cart-view.fxml` (UI layout)

- **Business Logic**
  - `ShoppingCartCalculator`

- **Services**
  - `CartService`
  - `LocalizationService`

- **Data Models**
  - `CartItem`

- **Infrastructure**
  - `DatabaseConnection`

---

## Database Setup

### 1. Create Database and Tables

Run the provided SQL script:
