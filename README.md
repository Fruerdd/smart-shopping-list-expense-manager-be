# Smart Shopping List & Expense Manager

## Project Overview

The **Smart Shopping List & Expense Manager** is a web application designed to help users efficiently manage their grocery shopping and expenses. It enables users to:

- Create and organize shopping lists
- Compare prices from registered markets
- Track spending habits
- Collaborate with family members or housemates

## Technology Stack

- **Backend:** Java, Spring  
- **Frontend:** Angular  
- **Database:** PostgreSQL
- **Hosting:** Netlify/Render  

## Team Members & Feature Assignment

### **Team Member 1:** Amina 
- **User Authentication & Profile Management** – Sign up, log in, and manage profiles. [Issue #4](https://github.com/Fruerdd/smart-shopping-list-expense-manager-issues/issues/4)
- **Referral & Loyalty System** – Earn rewards for referring friends and accumulating loyalty points. [Issue #5](https://github.com/Fruerdd/smart-shopping-list-expense-manager-issues/issues/5)
- **Dark Mode Toggle** – Customize UI with light and dark mode. [Issue #6](https://github.com/Fruerdd/smart-shopping-list-expense-manager-issues/issues/6)

### **Team Member 2:** Tarik
- **Shopping List Management & Smart Item Search** – Search for and add items based on availability and pricing. [Issue #1](https://github.com/Fruerdd/smart-shopping-list-expense-manager-issues/issues/1)
- **Price Comparison & Intelligent Store Selection** – Compare prices and get smart store recommendations. [Issue #2](https://github.com/Fruerdd/smart-shopping-list-expense-manager-issues/issues/2)
- **Favorite Products & Stores** – Mark favorites for quick access and personalized recommendations. [Issue #3](https://github.com/Fruerdd/smart-shopping-list-expense-manager-issues/issues/3)

### **Team Member 3:** Pavel
- **Admin Management of Markets, Products & Users** – Manage market, product, and user data via an admin dashboard. [Issue #7](https://github.com/Fruerdd/smart-shopping-list-expense-manager-issues/issues/7)
- **Multi-User Sync & Shared Lists** – Sync and manage shared shopping lists. [Issue #8](https://github.com/Fruerdd/smart-shopping-list-expense-manager-issues/issues/8)
- **Expense Tracking & Budget Analytics** – Track grocery expenses and generate reports. [Issue #9](https://github.com/Fruerdd/smart-shopping-list-expense-manager-issues/issues/9)

## Site Map

![sitemap](https://github.com/Fruerdd/smart-shopping-list-expense-manager-issues/blob/main/sitemap.jpg)


## Mockups

- Visual mockups and wireframes will be designed using Figma.
- Each feature will have an associated wireframe illustrating its UI/UX.
- Mockups will be stored in the `mockups/` folder in the repository.
- [View the Figma Design](https://www.figma.com/design/XnMqWbvwiok3RbDHW1Vm1q/SE-Project?node-id=434-428&t=ghnVfiVUUWDewfmv-1)

## Data Source for Markets & Items

Instead of markets being user-managed, the admin will maintain the market and product database. A CSV file (randomly generated using AI tools) will contain:

- A list of grocery items
- Prices of items across different markets
- Market names and locations

This data will be used to populate and maintain the price comparison functionality.

## Intelligent Shopping List Management

Users can:

- **Search for Items:** Items available will appear under the search bar.
- **Smart Store Selection:** Automatically selects the product from:
  - The cheapest store
  - A preferred store (if set)
  - A favorite store (based on past purchases)
- **Grouped Shopping Logic:** Ensures most items are bought from the same store unless price differences are significant.
- **Manual Store Selection:** Users can override automatic selection.

## Referral & Loyalty System

### **Referral System**
- Users can invite friends via a unique referral code or link.
- Both the referrer and the new user receive rewards upon sign-up and first purchase.
- The system tracks successful referrals.

### **Loyalty Program**
- Users earn points for purchases.
- Points can be redeemed for discounts and exclusive deals.
- Loyalty tiers (Bronze, Silver, Gold) offer increasing rewards.

## API Documentation

### **API Endpoints**

#### **Authentication Endpoints**
| Method | URL Path         | Request       | Response       | Description                          |
|--------|------------------|---------------|----------------|--------------------------------------|
| POST   | `/auth/register` | `RegisterDTO` | `String`       | Registers a new user                 |
| POST   | `/auth/login`    | `AuthDTO`     | `AuthResponse` | Authenticates user and returns token |

#### **User Profile & Management Endpoints**
| Method | URL Path                            | Request         | Response        | Description                    |
|--------|-------------------------------------|-----------------|-----------------|--------------------------------|
| GET    | `/user/profile/{id}`                | -               | `UserDTO`       | Gets user profile by ID        |
| GET    | `/user/profile/me`                  | -               | `UserDTO`       | Gets current user profile      |
| PUT    | `/user/profile/{id}`                | `UserDTO`       | `UserDTO`       | Updates user profile           |
| PATCH  | `/user/profile/{id}`                | `UserDTO`       | `UserDTO`       | Partially updates user profile |
| POST   | `/user/profile/upload-picture/{id}` | `MultipartFile` | `String`        | Uploads profile picture        |
| GET    | `/user/search`                      | `?q=query`      | `List<UserDTO>` | Searches users                 |

#### **Friends & Social Endpoints**
| Method | URL Path                     | Request             | Response        | Description             |
|--------|------------------------------|---------------------|-----------------|-------------------------|
| GET    | `/user/friends/{id}`         | -                   | `List<UserDTO>` | Gets user's friends     |
| POST   | `/user/friends/{id}/request` | `?friendId=uuid`    | `String`        | Sends friend request    |
| POST   | `/user/friends/{id}/accept`  | `?requesterId=uuid` | `String`        | Accepts friend request  |
| POST   | `/user/friends/{id}/decline` | `?requesterId=uuid` | `String`        | Declines friend request |
| DELETE | `/user/friends/{id}/remove`  | `?friendId=uuid`    | `String`        | Removes friend          |

#### **Loyalty & Referral Endpoints**
| Method | URL Path                            | Request                         | Response  | Description              |
|--------|-------------------------------------|---------------------------------|-----------|--------------------------|
| GET    | `/user/profile/loyalty-points/{id}` | -                               | `Integer` | Gets user loyalty points |
| PUT    | `/user/profile/loyalty-points/{id}` | `?points=number`                | `Integer` | Updates loyalty points   |
| POST   | `/user/profile/award-points/{id}`   | `?activity=string&count=number` | `String`  | Awards loyalty points    |
| POST   | `/user/profile/referral/{id}`       | `?referralCode=string`          | `String`  | Applies referral code    |
| GET    | `/user/profile/referral-code/{id}`  | -                               | `String`  | Gets user referral code  |

#### **Notifications Endpoints**
| Method | URL Path                                    | Request | Response                | Description                     |
|--------|---------------------------------------------|---------|-------------------------|---------------------------------|
| GET    | `/user/notifications/{id}`                  | -       | `List<NotificationDTO>` | Gets user notifications         |
| GET    | `/user/notifications/{id}/unread`           | -       | `List<NotificationDTO>` | Gets unread notifications       |
| GET    | `/user/notifications/{id}/count`            | -       | `Long`                  | Gets unread notification count  |
| PATCH  | `/user/notifications/{notificationId}/read` | -       | `String`                | Marks notification as read      |
| PATCH  | `/user/notifications/{id}/read-all`         | -       | `String`                | Marks all notifications as read |

#### **Shopping List Management Endpoints**
| Method | URL Path                                 | Request           | Response                | Description                |
|--------|------------------------------------------|-------------------|-------------------------|----------------------------|
| POST   | `/api/shopping-lists`                    | `ShoppingListDTO` | `ShoppingListDTO`       | Creates shopping list      |
| GET    | `/api/shopping-lists/{id}`               | -                 | `ShoppingListDTO`       | Gets shopping list by ID   |
| GET    | `/api/shopping-lists/user/{userId}`      | -                 | `List<ShoppingListDTO>` | Gets user's shopping lists |
| PUT    | `/api/shopping-lists/{id}`               | `ShoppingListDTO` | `ShoppingListDTO`       | Updates shopping list      |
| PUT    | `/api/shopping-lists/{id}/soft-delete`   | -                 | `Void`                  | Soft deletes shopping list |
| GET    | `/api/shopping-lists/{id}/collaborators` | -                 | `List<CollaboratorDTO>` | Gets list collaborators    |

#### **Product & Store Search Endpoints**
| Method | URL Path                                               | Request        | Response                    | Description                     |
|--------|--------------------------------------------------------|----------------|-----------------------------|---------------------------------|
| GET    | `/api/shopping-lists/products`                         | -              | `List<ShoppingListItemDTO>` | Gets all products               |
| GET    | `/api/shopping-lists/products/search`                  | `?term=string` | `List<ShoppingListItemDTO>` | Searches products               |
| GET    | `/api/shopping-lists/products/category/{categoryId}`   | -              | `List<ShoppingListItemDTO>` | Gets products by category       |
| GET    | `/api/shopping-lists/categories`                       | -              | `List<CategoryDTO>`         | Gets all categories             |
| GET    | `/api/shopping-lists/sidebar-categories`               | -              | `List<CategoryDTO>`         | Gets sidebar categories         |
| GET    | `/api/shopping-lists/stores`                           | -              | `List<StoreDTO>`            | Gets all available stores       |
| GET    | `/api/shopping-lists/items/{itemId}/price-comparisons` | -              | `List<StoreItemDTO>`        | Gets price comparisons for item |

#### **User Dashboard Endpoints**
| Method | URL Path                                        | Request | Response                   | Description                       |
|--------|-------------------------------------------------|---------|----------------------------|-----------------------------------|
| GET    | `/api/users/{id}`                               | -       | `UserDTO`                  | Gets user by ID                   |
| GET    | `/api/users/{id}/favorite-products`             | -       | `List<FavoriteProductDTO>` | Gets user's favorite products     |
| POST   | `/api/users/{id}/favorite-products/{productId}` | -       | `FavoriteProductDTO`       | Adds favorite product             |
| DELETE | `/api/users/{id}/favorite-products/{productId}` | -       | `Void`                     | Removes favorite product          |
| GET    | `/api/users/{id}/favorite-stores`               | -       | `List<FavoriteStoreDTO>`   | Gets user's favorite stores       |
| POST   | `/api/users/{id}/favorite-stores/{storeId}`     | -       | `FavoriteStoreDTO`         | Adds favorite store               |
| DELETE | `/api/users/{id}/favorite-stores/{storeId}`     | -       | `Void`                     | Removes favorite store            |
| GET    | `/api/users/{id}/products/{productId}/prices`   | -       | `List<StorePriceDTO>`      | Gets product prices across stores |
| GET    | `/api/users/loyalty/{userId}`                   | -       | `LoyaltyTierEnum`          | Gets user loyalty tier            |

#### **User Analytics Endpoints**
| Method | URL Path                         | Request | Response                 | Description                      |
|--------|----------------------------------|---------|--------------------------|----------------------------------|
| GET    | `/api/profile/money-spent`       | -       | `List<MoneySpentDTO>`    | Gets money spent analytics       |
| GET    | `/api/profile/category-spending` | -       | `List<CategorySpendDTO>` | Gets category spending breakdown |
| GET    | `/api/profile/price-averages`    | -       | `List<PriceAverageDTO>`  | Gets price averages              |
| GET    | `/api/profile/store-expenses`    | -       | `List<StoreExpenseDTO>`  | Gets store expense breakdown     |
| GET    | `/api/profile/savings`           | -       | `List<SavingDTO>`        | Gets savings analytics           |

#### **Admin Store Management Endpoints**
| Method | URL Path                         | Request                | Response                | Description             |
|--------|----------------------------------|------------------------|-------------------------|-------------------------|
| GET    | `/api/stores`                    | -                      | `List<StoreDTO>`        | Gets all stores         |
| POST   | `/api/stores`                    | `StoreCreateUpdateDTO` | `StoreDTO`              | Creates new store       |
| PUT    | `/api/stores/{storeId}`          | `StoreCreateUpdateDTO` | `StoreDTO`              | Updates store           |
| GET    | `/api/stores/{storeId}`          | -                      | `StoreDTO`              | Gets store by ID        |
| GET    | `/api/stores/{storeId}/products` | -                      | `List<StoreProductDTO>` | Gets products for store |
| GET    | `/api/stores/popular`            | -                      | `List<PopularShopDTO>`  | Gets popular stores     |

#### **Admin Product Management Endpoints**
| Method | URL Path             | Request               | Response        | Description        |
|--------|----------------------|-----------------------|-----------------|--------------------|
| POST   | `/api/products/bulk` | `List<AddProductDTO>` | `BulkResultDTO` | Bulk adds products |

#### **Admin Analytics Endpoints**
| Method | URL Path                                  | Request | Response                     | Description                   |
|--------|-------------------------------------------|---------|------------------------------|-------------------------------|
| GET    | `/api/products/analytics/daily-searches`  | -       | `List<DailySearchDTO>`       | Gets daily search analytics   |
| GET    | `/api/products/analytics/weekly-adds`     | -       | `List<DailySearchDTO>`       | Gets weekly product additions |
| GET    | `/api/products/analytics/monthly-adds`    | -       | `List<MonthlyProductAddDTO>` | Gets monthly additions        |
| GET    | `/api/products/analytics/weekly-searches` | -       | `List<DailySearchDTO>`       | Gets weekly search analytics  |
| GET    | `/api/products/analytics/top`             | -       | `List<TopProductDTO>`        | Gets top products             |

#### **Health Check Endpoint**
| Method | URL Path  | Request | Response              | Description           |
|--------|-----------|---------|-----------------------|-----------------------|
| GET    | `/health` | -       | `Map<String, Object>` | Health check endpoint |

### **Data Transfer Objects (DTOs)**

#### **Authentication DTOs**


`RegisterDTO`:
```json
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```
`AuthDTO`:
```json
{
  "email": "string",
  "password": "string"
}
```
`AuthResponse`:
```json
{
  "token": "string",
  "message": "string",
  "userType": "string"
}
```

#### **User DTOs**
`UserDTO`:
```json
{
  "id": "uuid",
  "email": "string",
  "name": "string",
  "phone": "string",
  "address": "string",
  "avatar": "string",
  "loyaltyTier": "BRONZE|SILVER|GOLD",
  "bonus_points": "integer",
  "loyaltyPoints": "integer",
  "couponCode": "string",
  "creditsAvailable": "double",
  "qrCodeValue": "string",
  "shoppingLists": "List<ShoppingListDTO>"
}
```
`UserStatisticsDTO`:
```json
{
  "totalShoppingLists": "integer",
  "totalFriends": "integer",
  "totalLoyaltyPoints": "integer",
  "totalExpenses": "double"
}
```

#### **Shopping List DTOs**
`ShoppingListDTO`:
```json
{
  "id": "uuid",
  "name": "string",
  "description": "string",
  "listType": "string",
  "isActive": "boolean",
  "ownerId": "uuid",
  "ownerName": "string",
  "ownerAvatar": "string",
  "storeId": "uuid",
  "storeName": "string",
  "image": "string",
  "category": "string",
  "createdAt": "instant",
  "updatedAt": "instant",
  "items": "List<ShoppingListItemDTO>",
  "collaborators": "List<CollaboratorDTO>"
}
```
`ShoppingListItemDTO`:
```json
{
  "id": "uuid",
  "productId": "uuid",
  "productName": "string",
  "image": "string",
  "price": "double",
  "storeName": "string",
  "categoryId": "uuid",
  "quantity": "double",
  "isChecked": "boolean",
  "status": "string"
}
```
`CollaboratorDTO`:
```json
{
  "userId": "uuid",
  "userName": "string",
  "permission": "VIEW|EDIT"
}
```

#### **Product & Store DTOs**
`ProductDTO`:
```json
{
  "id": "uuid",
  "name": "string",
  "category": "CategoryEntity"
}
```
`StoreDTO`:
```json
{
  "id": "uuid",
  "name": "string",
  "icon": "string"
}
```
`CategoryDTO`:
```json
{
  "id": "uuid",
  "name": "string",
  "icon": "string",
  "products": "List<ShoppingListItemDTO>"
}
```
`StorePriceDTO`:
```json
{
  "storeId": "uuid",
  "storeName": "string",
  "price": "BigDecimal"
}
```
`StoreItemDTO`:
```json
{
  "storeId": "uuid",
  "storeName": "string",
  "storeIcon": "string",
  "price": "double"
}
```

#### **Favorites DTOs**
`FavoriteProductDTO`:
```json
{
  "id": "uuid",
  "productId": "uuid",
  "productName": "string"
}
```
`FavoriteStoreDTO`:
```json
{
  "id": "uuid",
  "storeId": "uuid",
  "storeName": "string"
}
```

#### **Notification DTO**
`NotificationDTO`:
```json
{
  "id": "uuid",
  "sourceUserId": "uuid",
  "sourceUserName": "string",
  "sourceUserAvatar": "string",
  "destinationUserId": "uuid",
  "title": "string",
  "message": "string",
  "notificationType": "string",
  "isRead": "boolean",
  "createdAt": "instant"
}
```

#### **Analytics DTOs**
`MoneySpentDTO`:
```json
{
  "month": "string",
  "thisYear": "double",
  "lastYear": "double"
}
```
`CategorySpendDTO`:
```json
{
  "category": "string",
  "spent": "double"
}
```
`PriceAverageDTO`:
```json
{
  "product": "string",
  "averagePrice": "double"
}
```
`StoreExpenseDTO`:
```json
{
  "store": "string",
  "spent": "double"
}
```
`SavingDTO`:
```json
{
  "month": "string",
  "saved": "double"
}
```

#### **Admin DTOs**
`StoreCreateUpdateDTO`:
```json
{
  "name": "string",
  "icon": "string",
  "location": "string",
  "contact": "string"
}
```
`StoreProductDTO`:
```json
{
  "storePriceId": "string",
  "storeId": "string",
  "productId": "string",
  "productName": "string",
  "category": "string",
  "description": "string",
  "isActive": "boolean",
  "price": "double",
  "barcode": "string"
}
```
`AddProductDTO`:
```json
{
  "storePriceId": "uuid",
  "storeId": "uuid",
  "productId": "uuid",
  "productName": "string",
  "category": "string",
  "description": "string",
  "isActive": "boolean",
  "price": "BigDecimal",
  "barcode": "string"
}
```
`BulkResultDTO`:
```json
{
  "success": "boolean",
  "errors": "List<string>",
  "count": "integer"
}
```
`PopularShopDTO`:
```json
{
  "storeId": "uuid",
  "storeName": "string",
  "usageCount": "long"
}
```
`DailySearchDTO`:
```json
{
  "date": "string",
  "searches": "long"
}
```
`MonthlyProductAddDTO`:
```json
{
  "month": "string",
  "products": "long"
}
```
`TopProductDTO`:
```json
{
  "productName": "string",
  "usageCount": "long"
}
```

## Local Development Setup

### Prerequisites
- **Java 21** or higher
- **PostgreSQL 12+** 
- **Maven 3.6+**
- **Git**

### Environment Variables
Create a `.env` file or set the following environment variables:
```bash
# Database Configuration
POSTGRES_PASSWORD=your_database_password
JWT_SECRET=your_jwt_secret_key_here
```

### Setup Steps
1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd smart-shopping-list-expense-manager-be
   ```

2. **Setup PostgreSQL Database (Locally)**
   ```bash
   # Create database
   createdb shopping_db
   
   # Or using psql
   psql -U postgres
   CREATE DATABASE shopping_db;
   ```

3. **Configure Environment Variables**
   - Set `POSTGRES_PASSWORD` to your PostgreSQL password
   - Set `JWT_SECRET` to a secure random string

4. **Run the Application**
   ```bash
   # Using Maven
   mvn spring-boot:run
   
   # Or compile and run
   mvn clean package
   java -jar target/smart-shopping-list-expense-manager-*.jar
   ```

5. **Verify Setup**
   - Application runs on `http://localhost:8080`
   - Health check: `GET http://localhost:8080/health`
   - Database tables are auto-created on first run

### Authentication
- **JWT Token Required** for most endpoints
- Include in Authorization header: `Bearer <your_jwt_token>`
- Get token via `/auth/login` endpoint
- Token expires after 10 hours

### Error Response Format
```json
{
  "timestamp": "2025-01-01T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/endpoint"
}
```

## Out of Scope

- **AI-based purchase predictions:** Not included in the initial version.
- **Real-time Store Inventory:** Live stock updates not implemented initially.
- **Mobile App Version:** Initially, the application is web-based.

## Deployment Setup

### Backend Deployment (Render)

**Platform:** Render (render.com)  
**Database:** PostgreSQL on Render  
**Backend URL:** `https://smart-shopping-list-api.onrender.com`

#### Setup Steps:
1. **Create PostgreSQL Database** on Render (Free tier)
2. **Create Web Service** connected to this GitHub repository
3. **Environment Variables Required:**
   ```
   SPRING_PROFILES_ACTIVE=prod
   DATABASE_URL=[Internal Database URL from Render]
   POSTGRES_USER=[Database username from Render]
   POSTGRES_PASSWORD=[Database password from Render] 
   JWT_SECRET=[JWT token]
   FRONTEND_URL=[https://grocerymate.netlify.app]
   ```

#### Deployment Configuration Files:
- `system.properties` - Specifies Java 21
- `Dockerfile` - Container configuration for deployment
- `src/main/resources/application-prod.yml` - Production configuration
- `src/main/java/.../config/CorsConfig.java` - CORS configuration for frontend connection

### Frontend Deployment (Netlify)

**Platform:** Netlify  
**Frontend URL:** `https://grocerymate.netlify.app`

#### Required Configuration:
- Ensure CORS is configured to allow Netlify domain

### Database Connection
- PostgreSQL database hosted on Render
- Backend connects via internal database URL (faster, free bandwidth)
- Environment variables manage connection securely

### Frontend-Backend Connection
- CORS configured to allow Netlify domain
- API calls from Angular frontend to Spring Boot backend
- JWT authentication for secure communication


