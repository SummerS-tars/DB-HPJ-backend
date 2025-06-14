✅ Updated Export Functionality - Tag is Now Optional:
1. UI Changes
✅ Form Label: Changed "标签" to "标签 (可选)" to indicate it's optional
✅ Placeholder: Updated to "选择标签（可选）"
✅ Clearable: Added clearable attribute so users can clear the tag selection
✅ Validation Message: Updated to only mention required parameters (type + version)
2. Validation Logic
✅ canExport: Now only requires type and version (removed tag requirement)
✅ Error Message: Updated to reflect only required parameters
3. Filename Generation
✅ Fallback: When no tag is selected, uses "all" in filename
✅ Examples:
With tag: v1.0_objective_linux.json
Without tag: v1.0_objective_all.json
4. API Call
✅ Conditional Parameter: Only adds tag parameter to URL if it's provided
✅ URL Examples:
With tag: /api/std-questions/export?type=OBJECTIVE&version=v1.0&tag=Linux
Without tag: /api/std-questions/export?type=OBJECTIVE&version=v1.0
5. User Experience
Users can now export all questions of a specific type and version without filtering by tag
The form clearly indicates which fields are required vs optional
The filename preview updates appropriately based on tag selection
The export functionality now correctly handles the tag parameter as optional, allowing users to export either:
Filtered by tag: Get questions matching specific type, version, and tag
All questions: Get all questions matching type and version (regardless of tag)