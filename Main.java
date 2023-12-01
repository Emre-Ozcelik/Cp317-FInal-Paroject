import java.io.*;
import java.util.*;

abstract class Person {
    String id;
    String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

class Student extends Person {
    public Student(String id, String name) {
        super(id, name);
    }
}

interface Record {
    void addRecord(String key, int value);
}

class Course implements Record {
    String id;
    String courseCode;
    int finalGrade;

    public Course(String id, String courseCode, int finalGrade) {
        this.id = id;
        this.courseCode = courseCode;
        this.finalGrade = finalGrade;
    }

    @Override
    public void addRecord(String key, int value) {
        // Implementation for adding a record
    }
}

class CompleteStudent extends Person implements Record {
    Map<String, Integer> courses;

    public CompleteStudent(String id, String name) {
        super(id, name);
        this.courses = new HashMap<>();
    }

    @Override
    public void addRecord(String courseCode, int finalGrade) {
        courses.put(courseCode, finalGrade);
    }
}

public class Main {

    public static void main(String[] args) {
        String pathToNameFile = "NameFile.txt";
        String pathToCourseFile = "CourseFile.txt";
        String pathToErrorLogFile = "ErrorLog.txt";
        String pathToResultsFile = "Results.txt";

        try (PrintWriter errorWriter = new PrintWriter(new FileWriter(pathToErrorLogFile, false));
             PrintWriter writer = new PrintWriter(new FileWriter(pathToResultsFile, false))) {

            Map<String, Student> students = readNameFile(pathToNameFile, errorWriter);
            Map<String, List<Course>> courses = readCourseFile(pathToCourseFile, errorWriter);

            if (students.isEmpty() || courses.isEmpty()) {
                System.err.println("Error: No valid student or course data found.");
                return;
            }

            Map<String, CompleteStudent> completeStudents = matchKeys(students, courses, errorWriter);

            for (String id : completeStudents.keySet()) {
                CompleteStudent student = completeStudents.get(id);
                writer.println("ID: " + student.id + ", Name: " + student.name);
                for (String courseCode : student.courses.keySet()) {
                    writer.println("  Course: " + courseCode + ", Grade: " + student.courses.get(courseCode));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error initializing files: " + e.getMessage());
        }
    }

    static Map<String, Student> readNameFile(String filePath, PrintWriter errorWriter) {
        Map<String, Student> students = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] parts = line.split(", ");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Invalid format in Name file: " + line);
                    }
                    students.put(parts[0], new Student(parts[0], parts[1]));
                } catch (IllegalArgumentException e) {
                    errorWriter.println("Error processing line in Name file: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            errorWriter.println("Error reading Name file: " + e.getMessage());
        }
        return students;
    }

    static Map<String, List<Course>> readCourseFile(String filePath, PrintWriter errorWriter) {
        Map<String, List<Course>> courses = new HashMap<>();
        int lineNumber = 0; // Line number counter
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++; // Increment line number
                String[] parts = line.split(", ");
                if (parts.length != 6 || !isValidStudentId(parts[0]) || !isValidCourseId(parts[1])) {
                    String errorDetail = !isValidStudentId(parts[0]) ? "Invalid Student ID format" : "Invalid Course ID format";
                    errorWriter.println("Error in file '" + filePath + "' at line " + lineNumber + ": " + line + " -- " + errorDetail);
                    continue; // Skip processing this line
                }
                try {
                    int finalGrade = calculateFinalGrade(
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3]),
                        Integer.parseInt(parts[4]),
                        Integer.parseInt(parts[5]));
                    Course course = new Course(parts[0], parts[1], finalGrade);

                    courses.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(course);
                } catch (NumberFormatException e) {
                    errorWriter.println("Error in file '" + filePath + "' at line " + lineNumber + ": " + line + " -- " + e.getMessage());
                }
            }
        } catch (IOException e) {
            errorWriter.println("Error reading file '" + filePath + "': " + e.getMessage());
        }
        return courses;
    }



    static int calculateFinalGrade(int test1, int test2, int test3, int finalExam) {
        return (int) (test1 * 0.2 + test2 * 0.2 + test3 * 0.2 + finalExam * 0.4);
    }



    static Map<String, CompleteStudent> matchKeys(Map<String, Student> students, Map<String, List<Course>> courses, PrintWriter errorWriter) {
        Map<String, CompleteStudent> completeStudents = new HashMap<>();
        for (Map.Entry<String, List<Course>> entry : courses.entrySet()) {
            String studentId = entry.getKey();
            List<Course> courseList = entry.getValue();

            if (!isValidStudentId(studentId)) {
                errorWriter.println("Error: Invalid Student ID format on line: " + studentId);
            } else if (!students.containsKey(studentId)) {
                errorWriter.println("Error: Student ID " + studentId + " not found in student records.");
            } else {
                Student student = students.get(studentId);
                CompleteStudent completeStudent = completeStudents.computeIfAbsent(studentId, k -> new CompleteStudent(student.id, student.name));

                for (Course course : courseList) {
                    completeStudent.addRecord(course.courseCode, course.finalGrade);
                }
            }
        }
        return completeStudents;
    }


    static boolean isValidStudentId(String id) {
        return id.matches("\\d{9}"); // Regex for exactly 8 digits
    }

    static boolean isValidCourseId(String courseId) {
        return courseId.matches("[A-Za-z]{2}\\d{3}"); // Regex for two letters followed by three digits
    }




}