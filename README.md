🚚 Logistics and Shipment Tracking Platform

A full-stack web application for managing and tracking shipments throughout their delivery lifecycle. The platform allows users to create shipments, view and search shipment information, filter shipments by status, update shipment status, and view shipment history through an interactive dashboard.

📌 Features

* Create and manage shipments
* View all shipments with pagination
* Search shipments by tracking number
* Retrieve shipments by customer
* Filter shipments by status
* Update shipment status
* View shipment details and status history
* Dashboard with shipment statistics
* Automatic creation and update timestamps
* Input validation and error handling
* PostgreSQL database persistence
* Docker-based database setup

Shipment Statuses

`CREATED` · `PICKED_UP` · `IN_TRANSIT` · `OUT_FOR_DELIVERY` · `DELIVERED` · `CANCELLED`

🛠️ Tech Stack

**Frontend**

* Python
* Streamlit
* Requests

**Backend**

* Java 21
* Spring Boot 4
* Spring Web
* Spring Data JPA
* Hibernate
* Bean Validation
* Flyway
* Spring Boot Actuator

**Database & Infrastructure**

* PostgreSQL 17
* Docker
* Docker Compose
* Maven
* Git/GitHub

🏗️ Architecture

The application follows a layered architecture:

**Streamlit Frontend → Python API Client → Spring Boot REST API → JPA/Hibernate → PostgreSQL**

The frontend communicates with the backend through REST APIs. The Spring Boot service handles business logic and database operations, while PostgreSQL provides persistent storage.

 🔌 REST APIs

Base URL:

```text
http://localhost:8081/api/v1/shipments
```

| Method | Endpoint                     | Description                             |
| ------ | ---------------------------- | --------------------------------------- |
| POST   | `/`                          | Create a shipment                       |
| GET    | `/`                          | Get shipments with pagination/filtering |
| GET    | `/{id}`                      | Get shipment by ID                      |
| GET    | `/tracking/{trackingNumber}` | Get shipment by tracking number         |
| GET    | `/customer/{customerId}`     | Get shipments by customer               |
| GET    | `/{id}/history`              | Get shipment status history             |
| PATCH  | `/{id}/status`               | Update shipment status                  |

 🗄️ Shipment Data

Each shipment contains:

* Shipment ID (UUID)
* Tracking number
* Customer ID
* Origin
* Destination
* Status
* Estimated delivery
* Created timestamp
* Updated timestamp

Indexes are maintained for frequently queried fields such as tracking number, customer ID, and status.

▶️ Running the Project

### 1. Start PostgreSQL

```bash
cd infrastructure
docker compose up -d
```

### 2. Start the Backend

```bash
cd services/shipment-service
```

**Windows:**

```bash
mvnw.cmd spring-boot:run
```

**Linux/macOS:**

```bash
./mvnw spring-boot:run
```

Backend:

```text
http://localhost:8081
```

### 3. Start the Frontend

```bash
cd frontend/streamlit_app
pip install -r requirements.txt
streamlit run app.py
```

The Streamlit application will be available at the local URL displayed in the terminal, typically:

```text
http://localhost:8501
```

 🔄 How It Works

1. User interacts with the Streamlit dashboard.
2. The frontend sends requests through the Python API client.
3. Spring Boot REST APIs process the requests.
4. JPA/Hibernate communicates with PostgreSQL.
5. Shipment data is stored or retrieved from the database.
6. The backend returns the response to the frontend.
7. The dashboard displays the updated shipment information.

 📊 Output

The application provides a dashboard showing:

* Total shipments
* Shipments by current status
* Recent shipments
* Shipment details
* Tracking information
* Shipment history

Users can create a shipment and progressively update its status from creation through delivery.

 🎯 Objective

The objective of this project is to provide a centralized shipment management and tracking solution while demonstrating practical implementation of **full-stack development, REST APIs, database management, API integration, and Docker-based infrastructure**.

