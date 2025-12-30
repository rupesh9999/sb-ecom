# Fix Summary Report
**Date:** December 30, 2025  
**Timestamp:** 22:20 IST

---

## Issue: USER_ADDRESS Table Not Appearing in H2 Database

### Problem Description
The user reported that the `USER_ADDRESS` table was not visible in the H2 database console while other tables like `ADDRESSES`, `CATEGORIES`, `PRODUCTS`, `ROLES`, `USERS`, and `USER_ROLE` were present.

### Root Cause Analysis
Upon investigation of the `User.java` entity file, I found an issue in the `@JoinTable` annotation for the user-address relationship:

**Original Code (Incorrect):**
```java
@JoinTable(name = "user_address", joinColumns = @JoinColumn(name = "user"), inverseJoinColumns = @JoinColumn(name = "address_id"))
```

**Issue:** The join column was set to `name = "user"` which is incorrect. It should reference the primary key column of the `users` table, which is `user_id`.

### Resolution Applied
**Fixed Code:**
```java
@JoinTable(name = "user_address", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "address_id"))
```

### File Modified
- `src/main/java/com/ecommerce/project/model/User.java` (Line 62)

### Verification
After the fix, the application logs show successful table creation:

```sql
Hibernate: create table user_address (user_id bigint not null, address_id bigint not null)
Hibernate: alter table if exists user_address add constraint FKpv7y2l6mvly37lngi3doarqhd foreign key (address_id) references addresses
Hibernate: alter table if exists user_address add constraint FKrmincuqpi8m660j1c57xj7twr foreign key (user_id) references users
```

### Database Schema After Fix

| Table Name | Description |
|------------|-------------|
| ADDRESSES | Stores address information (street, city, state, etc.) |
| CATEGORIES | Product categories |
| PRODUCTS | Product information with category and seller references |
| ROLES | User roles (ROLE_USER, ROLE_SELLER, ROLE_ADMIN) |
| USERS | User accounts (username, email, password) |
| USER_ROLE | Join table linking users to roles (Many-to-Many) |
| **USER_ADDRESS** | Join table linking users to addresses (Many-to-Many) |

### USER_ADDRESS Table Structure
| Column | Type | Constraints |
|--------|------|-------------|
| user_id | BIGINT | NOT NULL, FK to USERS |
| address_id | BIGINT | NOT NULL, FK to ADDRESSES |

### Build Status
✅ **Compilation:** Successful  
✅ **Table Creation:** Successful  
⚠️ **Application Start:** Failed due to port 8080 already in use (unrelated to this fix)

### Recommendations
1. To resolve the port conflict, either:
   - Stop the process using port 8080
   - Add `server.port=8081` to `application.properties`

2. Access H2 Console at: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `SA`
   - Password: (leave empty)

---

**Report Generated:** December 30, 2025 @ 22:20 IST

