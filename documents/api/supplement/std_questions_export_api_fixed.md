# Standard Questions Export API - Fixed

## ✅ **Correct Usage**

### **Endpoint:**
```
GET /api/v1/std-questions/export
```

### **Required Parameters:**
- `type`: Question type (`OBJECTIVE` or `SUBJECTIVE`)
- `version`: Version filter (e.g., `v1.0`, `v1.1-beta`)

### **Optional Parameters:**
- `tag`: Tag filter (e.g., `Linux`, `Security`, `SSH`)

## 🔧 **Fixed Examples:**

### **Export with version and type only:**
```bash
GET http://localhost:8080/api/v1/std-questions/export?type=SUBJECTIVE&version=v1.1-beta
```

### **Export with version, type, and tag:**
```bash
GET http://localhost:8080/api/v1/std-questions/export?type=OBJECTIVE&version=v1.0&tag=Linux
```

### **cURL Examples:**
```bash
# Without tag
curl -X GET "http://localhost:8080/api/v1/std-questions/export?type=SUBJECTIVE&version=v1.1-beta" \
  -H "Accept: application/json" \
  -o "v1.1-beta_subjective.json"

# With tag
curl -X GET "http://localhost:8080/api/v1/std-questions/export?type=OBJECTIVE&version=v1.0&tag=Linux" \
  -H "Accept: application/json" \
  -o "v1.0_objective_linux.json"
```

## 📁 **File Naming:**
- **Without tag:** `{version}_{type}.json`
- **With tag:** `{version}_{type}_{tag}.json`

**Examples:**
- `v1.1-beta_subjective.json`
- `v1.0_objective_linux.json`

## ❌ **Common Mistakes to Avoid:**

1. **Missing `/v1` in URL:**
   - ❌ Wrong: `/api/std-questions/export`
   - ✅ Correct: `/api/v1/std-questions/export`

2. **Missing required parameters:**
   - ❌ Wrong: `?type=OBJECTIVE`
   - ✅ Correct: `?type=OBJECTIVE&version=v1.0`

3. **Wrong parameter values:**
   - ❌ Wrong: `type=objective` (lowercase)
   - ✅ Correct: `type=OBJECTIVE` (uppercase) 