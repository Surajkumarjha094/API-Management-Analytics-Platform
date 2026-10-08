\# Gatekeeper – API Management \& Analytics Platform



Gatekeeper is a centralized API Management and Analytics Platform built with Java, Spring Boot, React, MySQL, and MongoDB.



It provides a centralized layer between API consumers and backend APIs for authentication, route management, gateway control, traffic policies, and API usage analytics.



\## 🚀 Key Features



\* JWT-based authentication and authorization

\* Multi-tenant organization support

\* Tenant-level data isolation

\* API route management

\* Route activation/deactivation

\* Request rate limiting

\* Response timeout configuration

\* Idempotency configuration

\* Request header rules

\* API configuration versioning

\* Audit logging

\* Smart API Gateway

\* API request analytics

\* React-based management dashboard

\* MySQL for management data

\* MongoDB for analytics events



\## 🏗️ Architecture



```text

\&#x20;                   ┌─────────────────────┐

\&#x20;                   │     React Frontend  │

\&#x20;                   │      Port: 5173     │

\&#x20;                   └──────────┬──────────┘

\&#x20;                              │

\&#x20;                              ▼

\&#x20;                   ┌─────────────────────┐

\&#x20;                   │ API Management      │

\&#x20;                   │ Service             │

\&#x20;                   │ Port: 8081          │

\&#x20;                   └──────────┬──────────┘

\&#x20;                              │

\&#x20;                   Route \\\& Policy Config

\&#x20;                              │

\&#x20;                              ▼

\&#x20;                   ┌─────────────────────┐

\&#x20;                   │   Smart Gateway     │

\&#x20;                   │      Port: 8080     │

\&#x20;                   └──────────┬──────────┘

\&#x20;                              │

\&#x20;                        JWT Validation

\&#x20;                        Route Matching

\&#x20;                        Policy Checks

\&#x20;                              │

\&#x20;                              ▼

\&#x20;                   ┌─────────────────────┐

\&#x20;                   │    Target API       │

\&#x20;                   └─────────────────────┘

\&#x20;                              │

\&#x20;                              ▼

\&#x20;                   ┌─────────────────────┐

\&#x20;                   │ Analytics Service   │

\&#x20;                   │      Port: 8082     │

\&#x20;                   │      MongoDB        │

\&#x20;                   └─────────────────────┘

```



\## 🧩 Services



\### 1. API Management Service



\*\*Port:\*\* `8081`



Responsible for managing API configurations and organization-level data.



Main responsibilities:



\* Organization management

\* Tenant admin management

\* API route CRUD operations

\* Header rule management

\* Configuration versions

\* Audit records

\* Authentication

\* Tenant isolation



\### 2. Smart Gateway Service



\*\*Port:\*\* `8080`



Acts as the gateway between API consumers and target APIs.



The gateway:



1\. Receives an API request

2\. Validates the JWT token

3\. Identifies the configured API route

4\. Applies gateway policies

5\. Applies configured request header rules

6\. Forwards the request to the target API

7\. Records the API request in analytics

8\. Returns the target API response



\### 3. Analytics Service



\*\*Port:\*\* `8082`



Stores API access events in MongoDB.



The analytics layer tracks:



\* Total requests

\* Successful requests

\* Failed requests

\* HTTP status codes

\* Response time

\* API request path

\* Request timestamps



\## 🔐 Security



Gatekeeper uses JWT-based authentication.



The authentication flow is:



```text

User

\&#x20;│

\&#x20;▼

Login

\&#x20;│

\&#x20;▼

JWT Token

\&#x20;│

\&#x20;▼

API Request

\&#x20;│

\&#x20;▼

JWT Validation

\&#x20;│

\&#x20;▼

Role \\\& Organization Validation

\&#x20;│

\&#x20;▼

Protected Resource

```



The platform also implements tenant isolation so that users belonging to one organization cannot access another organization's protected data.



\## 🛣️ API Route Management



Administrators can configure API routes with properties such as:



\* Route ID

\* Route name

\* Path pattern

\* Target base URL

\* Target path prefix

\* Requests per minute

\* Response timeout

\* Idempotency

\* Active/inactive status



Example:



```text

Client Request

GET /gateway/products/1



\&#x20;       ↓



Route Configuration



Route ID: products-api

Path: /products/\\\*\\\*

Target: https://jsonplaceholder.typicode.com/posts



\&#x20;       ↓



Smart Gateway



\&#x20;       ↓



Target API

```



\## 📋 Header Rules



Gatekeeper supports configurable request header rules.



Example:



```text

Direction: REQUEST

Action: ADD

Header: X-Test-Header

Value: Gatekeeper-Test

```



These rules can be configured for individual API routes.



\## 📊 Analytics



Every gateway request can generate an analytics event.



Example analytics information:



```text

HTTP Method: GET

Gateway Path: /gateway/products/1

Status Code: 200

Response Time: 330 ms

Timestamp: <request timestamp>

```



The React dashboard provides a summary of API traffic and recent request events.



\## 📝 Audit Logging



Important management operations are recorded as audit events.



Examples:



```text

CREATE\\\_ROUTE

UPDATE\\\_ROUTE

DELETE\\\_ROUTE

```



Audit records help track configuration changes made to the platform.



\## 🗄️ Database



\### MySQL



Used by the API Management Service for structured management data such as:



\* Organizations

\* Users

\* API routes

\* Header rules

\* Configuration versions

\* Audit records



Database migrations are managed using \*\*Flyway\*\*.



\### MongoDB



Used by the Analytics Service for API access events and traffic analytics.



\## 🛠️ Technology Stack



| Layer               | Technology  |

| ------------------- | ----------- |

| Backend             | Java        |

| Framework           | Spring Boot |

| Frontend            | React       |

| Build Tool          | Maven       |

| Relational Database | MySQL       |

| Analytics Database  | MongoDB     |

| Database Migration  | Flyway      |

| Authentication      | JWT         |

| API Communication   | REST        |

| Frontend Build      | Vite        |



\## 📁 Project Structure



```text

Gatekeeper-Recovered/

│

├── api-management-service/

│   └── src/

│

├── smart-gateway-service/

│   └── src/

│

├── analytics-service/

│   └── src/

│

├── frontend/

│   └── src/

│

├── .gitignore

└── README.md

```



\## ⚙️ Local Setup



\### Prerequisites



Make sure the following are installed:



\* Java 21+

\* Maven 3.9+

\* Node.js 20+

\* MySQL 8+

\* MongoDB



\### 1. Clone the repository



```bash

git clone https://github.com/Surajkumarjha094/API-Management-Analytics-Platform.git

cd API-Management-Analytics-Platform

```



\### 2. Configure environment variables



The backend services use environment variables for sensitive configuration.



Example:



```text

DB\\\_URL=jdbc:mysql://localhost:3306/gatekeeper\\\_management

DB\\\_USERNAME=root

DB\\\_PASSWORD=your\\\_mysql\\\_password

JWT\\\_SECRET=your\\\_jwt\\\_secret

```



Do not commit passwords, JWT secrets, or other credentials to the repository.



\### 3. Start API Management Service



```cmd

cd api-management-service

mvn spring-boot:run

```



Runs on:



```text

http://localhost:8081

```



\### 4. Start Smart Gateway



Open another terminal:



```cmd

cd smart-gateway-service

mvn spring-boot:run

```



Runs on:



```text

http://localhost:8080

```



\### 5. Start Analytics Service



Open another terminal:



```cmd

cd analytics-service

mvn spring-boot:run

```



Runs on:



```text

http://localhost:8082

```



\### 6. Start React Frontend



Open another terminal:



```cmd

cd frontend

npm install

npm run dev

```



Runs on:



```text

http://localhost:5173

```



\## 🔄 End-to-End Request Flow



```text

React / API Client

\&#x20;       │

\&#x20;       ▼

\&#x20;    Login

\&#x20;       │

\&#x20;       ▼

\&#x20;  JWT Token

\&#x20;       │

\&#x20;       ▼

\&#x20;Smart Gateway

\&#x20;       │

\&#x20;       ├── Validate JWT

\&#x20;       │

\&#x20;       ├── Check Route

\&#x20;       │

\&#x20;       ├── Apply Policies

\&#x20;       │

\&#x20;       └── Apply Header Rules

\&#x20;       │

\&#x20;       ▼

\&#x20;   Target API

\&#x20;       │

\&#x20;       ▼

\&#x20; API Response

\&#x20;       │

\&#x20;       ├──────────────► Client

\&#x20;       │

\&#x20;       ▼

\&#x20;Analytics Service

\&#x20;       │

\&#x20;       ▼

\&#x20;   MongoDB

```



\## 🧪 Testing



The project has been tested for:



\* Authentication

\* JWT authorization

\* Tenant isolation

\* Organization creation

\* Tenant admin creation

\* Route creation

\* Route update

\* Route deletion

\* Header rule creation

\* Header rule deletion

\* Gateway authentication

\* Gateway request forwarding

\* Analytics event recording

\* Analytics summary

\* Audit log recording

\* React frontend integration



Example gateway request:



```bash

curl -i http://localhost:8080/gateway/products/1 \\\\

\&#x20; -H "Authorization: Bearer <JWT\\\_TOKEN>"

```



\## 🎯 Project Objective



The objective of Gatekeeper is to provide a centralized platform for managing APIs instead of allowing API consumers to interact directly with backend services.



It combines:



\* API Management

\* Authentication

\* Gateway Routing

\* Traffic Policies

\* Header Rules

\* Audit Logging

\* Usage Analytics



into a single platform.



\## 🔮 Future Improvements



Possible future improvements include:



\* Distributed rate limiting using Redis

\* Event streaming using Kafka

\* Dynamic gateway route discovery

\* API key authentication

\* OAuth2 support

\* Advanced API monitoring

\* Centralized logging

\* Docker and Kubernetes deployment

\* Cloud-based deployment

\* More advanced analytics dashboards



\## 👨‍💻 Author



\*\*Suraj Kumar Jha\*\*



B.Tech CSE – Data Science



GitHub:

https://github.com/Surajkumarjha094



\---



⭐ If you find this project useful, consider giving the repository a star.



