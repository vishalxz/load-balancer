# Load Balancer

## Overview

This project implements a functional load balancer that distributes HTTP requests among a set of backend servers. It supports multiple load-balancing algorithms, dynamic server registration/removal, error handling, and monitoring. The default algorithm is **Round-Robin**, but it can be switched to **Random Selection** or **Least Connections** dynamically.

---

## Features

1. **Load Balancing Algorithms**:
   - Round-Robin (default)
   - Random Selection
   - Least Connections
2. **Dynamic Server Management**:
   - Register and remove backend servers at runtime.
3. **Error Handling**:
   - Retries failed requests up to 3 times with exponential backoff.
4. **Debugging and Monitoring**:
   - Logs key operations.
   - Provides APIs to monitor the current state and metrics of the load balancer.
5. **Health Check**:
   - Periodically checks the health of backend servers and temporarily removes unhealthy servers.
6. **Scalability**:
   - Designed to handle multiple concurrent requests efficiently.

---

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven
- Backend servers running on accessible ports (e.g., `http://localhost:8081`, `http://localhost:8082`).

### Steps


1. **Configure Backend Servers**:
   Update the `application.properties` file with the initial list of backend servers:
   ```properties
   loadbalancer.backend.servers=http://localhost:8081,http://localhost:8082
   ```

2. **Build the Application**:
   ```bash
   mvn clean install
   ```

3. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the APIs**:
   The load balancer will be available at `http://localhost:8080`. Use the following endpoints:
   - **Forward Request**: `/api/loadbalancer/forward`
   - **Register Server**: `/api/loadbalancer/register`
   - **Remove Server**: `/api/loadbalancer/remove`
   - **Set Algorithm**: `/api/loadbalancer/algorithm`
   - **Get Servers**: `/api/loadbalancer/servers`
   - **Get State**: `/api/loadbalancer/state`
   - **Get Metrics**: `/api/loadbalancer/metrics`

---

## High-Level Call Flow Diagram

```plaintext
Client
   |
   v
Load Balancer
   |
   v
Backend Server (Selected by Algorithm)
```


## Extending and Scaling the Implementation

### Extending

1. **Add New Algorithms**:
   - Implement the `LoadBalancingAlgorithm` interface.
   - Add the new algorithm to the `/api/loadbalancer/algorithm` endpoint.

2. **Health Checks**:
   - Add a periodic task to check the health of backend servers.
   - Temporarily remove unhealthy servers from the distribution list.

