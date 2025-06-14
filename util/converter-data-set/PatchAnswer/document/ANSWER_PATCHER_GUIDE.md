# Answer Patcher Tool - User Guide

## Overview

The Answer Patcher Tool extracts answers from large XML files based on question postIds. It matches question IDs from JSON files with ParentId in XML answer records, making it easy to extract related answers for specific questions.

## Key Features

- ✅ **Batch Processing**: Process all JSON files at once
- ✅ **Single File Mode**: Extract answers for specific question sets
- ✅ **Multiple Formats**: Output in XML, CSV, or both formats
- ✅ **Large File Support**: Efficiently handles large XML files (73MB+)
- ✅ **Progress Tracking**: Shows processing progress for large files
- ✅ **Safe Operations**: Creates backups before modifying source files
- ✅ **Detailed Reports**: Comprehensive statistics and summaries

## Directory Structure

```
PatchAnswer/
├── questions/          # JSON files with question postIds
├── output/             # Generated answer files
├── ../source/          # Source XML files (Posts-answers.xml)
├── document/           # Documentation
├── answer_patcher.py   # Main patcher tool
└── demo_patcher.py     # Demo script
```

## Quick Start

### 1. Basic Usage
```bash
# Extract all answers in XML format
python answer_patcher.py --all

# Extract all answers in CSV format
python answer_patcher.py --all --format csv

# Extract answers in both formats
python answer_patcher.py --all --format both
```

### 2. Specific JSON File
```bash
# Extract answers for specific questions
python answer_patcher.py --json questions/my_questions.json --format csv
```

### 3. Test the Tool
```bash
# Run interactive demo
python demo_patcher.py
```

## Command Reference

### Required Arguments (choose one)
- `--all` - Process all JSON files in questions/ directory
- `--json FILE` - Process specific JSON file

### Optional Arguments
- `--format {xml,csv,both}` - Output format (default: xml)
- `--remove-source` - Remove extracted answers from source XML
- `--help` - Show help message

## Input Data Format

### JSON Files (Question IDs)
Place JSON files in the `questions/` directory with this format:
```json
{
    "postIds": [
        88,
        1005,
        2898,
        5078
    ]
}
```

Alternative format also supported:
```json
{
    "postId": [1, 2, 3, 4, 5]
}
```

### XML Source File
The tool expects `../source/Posts-answers.xml` with answer records like:
```xml
<row Id="46075" PostTypeId="2" ParentId="32027" 
     CreationDate="2008-09-05T15:43:46.117" Score="1" 
     Body="&lt;p&gt;Content here&lt;/p&gt;" 
     OwnerUserId="3024" OwnerDisplayName="User Name" 
     LastActivityDate="2008-09-05T15:43:46.117" 
     CommentCount="0" ContentLicense="CC BY-SA 2.5" />
```

## Output Formats

### XML Output
```xml
<?xml version='1.0' encoding='utf-8'?>
<posts>
  <row Id="46075" PostTypeId="2" ParentId="32027" ... />
  <row Id="46076" PostTypeId="2" ParentId="32027" ... />
</posts>
```

### CSV Output
```csv
Id,PostTypeId,ParentId,CreationDate,Score,Body,OwnerUserId,OwnerDisplayName,LastActivityDate,CommentCount,ContentLicense
"46075","2","32027","2008-09-05T15:43:46.117","1","Content here","3024","User Name","2008-09-05T15:43:46.117","0","CC BY-SA 2.5"
```

## Usage Examples

### Example 1: Extract All Answers
```bash
# Process all JSON files and save as CSV
python answer_patcher.py --all --format csv
```

**Output:**
- Loads question IDs from all JSON files in `questions/`
- Searches through the entire answers XML file
- Saves matching answers to `output/all_questions_answers_TIMESTAMP.csv`

### Example 2: Specific Question Set
```bash
# Extract answers for specific questions
python answer_patcher.py --json questions/stackoverflow_raw_questions_postIds.json --format xml
```

**Output:**
- Loads IDs from the specified JSON file
- Extracts matching answers
- Saves to `output/stackoverflow_raw_questions_postIds_answers_TIMESTAMP.xml`

### Example 3: Clean Source File
```bash
# Extract answers and remove them from source
python answer_patcher.py --all --format both --remove-source
```

**Output:**
- Extracts answers to both XML and CSV
- Creates backup of source file
- Removes extracted answers from source XML

## Processing Large Files

The tool is optimized for large XML files:

### Performance Features
- **Streaming Processing**: Handles files larger than available RAM
- **Progress Tracking**: Shows processing rate (rows/second)
- **Memory Efficient**: Processes data in chunks
- **Auto-Fix XML**: Handles malformed XML structures

### Expected Performance
- **File Size**: 73MB XML file (~200K+ answers)
- **Processing Speed**: 10,000-50,000 rows/second
- **Memory Usage**: Moderate (depends on number of matches)

### XML Format Handling
- **Standard XML**: Files with single root element (works normally)
- **Multiple Root XML**: Files like Posts-answers.xml (automatically wrapped for parsing)
- **Write-back Support**: Both formats support `--remove-source` option
- **Format Preservation**: Original file structure maintained when writing back

## Error Handling

### Common Issues

**1. "Source XML file not found"**
- Ensure `../source/Posts-answers.xml` exists
- Check file path is correct

**2. "No JSON files found"**
- Place JSON files in `questions/` directory
- Ensure files have `.json` extension

**3. "XML parsing error"**
- Tool automatically fixes most XML issues (including multiple root elements)
- Check if source file is corrupted
- Multiple root XML files are automatically handled

**4. "No question IDs found"**
- Verify JSON format has `postIds` or `postId` key
- Check JSON is valid

### Safety Features

- **Automatic Backups**: Creates `.backup` files before modifications
- **Non-destructive**: Original files preserved unless `--remove-source` used
- **Validation**: Checks file existence and format before processing
- **Error Recovery**: Graceful handling of malformed data

## Output File Naming

Files are automatically named with timestamps:
```
Pattern: {source_name}_answers_{YYYYMMDD_HHMMSS}.{extension}

Examples:
- all_questions_answers_20240115_143022.xml
- stackoverflow_raw_questions_postIds_answers_20240115_143022.csv
```

## Summary Reports

The tool generates detailed reports showing:

```
============================================================
ANSWER PATCHING SUMMARY
============================================================
Total questions requested: 125
Questions with answers found: 98
Questions without answers: 27
Total answers extracted: 342
Average answers per question: 3.5
Max answers for a question: 15
Min answers for a question: 1

Questions without answers (27):
  1005
  2898
  5078
  ... and 24 more
============================================================
```

## Advanced Usage

### Integration with Scripts
```python
from answer_patcher import AnswerPatcher

patcher = AnswerPatcher()
patcher.patch_answers(
    specific_json="questions/my_data.json",
    output_format="csv",
    remove_from_source=False
)
```

### Custom Source Paths
Modify the source path in the AnswerPatcher class:
```python
patcher = AnswerPatcher()
patcher.source_xml = "/path/to/your/Posts-answers.xml"
```

### Batch Operations
Process multiple question sets:
```bash
# Process each JSON file separately
for json_file in questions/*.json; do
    python answer_patcher.py --json "$json_file" --format csv
done
```

## Performance Optimization

### For Large Files
1. Use CSV format for faster processing
2. Process one JSON file at a time for memory efficiency
3. Monitor disk space for output files
4. Consider splitting very large JSON files

### Memory Management
- Tool uses streaming XML parsing
- Memory usage scales with number of matches, not file size
- Typical RAM usage: 100-500MB for large operations

## Troubleshooting

### Debug Information
Run with verbose output by examining the console:
- Shows processing progress
- Reports file sizes and counts
- Displays timing information

### Validation Steps
1. Check directory structure
2. Verify JSON file format
3. Confirm source XML exists
4. Test with small JSON file first

### Common Solutions
- **Slow performance**: Use CSV format, process smaller batches
- **Memory issues**: Process one JSON file at a time
- **Missing answers**: Verify ParentId values match postIds
- **File permissions**: Ensure write access to output directory

## Support Files

### Demo Script
```bash
python demo_patcher.py
```
Interactive demonstration showing:
- Directory structure
- Available JSON files
- Usage examples
- Dry run capabilities

### Help System
```bash
python answer_patcher.py --help
```
Shows complete command reference and examples.

---

## Quick Reference

### Most Common Commands
```bash
# Extract all answers as CSV
python answer_patcher.py --all --format csv

# Extract specific questions as XML
python answer_patcher.py --json questions/my_file.json

# Extract with both formats
python answer_patcher.py --all --format both

# Run demo
python demo_patcher.py
```

### Directory Setup
1. Place JSON files in `questions/`
2. Ensure `../source/Posts-answers.xml` exists
3. Run the patcher
4. Find results in `output/`

**Happy Answer Patching! 🔧** 