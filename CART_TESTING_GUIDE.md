# Shopping Cart API Testing Guide

## Setup
1. Run the updated `query.sql` to create cart_items table
2. Run `test_data.sql` to add test products
3. Start your Spring Boot application

## Available Products for Testing
- PROD001: Test Product ($29.99)
- PROD002: Laptop Computer ($1299.99)  
- PROD003: Wireless Headphones ($199.99)
- PROD004: Smartphone ($899.99)

## Cart API Endpoints

### Browser Testing (Copy-paste these URLs):

#### 1. View Cart Items (GET - works in browser)
```
http://localhost:8080/api/cart
```

#### 2. Get Cart Count (GET - works in browser)  
```
http://localhost:8080/api/cart/count
```

### Full API Endpoints (Use Postman/REST Client):

#### 1. Add Item to Cart
```
POST http://localhost:8080/api/cart/add/PROD002?quantity=1
```
Response: "Added to cart" or "Updated quantity in cart"

#### 2. View Cart Items
```
GET http://localhost:8080/api/cart
```
Response: JSON array of cart items with product details

### 3. Update Item Quantity
```
PUT http://localhost:8080/api/cart/update/PROD002?quantity=3
```
Response: "Quantity updated"

### 4. Remove Item from Cart
```
DELETE http://localhost:8080/api/cart/remove/PROD002
```
Response: "Item removed from cart"

### 5. Get Cart Count
```
GET http://localhost:8080/api/cart/count
```
Response: Total number of items in cart

### 6. Clear Entire Cart
```
DELETE http://localhost:8080/api/cart/clear
```
Response: "Cart cleared successfully"

## Testing Flow
1. Add laptop to cart: `POST /api/cart/add/PROD002?quantity=1`
2. Add headphones: `POST /api/cart/add/PROD003?quantity=2`
3. View cart: `GET /api/cart`
4. Update laptop quantity: `PUT /api/cart/update/PROD002?quantity=2`
5. Check cart count: `GET /api/cart/count`
6. Remove headphones: `DELETE /api/cart/remove/PROD003`
7. View final cart: `GET /api/cart`

## Notes
- Each browser session gets its own temporary user ID
- Cart persists during the session and survives page refreshes
- Cart is stored in database, not just memory
- Ready for future user authentication integration