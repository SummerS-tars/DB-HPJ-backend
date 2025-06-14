#!/usr/bin/env python3
"""
Answer Patcher Tool

This tool extracts answers from a large XML file based on question postIds.
It matches postIds from JSON files with ParentId in the XML answers file.

Features:
- Process multiple JSON files with postIds
- Extract matching answers from XML
- Option to remove extracted answers from source
- Batch processing support
- Progress tracking for large files
"""

import xml.etree.ElementTree as ET
import json
import argparse
import os
import sys
from typing import List, Dict, Set, Any
import time

class AnswerPatcher:
    def __init__(self):
        self.questions_dir = "questions"
        self.output_dir = "output"
        self.source_xml = "../source/Posts-answers.xml"
        
        # Answer XML attributes
        self.answer_attributes = [
            'Id', 'PostTypeId', 'ParentId', 'CreationDate', 'Score', 
            'Body', 'OwnerUserId', 'OwnerDisplayName', 'LastActivityDate', 
            'CommentCount', 'ContentLicense'
        ]
    
    def load_question_ids(self, json_file: str) -> Set[str]:
        """Load question postIds from JSON file"""
        try:
            with open(json_file, 'r', encoding='utf-8') as f:
                data = json.load(f)
            
            # Support both "postIds" and "postId" keys
            if 'postIds' in data:
                post_ids = data['postIds']
            elif 'postId' in data:
                post_ids = data['postId']
            else:
                print(f"Warning: No 'postIds' or 'postId' key found in {json_file}")
                return set()
            
            # Convert all IDs to strings for consistent matching
            return {str(pid) for pid in post_ids}
            
        except FileNotFoundError:
            print(f"Error: JSON file not found: {json_file}")
            return set()
        except json.JSONDecodeError as e:
            print(f"Error: Invalid JSON in {json_file}: {e}")
            return set()
        except Exception as e:
            print(f"Error loading {json_file}: {e}")
            return set()
    
    def load_all_question_ids(self) -> Set[str]:
        """Load question postIds from all JSON files in questions directory"""
        if not os.path.exists(self.questions_dir):
            print(f"Error: Questions directory not found: {self.questions_dir}")
            return set()
        
        all_ids = set()
        json_files = [f for f in os.listdir(self.questions_dir) if f.endswith('.json')]
        
        if not json_files:
            print(f"No JSON files found in {self.questions_dir}")
            return set()
        
        print(f"Loading question IDs from {len(json_files)} JSON files...")
        
        for json_file in json_files:
            file_path = os.path.join(self.questions_dir, json_file)
            ids = self.load_question_ids(file_path)
            all_ids.update(ids)
            print(f"  {json_file}: {len(ids)} question IDs")
        
        print(f"Total unique question IDs: {len(all_ids)}")
        return all_ids
    
    def _write_unwrapped_xml(self, root, output_file):
        """Write XML content back in unwrapped format (without root wrapper)"""
        try:
            # Get all child elements (the original row elements)
            rows = root.findall('.//row')
            
            with open(output_file, 'w', encoding='utf-8') as f:
                for i, row in enumerate(rows):
                    # Convert element back to string and write with proper formatting
                    row_str = ET.tostring(row, encoding='unicode').strip()
                    f.write(f"  {row_str}\n")
                    
                    # Add blank line between rows (but not after the last row)
                    if i < len(rows) - 1:
                        f.write("\n")
            
            print(f"Successfully wrote {len(rows)} rows back to unwrapped format")
            
        except Exception as e:
            print(f"Error writing unwrapped XML: {e}")
            # Fallback: write the full tree (wrapped format)
            tree = ET.ElementTree(root)
            tree.write(output_file, encoding='utf-8', xml_declaration=True)
            print("Wrote in wrapped format as fallback")
    
    def extract_matching_answers(self, question_ids: Set[str], remove_from_source: bool = False) -> List[ET.Element]:
        """Extract answers that match the question IDs"""
        if not os.path.exists(self.source_xml):
            print(f"Error: Source XML file not found: {self.source_xml}")
            return []
        
        print(f"Processing answers XML file: {self.source_xml}")
        print(f"Looking for answers to {len(question_ids)} questions...")
        
        # For large files, we need to handle XML parsing carefully
        tree = None
        xml_was_wrapped = False
        original_content = None
        try:
            # First, try to parse normally
            try:
                tree = ET.parse(self.source_xml)
                root = tree.getroot()
            except ET.ParseError as e:
                # If parsing fails, try wrapping content in root element
                print(f"Initial parse failed: {e}")
                print("Attempting to fix XML structure...")
                
                with open(self.source_xml, 'r', encoding='utf-8') as f:
                    original_content = f.read()
                
                wrapped_content = f"<root>\n{original_content}\n</root>"
                root = ET.fromstring(wrapped_content)
                # Create tree object for the wrapped content
                tree = ET.ElementTree(root)
                xml_was_wrapped = True
        
        except Exception as e:
            print(f"Error reading XML file: {e}")
            return []
        
        # Find all answer rows
        answer_rows = root.findall('.//row')
        print(f"Found {len(answer_rows)} total answer rows")
        
        matching_answers = []
        removed_answers = []
        
        start_time = time.time()
        processed = 0
        
        for row in answer_rows:
            processed += 1
            
            # Show progress for large files
            if processed % 10000 == 0:
                elapsed = time.time() - start_time
                rate = processed / elapsed
                print(f"  Processed {processed:,} rows ({rate:.1f} rows/sec)")
            
            # Check if this is an answer (PostTypeId = 2) and matches our questions
            post_type = row.get('PostTypeId', '')
            parent_id = row.get('ParentId', '')
            
            if post_type == '2' and parent_id in question_ids:
                matching_answers.append(row)
                if remove_from_source:
                    removed_answers.append(row)
        
        # Remove from source if requested
        if remove_from_source and removed_answers:
            print(f"Removing {len(removed_answers)} answers from source file...")
            for answer in removed_answers:
                root.remove(answer)
            
            # Save modified source file
            backup_file = self.source_xml + '.backup'
            if not os.path.exists(backup_file):
                # Create backup first
                import shutil
                shutil.copy2(self.source_xml, backup_file)
                print(f"Created backup: {backup_file}")
            
            if tree is not None:
                if xml_was_wrapped:
                    # For wrapped XML, we need to unwrap and write back in original format
                    self._write_unwrapped_xml(root, self.source_xml)
                else:
                    # For normal XML, write normally
                    tree.write(self.source_xml, encoding='utf-8', xml_declaration=True)
                print(f"Updated source file: {self.source_xml}")
            else:
                print("Error: Cannot write to source file - tree object is None")
        
        elapsed = time.time() - start_time
        print(f"Extraction completed in {elapsed:.1f} seconds")
        print(f"Found {len(matching_answers)} matching answers")
        
        return matching_answers
    
    def save_answers_to_xml(self, answers: List[ET.Element], output_filename: str):
        """Save extracted answers to XML file"""
        if not answers:
            print("No answers to save")
            return
        
        # Ensure output directory exists
        if not os.path.exists(self.output_dir):
            os.makedirs(self.output_dir)
            print(f"Created output directory: {self.output_dir}")
        
        output_path = os.path.join(self.output_dir, output_filename)
        
        # Create root element for the output XML
        root = ET.Element("posts")
        
        # Add all matching answers
        for answer in answers:
            root.append(answer)
        
        # Create tree and save
        tree = ET.ElementTree(root)
        tree.write(output_path, encoding='utf-8', xml_declaration=True)
        
        print(f"Saved {len(answers)} answers to: {output_path}")
        
        # Show file size
        file_size = os.path.getsize(output_path)
        print(f"Output file size: {file_size:,} bytes ({file_size/1024/1024:.1f} MB)")
    
    def save_answers_to_csv(self, answers: List[ET.Element], output_filename: str):
        """Save extracted answers to CSV file"""
        if not answers:
            print("No answers to save")
            return
        
        import csv
        
        # Ensure output directory exists
        if not os.path.exists(self.output_dir):
            os.makedirs(self.output_dir)
        
        output_path = os.path.join(self.output_dir, output_filename)
        
        with open(output_path, 'w', newline='', encoding='utf-8') as csvfile:
            # Write header without quotes
            csvfile.write(','.join(self.answer_attributes) + '\n')
            
            # Write data rows with all fields quoted
            writer = csv.writer(csvfile, quoting=csv.QUOTE_ALL)
            
            for answer in answers:
                row = []
                for attr in self.answer_attributes:
                    value = answer.get(attr, '')
                    
                    # Clean HTML from Body field
                    if attr == 'Body' and value:
                        import re
                        import html
                        # Decode HTML entities
                        value = html.unescape(value)
                        # Remove HTML tags
                        value = re.sub(r'<[^>]+>', '', value)
                        # Clean up whitespace
                        value = re.sub(r'\s+', ' ', value).strip()
                    
                    row.append(value)
                
                writer.writerow(row)
        
        print(f"Saved {len(answers)} answers to CSV: {output_path}")
    
    def generate_summary_report(self, question_ids: Set[str], answers: List[ET.Element]):
        """Generate a summary report of the patching operation"""
        # Group answers by ParentId to show statistics
        answers_by_question = {}
        for answer in answers:
            parent_id = answer.get('ParentId', '')
            if parent_id not in answers_by_question:
                answers_by_question[parent_id] = []
            answers_by_question[parent_id].append(answer)
        
        print(f"\n{'='*60}")
        print("ANSWER PATCHING SUMMARY")
        print("="*60)
        print(f"Total questions requested: {len(question_ids)}")
        print(f"Questions with answers found: {len(answers_by_question)}")
        print(f"Questions without answers: {len(question_ids) - len(answers_by_question)}")
        print(f"Total answers extracted: {len(answers)}")
        
        if answers_by_question:
            answer_counts = [len(answers) for answers in answers_by_question.values()]
            print(f"Average answers per question: {sum(answer_counts) / len(answer_counts):.1f}")
            print(f"Max answers for a question: {max(answer_counts)}")
            print(f"Min answers for a question: {min(answer_counts)}")
        
        # Show questions without answers
        questions_without_answers = question_ids - set(answers_by_question.keys())
        if questions_without_answers:
            print(f"\nQuestions without answers ({len(questions_without_answers)}):")
            for qid in sorted(questions_without_answers, key=int)[:10]:  # Show first 10
                print(f"  {qid}")
            if len(questions_without_answers) > 10:
                print(f"  ... and {len(questions_without_answers) - 10} more")
        
        print("="*60)
    
    def patch_answers(self, specific_json: str = None, output_format: str = 'xml', 
                     remove_from_source: bool = False):
        """Main patching function"""
        print("Answer Patcher - Starting patch operation")
        print("="*60)
        
        # Load question IDs
        if specific_json:
            if not os.path.exists(specific_json):
                print(f"Error: Specified JSON file not found: {specific_json}")
                return
            
            question_ids = self.load_question_ids(specific_json)
            base_name = os.path.splitext(os.path.basename(specific_json))[0]
        else:
            question_ids = self.load_all_question_ids()
            base_name = "all_questions"
        
        if not question_ids:
            print("No question IDs found. Exiting.")
            return
        
        # Extract matching answers
        answers = self.extract_matching_answers(question_ids, remove_from_source)
        
        if not answers:
            print("No matching answers found.")
            return
        
        # Save results
        timestamp = time.strftime("%Y%m%d_%H%M%S")
        
        if output_format.lower() == 'xml':
            output_filename = f"{base_name}_answers_{timestamp}.xml"
            self.save_answers_to_xml(answers, output_filename)
        elif output_format.lower() == 'csv':
            output_filename = f"{base_name}_answers_{timestamp}.csv"
            self.save_answers_to_csv(answers, output_filename)
        else:
            # Save both formats
            xml_filename = f"{base_name}_answers_{timestamp}.xml"
            csv_filename = f"{base_name}_answers_{timestamp}.csv"
            self.save_answers_to_xml(answers, xml_filename)
            self.save_answers_to_csv(answers, csv_filename)
        
        # Generate summary report
        self.generate_summary_report(question_ids, answers)

def main():
    parser = argparse.ArgumentParser(
        description='Extract answers for specific questions from XML file',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Patch answers for all questions in JSON files
  python answer_patcher.py --all
  
  # Patch answers for specific JSON file
  python answer_patcher.py --json questions/my_questions.json
  
  # Save as CSV format
  python answer_patcher.py --all --format csv
  
  # Remove extracted answers from source file
  python answer_patcher.py --all --remove-source
  
  # Save in both XML and CSV formats
  python answer_patcher.py --all --format both
        """
    )
    
    # Input options
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument('--all', action='store_true',
                      help='Process all JSON files in questions/ directory')
    group.add_argument('--json', metavar='JSON_FILE',
                      help='Process specific JSON file')
    
    # Output options
    parser.add_argument('--format', choices=['xml', 'csv', 'both'], default='xml',
                       help='Output format (default: xml)')
    parser.add_argument('--remove-source', action='store_true',
                       help='Remove extracted answers from source XML file')
    
    args = parser.parse_args()
    
    # Create patcher instance
    patcher = AnswerPatcher()
    
    # Run patching operation
    if args.all:
        patcher.patch_answers(
            specific_json=None,
            output_format=args.format,
            remove_from_source=args.remove_source
        )
    else:
        patcher.patch_answers(
            specific_json=args.json,
            output_format=args.format,
            remove_from_source=args.remove_source
        )

if __name__ == "__main__":
    main() 