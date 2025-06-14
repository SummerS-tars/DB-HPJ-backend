# StackOverflow XML to CSV Converter

This directory contains tools to convert StackOverflow data dump XML files to CSV format for import into the LLM Evaluation Backend.

## Overview

StackOverflow provides data dumps in XML format, but our backend expects CSV format. These tools handle:

- **XML Parsing**: Extracts relevant fields from StackOverflow XML
- **HTML Cleaning**: Removes HTML tags from content and converts to plain text
- **Tag Format Conversion**: Converts `|tag1|tag2|` format to `tag1,tag2`
- **Field Mapping**: Maps XML attributes to our database schema

## Current Controller Requirements

### Raw Questions Controller Accepts:
- `title` (String, required)
- `content` (String)
- `sourcePlatform` (String)
- `tags` (String)
- `postId` (Integer)
- `score` (Integer)

### Raw Answers Controller Accepts:
- `rawQuestionId` (Integer, required) - **Important**: This should be the database ID, not the original post ID
- `content` (String)
- `sourcePlatform` (String)
- `postId` (Integer)
- `score` (Integer)

## XML to CSV Mapping

### Questions Mapping:
```
XML Attribute     → CSV Field      → Database Field
Id               → postId         → post_id
Title            → title          → title
Body             → content        → content
Tags             → tags           → tags
Score            → score          → score
(hardcoded)      → sourcePlatform → source_platform
```

### Answers Mapping:
```
XML Attribute     → CSV Field        → Database Field
ParentId         → rawQuestionId    → raw_question_id (needs mapping!)
Body             → content          → content
Id               → postId           → post_id
Score            → score            → score
(hardcoded)      → sourcePlatform   → source_platform
```

## Tools Available

### 1. Python Script (Recommended)

**File**: `stackoverflow_converter.py`

**Requirements**:
```bash
pip install beautifulsoup4 lxml html2text
```

**Usage**:
```bash
# Convert questions
python stackoverflow_converter.py questions Posts.xml questions.csv

# Convert answers
python stackoverflow_converter.py answers Posts.xml answers.csv
```

**Features**:
- Better HTML cleaning using `html2text`
- Proper CSV escaping
- Progress reporting
- Error handling

### 2. Java Utility

**File**: `StackOverflowXmlConverter.java`

**Usage**:
```bash
# Compile
javac StackOverflowXmlConverter.java

# Convert questions
java StackOverflowXmlConverter questions Posts.xml questions.csv

# Convert answers
java StackOverflowXmlConverter answers Posts.xml answers.csv
```

## Important Notes

### 1. Answer ID Mapping Issue

**Problem**: The XML `ParentId` field refers to the original StackOverflow question ID, but our database needs the internal `raw_question_id`.

**Current Solution**: The converter outputs `ParentId` directly as `rawQuestionId`. This means:

1. **You must import questions first**
2. **You must handle the ID mapping**

**Better Solutions**:

#### Option A: Two-pass import
```bash
# 1. Import questions first
python stackoverflow_converter.py questions Posts.xml questions.csv
# Upload questions.csv to backend

# 2. Create ID mapping file by querying your database
# SELECT id as raw_question_id, post_id FROM raw_questions;

# 3. Use mapping to convert answers
# (You'd need to modify the script)
```

#### Option B: Enhanced converter with database connection
```python
# Connect to your database and build mapping
cursor.execute("SELECT id, post_id FROM raw_questions")
mapping = {str(post_id): raw_id for raw_id, post_id in cursor.fetchall()}

# Use mapping during conversion
raw_question_id = mapping.get(parent_id)
```

### 2. Data Cleaning

The tools handle:
- **HTML Tag Removal**: Converts HTML content to plain text
- **HTML Entity Decoding**: `&lt;` → `<`, `&amp;` → `&`, etc.
- **Tag Format**: `|java|spring|` → `java,spring`
- **CSV Escaping**: Handles quotes and commas properly

### 3. Missing Fields

The tools set default values for:
- `sourcePlatform`: "stackoverflow"
- `score`: 0 (if empty)

## Example Output

### Questions CSV:
```csv
title,content,tags,postId,score
"How to use Spring Boot?","I want to learn Spring Boot framework","java,spring,spring-boot",1001,10
"What is REST API?","Can someone explain REST API concepts?","rest,api,web-services",1002,8
```

### Answers CSV:
```csv
rawQuestionId,content,postId,score
1001,"You can start with Spring Boot by creating a new project",2001,12
1002,"REST API is an architectural style for web services",2003,15
```

## Workflow

1. **Download StackOverflow data dump** (Posts.xml)
2. **Convert questions**: `python stackoverflow_converter.py questions Posts.xml questions.csv`
3. **Import questions** to backend via `/api/v1/raw-questions/import`
4. **Convert answers**: `python stackoverflow_converter.py answers Posts.xml answers.csv`
5. **Handle ID mapping** (see notes above)
6. **Import answers** to backend via `/api/v1/raw-answers/import`

## Troubleshooting

### Large Files
For very large XML files (>1GB), consider:
- Processing in chunks
- Using streaming XML parsing
- Filtering by date range or score threshold

### Memory Issues
If you get out-of-memory errors:
- Use smaller input files
- Increase Python/Java heap size
- Process questions and answers separately

### Character Encoding
All output is UTF-8 encoded. If you have encoding issues:
- Ensure your terminal supports UTF-8
- Check the original XML encoding
- Use text editors that support UTF-8 