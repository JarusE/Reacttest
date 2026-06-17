# Assessment

Created by Sierhieiev Vadym


## Requirements

- Java 17
- Maven 3.8
- Node.js 18
- npm


## How to run

### 1. Backend

```bash
cd backend
mvn spring-boot:run
```

Link: [http://localhost:8080/api/users](http://localhost:8080/api/users)

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

Link: [http://localhost:5173](http://localhost:5173)

## Design choices for the "User -> Address" Flow

1. The app has two screens. Users open a detail page via row click or click on Manage, and return via breadcrumbs.
2. The list uses a lightweight DTO with addressCount.
3. Profile changes stay in local form state until Save Profile button sends a request.
4. UI refetches the user so the client stays aligned with the server.
5. Only one address can be primary per user.
6. Data is stored in memory

## Why this structure

1. The list and detail screens are separated so the overview stays fast and simple, while all editing happens in one focused user context.
2. Addresses use nested REST paths under /users/{userId} because an address belongs to a user and this keeps the API contract clear for the UI.

