# Doggy Daycare Attendance Management System

## Description

### Introduction
The Doggy Daycare AMS is a command-line application designed for managing the attendance and care details of dogs at a daycare business.  
Staff members can add, remove, update, display, and batch-import dog records, as well as generate an attendance and food needs report.

### Requirements Definition
Daycare staff need to keep accurate records of each dog, including: unique ID, name, breed, date of birth, food type, gender, spay/neuter status, and check-in status.  
Staff must be able to add, remove, update, and display dogs individually, and load a group of dogs from a text file.  
The system must also provide an attendance report that lists all checked-in dogs and summarizes food requirements.

## Course Info
Name: **Clay VanZile**  
Course: **CEN 3024C — Software Development I**  
CRN: **31774**

## Concepts
This program demonstrates using a console-based menu to manage dog records for a daycare, including data validation, batch loading from a text file, and a custom attendance report that lists all checked-in dogs and their food needs.

## Objectives

- The system is fully console-based; all interaction happens in the terminal.
- Staff interact with an on-screen menu to manage dogs.
- Each dog record contains:
  - Unique integer ID
  - Name
  - Breed
  - Date of Birth (YYYY-MM-DD)
  - Food Type (0 = no food, 1 = dry, 2 = wet, 3 = customer provided)
  - Gender (M/F)
  - Spayed/Neutered status (U, Y, N)
  - Checked-In status (true/false)
- The system can import dogs from a text file (comma-separated values).
- Staff can manually add, update, and remove dogs.
- All dog records can be displayed at any time.
- The system generates a report listing all checked-in dogs and a food breakdown.
- Data is stored in memory during runtime (no database or GUI in Phase 1).

## Inputs and Outputs

### Inputs
- Dog information: id, name, breed, date of birth, food type, gender, spayed/neutered status, and checked-in status.
- Text file with lines formatted as:  
  `id,name,breed,dob,food,gender,spayedNeutered,checkedIn`

### Outputs
- List of all dogs with their information.
- Attendance report showing checked-in dogs and food type totals.
