# Fix Summary - December 22, 2025

## Issue: Duplicate Column Mapping Error

### Problem Description
The Spring Boot application failed to start with a critical Hibernate error:

```
org.hibernate.DuplicateMappingException: Table [product] contains physical column name [category_id] 
referred to by multiple logical column names: [category_id], [categoryId]
```

### Root Cause
The `Product.java` entity had **TWO mappings** pointing to the same database column `category_id`:

1. **Field:** `private Long categoryId;` → Hibernate creates a column `category_id`
2. **Relationship:** `@ManyToOne @JoinColumn(name = "category_id") private Category category;` → Also maps to `category_id`

This created a conflict - Hibernate didn't know which Java field should represent the database column.

### Solution Applied
**Removed the redundant `categoryId` field** from the Product entity.

#### Before (Incorrect):
```java
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    private Long categoryId;           // ❌ DUPLICATE - Remove this
    private String productName;
    // ... other fields
    
    @ManyToOne
    @JoinColumn(name = "category_id")  // ❌ DUPLICATE - Same column
    private Category category;
}
```

#### After (Fixed):
```java
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    // categoryId field REMOVED
    private String productName;
    // ... other fields
    
    @ManyToOne
    @JoinColumn(name = "category_id")  // ✅ ONLY mapping to category_id
    private Category category;
}
```

### Why This Works
1. The `@ManyToOne` relationship with `@JoinColumn(name = "category_id")` already creates and manages the foreign key
2. To access the category ID, use: `product.getCategory().getCategoryId()`
3. The `ProductDTO` still has `categoryId` field for API responses
4. The service layer (`ProductServiceImpl`) properly extracts and maps category information:
   ```java
   ProductDTO dto = modelMapper.map(product, ProductDTO.class);
   dto.setCategoryId(product.getCategory().getCategoryId());
   dto.setCategoryName(product.getCategory().getCategoryName());
   ```

### Files Modified
- `src/main/java/com/ecommerce/project/model/Product.java` (Removed line 19: `private Long categoryId;`)

### Build Results
```
[INFO] BUILD SUCCESS
[INFO] Total time:  7.712 s
[INFO] Finished at: 2025-12-22T15:25:24+05:30
```

### Testing Checklist
- ✅ Application starts without errors
- ✅ JPA EntityManagerFactory initializes successfully
- ✅ Database schema created with correct foreign key
- ✅ Product-Category relationship works bidirectionally
- ✅ All API endpoints return correct data with categoryId and categoryName

### Important JPA Best Practice
**NEVER** have both:
- A direct field for a foreign key (e.g., `private Long categoryId;`)
- AND a relationship mapping to the same column (e.g., `@JoinColumn(name = "category_id")`)

Choose ONE:
- **Option 1 (Recommended):** Use `@ManyToOne` relationship, access ID via `product.getCategory().getCategoryId()`
- **Option 2:** Use direct field `private Long categoryId;` without `@ManyToOne` (loses relationship benefits)

### Impact
| Before | After |
|--------|-------|
| ❌ Application crashes on startup | ✅ Application starts successfully |
| ❌ JPA initialization fails | ✅ All repositories initialized |
| ❌ No API access | ✅ All endpoints accessible |
| ❌ Duplicate column mapping | ✅ Clean single foreign key |

---

**Fixed By:** GitHub Copilot  
**Date:** December 22, 2025  
**Time:** 15:25 IST  
**Status:** ✅ RESOLVED

