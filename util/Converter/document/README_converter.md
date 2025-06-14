# XML to CSV Converter Tools

This collection of tools converts XML data to CSV format according to your specific requirements. The conversion process is broken down into clear, debuggable steps as requested.

## Files Overview

- `xml_to_csv_converter.py` - Main converter tool with step-by-step processing
- `convert_example.py` - Simple example showing how to use the converter
- `step_by_step_test.py` - Interactive testing tool for debugging each step
- `README_converter.md` - This documentation file

## Requirements

- Python 3.6+
- Standard library modules only (no external dependencies)

## Conversion Process

The tool follows your specified 5-step process:

1. **Extract Data**: Selects only required fields (Id, Title, Body, Tags, Score)
2. **Handle Special Characters**: Decodes HTML entities and special characters
3. **Remove HTML Tags**: Cleans HTML markup from content
4. **Convert Tags Format**: Changes "|tag1|tag2|" to "tag1,tag2"
5. **Generate CSV**: Creates final CSV with columns: title, content, tags, postId, score

## Usage Options

### Option 1: Command Line Tool

```bash
# Basic usage
python xml_to_csv_converter.py input.xml output.csv

# With validation
python xml_to_csv_converter.py input.xml output.csv --validate

# Example with your reference files
python xml_to_csv_converter.py xml-converter/xml/problem.xml my_output.csv --validate
```

### Option 2: Simple Example Script

```bash
python convert_example.py
```

This will automatically convert your reference XML file and compare with the expected output.

### Option 3: Step-by-Step Testing (Recommended for debugging)

```bash
python step_by_step_test.py
```

This interactive tool allows you to:
- Run each step individually
- Inspect intermediate results
- Compare with reference data
- Debug any issues step by step

## Input Format

The tool expects XML files with `<row>` elements containing these attributes:
- `Id` - Question ID
- `Title` - Question title
- `Body` - Question content (may contain HTML)
- `Tags` - Tags in format "|tag1|tag2|tag3|"
- `Score` - Question score

Example XML structure:
```xml
<row Id="27266" Title="Example Title" Body="&lt;p&gt;Content with HTML&lt;/p&gt;" Tags="|linux|ssh|" Score="6" />
```

## Output Format

The tool generates CSV files with exactly these columns in this order:
1. `title` - Clean title text
2. `content` - HTML-free content
3. `tags` - Comma-separated tags
4. `postId` - Original ID
5. `score` - Score value

## Features

### Special Character Handling
- Decodes HTML entities (`&lt;`, `&gt;`, `&amp;`, etc.)
- Handles Unicode characters properly
- Preserves text formatting while removing HTML

### HTML Tag Removal
- Removes all HTML tags from content
- Cleans up extra whitespace
- Preserves meaningful text structure

### Tags Conversion
- Converts from `"|tag1|tag2|tag3|"` to `"tag1,tag2,tag3"`
- Handles empty tags gracefully
- Trims whitespace from individual tags

### Error Handling
- Validates input files exist
- Handles malformed XML gracefully
- Provides clear error messages
- Validates output format

## Example Workflow

1. **Start with step-by-step testing**:
   ```bash
   python step_by_step_test.py
   ```

2. **Inspect each step's output** to ensure correctness

3. **Generate final CSV** once satisfied with results

4. **Use command-line tool** for batch processing:
   ```bash
   python xml_to_csv_converter.py your_data.xml final_output.csv --validate
   ```

## Troubleshooting

### Common Issues

1. **XML parsing errors**: Check if your XML file is well-formed
2. **Missing fields**: Ensure your XML has all required attributes
3. **Encoding issues**: The tool uses UTF-8 encoding by default
4. **Large files**: The tool processes files in memory; very large files may need chunking

### Debugging Steps

1. Use `step_by_step_test.py` to isolate issues
2. Check the console output for each step
3. Examine intermediate results
4. Compare with reference data
5. Validate final CSV format

## Customization

To modify the conversion for different requirements:

1. Edit `required_fields` in `XMLToCSVConverter.__init__()`
2. Modify `output_headers` for different column names
3. Adjust individual step functions for different processing logic
4. Add custom validation rules in `validate_conversion()`

## Testing

The tools include comprehensive testing features:
- Step-by-step validation
- Reference comparison
- Output format validation
- Sample data display

Run the interactive tester to verify everything works correctly with your data. 