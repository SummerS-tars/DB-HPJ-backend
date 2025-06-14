# Answer Patcher - Quick Reference

## 🚀 Quick Start

```bash
# 1. Place JSON files with question IDs in questions/
# 2. Extract all answers
python answer_patcher.py --all --format csv
# 3. Find results in output/
```

## 📋 Command Reference

| Command | Description |
|---------|-------------|
| `--all` | Process all JSON files in questions/ |
| `--json FILE` | Process specific JSON file |
| `--format xml` | Output as XML (default) |
| `--format csv` | Output as CSV |
| `--format both` | Output both formats |
| `--remove-source` | Remove extracted answers from source |
| `--help` | Show help |

## 📁 Directory Structure

```
PatchAnswer/
├── questions/          ← Put JSON files here
├── output/            ← Results appear here
├── ../source/         ← Posts-answers.xml location
├── answer_patcher.py  ← Main tool
└── demo_patcher.py    ← Interactive demo
```

## 🔧 How It Works

```
JSON postIds → Match ParentId in XML → Extract Answers → Save to Output
```

1. **Load**: Read question IDs from JSON files
2. **Match**: Find answers where ParentId = question postId
3. **Extract**: Get matching answer records
4. **Save**: Output in XML/CSV format

## 📊 Input/Output Formats

### Input JSON:
```json
{
    "postIds": [88, 1005, 2898, 5078]
}
```

### Output XML:
```xml
<posts>
  <row Id="46075" PostTypeId="2" ParentId="88" ... />
</posts>
```

### Output CSV:
```csv
Id,PostTypeId,ParentId,CreationDate,Score,Body,...
"46075","2","88","2008-09-05","1","Answer content",...
```

## ⚡ Most Common Commands

```bash
# Extract all answers as CSV (recommended)
python answer_patcher.py --all --format csv

# Extract specific questions
python answer_patcher.py --json questions/my_file.json --format xml

# Extract with both formats
python answer_patcher.py --all --format both

# Test with demo
python demo_patcher.py
```

## 🔍 Data Matching

| Source | Field | Matches | Target Field |
|--------|-------|---------|-------------|
| JSON | `postIds` array | → | XML `ParentId` |
| JSON | `postId` array | → | XML `ParentId` |

**Example Match:**
- JSON: `"postIds": [88, 1005]`
- XML: `ParentId="88"` ✅ (matches)
- XML: `ParentId="1005"` ✅ (matches)

## 📈 Performance

| File Size | Processing Time | Memory Usage |
|-----------|----------------|--------------|
| 73MB XML | 30-120 seconds | 100-500MB |
| 125 questions | ~5 seconds | <100MB |

**Progress Tracking:** Shows "Processed 10,000 rows (15,000 rows/sec)"

## 🛠️ Troubleshooting

| Problem | Solution |
|---------|----------|
| "Source XML file not found" | Check `../source/Posts-answers.xml` exists |
| "No JSON files found" | Place `.json` files in `questions/` |
| "No question IDs found" | Verify JSON has `postIds` or `postId` key |
| "XML parsing error" | Tool auto-fixes multiple root elements |
| Slow processing | Use `--format csv` for faster output |

## 📋 Output Files

**Naming Pattern:** `{source}_answers_{timestamp}.{ext}`

**Examples:**
- `all_questions_answers_20240115_143022.csv`
- `stackoverflow_raw_questions_postIds_answers_20240115_143022.xml`

## 📊 Summary Report Example

```
============================================================
ANSWER PATCHING SUMMARY
============================================================
Total questions requested: 125
Questions with answers found: 98
Questions without answers: 27
Total answers extracted: 342
Average answers per question: 3.5
============================================================
```

## 🎯 Best Practices

- ✅ Use CSV format for better performance
- ✅ Test with small JSON files first
- ✅ Keep backups of source XML files
- ✅ Monitor disk space for large outputs
- ✅ Use `demo_patcher.py` to explore features

## 💡 Pro Tips

- **Large Files**: Process one JSON at a time
- **Multiple Sets**: Use separate JSON files for different question groups
- **Backup**: Tool creates automatic backups when using `--remove-source`
- **Validation**: Check summary report for missing answers

---

**Need more help?** See `ANSWER_PATCHER_GUIDE.md` for detailed instructions. 