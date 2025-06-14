#!/usr/bin/env python3
"""
Example usage of XML to CSV converter
This script demonstrates how to use the converter with the new folder structure
"""

from xml_to_csv_converter import XMLToCSVConverter
import os
import shutil

def create_sample_data():
    """Create sample XML data for demonstration"""
    source_dir = "source"
    
    # Create source directory if it doesn't exist
    if not os.path.exists(source_dir):
        os.makedirs(source_dir)
        print(f"Created source directory: {source_dir}")
    
    # Sample XML content (similar to your problem.xml structure)
    sample_xml_content = '''  <row Id="1001" PostTypeId="1" AcceptedAnswerId="1002" CreationDate="2023-01-01T10:00:00.000" Score="5" ViewCount="1234" Body="&lt;p&gt;This is a sample question about Python programming.&lt;/p&gt;&lt;p&gt;How do I convert XML to CSV?&lt;/p&gt;" OwnerUserId="100" OwnerDisplayName="SampleUser" Title="How to convert XML to CSV in Python?" Tags="|python|xml|csv|programming|" AnswerCount="2" CommentCount="1" ContentLicense="CC BY-SA 4.0" />
  <row Id="1003" PostTypeId="1" AcceptedAnswerId="1004" CreationDate="2023-01-02T15:30:00.000" Score="8" ViewCount="2567" Body="&lt;p&gt;I need help with data processing in Python.&lt;/p&gt;&lt;ul&gt;&lt;li&gt;Reading files&lt;/li&gt;&lt;li&gt;Processing data&lt;/li&gt;&lt;li&gt;Saving results&lt;/li&gt;&lt;/ul&gt;" OwnerUserId="101" OwnerDisplayName="DataGuru" Title="Python data processing best practices" Tags="|python|data-processing|best-practices|" AnswerCount="5" CommentCount="3" ContentLicense="CC BY-SA 4.0" />'''
    
    # Create sample files
    sample_files = {
        "sample_questions_1.xml": sample_xml_content,
        "sample_questions_2.xml": sample_xml_content.replace("1001", "2001").replace("1003", "2003")
    }
    
    for filename, content in sample_files.items():
        filepath = os.path.join(source_dir, filename)
        if not os.path.exists(filepath):
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Created sample file: {filepath}")
    
    return list(sample_files.keys())

def demonstrate_batch_conversion():
    """Demonstrate batch conversion of all files in source directory"""
    print("="*70)
    print("DEMONSTRATION: BATCH CONVERSION")
    print("="*70)
    
    # Create sample data
    sample_files = create_sample_data()
    
    print(f"\nSample files created in source/ directory:")
    for file in sample_files:
        print(f"  - {file}")
    
    print(f"\n{'='*50}")
    print("Running batch conversion...")
    print("="*50)
    
    # Import and run batch conversion
    import subprocess
    import sys
    
    try:
        result = subprocess.run([
            sys.executable, "xml_to_csv_converter.py", "--batch", "--validate"
        ], capture_output=True, text=True, cwd=".")
        
        print("BATCH CONVERSION OUTPUT:")
        print("-" * 30)
        print(result.stdout)
        
        if result.stderr:
            print("ERRORS:")
            print(result.stderr)
            
    except Exception as e:
        print(f"Error running batch conversion: {e}")
    
    # Show results
    output_dir = "output"
    if os.path.exists(output_dir):
        csv_files = [f for f in os.listdir(output_dir) if f.endswith('.csv')]
        if csv_files:
            print(f"\n{'='*50}")
            print("CONVERSION RESULTS:")
            print("="*50)
            print(f"Output CSV files created in {output_dir}/:")
            for file in csv_files:
                file_path = os.path.join(output_dir, file)
                file_size = os.path.getsize(file_path)
                print(f"  ✓ {file} ({file_size} bytes)")

def demonstrate_single_file_conversion():
    """Demonstrate single file conversion"""
    print("\n" + "="*70)
    print("DEMONSTRATION: SINGLE FILE CONVERSION")
    print("="*70)
    
    source_dir = "source"
    if not os.path.exists(source_dir):
        create_sample_data()
    
    # Find first XML file in source directory
    xml_files = [f for f in os.listdir(source_dir) if f.endswith('.xml')]
    if not xml_files:
        print("No XML files found for single file demonstration")
        return
    
    input_file = os.path.join(source_dir, xml_files[0])
    output_file = "single_conversion_output.csv"
    
    print(f"Converting single file:")
    print(f"  Input:  {input_file}")
    print(f"  Output: {output_file}")
    
    # Run single file conversion
    import subprocess
    import sys
    
    try:
        result = subprocess.run([
            sys.executable, "xml_to_csv_converter.py", 
            "--file", input_file, output_file, "--validate"
        ], capture_output=True, text=True, cwd=".")
        
        print(f"\n{'='*50}")
        print("SINGLE FILE CONVERSION OUTPUT:")
        print("-" * 30)
        print(result.stdout)
        
        if result.stderr:
            print("ERRORS:")
            print(result.stderr)
            
        # Show result
        if os.path.exists(output_file):
            file_size = os.path.getsize(output_file)
            print(f"\n✓ Output file created: {output_file} ({file_size} bytes)")
            
    except Exception as e:
        print(f"Error running single file conversion: {e}")

def show_usage_examples():
    """Show usage examples"""
    print("\n" + "="*70)
    print("USAGE EXAMPLES")
    print("="*70)
    
    examples = [
        ("Batch convert all XML files in source/ directory:", 
         "python xml_to_csv_converter.py --batch"),
        
        ("Batch convert with validation:", 
         "python xml_to_csv_converter.py --batch --validate"),
        
        ("Convert a specific file:", 
         "python xml_to_csv_converter.py --file input.xml output.csv"),
        
        ("Convert a specific file with validation:", 
         "python xml_to_csv_converter.py --file input.xml output.csv --validate"),
        
        ("Get help:", 
         "python xml_to_csv_converter.py --help")
    ]
    
    for description, command in examples:
        print(f"\n{description}")
        print(f"  {command}")

def cleanup_demo_files():
    """Clean up demonstration files"""
    print(f"\n{'='*70}")
    response = input("Do you want to clean up the demonstration files? (y/n): ").lower().strip()
    
    if response == 'y':
        files_to_remove = [
            "single_conversion_output.csv"
        ]
        
        dirs_to_clean = [
            ("source", "sample_questions_*.xml"),
            ("output", "sample_questions_*.csv")
        ]
        
        # Remove specific files
        for file in files_to_remove:
            if os.path.exists(file):
                os.remove(file)
                print(f"Removed: {file}")
        
        # Clean directories
        import glob
        for dir_name, pattern in dirs_to_clean:
            if os.path.exists(dir_name):
                files = glob.glob(os.path.join(dir_name, pattern))
                for file in files:
                    os.remove(file)
                    print(f"Removed: {file}")
        
        print("✓ Cleanup completed")
    else:
        print("Demo files kept for your reference")

def main():
    print("XML to CSV Converter - Usage Demonstration")
    print("="*70)
    
    # Show current directory structure
    print("\nCurrent directory structure:")
    for item in ["source", "output", "document"]:
        if os.path.exists(item):
            print(f"  📁 {item}/")
        else:
            print(f"  📁 {item}/ (will be created)")
    
    # Show available XML files
    source_dir = "source"
    if os.path.exists(source_dir):
        xml_files = [f for f in os.listdir(source_dir) if f.endswith('.xml')]
        if xml_files:
            print(f"\nXML files in source/ directory:")
            for file in xml_files:
                print(f"  📄 {file}")
        else:
            print(f"\nNo XML files found in source/ directory")
    
    # Demonstrate both conversion modes
    demonstrate_batch_conversion()
    demonstrate_single_file_conversion()
    
    # Show usage examples
    show_usage_examples()
    
    # Cleanup option
    cleanup_demo_files()
    
    print(f"\n{'='*70}")
    print("DEMONSTRATION COMPLETED")
    print("="*70)
    print("You can now use the converter with your own XML files!")
    print("Place your XML files in the source/ directory and run:")
    print("  python xml_to_csv_converter.py --batch")

if __name__ == "__main__":
    main() 