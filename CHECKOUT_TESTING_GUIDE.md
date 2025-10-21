# Checkout System Testing Guide

## Testing with Postman (Recommended)

### Step 1: Add Items to Cart

#### Add Laptop to Cart
- **Method**: POST
- **URL**: `http://localhost:8080/api/cart/add/PROD002`
- **Params**: 
  - Key: `quantity`
  - Value: `1`
- **Response**: "Added to cart"

#### Add Headphones to Cart
- **Method**: POST
- **URL**: `http://localhost:8080/api/cart/add/PROD003`
- **Params**: 
  - Key: `quantity`
  - Value: `2`
- **Response**: "Added to cart"

### Step 2: View Cart Before Checkout
- **Method**: GET
- **URL**: `http://localhost:8080/api/cart`
- **Response**: JSON array with cart items

### Step 3: Get Checkout Summary
- **Method**: GET
- **URL**: `http://localhost:8080/api/checkout/summary`
- **Response**: JSON with items, total amount, and item count

### Step 4: Process Checkout

#### Option A: Cash Payment
- **Method**: POST
- **URL**: `http://localhost:8080/api/checkout/process`
- **Params**: 
  - Key: `paymentMethod`
  - Value: `cash`
- **Response**: "Checkout successful! Order ID: ORDER_xxx..."

#### Option B: Card Payment
- **Method**: POST
- **URL**: `http://localhost:8080/api/checkout/process`
- **Params**: 
  - Key: `paymentMethod`
  - Value: `card`
- **Response**: "Checkout successful! Order ID: ORDER_xxx..."

## Testing with Browser (Alternative)

### Step 1: Add Items to Cart
```
http://localhost:8080/api/cart/test-add/PROD002?quantity=1
http://localhost:8080/api/cart/test-add/PROD003?quantity=2
```

### Step 2: View Cart Before Checkout
```
http://localhost:8080/api/cart
```

### Step 3: Get Checkout Summary
```
http://localhost:8080/api/checkout/summary
```

### Step 4: Process Checkout (Choose One)

#### Option A: Cash Payment
```
http://localhost:8080/api/checkout/test-cash
```

#### Option B: Card Payment
```
http://localhost:8080/api/checkout/test-card
```

### Step 5: Verify Results

#### Check if cart is empty after checkout
```
http://localhost:8080/api/cart
```
Should show: [] (empty array)

#### Test notification system with new order
```
http://localhost:8080/api/test/notification/ORDER_xxx
```
Replace "ORDER_xxx" with the actual order ID from step 4

## What Happens During Checkout:

1. **Authentication Check** - Currently allows temporary users (ready for future login)
2. **Cart Validation** - Ensures cart has items
3. **Order Creation** - Creates new order record
4. **Order Items Creation** - Converts cart items to order items
5. **Total Calculation** - Calculates final amount
6. **Cart Cleanup** - Removes items from cart
7. **Confirmation** - Returns order ID

## Database Changes After Checkout:

- **orders table**: New order record with status "processing"
- **order_items table**: New records for each product
- **cart_items table**: Records deleted (cart cleared)
- **notifications table**: Can send notification using order ID

## Future Authentication Integration:

The system is ready for user authentication:
- `isUserAuthenticated()` method checks session
- Will redirect to login when user system is implemented
- Currently allows temporary users to complete purchases

## Payment Methods Supported:

- **cash**: Cash on delivery
- **card**: Credit/Debit card payment

Ready for future payment gateway integration!

## Complete Postman Test Collection

### 1. Cart Operations

#### Add Laptop
- **Method**: POST
- **URL**: `http://localhost:8080/api/cart/add/PROD002?quantity=1`
- **Expected**: "Added to cart"

#### Add Headphones
- **Method**: POST
- **URL**: `http://localhost:8080/api/cart/add/PROD003?quantity=2`
- **Expected**: "Added to cart"

#### View Cart
- **Method**: GET
- **URL**: `http://localhost:8080/api/cart`
- **Expected**: JSON array with 2 items

#### Update Laptop Quantity
- **Method**: PUT
- **URL**: `http://localhost:8080/api/cart/update/PROD002?quantity=3`
- **Expected**: "Quantity updated"

#### Get Cart Count
- **Method**: GET
- **URL**: `http://localhost:8080/api/cart/count`
- **Expected**: Number (total items)

### 2. Checkout Operations

#### Get Checkout Summary
- **Method**: GET
- **URL**: `http://localhost:8080/api/checkout/summary`
- **Expected**: JSON with items and total amount

#### Process Checkout - Cash
- **Method**: POST
- **URL**: `http://localhost:8080/api/checkout/process?paymentMethod=cash`
- **Expected**: "Checkout successful! Order ID: ORDER_xxx..."

#### Verify Cart Empty After Checkout
- **Method**: GET
- **URL**: `http://localhost:8080/api/cart`
- **Expected**: [] (empty array)

### 3. Notification Testing

#### Send Order Notification
- **Method**: GET
- **URL**: `http://localhost:8080/api/test/notification/ORDER_xxx`
- **Note**: Replace ORDER_xxx with actual order ID from checkout
- **Expected**: "Notification sent successfully"

## Important Postman Settings:

### Required Settings (Fix 403 Error):
1. **Enable Cookies**: 
   - Go to Settings → General → Enable "Send cookies with requests"
   - This is CRUCIAL for session management

2. **Headers for POST/PUT requests**:
   - Add Header: `Content-Type: application/x-www-form-urlencoded`
   - OR let Postman auto-detect (recommended)

3. **Use same Postman tab/session**:
   - Don't open new tabs between requests
   - Use the same Postman window for the entire test sequence

### Troubleshooting 403 Forbidden:
If you still get 403 errors:
1. **Restart your Spring Boot application** after the security config update
2. **Clear Postman cookies**: Settings → Cookies → Remove all
3. **Use browser first**: Test with browser URLs to establish session, then switch to Postman
4. **Check console**: Look at your Spring Boot console for security errors

### Alternative Headers (if needed):
- `Accept: application/json`
- `User-Agent: PostmanRuntime/7.x.x`

## Testing Sequence:

1. Add items to cart (POST requests)
2. View cart contents (GET)
3. Get checkout summary (GET)  
4. Process checkout (POST)
5. Verify cart is empty (GET)
6. Test notification with order ID (GET)