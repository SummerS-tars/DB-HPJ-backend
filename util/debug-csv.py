#!/usr/bin/env python3
"""
Debug script to analyze CSV format and help identify parsing issues
"""
import csv
import sys

def analyze_csv(filename):
    print(f"Analyzing CSV file: {filename}")
    print("=" * 50)
    
    with open(filename, 'r', encoding='utf-8') as file:
        # Read first few lines to analyze structure
        lines = []
        for i, line in enumerate(file):
            lines.append(line.strip())
            if i >= 5:  # Read first 6 lines (header + 5 data lines)
                break
    
    print(f"Total lines read: {len(lines)}")
    print()
    
    # Analyze header
    if lines:
        header = lines[0]
        print(f"Header line: {header}")
        
        # Try different splitting methods
        print("\nSplitting analysis:")
        
        # Simple comma split
        simple_split = header.split(',')
        print(f"Simple comma split ({len(simple_split)} fields): {simple_split[:5]}...")
        
        # CSV module split
        try:
            csv_reader = csv.reader([header])
            csv_split = next(csv_reader)
            print(f"CSV module split ({len(csv_split)} fields): {csv_split[:5]}...")
        except Exception as e:
            print(f"CSV module failed: {e}")
        
        print("\nData line analysis:")
        if len(lines) > 1:
            for i, line in enumerate(lines[1:], 1):
                print(f"\nLine {i}: {line[:100]}...")
                try:
                    csv_reader = csv.reader([line])
                    fields = next(csv_reader)
                    print(f"  Fields ({len(fields)}): {[f[:30] + '...' if len(f) > 30 else f for f in fields[:5]]}")
                except Exception as e:
                    print(f"  Error parsing: {e}")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python debug-csv.py <csv_file>")
        sys.exit(1)
    
    analyze_csv(sys.argv[1]) 