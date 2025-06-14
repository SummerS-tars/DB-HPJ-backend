#!/usr/bin/env python3
"""
XML to CSV Converter Tool

This tool converts XML data to CSV format according to specific requirements:
1. Extract specific fields from XML
2. Handle special characters in content and title
3. Remove HTML tags from content
4. Convert tags format from "|tag1|tag2|" to "tag1,tag2"
5. Output in specific CSV format: title, content, tags, postID, score
"""

import xml.etree.ElementTree as ET
import csv
import re
import html
import argparse
import os
from typing import List, Dict, Any

class XMLToCSVConverter:
    def __init__(self):
        self.required_fields = ['Id', 'Title', 'Body', 'Tags', 'Score']
        self.output_headers = ['title', 'content', 'tags', 'postId', 'score']
    
    def step1_extract_data(self, xml_file: str) -> List[Dict[str, Any]]:
        """
        Step 1: Extract required data from XML file
        Extracts only the fields we need: Id, Title, Body, Tags, Score
        """
        print("Step 1: Extracting data from XML...")
        
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
                print(f"Skipping row with Id={row.get('Id', 'unknown')} - missing fields: {missing_fields}")
        
        print(f"Extracted {len(extracted_data)} complete records")
        return extracted_data
    
    def step2_handle_special_characters(self, data: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Step 2: Handle special characters in content and title
        Decode HTML entities and handle special characters
        """
        print("Step 2: Handling special characters...")
        
        processed_data = []
        
        for record in data:
            processed_record = record.copy()
            
            # Handle special characters in Title and Body
            for field in ['Title', 'Body']:
                if field in processed_record:
                    # Decode HTML entities
                    text = html.unescape(processed_record[field])
                    # Replace common HTML entities that might remain
                    text = text.replace('&amp;', '&')
                    text = text.replace('&lt;', '<')
                    text = text.replace('&gt;', '>')
                    text = text.replace('&quot;', '"')
                    text = text.replace('&#xA;', '\n')
                    processed_record[field] = text
            
            processed_data.append(processed_record)
        
        print(f"Processed special characters for {len(processed_data)} records")
        return processed_data
    
    def step3_remove_html_tags(self, data: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Step 3: Remove HTML tags from content
        Clean HTML tags from the Body field
        """
        print("Step 3: Removing HTML tags...")
        
        def clean_html(text: str) -> str:
            # Remove HTML tags using regex
            text = re.sub(r'<[^>]+>', '', text)
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
    
    def step4_convert_tags_format(self, data: List[Dict[str, Any]]) -> List[Dict[str, Any]]:
        """
        Step 4: Convert tags format from "|tag1|tag2|" to "tag1,tag2"
        """
        print("Step 4: Converting tags format...")
        
        def convert_tags(tags_str: str) -> str:
            if not tags_str:
                return ""
            
            # Remove leading and trailing pipes
            tags_str = tags_str.strip('|')
            
            # Split by pipe and join with comma
            if tags_str:
                tags_list = [tag.strip() for tag in tags_str.split('|') if tag.strip()]
                return ','.join(tags_list)
            
            return ""
        
        converted_data = []
        
        for record in data:
            converted_record = record.copy()
            
            # Convert Tags field
            if 'Tags' in converted_record:
                converted_record['Tags'] = convert_tags(converted_record['Tags'])
            
            converted_data.append(converted_record)
        
        print(f"Converted tags format for {len(converted_data)} records")
        return converted_data
    
    def step5_convert_to_csv_format(self, data: List[Dict[str, Any]], output_file: str):
        """
        Step 5: Convert to CSV format with specific column order
        Output format: title, content, tags, postId, score
        """
        print("Step 5: Converting to CSV format...")
        
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
                writer = csv.writer(csvfile, quoting=csv.QUOTE_ALL)
                for record in data:
                    row = [
                        record.get('Title', ''),      # title
                        record.get('Body', ''),       # content
                        record.get('Tags', ''),       # tags
                        record.get('Id', ''),         # postId
                        record.get('Score', '')       # score
                    ]
                    writer.writerow(row)
            
            print(f"Successfully converted {len(data)} records to CSV: {output_file}")
            
        except Exception as e:
            print(f"Error writing CSV file: {e}")
    
    def convert_xml_to_csv(self, xml_file: str, output_file: str):
        """
        Main conversion function that runs all steps in sequence
        """
        print(f"Starting XML to CSV conversion...")
        print(f"Input file: {xml_file}")
        print(f"Output file: {output_file}")
        print("-" * 50)
        
        # Step 1: Extract data
        data = self.step1_extract_data(xml_file)
        if not data:
            print("No data extracted. Conversion failed.")
            return
        
        # Step 2: Handle special characters
        data = self.step2_handle_special_characters(data)
        
        # Step 3: Remove HTML tags
        data = self.step3_remove_html_tags(data)
        
        # Step 4: Convert tags format
        data = self.step4_convert_tags_format(data)
        
        # Step 5: Convert to CSV
        self.step5_convert_to_csv_format(data, output_file)
        
        print("-" * 50)
        print("Conversion completed!")
    
    def validate_conversion(self, csv_file: str):
        """
        Validation function to check the output CSV
        """
        print(f"\nValidating CSV file: {csv_file}")
        
        try:
            with open(csv_file, 'r', encoding='utf-8') as file:
                reader = csv.reader(file)
                headers = next(reader)
                
                print(f"Headers: {headers}")
                
                # Check if headers match expected format
                if headers == self.output_headers:
                    print("✓ Headers are correct")
                else:
                    print("✗ Headers don't match expected format")
                
                # Count rows
                row_count = sum(1 for _ in reader)
                print(f"Data rows: {row_count}")
                
                # Show first few rows as sample
                with open(csv_file, 'r', encoding='utf-8') as file:
                    reader = csv.reader(file)
                    next(reader)  # Skip header
                    
                    print("\nSample rows:")
                    for i, row in enumerate(reader):
                        if i >= 3:  # Show first 3 rows
                            break
                        print(f"Row {i+1}: {[field[:50] + '...' if len(field) > 50 else field for field in row]}")
                
        except Exception as e:
            print(f"Error validating CSV: {e}")

def batch_convert_all_files():
    """Convert all XML files in the source directory"""
    converter = XMLToCSVConverter()
    source_dir = "source"
    output_dir = "output"
    
    # Create output directory if it doesn't exist
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
        print(f"Created output directory: {output_dir}")
    
    # Find all XML files in source directory
    if not os.path.exists(source_dir):
        print(f"Error: Source directory '{source_dir}' not found")
        return
    
    xml_files = []
    for file in os.listdir(source_dir):
        if file.lower().endswith('.xml'):
            xml_files.append(file)
    
    if not xml_files:
        print(f"No XML files found in '{source_dir}' directory")
        return
    
    print(f"Found {len(xml_files)} XML files to convert:")
    for file in xml_files:
        print(f"  - {file}")
    
    print("\n" + "="*60)
    print("BATCH CONVERSION STARTED")
    print("="*60)
    
    successful_conversions = 0
    failed_conversions = 0
    
    for xml_file in xml_files:
        input_path = os.path.join(source_dir, xml_file)
        # Change extension from .xml to .csv
        csv_filename = os.path.splitext(xml_file)[0] + '.csv'
        output_path = os.path.join(output_dir, csv_filename)
        
        print(f"\nProcessing: {xml_file}")
        print("-" * 40)
        
        try:
            converter.convert_xml_to_csv(input_path, output_path)
            successful_conversions += 1
            print(f"✓ Successfully converted: {xml_file} -> {csv_filename}")
        except Exception as e:
            failed_conversions += 1
            print(f"✗ Failed to convert {xml_file}: {e}")
    
    print("\n" + "="*60)
    print("BATCH CONVERSION SUMMARY")
    print("="*60)
    print(f"Total files processed: {len(xml_files)}")
    print(f"Successful conversions: {successful_conversions}")
    print(f"Failed conversions: {failed_conversions}")
    
    if successful_conversions > 0:
        print(f"\nOutput files saved in: {output_dir}/")

def main():
    parser = argparse.ArgumentParser(
        description='Convert XML to CSV format',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Convert all XML files in source/ directory
  python xml_to_csv_converter.py --batch
  
  # Convert a specific file
  python xml_to_csv_converter.py --file input.xml output.csv
  
  # Convert a specific file with validation
  python xml_to_csv_converter.py --file input.xml output.csv --validate
        """
    )
    
    # Create mutually exclusive group for batch vs single file
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument('--batch', action='store_true', 
                      help='Convert all XML files in source/ directory to output/ directory')
    group.add_argument('--file', nargs=2, metavar=('INPUT_XML', 'OUTPUT_CSV'),
                      help='Convert a specific XML file to CSV')
    
    parser.add_argument('--validate', action='store_true', 
                       help='Validate the output CSV file(s)')
    
    args = parser.parse_args()
    
    # Create converter instance
    converter = XMLToCSVConverter()
    
    if args.batch:
        # Batch conversion mode
        batch_convert_all_files()
        
        # Validate all output files if requested
        if args.validate:
            output_dir = "output"
            if os.path.exists(output_dir):
                csv_files = [f for f in os.listdir(output_dir) if f.lower().endswith('.csv')]
                if csv_files:
                    print(f"\n{'='*60}")
                    print("VALIDATION RESULTS")
                    print("="*60)
                    for csv_file in csv_files:
                        csv_path = os.path.join(output_dir, csv_file)
                        print(f"\nValidating: {csv_file}")
                        converter.validate_conversion(csv_path)
    
    else:
        # Single file conversion mode
        input_xml, output_csv = args.file
        
        # Check if input file exists
        if not os.path.exists(input_xml):
            print(f"Error: Input file '{input_xml}' not found")
            return
        
        # Run conversion
        converter.convert_xml_to_csv(input_xml, output_csv)
        
        # Validate if requested
        if args.validate:
            converter.validate_conversion(output_csv)

if __name__ == "__main__":
    main() 