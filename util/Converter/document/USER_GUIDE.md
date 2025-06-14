# XML to CSV Converter - User Guide

## Quick Start

The XML to CSV Converter is a powerful tool that transforms XML data into CSV format. It supports two main modes:

1. **Batch Mode**: Convert all XML files in the `source/` directory at once
2. **Single File Mode**: Convert one specific XML file

## Directory Structure

```
Converter/
├── source/          # Place your XML files here
├── output/          # CSV files will be saved here
├── document/        # Documentation files
├── xml_to_csv_converter.py    # Main converter tool
└── convert_example.py         # Example/demo script
```

## Usage

### 1. Batch Conversion (Recommended)

Convert all XML files in the `source/` directory:

```bash
# Basic batch conversion
python xml_to_csv_converter.py --batch

# Batch conversion with validation
python xml_to_csv_converter.py --batch --validate
```

**Steps:**
1. Place your XML files in the `source/` directory
2. Run the batch conversion command
3. Find your CSV files in the `output/` directory

### 2. Single File Conversion

Convert one specific XML file:

```bash
# Convert a specific file
python xml_to_csv_converter.py --file input.xml output.csv

# With validation
python xml_to_csv_converter.py --file input.xml output.csv --validate
```

### 3. Get Help

```bash
python xml_to_csv_converter.py --help
```

## Examples

### Example 1: Batch Processing

```bash
# Put your XML files in source/
# source/questions_2023.xml
# source/posts_data.xml
# source/user_content.xml

# Run batch conversion
python xml_to_csv_converter.py --batch --validate

# Results will be in output/
# output/questions_2023.csv
# output/posts_data.csv  
# output/user_content.csv
```

### Example 2: Single File

```bash
# Convert one file
python xml_to_csv_converter.py --file my_data.xml results.csv --validate
```

### Example 3: Using Relative Paths

```bash
# Convert from anywhere
python xml_to_csv_converter.py --file ../data/input.xml ../results/output.csv
```

## Input XML Format

The converter expects XML files with `<row>` elements containing these attributes:

- `Id` - Record identifier
- `Title` - Title text
- `Body` - Content (may contain HTML)
- `Tags` - Tags in format `"|tag1|tag2|tag3|"`
- `Score` - Numeric score

### Sample XML Structure:
```xml
<row Id="1001" Title="Sample Question" 
     Body="&lt;p&gt;This is the question content&lt;/p&gt;" 
     Tags="|python|xml|csv|" Score="5" />
<row Id="1002" Title="Another Question" 
     Body="&lt;p&gt;More content here&lt;/p&gt;" 
     Tags="|programming|help|" Score="3" />
```

## Output CSV Format

The converter produces CSV files with these columns (in order):

1. **title** - Clean title text
2. **content** - HTML-free content  
3. **tags** - Comma-separated tags
4. **postId** - Original ID
5. **score** - Score value

### Sample CSV Output:
```csv
title,content,tags,postId,score
"Sample Question","This is the question content","python,xml,csv","1001","5"
"Another Question","More content here","programming,help","1002","3"
```

## Conversion Process

The tool follows a 5-step process:

1. **Extract Data**: Gets required fields from XML
2. **Handle Special Characters**: Decodes HTML entities
3. **Remove HTML Tags**: Cleans markup from content
4. **Convert Tags**: Changes `"|tag1|tag2|"` to `"tag1,tag2"`
5. **Generate CSV**: Creates final CSV with proper quoting

## Features

### ✅ Automatic XML Fixing
- Handles XML files with multiple root elements
- Automatically wraps content when needed

### ✅ Smart Quoting
- Header row: unquoted (`title,content,tags,postId,score`)
- Data rows: all fields quoted (`"title","content","tags","postId","score"`)

### ✅ HTML Processing
- Removes HTML tags from content
- Decodes HTML entities (`&lt;`, `&gt;`, `&amp;`, etc.)
- Preserves meaningful text

### ✅ Batch Processing
- Converts multiple files at once
- Shows progress and summary
- Handles errors gracefully

### ✅ Validation
- Checks output format
- Shows sample data
- Verifies conversion success

## Troubleshooting

### Common Issues

**1. "No XML files found in source directory"**
- Make sure XML files are in the `source/` folder
- Check file extensions (should be `.xml`)

**2. "XML parsing error"**
- The tool automatically fixes most XML issues
- Check if your XML file is corrupted

**3. "Missing required fields"**
- Ensure your XML has: Id, Title, Body, Tags, Score
- Check attribute names match exactly

**4. "Permission denied"**
- Make sure you have write access to the output directory
- Check if output files are open in another program

### Getting Debug Information

Use the validation flag to see detailed information:

```bash
python xml_to_csv_converter.py --batch --validate
```

This shows:
- Number of records processed
- Sample output data
- Validation results
- File sizes and locations

## Advanced Usage

### Custom File Locations

You can specify files outside the default directories:

```bash
# Convert from any location
python xml_to_csv_converter.py --file /path/to/input.xml /path/to/output.csv

# Using relative paths
python xml_to_csv_converter.py --file ../data/input.xml ./results/output.csv
```

### Processing Large Files

For very large XML files:
1. Use single file mode to process one at a time
2. Monitor memory usage
3. Consider splitting large files if needed

### Integration with Scripts

You can call the converter from other Python scripts:

```python
from xml_to_csv_converter import XMLToCSVConverter

converter = XMLToCSVConverter()
converter.convert_xml_to_csv("input.xml", "output.csv")
converter.validate_conversion("output.csv")
```

## Testing the Tool

Run the example script to test with sample data:

```bash
python convert_example.py
```

This will:
- Create sample XML files
- Demonstrate batch conversion
- Show single file conversion
- Display usage examples
- Offer to clean up demo files

## Support

If you encounter issues:

1. Check this user guide
2. Run with `--validate` flag for more information
3. Test with the example script
4. Verify your XML format matches the expected structure

## File Management Tips

### Organizing Your Files

```
Converter/
├── source/
│   ├── 2023/
│   │   ├── jan_data.xml
│   │   └── feb_data.xml
│   └── 2024/
│       └── current_data.xml
├── output/
│   ├── 2023/
│   │   ├── jan_data.csv
│   │   └── feb_data.csv
│   └── 2024/
│       └── current_data.csv
```

### Backup Strategy

- Keep original XML files safe
- Archive processed CSV files
- Use version control for important data

---

**Happy Converting! 🎉**

Place your XML files in `source/` and run:
```bash
python xml_to_csv_converter.py --batch --validate
``` 