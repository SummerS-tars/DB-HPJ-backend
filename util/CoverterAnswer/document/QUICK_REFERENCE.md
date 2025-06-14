# Answer Converter - Quick Reference

## Quick Start

```bash
# 1. Place XML files in source/ directory
cp your_answers.xml source/

# 2. Run batch conversion
python xml_to_csv_answer_converter.py --batch --validate

# 3. Check results in output/ directory
ls output/
```

## Command Summary

| Command | Purpose |
|---------|---------|
| `--batch` | Convert all XML files in source/ |
| `--file input.xml output.csv` | Convert specific file |
| `--validate` | Validate output CSV format |
| `--help` | Show help information |

## Input/Output Format

### Input (XML)

```xml
<row Id="68203188" PostTypeId="2" ParentId="67987244" 
     Score="3" Body="&lt;p&gt;Answer content&lt;/p&gt;" />
```

### Output (CSV)

```csv
rawQuestionId,content,postId,score
"67987244","Answer content","68203188","3"
```

## Field Mapping

| CSV Column | XML Attribute | Description |
|------------|---------------|-------------|
| `rawQuestionId` | `ParentId` | Parent question ID |
| `content` | `Body` | Answer content (HTML removed) |
| `postId` | `Id` | Answer ID |
| `score` | `Score` | Answer score |

## Common Commands

### Batch Processing

```bash
# Convert all files
python xml_to_csv_answer_converter.py --batch

# Convert with validation
python xml_to_csv_answer_converter.py --batch --validate
```

### Single File

```bash
# Basic conversion
python xml_to_csv_answer_converter.py --file answers.xml output.csv

# With validation
python xml_to_csv_answer_converter.py --file answers.xml output.csv --validate
```

### Demo

```bash
# Run demonstration
python convert_answer_example.py
```

## File Structure

```
CoverterAnswer/
├── xml_to_csv_answer_converter.py    # Main tool
├── convert_answer_example.py         # Demo script
├── source/                           # Input XML files
├── output/                           # Output CSV files
└── document/                         # Documentation
```

## Error Handling

- **XML parsing errors**: Tool auto-fixes malformed XML
- **Missing fields**: Skips incomplete records
- **No answer records**: Creates empty CSV with headers
- **Permission errors**: Check file/directory permissions

## Validation Checks

When using `--validate`:

- ✅ CSV file exists and readable
- ✅ Headers match expected format  
- ✅ Record count and sample data
- ✅ Field content verification

## Performance

- **Speed**: ~10K-50K records/second
- **Memory**: ~1-2x file size
- **File limit**: ~100MB recommended

## Integration

### Python API

```python
from xml_to_csv_answer_converter import XMLToCSVAnswerConverter

converter = XMLToCSVAnswerConverter()
success = converter.convert_xml_to_csv('input.xml', 'output.csv')
```

### Pandas

```python
import pandas as pd
df = pd.read_csv('output.csv')
```

## Troubleshooting

| Problem | Solution |
|---------|----------|
| No records extracted | Check PostTypeId="2" in XML |
| Empty output | Verify required fields present |
| Permission denied | Check directory write permissions |
| Malformed CSV | Use --validate to check format |

## File Naming

Batch processing naming pattern:

- `answers.xml` → `answers_answers.csv`
- `stackoverflow_answers.xml` → `stackoverflow_answers_answers.csv`

## Key Features

- ✅ **PostType Filtering**: Only processes answer records (PostTypeId="2")
- ✅ **HTML Processing**: Removes HTML tags and decodes entities
- ✅ **Batch Support**: Process multiple files at once
- ✅ **Validation**: Verify output format and content
- ✅ **Error Recovery**: Handle malformed XML gracefully
- ✅ **Standard Library**: No external dependencies

## Support

1. Check documentation in `document/` folder
2. Run example script: `python convert_answer_example.py`
3. Use validation: `--validate` flag
4. Check console output for detailed error messages
