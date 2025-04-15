# Internship Applicant Processing System

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-25A162?style=for-the-badge&logo=junit5&logoColor=white)

## Overview ✨📊

The **Internship Applicant Processing System** is a robust Java application designed to process internship applicant data efficiently. The system handles the extraction, validation, and analysis of applicant data from CSV files, offering precise statistical outputs such as:

- 📌 Number of **unique applicants**  
- 🏆 Last names of the **top 3 applicants**  
- 📈 **Average score** of the top applicants before score adjustments  

The application implements a comprehensive pipeline for processing applicant data, applying business logic for score adjustments, and providing clear JSON-formatted output.

## Key Features 💡🚀

### Data Processing Pipeline 🧾

- **CSV Parsing**: Parses applicant data from CSV files with strict validation on:
  - 🧍 Applicant names (First, Middle, and Last)
  - 📧 Email format and uniqueness
  - 🕒 Date and time of submission (ISO 8601 format)
  - 📏 Score range validation (0 to 10)

- **Error Handling**: ❌ Automatic filtering of malformed or invalid data entries, ensuring only valid applicants are processed.

- **Memory Efficiency**: 💾 Stream-based processing to handle large datasets without excessive memory usage.

### Business Logic 🧠

- **Score Adjustments**:
  - 🎁 **Bonus**: +1 point for submissions made on the first day.
  - ⏳ **Malus**: -1 point for submissions made in the second half of the last day.

- **Top Applicants**: 🥇 Identifies the top applicants based on adjusted scores with a tie-breaking mechanism that considers:
  - Original score
  - Submission date
  - Email address

- **Statistical Calculations**:
  - 📊 Calculates the **unique applicants** based on email address.
  - 🥈 Extracts the **last names** of the top 3 applicants.
  - 📉 Computes the **average score** of the top half before any adjustments (rounded to two decimal places).

### Output Generation 🧾➡️🟰

- **JSON Format**: Outputs the results in a standardized JSON format with three key properties:
  - `uniqueApplicants`: 📌 Number of unique applicants
  - `topApplicants`: 🏆 Last names of the top 3 applicants
  - `averageScore`: 📈 Average score of the top half before adjustments

## Technical Highlights 🔧📐

### Testing 🧪✅

- **96%+ Test Coverage / 120 tests**: Thorough testing ensures reliability:
  - 🧩 Unit tests for business logic, CSV parsing, and date handling
  - 🔄 Integration tests for full pipeline validation
  - ⚠️ Edge case coverage (e.g., malformed data, tie-breaking logic)

### Code Quality 🧼💻

- **Clean Architecture**: 🔍 Proper separation of concerns, modular design, and clear documentation
- **Immutable Data Models**: 🛡️ Ensures data integrity
- **JavaDoc**: 📚 Comprehensive comments on all public components
- **Consistent Code Style**: 🧭 Follows Java best practices for maintainability

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/dosqas/Mini-Projects.git
   ```

2. Navigate to the project directory:
   ```bash
   cd Mini-Projects/Java/Internship-Applicant-Processor
   ```

3. Build the project with Maven:
    ```bash
    mvn clean install
    ```

4. Run the application:
    ```bash
    mvn exec:java "-Dinput-file=<input-file>"
    ```
    #### Example:
    ```bash
    mvn exec:java "-Dinput-file=input.csv"
    ```

### Example Usage

- #### Input CSV Format
  ```csv
  name,email,delivery_datetime,score
  Speranța Cruce,speranta_cruce@gmail.com,2023-01-24T20:14:53,2.33
  Ionică Sergiu Ramos,chiarel@ionicaromass.ro,2023-01-24T16:32:19,9.00
  Carla Ștefănescu,carlita_ste@yahoo.com,2023-01-23T23:59:01,5.20
  Lucrețiu Hambare,hambare_lucretiu@outlook.com,2023-01-24T22:30:15,10
  Robin Hoffman-Rus,robman@dasmail.de,2023-01-23T12:00:46,8.99
  ```

- #### Expected output
  ```json
  {
    "uniqueApplicants": 5,
    "topApplicants": [
      "Hoffman-Rus",
      "Hambare",
      "Ramos"
    ],
    "averageScore": 9.33
  }
  ```

#### Notes:

- Invalid rows (missing names, malformed email/date/score) are automatically filtered.
- Scores are adjusted:
  - +1 bonus for first-day submissions
  - -1 penalty for last-day late submissions
