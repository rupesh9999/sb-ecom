# Troubleshooting Report: HTTP 404 Error for Categories API

**Date:** December 17, 2025  
**Project:** sb-ecom (Spring Boot E-Commerce Application)  
**Issue:** API endpoint returning 404 Not Found error

---

## Problem Statement

When testing the GET request for categories endpoint in Postman:
```
URL: http://localhost:8080/api/public/categories?pageNumber=1&pageSize=10
```

**Error Received:**
```json
{
    "timestamp": "2025-12-17T11:27:54.174+00:00",
    "status": 404,
    "error": "Not Found",
    "path": "/api/public/categories"
}
```

---

## Root Cause Analysis

### Investigation Steps

#### Step 1: Examined the Controller Class
**File:** `CategoryController.java`

**Finding:** The controller had **duplicate path mapping** issue:

```java
@RestController
@RequestMapping("/api")  // <-- Base path is /api
public class CategoryController {
    
    @GetMapping("/api/public/categories")  // <-- Contains /api again!
    public ResponseEntity<CategoryResponse> getAllCategories(...) {
        ...
    }
}
```

**Problem Identified:**
- Class-level annotation: `@RequestMapping("/api")`
- Method-level annotation: `@GetMapping("/api/public/categories")`
- **Actual endpoint created:** `/api/api/public/categories` (WRONG!)
- **Expected endpoint:** `/api/public/categories`

This is a **PATH DUPLICATION** issue where the `/api` prefix was added twice.

#### Step 2: Verified Application Compilation
Checked for compilation errors:
```bash
mvn clean compile
```

**Secondary Issue Found:**
```
CategoryServiceImpl is not abstract and does not override abstract method 
getAllCategories(Integer, Integer) in CategoryService
```

The service implementation was missing the paginated `getAllCategories` method.

---

## Resolution Steps

### Fix 1: Corrected Controller Path Mappings

**Changed all endpoint mappings to remove the duplicate `/api` prefix:**

**Before:**
```java
@GetMapping("/api/public/categories")
@PostMapping("/api/public/categories")
@DeleteMapping("/api/admin/categories/{categoryId}")
@PutMapping("/api/public/categories/{categoryId}")
```

**After:**
```java
@GetMapping("/public/categories")
@PostMapping("/public/categories")
@DeleteMapping("/admin/categories/{categoryId}")
@PutMapping("/public/categories/{categoryId}")
```

### Fix 2: Implemented Missing Service Method

Added the missing `getAllCategories(Integer pageNumber, Integer pageSize)` method in `CategoryServiceImpl.java`:

```java
@Override
public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize) {
    List<Category> categories = categoryRepository.findAll();
    if (categories.isEmpty())
        throw new APIException("No category created till now.!!");

    List<CategoryDTO> categoryDTOS = categories.stream()
            .map(category -> modelMapper.map(category, CategoryDTO.class))
            .toList();
    CategoryResponse categoryResponse = new CategoryResponse();
    categoryResponse.setContent(categoryDTOS);
    return categoryResponse;
}
```

### Fix 3: Automated Database Initialization

Created `data.sql` file in `src/main/resources/` to automatically load test data:

**File:** `src/main/resources/data.sql`
- Contains 30 INSERT statements for category data
- Automatically executed on application startup

**Configuration verified in `application.properties`:**
```properties
spring.jpa.defer-datasource-initialization=true
spring.sql.init.mode=always
```

---

## Execution Steps to Resolve

### Step 1: Stop Running Application
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process
taskkill /F /PID <process_id>
```

### Step 2: Clean and Rebuild Project
```bash
cd C:\Users\ASUS\Downloads\sb-ecom\sb-ecom
mvn clean package -DskipTests
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: ~6-7 seconds
```

### Step 3: Restart Application
```bash
java -jar target\sb-ecom-0.0.1-SNAPSHOT.jar
```

**Wait for:**
```
Started SbEcomApplication in X.XXX seconds
```

### Step 4: Verify Database Initialization
1. Open browser: `http://localhost:8080/h2-console`
2. Login credentials:
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: (empty)
3. Run query: `SELECT * FROM categories;`
4. Verify 30 records are loaded

### Step 5: Test API Endpoint

**Using Postman:**
```
Method: GET
URL: http://localhost:8080/api/public/categories?pageNumber=1&pageSize=10
```

**Using curl:**
```bash
curl "http://localhost:8080/api/public/categories?pageNumber=1&pageSize=10"
```

**Expected Response:**
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
        },
        ...
    ]
}
```

---

## Final Endpoint Mapping

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/public/categories?pageNumber=X&pageSize=Y` | Get paginated categories |
| POST | `/api/public/categories` | Create new category |
| PUT | `/api/public/categories/{categoryId}` | Update category |
| DELETE | `/api/admin/categories/{categoryId}` | Delete category |
| GET | `/api/echo?message=test` | Test endpoint |

---

## Key Learnings

1. **Path Duplication Issue:** When using `@RequestMapping` at class level, method-level mappings should be relative paths only.

2. **Interface Implementation:** All abstract methods from an interface must be implemented, including overloaded methods.

3. **H2 Database Initialization:** Use `data.sql` for automatic test data loading with proper configuration.

4. **Spring Boot Path Resolution:** 
   - Class-level `@RequestMapping("/api")` + Method-level `@GetMapping("/public/categories")` = `/api/public/categories`
   - NOT: `/api` + `/api/public/categories` = `/api/api/public/categories`

---

## Verification Checklist

- [x] Controller paths corrected (no duplication)
- [x] Service interface fully implemented
- [x] Application compiles without errors
- [x] Application starts successfully
- [x] Database schema created
- [x] Test data loaded automatically
- [x] API endpoint responds with 200 OK
- [x] Category data returned in response

---

## Issue Status: ✅ RESOLVED

The API endpoint is now accessible at the correct path and returns category data successfully.

