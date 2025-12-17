# API Testing Guide - Postman Collection

## Base URL
```
http://localhost:8080
```

---

## 1. GET Categories (Paginated)

**Method:** GET  
**URL:** `http://localhost:8080/api/public/categories?pageNumber=1&pageSize=10`  
**Parameters:**
- `pageNumber` (Integer): Page number (e.g., 1, 2, 3)
- `pageSize` (Integer): Items per page (e.g., 5, 10, 20)

**Response (200 OK):**
```json
{
  "content": [
    {
      "categoryId": 1,
      "categoryName": "Mens T-Shirts"
    },
    {
      "categoryId": 2,
      "categoryName": "Smartphones"
    }
  ]
}
```

---

## 2. CREATE Category

**Method:** POST  
**URL:** `http://localhost:8080/api/public/categories`  
**Headers:**
- `Content-Type`: `application/json`

**Request Body:**
```json
{
  "categoryName": "New Category Name"
}
```

**Response (201 CREATED):**
```json
{
  "categoryId": 31,
  "categoryName": "New Category Name"
}
```

---

## 3. UPDATE Category

**Method:** PUT  
**URL:** `http://localhost:8080/api/public/categories/{categoryId}`  
**Example:** `http://localhost:8080/api/public/categories/1`  
**Headers:**
- `Content-Type`: `application/json`

**Request Body:**
```json
{
  "categoryName": "Updated Category Name"
}
```

**Response (200 OK):**
```json
{
  "categoryId": 1,
  "categoryName": "Updated Category Name"
}
```

---

## 4. DELETE Category

**Method:** DELETE  
**URL:** `http://localhost:8080/api/admin/categories/{categoryId}`  
**Example:** `http://localhost:8080/api/admin/categories/1`

**Response (200 OK):**
```json
{
  "categoryId": 1,
  "categoryName": "Mens T-Shirts"
}
```

---

## 5. Test Echo Endpoint

**Method:** GET  
**URL:** `http://localhost:8080/api/echo?message=Hello`  
**Parameter:**
- `message` (String): Any text message

**Response (200 OK):**
```
Echoed MessageHello
```

---

## H2 Database Console

**URL:** `http://localhost:8080/h2-console`

**Login Details:**
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **Username:** `sa`
- **Password:** (leave empty)

**Test Query:**
```sql
SELECT * FROM categories;
```

---

## Pre-loaded Data

The database automatically loads 30 categories on startup:
1. Mens T-Shirts
2. Smartphones
3. Apparel
4. Home Appliances
5. Toys
6. Furniture
7. Books
8. Sports Equipment
9. Beauty Products
10. Automotive
11. Outdoor Gear
12. Electronics
13. Kitchen Appliances
14. Baby Products
15. Health & Fitness
16. Garden & Outdoor
17. Pet Supplies
18. Office Supplies
19. Jewelry & Watches
20. Travel & Luggage
21. Musical Instruments
22. Crafts & Hobbies
23. Collectibles & Memorabilia
24. Art & Decor
25. Food & Beverages
26. Stationery & Gift Wrapping
27. Electrical & Lighting
28. DIY & Tools
29. Party Supplies
30. Educational Toys

---

## Error Handling

### Category Not Found (404)
```json
{
  "timestamp": "2025-12-17T11:27:54.174+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Category not found with categoryId: 999"
}
```

### Category Already Exists (400)
```json
{
  "timestamp": "2025-12-17T11:27:54.174+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Category with the name 'Electronics' already exists !!!"
}
```

### No Categories (500)
```json
{
  "timestamp": "2025-12-17T11:27:54.174+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "No category created till now.!!"
}
```

---

## cURL Commands (Alternative to Postman)

### GET Categories
```bash
curl "http://localhost:8080/api/public/categories?pageNumber=1&pageSize=10"
```

### POST Category
```bash
curl -X POST "http://localhost:8080/api/public/categories" ^
  -H "Content-Type: application/json" ^
  -d "{\"categoryName\":\"New Category\"}"
```

### PUT Category
```bash
curl -X PUT "http://localhost:8080/api/public/categories/1" ^
  -H "Content-Type: application/json" ^
  -d "{\"categoryName\":\"Updated Category\"}"
```

### DELETE Category
```bash
curl -X DELETE "http://localhost:8080/api/admin/categories/1"
```

---

## Tips

1. **Pagination Testing:** Try different `pageNumber` and `pageSize` values
2. **Validation:** Category name is required (validation enabled with `@Valid`)
3. **H2 Console:** Use to verify data directly in the database
4. **Auto-restart:** Changes require application restart

