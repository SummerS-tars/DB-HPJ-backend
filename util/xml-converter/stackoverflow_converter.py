#!/usr/bin/env python3
"""
StackOverflow XML to CSV Converter

Converts StackOverflow data dump XML files to CSV format for backend import.

Requirements:
    pip install beautifulsoup4 lxml html2text

Usage:
    python stackoverflow_converter.py questions Posts.xml questions.csv
    python stackoverflow_converter.py answers Posts.xml answers.csv
"""

import xml.etree.ElementTree as ET
import csv
import sys
import html
import re
from typing import Optional, Dict, Any

try:
    from bs4 import BeautifulSoup
    from html2text import html2text
    HTML_CLEANING_AVAILABLE = True
except ImportError:
    print("Warning: beautifulsoup4 and html2text not available. Using basic HTML cleaning.")
    HTML_CLEANING_AVAILABLE = False


class StackOverflowConverter:
    def __init__(self):
        self.html_tag_pattern = re.compile(r'<[^>]+>')
        
    def clean_html_content(self, html_content: str) -> str:
        """Remove HTML tags and convert to plain text"""
        if not html_content:
            return ""
            
        if HTML_CLEANING_AVAILABLE:
            # Use html2text for better conversion
            try:
                # Remove excessive newlines and clean up
                cleaned = html2text(html_content)
                cleaned = re.sub(r'\n\s*\n', '\n', cleaned)  # Remove multiple newlines
                cleaned = cleaned.strip()
                return cleaned
            except Exception:
                pass
        
        # Fallback to basic cleaning
        # Decode HTML entities first
        content = html.unescape(html_content)
        
        # Remove HTML tags
        content = self.html_tag_pattern.sub('', content)
        
        # Clean up whitespace
        content = re.sub(r'\s+', ' ', content)
        content = content.strip()
        
        return content
    
    def convert_tags_format(self, tags: str) -> str:
        """Convert tags from |tag1|tag2| format to tag1,tag2"""
        if not tags:
            return ""
            
        # Remove leading and trailing |
        if tags.startswith('|'):
            tags = tags[1:]
        if tags.endswith('|'):
            tags = tags[:-1]
            
        # Replace | with ,
        return tags.replace('|', ',')
    
    def convert_questions(self, input_file: str, output_file: str):
        """Convert StackOverflow questions XML to CSV"""
        print(f"Converting questions from {input_file} to {output_file}")
        
        # Parse XML
        tree = ET.parse(input_file)
        root = tree.getroot()
        
        questions_found = 0
        questions_processed = 0
        
        with open(output_file, 'w', newline='', encoding='utf-8') as csvfile:
            writer = csv.writer(csvfile, quoting=csv.QUOTE_MINIMAL)
            
            # Write header
            writer.writerow(['title', 'content', 'tags', 'postId', 'score'])
            
            for row in root.findall('.//row'):
                # Only process questions (PostTypeId=1)
                if row.get('PostTypeId') != '1':
                    continue
                    
                questions_found += 1
                
                try:
                    post_id = row.get('Id', '')
                    title = row.get('Title', '')
                    body = row.get('Body', '')
                    tags = row.get('Tags', '')
                    score = row.get('Score', '0')
                    
                    # Skip if essential fields are missing
                    if not title or not body:
                        continue
                    
                    # Clean title and content
                    title = self.clean_html_content(title)
                    body = self.clean_html_content(body)
                    
                    # Convert tags format
                    tags = self.convert_tags_format(tags)
                    
                    # Write CSV row
                    writer.writerow([
                        title,
                        body,
                        tags,
                        post_id,
                        score if score else '0'
                    ])
                    
                    questions_processed += 1
                    
                    if questions_processed % 1000 == 0:
                        print(f"Processed {questions_processed} questions...")
                        
                except Exception as e:
                    print(f"Error processing question {questions_found}: {e}")
                    continue
        
        print(f"Questions found: {questions_found}")
        print(f"Questions processed: {questions_processed}")
        print(f"Output written to: {output_file}")
    
    def convert_answers(self, input_file: str, output_file: str):
        """Convert StackOverflow answers XML to CSV"""
        print(f"Converting answers from {input_file} to {output_file}")
        
        # Parse XML
        tree = ET.parse(input_file)
        root = tree.getroot()
        
        answers_found = 0
        answers_processed = 0
        
        with open(output_file, 'w', newline='', encoding='utf-8') as csvfile:
            writer = csv.writer(csvfile, quoting=csv.QUOTE_MINIMAL)
            
            # Write header
            writer.writerow(['rawQuestionId', 'content', 'postId', 'score'])
            
            for row in root.findall('.//row'):
                # Only process answers (PostTypeId=2)
                if row.get('PostTypeId') != '2':
                    continue
                    
                answers_found += 1
                
                try:
                    post_id = row.get('Id', '')
                    parent_id = row.get('ParentId', '')
                    body = row.get('Body', '')
                    score = row.get('Score', '0')
                    
                    # Skip if essential fields are missing
                    if not parent_id or not body:
                        continue
                    
                    # Clean content
                    body = self.clean_html_content(body)
                    
                    # Write CSV row
                    # Note: Using ParentId as rawQuestionId for now
                    # In production, you'd need to map this to actual database IDs
                    writer.writerow([
                        parent_id,  # This maps to rawQuestionId
                        body,
                        post_id,
                        score if score else '0'
                    ])
                    
                    answers_processed += 1
                    
                    if answers_processed % 1000 == 0:
                        print(f"Processed {answers_processed} answers...")
                        
                except Exception as e:
                    print(f"Error processing answer {answers_found}: {e}")
                    continue
        
        print(f"Answers found: {answers_found}")
        print(f"Answers processed: {answers_processed}")
        print(f"Output written to: {output_file}")


def main():
    if len(sys.argv) != 4:
        print("Usage: python stackoverflow_converter.py <type> <input.xml> <output.csv>")
        print("  type: 'questions' or 'answers'")
        print("\nExample:")
        print("  python stackoverflow_converter.py questions Posts.xml questions.csv")
        print("  python stackoverflow_converter.py answers Posts.xml answers.csv")
        sys.exit(1)
    
    conversion_type = sys.argv[1]
    input_file = sys.argv[2]
    output_file = sys.argv[3]
    
    converter = StackOverflowConverter()
    
    try:
        if conversion_type == 'questions':
            converter.convert_questions(input_file, output_file)
        elif conversion_type == 'answers':
            converter.convert_answers(input_file, output_file)
        else:
            print("Error: Invalid type. Use 'questions' or 'answers'")
            sys.exit(1)
            
        print("Conversion completed successfully!")
        
    except Exception as e:
        print(f"Error during conversion: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main() 