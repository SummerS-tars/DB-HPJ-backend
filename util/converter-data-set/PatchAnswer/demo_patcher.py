#!/usr/bin/env python3
"""
Answer Patcher Demo Script

This script demonstrates how to use the answer patcher tool.
It shows examples of both single JSON file processing and batch processing.
"""

import os
import json
import subprocess
import sys
from answer_patcher import AnswerPatcher

def show_directory_structure():
    """Show the current directory structure"""
    print("Current Directory Structure:")
    print("=" * 40)
    
    items = [
        ("questions/", "JSON files with question postIds"),
        ("output/", "Generated answer files"),
        ("../source/", "Source XML files"),
        ("document/", "Documentation"),
        ("answer_patcher.py", "Main patcher tool"),
        ("demo_patcher.py", "This demo script")
    ]
    
    for path, description in items:
        if os.path.exists(path):
            if os.path.isdir(path):
                file_count = len([f for f in os.listdir(path) if os.path.isfile(os.path.join(path, f))])
                print(f"  📁 {path:<20} {description} ({file_count} files)")
            else:
                file_size = os.path.getsize(path)
                print(f"  📄 {path:<20} {description} ({file_size:,} bytes)")
        else:
            print(f"  ❌ {path:<20} {description} (not found)")

def show_available_json_files():
    """Show available JSON files with question IDs"""
    questions_dir = "questions"
    
    if not os.path.exists(questions_dir):
        print(f"Questions directory not found: {questions_dir}")
        return []
    
    json_files = [f for f in os.listdir(questions_dir) if f.endswith('.json')]
    
    if not json_files:
        print("No JSON files found in questions directory")
        return []
    
    print(f"\nAvailable JSON files in {questions_dir}/:")
    print("=" * 50)
    
    for json_file in json_files:
        file_path = os.path.join(questions_dir, json_file)
        try:
            with open(file_path, 'r') as f:
                data = json.load(f)
            
            # Count postIds
            if 'postIds' in data:
                count = len(data['postIds'])
            elif 'postId' in data:
                count = len(data['postId'])
            else:
                count = 0
            
            file_size = os.path.getsize(file_path)
            print(f"  📄 {json_file}")
            print(f"     Question IDs: {count}")
            print(f"     File size: {file_size:,} bytes")
            
            # Show sample IDs
            if 'postIds' in data and data['postIds']:
                sample_ids = data['postIds'][:5]
                print(f"     Sample IDs: {sample_ids}")
                if len(data['postIds']) > 5:
                    print(f"     ... and {len(data['postIds']) - 5} more")
            
            print()
            
        except Exception as e:
            print(f"  ❌ {json_file} (error reading: {e})")
    
    return json_files

def demonstrate_help():
    """Show the help message for the patcher"""
    print("\n" + "=" * 60)
    print("ANSWER PATCHER HELP")
    print("=" * 60)
    
    try:
        result = subprocess.run([
            sys.executable, "answer_patcher.py", "--help"
        ], capture_output=True, text=True)
        
        print(result.stdout)
        
        if result.stderr:
            print("Errors:")
            print(result.stderr)
            
    except Exception as e:
        print(f"Error running help command: {e}")

def demonstrate_dry_run():
    """Demonstrate what the patcher would do without actually running it"""
    print("\n" + "=" * 60)
    print("DRY RUN DEMONSTRATION")
    print("=" * 60)
    
    # Load question IDs
    patcher = AnswerPatcher()
    question_ids = patcher.load_all_question_ids()
    
    if not question_ids:
        print("No question IDs found for dry run")
        return
    
    print(f"Would search for answers to {len(question_ids)} questions")
    print(f"Sample question IDs: {list(question_ids)[:10]}")
    
    # Check if source file exists
    if os.path.exists(patcher.source_xml):
        file_size = os.path.getsize(patcher.source_xml)
        print(f"Source XML file: {patcher.source_xml}")
        print(f"Source file size: {file_size:,} bytes ({file_size/1024/1024:.1f} MB)")
    else:
        print(f"❌ Source XML file not found: {patcher.source_xml}")
        return
    
    print(f"Output directory: {patcher.output_dir}")
    print("Would extract matching answers and save to output files")

def run_example_command(command_description: str, command_args: list):
    """Run an example command and show results"""
    print(f"\n{'='*60}")
    print(f"EXAMPLE: {command_description}")
    print("="*60)
    
    print(f"Command: python answer_patcher.py {' '.join(command_args)}")
    print("-" * 40)
    
    try:
        result = subprocess.run([
            sys.executable, "answer_patcher.py"
        ] + command_args, capture_output=True, text=True)
        
        print("OUTPUT:")
        print(result.stdout)
        
        if result.stderr:
            print("ERRORS:")
            print(result.stderr)
        
        if result.returncode == 0:
            print("✅ Command completed successfully")
        else:
            print(f"❌ Command failed with exit code {result.returncode}")
            
    except Exception as e:
        print(f"❌ Error running command: {e}")

def show_usage_examples():
    """Show common usage examples"""
    print("\n" + "=" * 60)
    print("COMMON USAGE EXAMPLES")
    print("=" * 60)
    
    examples = [
        ("Extract answers for all questions (XML format)", ["--all"]),
        ("Extract answers for all questions (CSV format)", ["--all", "--format", "csv"]),
        ("Extract answers in both XML and CSV formats", ["--all", "--format", "both"]),
        ("Extract answers for specific JSON file", ["--json", "questions/stackoverflow_raw_questions_postIds.json"]),
        ("Extract answers and remove from source", ["--all", "--remove-source"]),
    ]
    
    for description, args in examples:
        print(f"\n{description}:")
        print(f"  python answer_patcher.py {' '.join(args)}")

def interactive_demo():
    """Interactive demonstration"""
    print("\n" + "=" * 60)
    print("INTERACTIVE DEMO")
    print("=" * 60)
    
    json_files = show_available_json_files()
    
    if not json_files:
        print("No JSON files available for interactive demo")
        return
    
    print("Would you like to:")
    print("1. Run a dry run (show what would happen)")
    print("2. Extract answers for all questions")
    print("3. Extract answers for specific JSON file")
    print("4. Show help")
    print("5. Exit")
    
    while True:
        choice = input("\nEnter your choice (1-5): ").strip()
        
        if choice == '1':
            demonstrate_dry_run()
            break
        elif choice == '2':
            print("\nRunning: python answer_patcher.py --all --format csv")
            response = input("Proceed? (y/n): ").lower().strip()
            if response == 'y':
                run_example_command("Extract all answers as CSV", ["--all", "--format", "csv"])
            break
        elif choice == '3':
            if json_files:
                print(f"\nAvailable JSON files:")
                for i, file in enumerate(json_files, 1):
                    print(f"  {i}. {file}")
                
                try:
                    file_choice = int(input("Select file number: ")) - 1
                    if 0 <= file_choice < len(json_files):
                        selected_file = f"questions/{json_files[file_choice]}"
                        run_example_command(f"Extract answers for {json_files[file_choice]}", 
                                          ["--json", selected_file, "--format", "csv"])
                    else:
                        print("Invalid selection")
                except ValueError:
                    print("Invalid input")
            break
        elif choice == '4':
            demonstrate_help()
            break
        elif choice == '5':
            print("Demo ended")
            return
        else:
            print("Invalid choice. Please enter 1-5.")

def main():
    print("Answer Patcher - Demo Script")
    print("=" * 60)
    
    # Show directory structure
    show_directory_structure()
    
    # Check if source files exist
    source_xml = "../source/Posts-answers.xml"
    if not os.path.exists(source_xml):
        print(f"\n❌ Source XML file not found: {source_xml}")
        print("Please ensure the Posts-answers.xml file is in the correct location")
        return
    
    # Show available data
    show_available_json_files()
    
    # Show usage examples
    show_usage_examples()
    
    # Interactive demo
    interactive_demo()
    
    print("\n" + "=" * 60)
    print("DEMO COMPLETED")
    print("=" * 60)
    print("You can now use the answer patcher with your data!")
    print("\nQuick start:")
    print("  python answer_patcher.py --all --format both")

if __name__ == "__main__":
    main() 