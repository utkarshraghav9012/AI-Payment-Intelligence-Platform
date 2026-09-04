# AI-Powered Payment Intelligence Platform

A merchant-focused payment intelligence platform built with **React, Spring Boot, REST APIs, and transaction analytics** to help businesses understand payment performance, identify transaction risk, discover revenue recovery opportunities, and measure recovery outcomes.

The platform connects three core areas:

**RISK → RECOVERY → PERFORMANCE**

---

## 🚀 Overview

Payment failures can directly impact merchant revenue. However, simply identifying failed transactions is not enough.

This platform provides a unified operational dashboard that analyzes transaction data and helps merchants:

- Monitor payment and revenue performance
- Analyze successful and failed transactions
- Identify transaction risk
- Understand risk signals
- Find potential recovery opportunities
- Generate recovery recommendations
- Track recovery execution
- Measure recovery performance
- Analyze payment methods and merchant behaviour

The system is designed as a portfolio-level **payment intelligence and recovery platform** with a React frontend and Spring Boot backend.

---

## ✨ Key Features

### 1. Payment & Revenue Intelligence

Provides an operational view of transaction and revenue performance.

Capabilities include:

- Total transaction value
- Successful transactions
- Failed transactions
- Revenue analytics
- Failure analysis
- Payment-method analytics
- Peak sales analysis
- Time-based analytics
- Merchant analytics

---

### 2. Risk Intelligence

The platform performs transaction-level risk analysis using transaction and behavioural signals.

For a transaction, the system can provide:

- Risk score
- Risk level
- Risk signals
- Recommended action
- Decision confidence

Supported risk levels:

```text
LOW
MEDIUM
HIGH
```

The current implementation uses a **rule-based behavioural scoring approach** rather than a trained machine-learning prediction model.

---

### 3. Revenue Recovery Intelligence

Failed payments can represent potential recoverable revenue.

The recovery module evaluates failed transactions and provides:

- Recovery probability
- Expected recovery value
- Recovery score
- Recommended recovery action
- Recovery decisions
- Recovery opportunities
- Recovery execution
- Recovery history

Possible recovery actions include:

```text
RETRY
RETRY_WITH_ALTERNATIVE
CUSTOMER_REMINDER
MANUAL_REVIEW
```

---

### 4. Recovery Performance

The platform separates recovery recommendations from actual recovery outcomes.

Recovery performance includes:

- Total recovery attempts
- Successful attempts
- Failed attempts
- Recovery rate
- Recovered revenue
- Recovery outcomes
- Recovery performance analytics

The workflow is:

```text
Recovery Opportunity
        ↓
Recovery Decision
        ↓
Recovery Execution
        ↓
Recovery Outcome
        ↓
Performance Analysis
```

This makes it possible to evaluate whether recommended recovery actions actually produce successful outcomes.

---

### 5. Transaction Intelligence

The backend supports transaction processing through REST APIs and CSV-based transaction data.

The general data flow is:

```text
Transaction Data
       ↓
Transaction Processing
       ↓
Analytics
       ↓
Risk Analysis
       ↓
Recovery Intelligence
       ↓
Merchant Dashboard
```

A sample transaction dataset is included in:

```text
datasets/transactions/transactions.csv
```

---

### 6. Merchant Dashboard

The React application provides a merchant-facing dashboard for payment intelligence and operational analysis.

The frontend includes:

- Payment analytics
- Risk monitoring
- Revenue recovery
- Recovery performance
- Transaction intelligence
- Financial metrics
- Merchant insights
- Authentication interface
- Settings
- Notification preferences
- AI preference controls
- Light/Dark appearance

---

## 🏗️ System Architecture

```text
                    ┌──────────────────────┐
                    │      Merchant        │
                    │        User          │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │    React Frontend    │
                    │        + Vite        │
                    └──────────┬───────────┘
                               │
                            REST API
                               │
                               ▼
              ┌─────────────────────────────────┐
              │       Spring Boot Backend       │
              ├─────────────────────────────────┤
              │ Transaction Management           │
              │ Payment Analytics                │
              │ Risk Intelligence                │
              │ Failure Analysis                 │
              │ Revenue Analytics                │
              │ Recovery Intelligence             │
              │ Recovery Decisions               │
              │ Recovery Execution               │
              │ Recovery History                 │
              │ Recovery Performance              │
              │ Merchant Analytics               │
              └───────────────┬─────────────────┘
                              │
                ┌─────────────┴──────────────┐
                ▼                            ▼
       ┌──────────────────┐        ┌──────────────────┐
       │ Transaction      │        │ Recovery &       │
       │ Dataset / Input  │        │ Performance Data │
       └──────────────────┘        └──────────────────┘
```

---

## 🔄 End-to-End Workflow

```text
Transaction Input
       ↓
Transaction Processing
       ↓
Payment & Revenue Analytics
       ↓
Risk Scoring
       ↓
Risk Signals
       ↓
Failed Payment Analysis
       ↓
Recovery Opportunity Detection
       ↓
Recovery Decision
       ↓
Recovery Execution
       ↓
Recovery History
       ↓
Recovery Performance
```

---

## 🧠 Intelligence Workflow

The platform combines transaction analytics with rule-based intelligence.

```text
Transaction Behaviour
        +
Transaction Characteristics
        ↓
   Risk Evaluation
        ↓
 Risk Score / Level
        ↓
Recommended Action
        ↓
Recovery Evaluation
        ↓
Expected Recovery Value
```

The current project does **not** claim to use a trained ML model for risk prediction.

The `ml-service` directory currently contains a Python utility for generating transaction data.

---

## 🛠️ Technology Stack

### Frontend

- React
- Vite
- JavaScript
- CSS
- Lucide React

### Backend

- Java
- Spring Boot
- Maven
- REST APIs

### Data & Processing

- CSV transaction dataset
- Java transaction processing
- Python transaction-data generation utility

### Development Tools

- Git
- GitHub
- npm
- Maven

---

## 📁 Project Structure

```text
AI-Payment-Intelligence-Platform/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
│
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   ├── dark-mode.css
│   │   ├── index.css
│   │   └── main.jsx
│   ├── package.json
│   ├── package-lock.json
│   └── vite.config.js
│
├── datasets/
│   └── transactions/
│       └── transactions.csv
│
├── ml-service/
│   └── generate_transactions.py
│
├── .gitignore
└── README.md
```

---

## 🔌 Backend Modules

The Spring Boot backend contains separate modules for different areas of payment intelligence.

### Transaction

- Transaction management
- Transaction ingestion
- CSV transaction processing

### Analytics

- Business intelligence summary
- Revenue analytics
- Failure reason analytics
- Payment-method analytics
- Peak sales analytics
- Time analytics
- Merchant analytics
- Merchant segmentation

### Risk

- Risk analytics
- Risk signals
- Risk scoring

### Recovery

- Recovery probability
- Recovery scoring
- Recovery decisions
- Recovery opportunities
- Recovery execution
- Recovery history
- Recovery performance

---

## 📊 Example Decision Flow

For a failed transaction, the system can evaluate:

```text
Transaction
     ↓
Risk Score
     ↓
Risk Level
     ↓
Behavioural Signals
     ↓
Recovery Probability
     ↓
Expected Recovery Value
     ↓
Recommended Recovery Action
     ↓
Decision Confidence
```

The actual recommendation depends on the transaction data and implemented scoring rules.

---

## 🔐 Security & Configuration

The project uses `.gitignore` rules to prevent common development and environment files from being committed.

Ignored files include:

```text
node_modules/
target/
dist/
.env
*.log
.idea/
.vscode/
```

Sensitive production configuration should be supplied through environment-specific configuration and should not be committed to the repository.

---

## 💻 Local Setup

### Prerequisites

Install:

- Java
- Maven
- Node.js
- npm
- PostgreSQL

---

### 1. Clone the Repository

```bash
git clone https://github.com/utkarshraghav9012/AI-Payment-Intelligence-Platform.git
```

```bash
cd AI-Payment-Intelligence-Platform
```

---

### 2. Start the Backend

Windows:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The backend is configured to run on:

```text
http://localhost:8080
```

---

### 3. Start the Frontend

Open another terminal:

```powershell
cd frontend
npm install
npm run dev
```

The frontend runs on:

```text
http://localhost:5173
```

---

## 📦 Production Frontend Build

To create the production build:

```powershell
cd frontend
npm run build
```

The Vite production build generates the `dist` directory.

The generated build directory is intentionally excluded from Git through `.gitignore`.

---

## 🔗 API Architecture

The backend exposes REST endpoints for the platform's major functional areas.

These include APIs for:

```text
Transactions
Business Intelligence
Revenue Analytics
Failure Analysis
Payment Methods
Time Analytics
Peak Sales
Risk Analytics
Risk Signals
Recovery Probability
Recovery Scoring
Recovery Decisions
Recovery Opportunities
Recovery Execution
Recovery History
Recovery Performance
Merchant Analytics
Merchant Segmentation
```

The React frontend consumes these backend APIs to display merchant-facing intelligence.

---

## 📈 Recovery Intelligence Model

The recovery workflow is designed around expected recovery value.

Conceptually:

```text
Failed Transaction
        ↓
Recovery Probability
        ↓
Expected Recovery Value
        ↓
Recommended Action
```

This allows the platform to prioritize failed transactions based on their potential recovery value rather than treating every failure equally.

---

## ⚙️ Recovery Execution

Recovery execution is implemented as an application workflow rather than a direct integration with a live payment gateway.

The system separates:

```text
Recommendation
      ↓
Decision
      ↓
Execution
      ↓
Outcome
```

This separation allows recovery recommendations and actual recovery results to be measured independently.

---

## 🧪 Testing & Verification

The project has been tested locally across its major frontend and backend workflows.

Verified areas include:

- Frontend production build
- Payment and revenue intelligence
- Risk intelligence
- Risk filters and transaction analysis
- Revenue recovery intelligence
- Recovery decisions
- Recovery opportunity calculations
- Recovery execution workflow
- Recovery performance
- Settings interface
- Authentication interface
- Dark mode

Frontend production build:

```text
Vite build: PASS
```

---

## ⚠️ Current Limitations

The current portfolio version has several limitations:

1. Risk intelligence is rule-based and is not a trained ML prediction model.
2. Recovery execution is an application-level workflow and is not connected to a real payment gateway.
3. The current authentication interface is primarily frontend/demo-level authentication.
4. Production deployment requires environment-specific backend and database configuration.
5. External notification channels such as SMS, email, or WhatsApp are not directly integrated.

---

## 🚀 Future Scope

Possible future improvements include:

- Real payment gateway integration
- Production-grade authentication and authorization
- Role-based access control
- Real-time transaction streaming
- Trained ML risk prediction
- Advanced anomaly detection
- Merchant-specific risk models
- Automated recovery campaigns
- Email/SMS/WhatsApp recovery workflows
- Real-time alerts
- Revenue forecasting
- Advanced merchant segmentation
- Cloud deployment
- Monitoring and observability

---

## 🎯 Project Objective

The primary objective is to build a unified payment intelligence system that helps merchants move from simply observing failed payments to understanding:

```text
What happened?
     ↓
Why did it happen?
     ↓
What is the risk?
     ↓
Can the revenue be recovered?
     ↓
What action should be taken?
     ↓
Did the recovery work?
```

This creates a complete operational loop:

**RISK → RECOVERY → PERFORMANCE**

---

## 📌 Portfolio Highlights

This project demonstrates practical experience with:

- Java
- Spring Boot
- REST API development
- React
- Vite
- JavaScript
- API integration
- Transaction analytics
- Rule-based risk scoring
- Revenue recovery logic
- Backend service architecture
- Data processing
- Git/GitHub
- Production frontend builds

---

## 👨‍💻 Project

**AI-Powered Payment Intelligence Platform**

Built as an educational and portfolio project demonstrating full-stack development and payment intelligence concepts.

---

## 📄 License

Developed for educational, research, and portfolio purposes.