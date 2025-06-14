#!/usr/bin/env python3
"""
Example usage of XML to CSV Answer Converter
This script demonstrates how to use the answer converter with the folder structure
"""

from xml_to_csv_answer_converter import XMLToCSVAnswerConverter
import os
import shutil

def create_sample_answer_data():
    """Create sample answer XML data for demonstration"""
    source_dir = "source"
    
    # Create source directory if it doesn't exist
    if not os.path.exists(source_dir):
        os.makedirs(source_dir)
        print(f"Created source directory: {source_dir}")
    
    # Sample XML content (using the structure from reference answer.xml)
    sample_xml_content = '''  <row Id="68203188" PostTypeId="2" ParentId="67987244" CreationDate="2021-07-01T01:18:23.933" Score="3" Body="&lt;p&gt;I have something similar and is caused by nodemon. Had to revert to nodemon v1.19.4&lt;/p&gt;&#10;" OwnerUserId="4352368" LastActivityDate="2021-07-01T01:18:23.933" CommentCount="3" ContentLicense="CC BY-SA 4.0" />

  <row Id="69995208" PostTypeId="2" ParentId="67987244" CreationDate="2021-11-16T19:45:30.310" Score="0" Body="&lt;p&gt;I got this problem when using a GitHub Action task to deploy a Vue application in an Azure Static Web App.&lt;/p&gt;&#10;&lt;p&gt;Running &lt;code&gt;npm audit fix&lt;/code&gt; and pushing the changed &lt;code&gt;package-lock.json&lt;/code&gt; solved for me.&lt;/p&gt;&#10;" OwnerUserId="1851755" LastActivityDate="2021-11-16T19:45:30.310" CommentCount="0" ContentLicense="CC BY-SA 4.0" />

  <row Id="71884719" PostTypeId="2" ParentId="67987244" CreationDate="2022-04-15T13:34:26.027" Score="-1" Body="&lt;p&gt;You have to remove package.lock.json and than use npm i&lt;/p&gt;&#10;" OwnerUserId="16162963" LastActivityDate="2022-04-15T13:34:26.027" CommentCount="0" ContentLicense="CC BY-SA 4.0" />

  <row Id="78064151" PostTypeId="2" ParentId="67987244" CreationDate="2024-02-26T22:05:30.340" Score="0" Body="&lt;p&gt;Remove Package.lock.json and run npm install&lt;/p&gt;&#10;" OwnerUserId="14356867" LastActivityDate="2024-02-26T22:05:30.340" CommentCount="0" ContentLicense="CC BY-SA 4.0" />'''
    
    # Additional sample with different parent IDs
    sample_xml_content2 = '''  <row Id="101" PostTypeId="2" ParentId="100" CreationDate="2023-01-01T10:30:00.000" Score="5" Body="&lt;p&gt;Here's a comprehensive solution:&lt;/p&gt;&lt;ol&gt;&lt;li&gt;First, install the required package&lt;/li&gt;&lt;li&gt;Then configure the settings&lt;/li&gt;&lt;li&gt;Finally, test your implementation&lt;/li&gt;&lt;/ol&gt;&lt;p&gt;This approach has worked well for me in production.&lt;/p&gt;" OwnerUserId="200" LastActivityDate="2023-01-01T10:30:00.000" CommentCount="2" ContentLicense="CC BY-SA 4.0" />

  <row Id="102" PostTypeId="2" ParentId="100" CreationDate="2023-01-01T11:15:00.000" Score="2" Body="&lt;p&gt;Alternative approach using &lt;strong&gt;modern JavaScript&lt;/strong&gt;:&lt;/p&gt;&lt;pre&gt;&lt;code&gt;const result = await processData(input);&#10;console.log(result);&lt;/code&gt;&lt;/pre&gt;&lt;p&gt;This is more concise and readable.&lt;/p&gt;" OwnerUserId="201" LastActivityDate="2023-01-01T11:15:00.000" CommentCount="1" ContentLicense="CC BY-SA 4.0" />

  <row Id="103" PostTypeId="2" ParentId="105" CreationDate="2023-01-02T09:00:00.000" Score="1" Body="&lt;p&gt;I encountered the same issue and found that updating the dependencies fixed it.&lt;/p&gt;&lt;p&gt;Run: &lt;code&gt;npm update&lt;/code&gt;&lt;/p&gt;" OwnerUserId="202" LastActivityDate="2023-01-02T09:00:00.000" CommentCount="0" ContentLicense="CC BY-SA 4.0" />'''
    
    # Create sample files
    sample_files = {
        "sample_answers_1.xml": sample_xml_content,
        "sample_answers_2.xml": sample_xml_content2
    }
    
    for filename, content in sample_files.items():
        filepath = os.path.join(source_dir, filename)
        if not os.path.exists(filepath):
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Created sample file: {filepath}")
    
    return list(sample_files.keys())

def demonstrate_batch_conversion():
    """Demonstrate batch conversion of all answer files in source directory"""
    print("="*70)
    print("DEMONSTRATION: BATCH ANSWER CONVERSION")
    print("="*70)
    
    # Create sample data
    sample_files = create_sample_answer_data()
    
    print(f"\nSample answer files created in source/ directory:")
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
            sys.executable, "xml_to_csv_answer_converter.py", "--batch", "--validate"
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
        csv_files = [f for f in os.listdir(output_dir) if f.endswith('_answers.csv')]
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
    print("DEMONSTRATION: SINGLE ANSWER FILE CONVERSION")
    print("="*70)
    
    source_dir = "source"
    if not os.path.exists(source_dir):
        create_sample_answer_data()
    
    # Find first XML file in source directory
    xml_files = [f for f in os.listdir(source_dir) if f.endswith('.xml')]
    if not xml_files:
        print("No XML files found for single file demonstration")
        return
    
    input_file = os.path.join(source_dir, xml_files[0])
    output_file = "single_answer_conversion_output.csv"
    
    print(f"Converting single answer file:")
    print(f"  Input:  {input_file}")
    print(f"  Output: {output_file}")
    
    # Run single file conversion
    import subprocess
    import sys
    
    try:
        result = subprocess.run([
            sys.executable, "xml_to_csv_answer_converter.py", 
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
            
            # Show a sample of the content
            print(f"\nSample content:")
            with open(output_file, 'r', encoding='utf-8') as f:
                lines = f.readlines()[:5]  # First 5 lines
                for i, line in enumerate(lines):
                    print(f"  Line {i+1}: {line.strip()[:100]}{'...' if len(line.strip()) > 100 else ''}")
            
    except Exception as e:
        print(f"Error running single file conversion: {e}")

def demonstrate_step_by_step_conversion():
    """Demonstrate step-by-step conversion process"""
    print("\n" + "="*70)
    print("DEMONSTRATION: STEP-BY-STEP ANSWER CONVERSION")
    print("="*70)
    
    source_dir = "source"
    if not os.path.exists(source_dir):
        create_sample_answer_data()
    
    # Find first XML file
    xml_files = [f for f in os.listdir(source_dir) if f.endswith('.xml')]
    if not xml_files:
        print("No XML files found")
        return
    
    input_file = os.path.join(source_dir, xml_files[0])
    print(f"Using input file: {input_file}")
    
    # Create converter instance
    converter = XMLToCSVAnswerConverter()
    
    # Step by step process
    print(f"\n{'='*50}")
    print("STEP-BY-STEP PROCESS:")
    print("="*50)
    
    # Step 1: Extract data
    data = converter.step1_extract_data(input_file)
    print(f"Step 1 result: Extracted {len(data)} answer records")
    if data:
        print(f"Sample record: {data[0]}")
    
    # Step 2: Handle special characters
    if data:
        data = converter.step2_handle_special_characters(data)
        print(f"Step 2 result: Processed {len(data)} records")
        print(f"Sample Body after processing: {data[0]['Body'][:100]}...")
    
    # Step 3: Remove HTML tags
    if data:
        data = converter.step3_remove_html_tags(data)
        print(f"Step 3 result: Cleaned {len(data)} records")
        print(f"Sample Body after cleaning: {data[0]['Body'][:100]}...")
    
    # Step 4: Convert to CSV
    if data:
        output_file = "step_by_step_answer_output.csv"
        converter.step4_convert_to_csv_format(data, output_file)
        print(f"Step 4 result: Created {output_file}")

def show_answer_usage_examples():
    """Show usage examples for answer converter"""
    print("\n" + "="*70)
    print("ANSWER CONVERTER USAGE EXAMPLES")
    print("="*70)
    
    examples = [
        ("Batch convert all answer XML files in source/ directory:", 
         "python xml_to_csv_answer_converter.py --batch"),
        
        ("Batch convert with validation:", 
         "python xml_to_csv_answer_converter.py --batch --validate"),
        
        ("Convert a specific answer file:", 
         "python xml_to_csv_answer_converter.py --file answers.xml output.csv"),
        
        ("Convert a specific file with validation:", 
         "python xml_to_csv_answer_converter.py --file answers.xml output.csv --validate"),
        
        ("Get help:", 
         "python xml_to_csv_answer_converter.py --help")
    ]
    
    for description, command in examples:
        print(f"\n{description}")
        print(f"  {command}")
    
    print(f"\n{'='*50}")
    print("OUTPUT FORMAT:")
    print("="*50)
    print("The CSV output will have the following columns:")
    print("  1. rawQuestionId - The ID of the parent question (from ParentId)")
    print("  2. content - The answer content with HTML removed (from Body)")
    print("  3. postId - The answer ID (from Id)")
    print("  4. score - The answer score (from Score)")

def cleanup_demo_files():
    """Clean up demonstration files"""
    print(f"\n{'='*70}")
    response = input("Do you want to clean up the answer demonstration files? (y/n): ").lower().strip()
    
    if response == 'y':
        files_to_remove = [
            "single_answer_conversion_output.csv",
            "step_by_step_answer_output.csv"
        ]
        
        dirs_to_clean = [
            ("source", "sample_answers_*.xml"),
            ("output", "sample_answers_*_answers.csv")
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
        
        print("✓ Answer demo cleanup completed")
    else:
        print("Answer demo files kept for your reference")

def main():
    """
    Main demonstration function
    """
    print("="*70)
    print("XML to CSV ANSWER CONVERTER - DEMONSTRATION")
    print("="*70)
    print("This script demonstrates the XML to CSV answer converter tool")
    print("="*70)
    
    # Run demonstrations
    demonstrate_batch_conversion()
    demonstrate_single_file_conversion()
    demonstrate_step_by_step_conversion()
    show_answer_usage_examples()
    
    # Optional cleanup
    cleanup_demo_files()
    
    print(f"\n{'='*70}")
    print("DEMONSTRATION COMPLETED")
    print("="*70)
    print("The answer converter tool is ready to use!")
    print("Place your answer XML files in the 'source/' directory and run:")
    print("  python xml_to_csv_answer_converter.py --batch --validate")

if __name__ == "__main__":
    main() 