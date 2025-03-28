# Airbnb - Spring Boot Application
This is a full-featured backend implementation of an Airbnb booking platform using Spring Boot. It provides functionalities for hotel listings, room bookings, user authentication, and payment integrations, making it a comprehensive backend solution for an online accommodation booking system.

## 🚀 Features
✅ User Authentication & Authorization (JWT-based)

✅ Hotel, Room, and Inventory Management (CRUD operations)

✅ Booking System (Real-time availability and reservations)

✅ Payment Integration (Razorpay) with Webhook support

✅ Role-based Access Control (Super Admin, Hotel Manager, and Guest)

✅ Hotel Search (Filter and search functionality)

✅ Dynamic Pricing Model (Room prices based on demand & availability)

✅ Booking Status Tracking (Track booking confirmations, cancellations)

✅ User Profile Management (Update personal details & preferences)

✅ Admin Reports & Hotel Analytics (View revenue and booking trends)

✅ Inventory Management (Update room availability & pricing)

✅ Secure APIs (JWT authentication & role-based access control)


## 🗄 Database Design
The database schema follows a relational model with **one-to-many** and **many-to-many** relationships.

![Image](https://github.com/user-attachments/assets/c4799821-f4b2-4731-87cc-57a4061d2d0f)
<p align="center"><i>Figure 1: ER Diagram</i></p>

## 🛠 API Endpoints
### 🔐 Authentication Endpoints `/auth`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/signup` | Create a new account |
| POST | `/auth/login` | User login |
| POST | `/auth/refresh` | Refresh JWT token |

### 👤 User Profile Endpoints `/users`
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users/profile` | Get user profile |
| PATCH | `/users/profile` | Update user profile |

### 🏠 User Guests Endpoints `/users/guests`
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/users/guests` | Get all user's guests |
| POST | `/users/guests` | Add a new guest |
| PUT | `/users/guests/{guestId}` | Update a guest |
| DELETE | `/users/guests/{guestId}` | Remove a guest |
| GET | `/users/guests/{guestId}` | Get guest by ID |

### 🏨 Booking Endpoints `/bookings`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/bookings/init` | Initiate a booking |
| POST | `/bookings/{bookingId}/addGuests` | Add guests to booking |
| POST | `/bookings/{bookingId}/payments` | Initiate payment |
| POST | `/bookings/{bookingId}/cancel` | Cancel booking |
| GET | `/bookings/{bookingId}/status` | Check booking status |
| GET | `/users/myBookings` | Get user's bookings |

### 🏠 Room Management `/admin/hotels/{hotelId}/rooms`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/admin/hotels/{hotelId}/rooms` | Create a new room |
| GET | `/admin/hotels/{hotelId}/rooms` | Get all rooms in hotel |
| GET | `/admin/hotels/{hotelId}/rooms/{roomId}` | Get room by ID |
| PUT | `/admin/hotels/{hotelId}/rooms/{roomId}` | Update room |
| DELETE | `/admin/hotels/{hotelId}/rooms/{roomId}` | Delete room |

### 📦 Inventory Management `/admin/inventory`
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/inventory/rooms/{roomId}` | Get room inventory |
| PATCH | `/admin/inventory/rooms/{roomId}` | Update room inventory |

### 👑 Super Admin Endpoints `/superAdmin`
| Method | Endpoint | Description |
|--------|----------|-------------|
| PATCH | `/superAdmin/addRoles/{userId}` | Add roles to user |

### 🔔 Webhook Endpoints `/webhook`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/webhook/payment` | Razorpay payment webhook |


## 📬 Postman Collection
Import this Postman collection to test APIs quickly: [Postman](https://www.postman.com/nishantdongre30/public-workspace/request/yq4g0js/create-hotel?tab=body)

## 📘 Swagger API Documentation is included for interactive exploration.
- **Access Swagger UI**: `http://localhost:8080/api/v1/swagger-ui/index.html#/`

<img width="1467" alt="Image" src="https://github.com/user-attachments/assets/5fded53a-25bd-415e-8bb1-1069830d6496" />
<p align="center"><i>Figure 2: Swagger API's Docs</i></p>

<img width="1467" alt="Image" src="https://github.com/user-attachments/assets/80b6fceb-3875-4173-8d29-cffbcc75fd48" />
<p align="center"><i>Figure 3: Swagger API's Docs</i></p>

<img width="1467" alt="Image" src="https://github.com/user-attachments/assets/74ead181-0f74-4c3e-816d-a30b84e8b0e7" />
<p align="center"><i>Figure 4: Swagger API's Docs</i></p>


## 🚀 Key Features Breakdown
### 🔑 Authentication & Authorization (JWT-Based)
- Implements user authentication (signup, login) using Spring Security & JWT.
- Secure role-based access control (Super Admin, Hotel Manager, Guest).

### 🏨 Hotel & Room Management
- CRUD operations for hotels and rooms.
- Hotel Search with filtering options.
- Dynamic Pricing based on demand & availability.

### 🛏️ Booking System
- Real-time availability check before booking.
- Guest management (add/remove guests per booking).
- Booking status tracking (confirmed, cancelled, pending).

### 💳 Payment Integration (Razorpay)
- Online payments using Razorpay API.
- Supports webhooks for payment status updates.

<img width="1466" alt="Image" src="https://github.com/user-attachments/assets/867da1de-e804-40d8-979a-9f8da64eb883" />
<p align="center"><i>Figure 5: Razorpay Payment Screen</i></p>

### 📊 Admin & Reporting
- Super Admin panel for managing users and roles.
- Booking reports & revenue analytics.
- Hotel managers can manage their own inventory and pricing.

### 🔗 API Documentation (Swagger)
- Integrated Swagger UI for testing API endpoints interactively.

## 🏗 Tech Stack
- **Backend:** Java, Spring Boot, Spring Security, Hibernate
- **Database:** Postgresql
- **API Documentation:** Swagger
- **Payment Gateway:** Razorpay


## 🔧 Setup and Installation
### Prerequisites
- Java
- Postgresql Database

### Steps to Run
1. Clone the repository:
   ```sh
   git clone https://github.com/NishantDongre/Airbnb.git
   cd Airbnb
   ```

2. Configure the database in `application.properties`:
   ```properties
    # Application Name
    spring.application.name=airbnb-app
    
    #DB Configuration
    spring.datasource.url=<DB URL>  # jdbc:postgresql://localhost:5432/airbnb
    spring.datasource.username=<DB Username>
    spring.datasource.password=<DB Password>
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true
    
    # Server Configuration
    server.servlet.context-path=/api/v1
    
    # JWT Authentication Configuration
    jwt.secretKey=<JWT Secret Key>
    
    # Razorpay Configuration 
    razorpay.api.key=<Razorpay API Key>
    razorpay.api.secret=<RazorPay API Secret>
    razorpay.webhook.secret=<RazorPay Webhook Secret>
    
    # Frontend URL for Razorpay Payment Screen
    frontend.url=<Frontend URL for RazorPay Payment Screen> # http://127.0.0.1:5500/index.html

   ```
   
3. Build and run the application:
   ```sh
   mvn clean install
   mvn spring-boot:run
   ```

4. The application will be available at `http://localhost:8080`.


5. Setup RazorPay Payment Frontend: Follow the setup instructions in the [Airbnb-Razorpay-Payment-Frontend](https://github.com/NishantDongre/Airbnb-Razorpay-Payment-Frontend) repository.

## Contact

For queries, reach out at [Email](mailto:nishantdongre30@gmail.com) or [Linkedin](https://www.linkedin.com/in/nishant-dongre/)
