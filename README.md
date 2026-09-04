# AI-Powered Payment Intelligence & Recovery System

An intelligent payment operations platform for online merchants to analyze transaction risk, identify revenue recovery opportunities, and measure recovery performance.

## Overview

The platform connects three core areas:

**RISK → RECOVERY → PERFORMANCE**

- **Risk Intelligence** — transaction risk scoring, risk levels, signals and recommended actions.
- **Revenue Recovery** — recovery probability, expected recovery, recovery decisions and execution.
- **Recovery Performance** — recovery attempts, outcomes, recovery rate and recovered revenue.

## Technology Stack

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

### Intelligence / ML
- Dedicated ML service
- Transaction intelligence and analytical processing

### Data
- Transaction dataset
- CSV-based transaction data

## Project Structure

```text
merchant-intelligence-ai/
├── backend/
├── frontend/
├── ml-service/
├── datasets/
└── .gitignore
```

## Core Capabilities

### Risk Intelligence

The system provides transaction-level risk analysis using available transaction and behavioral signals.

Outputs include:
- Risk score
- Risk level
- Risk signals
- Recommended action
- Decision confidence

Risk levels:
- LOW
- MEDIUM
- HIGH

### Revenue Recovery

The recovery system evaluates failed transactions and determines whether recovery should be attempted.

It supports:
- Recovery probability
- Expected recovery value
- Recommended recovery action
- Recovery execution
- Recovery history
- Recovery opportunities
- Recovery performance analytics

### Recovery Performance

Recovery execution is tracked separately from the initial recommendation.

Key metrics:
- Total attempts
- Successful attempts
- Failed attempts
- Recovery rate
- Recovered revenue
- Recovery outcomes

## Recovery Decision Example

```json
{
  "transactionId": "TXN100083",
  "riskScore": 35,
  "riskLevel": "LOW",
  "recoveryProbability": 0.6,
  "expectedRecoveryValue": 13896.61,
  "recommendedAction": "RETRY",
  "decisionConfidence": "HIGH"
}
```

## Recovery Performance Example

```text
Total attempts      : 2
Successful attempts : 0
Failed attempts     : 2
Recovery rate       : 0%
Recovered revenue   : ₹0
```

This separation allows the platform to measure the difference between a recovery recommendation and its actual execution outcome.

## Backend Modules

- Transaction management
- Risk analytics
- Risk signals
- Failure analysis
- Revenue analytics
- Recovery probability
- Recovery scoring
- Recovery decisions
- Recovery opportunities
- Recovery execution
- Recovery history
- Recovery performance
- Merchant analytics
- Merchant segmentation
- Payment method analytics

## Frontend

The frontend provides the merchant-facing control center for payment intelligence.

The interface is designed around:
- Risk monitoring
- Revenue recovery
- Recovery performance
- Transaction intelligence
- Operational visibility
- Financial metrics

Frontend development is kept separate from backend implementation.

## Running the Project

### Backend

Windows:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

### Frontend

```powershell
cd frontend
npm install
npm run dev
```

## Development Principle

The backend is treated as the source of truth for existing APIs and business logic.

Frontend development does not modify:
- Backend controllers
- Backend services
- Repositories
- Models
- Database schema
- Authentication
- API contracts
- Backend configuration

## Current Status

Core transaction intelligence, recovery decision, recovery execution and recovery performance functionality has been implemented and tested.

Current focus:

**Frontend integration → End-to-end verification → Final audit**

## Project Goal

Provide merchants with a unified operational view of:

**RISK → RECOVERY → PERFORMANCE**

The system connects payment risk analysis with recovery decisions and measurable recovery outcomes.

## License

Developed for educational, research and portfolio purposes.

