# XML to CSV Converter - Quick Reference

## 🚀 Quick Start

```bash
# 1. Put your XML files in source/ directory
# 2. Run batch conversion
python xml_to_csv_converter.py --batch --validate
# 3. Get your CSV files from output/ directory
```

## 📋 Command Reference

| Command | Description |
|---------|-------------|
| `--batch` | Convert all XML files in source/ |
| `--batch --validate` | Batch convert + check results |
| `--file input.xml output.csv` | Convert single file |
| `--file input.xml output.csv --validate` | Single file + validation |
| `--help` | Show help message |

## 📁 Directory Structure

```
Converter/
├── source/     ← Put XML files here
├── output/     ← CSV files appear here  
├── document/   ← Documentation
└── *.py        ← Converter tools
```

## 🔄 Conversion Process

```
XML → Extract → Decode → Clean HTML → Fix Tags → CSV
```

1. **Extract**: Get Id, Title, Body, Tags, Score
2. **Decode**: Handle HTML entities (&lt;, &gt;, etc.)
3. **Clean**: Remove HTML tags from content
4. **Fix Tags**: Convert "|tag1|tag2|" → "tag1,tag2"
5. **CSV**: Generate with proper quoting

## 📊 Input/Output Format

### Input XML:
```xml
<row Id="123" Title="Question Title" 
     Body="&lt;p&gt;Content&lt;/p&gt;" 
     Tags="|python|xml|" Score="5" />
```

### Output CSV:
```csv
title,content,tags,postId,score
"Question Title","Content","python,xml","123","5"
```

## ⚡ Most Common Commands

```bash
# Batch process all files (most common)
python xml_to_csv_converter.py --batch --validate

# Convert one specific file
python xml_to_csv_converter.py --file mydata.xml result.csv --validate

# Test with examples
python convert_example.py
```

## 🛠️ Troubleshooting

| Problem | Solution |
|---------|----------|
| "No XML files found" | Check files are in `source/` with `.xml` extension |
| "XML parsing error" | Tool auto-fixes most issues, check file corruption |
| "Missing fields" | Ensure XML has: Id, Title, Body, Tags, Score |
| "Permission denied" | Check write access, close files in other programs |

## 💡 Pro Tips

- ✅ Always use `--validate` to check results
- ✅ Use batch mode for multiple files
- ✅ Keep original XML files as backup
- ✅ Test with `convert_example.py` first
- ✅ Check output/ directory for results

---

**Need more help?** See `USER_GUIDE.md` for detailed instructions. 