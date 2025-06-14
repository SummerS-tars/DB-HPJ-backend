#!/usr/bin/env python3
"""
XML to CSV Answer Converter Tool

This tool converts XML answer data to CSV format according to specific requirements:
1. Extract specific fields from XML answer data
2. Handle special characters in content
3. Remove HTML tags from content
4. Output in specific CSV format: rawQuestionId, content, postId, score

Requirements:
- Input XML attributes: Id, PostTypeId, ParentId, CreationDate, Score, Body, OwnerUserId, etc.
- Output CSV fields: rawQuestionId (from ParentId), content (from Body), postId (from Id), score (from Score)
"""

import xml.etree.ElementTree as ET
import csv
import re
import html
import argparse
import os
from typing import List, Dict, Any

class XMLToCSVAnswerConverter:
    def __init__(self):
        self.required_fields = ['Id', 'ParentId', 'Body', 'Score']
        self.output_headers = ['rawQuestionId', 'content', 'postId', 'score']
    
    def step1_extract_data(self, xml_file: str) -> List[Dict[str, Any]]:
        """
        Step 1: Extract required data from XML file
        Extracts only the fields we need: Id, ParentId, Body, Score
        """
        print("Step 1: Extracting answer data from XML...")
        
        try:
            # First, try to parse the XML file normally
            try:
                tree = ET.parse(xml_file)
                root = tree.getroot()
            except ET.ParseError as e:
                # If parsing fails, it might be because of multiple root elements
                # Try wrapping the content in a root element
                print(f"Initial parse failed: {e}")
                print("Attempting to fix XML structure by adding root wrapper...")
                
                with open(xml_file, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                # Wrap the content in a root element
                wrapped_content = f"<root>\n{content}\n</root>"
                
                # Parse the wrapped content
                root = ET.fromstring(wrapped_content)
                
        except ET.ParseError as e:
            print(f"Error parsing XML file even after fixing: {e}")
            return []
        except FileNotFoundError:
            print(f"XML file not found: {xml_file}")
            return []
        except Exception as e:
            print(f"Unexpected error reading XML file: {e}")
            return []
        
        extracted_data = []
        
        # Find all row elements (could be direct children or nested)
        rows = root.findall('.//row')
        
        print(f"Found {len(rows)} row elements")
        
        for row in rows:
            # Only process answer rows (PostTypeId="2" indicates answers)
            post_type = row.get('PostTypeId', '')
            if post_type != '2':
                continue
                
            # Extract attributes from the row
            row_data = {}
            for field in self.required_fields:
                value = row.get(field, '')
                if value:  # Only include rows that have the required data
                    row_data[field] = value
            
            # Only add if we have all required fields
            if len(row_data) == len(self.required_fields):
                extracted_data.append(row_data)
            else:
                missing_fields = [f for f in self.required_fields if f not in row_data or not row_data[f]]
                print(f"Skipping answer row with Id={row.get('Id', 'unknown')} - missing fields: {missing_fields}")
        
        print(f"Extracted {len(extracted_data)} complete answer records")
        return extracted_data
    
    def step2_handle_special_characters(self, data: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Step 2: Handle special characters in content
        Decode HTML entities and handle special characters
        """
        print("Step 2: Handling special characters...")
        
        processed_data = []
        
        for record in data:
            processed_record = record.copy()
            
            # Handle special characters in Body
            if 'Body' in processed_record:
                # Decode HTML entities
                text = html.unescape(processed_record['Body'])
                # Replace common HTML entities that might remain
                text = text.replace('&amp;', '&')
                text = text.replace('&lt;', '<')
                text = text.replace('&gt;', '>')
                text = text.replace('&quot;', '"')
                text = text.replace('&#xA;', '\n')
                text = text.replace('&#10;', '\n')  # Common newline entity in answers
                processed_record['Body'] = text
            
            processed_data.append(processed_record)
        
        print(f"Processed special characters for {len(processed_data)} records")
        return processed_data
    
    def step3_remove_html_tags(self, data: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Step 3: Remove HTML tags from content
        Clean HTML tags from the Body field using a robust method that preserves code content
        """
        print("Step 3: Removing HTML tags...")
        
        def clean_html(text: str) -> str:
            """
            Robust HTML cleaning that handles code content with < and > operators
            Uses character-by-character parsing to distinguish HTML tags from content
            """
            # Parse character by character to find real HTML tags
            result = []
            i = 0
            while i < len(text):
                if text[i] == '<':
                    # Check if this looks like an HTML tag
                    tag_end = text.find('>', i)
                    if tag_end == -1:
                        # No closing >, just add the character
                        result.append(text[i])
                        i += 1
                        continue
                    
                    # Extract potential tag
                    potential_tag = text[i:tag_end + 1]
                    
                    # Check if it's a valid HTML tag pattern
                    # HTML tags start with a letter and contain only valid characters
                    if re.match(r'^<\s*/?[a-zA-Z][a-zA-Z0-9]*(?:\s+[^<>]*)?>\s*$', potential_tag):
                        # It's a valid HTML tag, skip it (add space instead)
                        result.append(' ')
                        i = tag_end + 1
                    else:
                        # Not a valid HTML tag, keep the < character
                        result.append(text[i])
                        i += 1
                else:
                    result.append(text[i])
                    i += 1
            
            text = ''.join(result)
            
            # Clean up multiple spaces and newlines
            text = re.sub(r'\s+', ' ', text)
            # Remove leading/trailing whitespace
            text = text.strip()
            return text
        
        cleaned_data = []
        
        for record in data:
            cleaned_record = record.copy()
            
            # Clean HTML from Body field
            if 'Body' in cleaned_record:
                cleaned_record['Body'] = clean_html(cleaned_record['Body'])
            
            cleaned_data.append(cleaned_record)
        
        print(f"Cleaned HTML tags for {len(cleaned_data)} records")
        return cleaned_data
    
    def step4_convert_to_csv_format(self, data: List[Dict[str, Any]], output_file: str):
        """
        Step 4: Convert to CSV format with specific column order
        Output format: rawQuestionId, content, postId, score
        """
        print("Step 4: Converting to CSV format...")
        
        # Ensure output directory exists
        output_dir = os.path.dirname(output_file)
        if output_dir and not os.path.exists(output_dir):
            os.makedirs(output_dir)
            print(f"Created output directory: {output_dir}")
        
        try:
            with open(output_file, 'w', newline='', encoding='utf-8') as csvfile:
                # Write header without quotes
                csvfile.write(','.join(self.output_headers) + '\n')
                
                # Write data rows with all fields quoted
                for record in data:
                    row_values = [
                        f'"{record.get("ParentId", "")}"',  # rawQuestionId
                        f'"{record.get("Body", "")}"',      # content
                        f'"{record.get("Id", "")}"',        # postId
                        f'"{record.get("Score", "")}"'      # score
                    ]
                    csvfile.write(','.join(row_values) + '\n')
            
            print(f"✓ Successfully created CSV file: {output_file}")
            print(f"  Records written: {len(data)}")
            
            # Show file size
            if os.path.exists(output_file):
                file_size = os.path.getsize(output_file)
                print(f"  File size: {file_size} bytes")
            
        except Exception as e:
            print(f"Error creating CSV file: {e}")
    
    def convert_xml_to_csv(self, xml_file: str, output_file: str):
        """
        Main conversion process - runs all steps in sequence
        """
        print(f"\n{'='*70}")
        print(f"CONVERTING ANSWER XML TO CSV")
        print(f"{'='*70}")
        print(f"Input file:  {xml_file}")
        print(f"Output file: {output_file}")
        print(f"{'='*70}")
        
        # Step 1: Extract data
        data = self.step1_extract_data(xml_file)
        if not data:
            print("❌ No data extracted. Conversion failed.")
            return False
        
        # Step 2: Handle special characters
        data = self.step2_handle_special_characters(data)
        
        # Step 3: Remove HTML tags
        data = self.step3_remove_html_tags(data)
        
        # Step 4: Convert to CSV
        self.step4_convert_to_csv_format(data, output_file)
        
        print(f"\n{'='*70}")
        print("✅ CONVERSION COMPLETED SUCCESSFULLY!")
        print(f"{'='*70}")
        
        return True
    
    def validate_conversion(self, csv_file: str):
        """
        Validate the converted CSV file
        """
        print(f"\n{'='*50}")
        print("VALIDATION REPORT")
        print(f"{'='*50}")
        
        if not os.path.exists(csv_file):
            print("❌ CSV file not found for validation")
            return False
        
        try:
            with open(csv_file, 'r', encoding='utf-8') as f:
                reader = csv.DictReader(f)
                
                # Check headers
                expected_headers = self.output_headers
                actual_headers = reader.fieldnames
                
                print(f"Expected headers: {expected_headers}")
                print(f"Actual headers:   {actual_headers}")
                
                if actual_headers == expected_headers:
                    print("✅ Headers match expected format")
                else:
                    print("❌ Headers do not match expected format")
                    return False
                
                # Count rows and check for data
                row_count = 0
                sample_rows = []
                
                for row in reader:
                    row_count += 1
                    if row_count <= 3:  # Keep first 3 rows as samples
                        sample_rows.append(row)
                
                print(f"✅ Total rows: {row_count}")
                
                # Show sample data
                if sample_rows:
                    print(f"\nSample data (first {len(sample_rows)} rows):")
                    for i, row in enumerate(sample_rows, 1):
                        print(f"  Row {i}:")
                        for header in expected_headers:
                            value = row[header][:50] + "..." if len(row[header]) > 50 else row[header]
                            print(f"    {header}: {value}")
                        print()
                
                print("✅ Validation completed successfully")
                return True
                
        except Exception as e:
            print(f"❌ Error during validation: {e}")
            return False

def batch_convert_all_files():
    """
    Convert all XML files in the source directory
    """
    source_dir = "source"
    output_dir = "output"
    
    print(f"\n{'='*70}")
    print("BATCH CONVERSION - ALL FILES")
    print(f"{'='*70}")
    
    if not os.path.exists(source_dir):
        print(f"❌ Source directory '{source_dir}' not found")
        print("Please create the source directory and add XML files to convert")
        return
    
    # Find all XML files in source directory
    xml_files = [f for f in os.listdir(source_dir) if f.lower().endswith('.xml')]
    
    if not xml_files:
        print(f"❌ No XML files found in '{source_dir}' directory")
        return
    
    print(f"Found {len(xml_files)} XML file(s) to convert:")
    for xml_file in xml_files:
        print(f"  - {xml_file}")
    
    # Create output directory if it doesn't exist
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
        print(f"Created output directory: {output_dir}")
    
    # Convert each file
    converter = XMLToCSVAnswerConverter()
    successful_conversions = 0
    
    for xml_file in xml_files:
        input_path = os.path.join(source_dir, xml_file)
        output_filename = xml_file.replace('.xml', '_answers.csv')
        output_path = os.path.join(output_dir, output_filename)
        
        print(f"\n{'-'*50}")
        print(f"Converting: {xml_file}")
        print(f"{'-'*50}")
        
        success = converter.convert_xml_to_csv(input_path, output_path)
        if success:
            successful_conversions += 1
    
    print(f"\n{'='*70}")
    print("BATCH CONVERSION SUMMARY")
    print(f"{'='*70}")
    print(f"Total files processed: {len(xml_files)}")
    print(f"Successful conversions: {successful_conversions}")
    print(f"Failed conversions: {len(xml_files) - successful_conversions}")
    
    if successful_conversions > 0:
        print(f"\n✅ Output files created in '{output_dir}' directory:")
        output_files = [f for f in os.listdir(output_dir) if f.endswith('_answers.csv')]
        for output_file in output_files:
            file_path = os.path.join(output_dir, output_file)
            file_size = os.path.getsize(file_path)
            print(f"  - {output_file} ({file_size} bytes)")

def main():
    """
    Main function to handle command line arguments and run conversion
    """
    parser = argparse.ArgumentParser(
        description="Convert XML answer data to CSV format",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Convert all XML files in source/ directory
  python xml_to_csv_answer_converter.py --batch
  
  # Convert all files with validation
  python xml_to_csv_answer_converter.py --batch --validate
  
  # Convert a specific file
  python xml_to_csv_answer_converter.py --file input.xml output.csv
  
  # Convert a specific file with validation
  python xml_to_csv_answer_converter.py --file input.xml output.csv --validate
        """
    )
    
    parser.add_argument('--batch', action='store_true',
                       help='Convert all XML files in source/ directory')
    
    parser.add_argument('--file', nargs=2, metavar=('INPUT', 'OUTPUT'),
                       help='Convert a specific file: --file input.xml output.csv')
    
    parser.add_argument('--validate', action='store_true',
                       help='Validate the converted CSV files')
    
    args = parser.parse_args()
    
    if args.batch:
        # Batch conversion
        batch_convert_all_files()
        
        if args.validate:
            # Validate all output files
            output_dir = "output"
            if os.path.exists(output_dir):
                csv_files = [f for f in os.listdir(output_dir) if f.endswith('_answers.csv')]
                converter = XMLToCSVAnswerConverter()
                
                for csv_file in csv_files:
                    csv_path = os.path.join(output_dir, csv_file)
                    print(f"\nValidating: {csv_file}")
                    converter.validate_conversion(csv_path)
    
    elif args.file:
        # Single file conversion
        input_file, output_file = args.file
        
        converter = XMLToCSVAnswerConverter()
        success = converter.convert_xml_to_csv(input_file, output_file)
        
        if success and args.validate:
            converter.validate_conversion(output_file)
    
    else:
        # No arguments provided, show help
        parser.print_help()

if __name__ == "__main__":
    main() 