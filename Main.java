import java.io.*;
import java.util.*;

abstract class Person { //encapsulation
    String id;
    String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
        //inheritance
    }
}

class Student extends Person {
    public Student(String id, String name) {
        super(id, name);
    }
}

interface Record { //abstraction
    void addRecord(String key, int value);
}

class Course implements Record { // polymorphosm 
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

        processFiles(pathToNameFile, pathToCourseFile, pathToErrorLogFile, pathToResultsFile);
    }

    public static void processFiles(String pathToNameFile, String pathToCourseFile, String pathToErrorLogFile, String pathToResultsFile) {
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

        } catch (Exception e) {
            System.err.println("Error initializing files: " + e.getMessage());
        }
    }

    static Map<String, Student> readNameFile(String filePath, PrintWriter errorWriter) {
        Map<String, Student> students = new HashMap<>();
        int lineNumber = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(", ");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Incorrect number of fields " + parts.length);
                    }
                    String studentId = parts[0].trim();
                    String studentName = parts[1].trim();

                    if (!isValidStudentId(studentId)) {
                        throw new IllegalArgumentException("Invalid Student ID format");
                    }
                    if (!isValidName(studentName)) {
                        throw new IllegalArgumentException("Name must include first name and surname");
                    }

                    students.put(studentId, new Student(studentId, studentName));
                } catch (IllegalArgumentException e) {
                    errorWriter.println("Error in file '" + filePath + "' at line " + lineNumber + ": " + e.getMessage() + " on line: " + line);
                }
            }
        } catch (Exception e) {
            errorWriter.println("Error reading file '" + filePath + "': " + e.getMessage());
        }
        return students;
    }

    static boolean isValidName(String name) {
        String[] parts = name.split("\\s+");
        return parts.length >= 2; // Checks if the name has at least two parts
    }

    static Map<String, List<Course>> readCourseFile(String filePath, PrintWriter errorWriter) {
        Map<String, List<Course>> courses = new HashMap<>();
        int lineNumber = 0; // Line number counter
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++; // Increment line number
                String[] parts = line.split(", ");
                if (parts.length != 6) {
                    errorWriter.println("Error in file '" + filePath + "' at line " + lineNumber + ": Incorrect number of fields on line: " + line);
                    continue; // Skip processing this line
                }
                String studentId = parts[0].trim();
                String courseCode = parts[1].trim();
                try {
                    int finalGrade = calculateFinalGrade(
                        Integer.parseInt(parts[2].trim()),
                        Integer.parseInt(parts[3].trim()),
                        Integer.parseInt(parts[4].trim()),
                        Integer.parseInt(parts[5].trim()));
                    Course course = new Course(studentId, courseCode, finalGrade);
                    courses.computeIfAbsent(studentId, k -> new ArrayList<>()).add(course);
                } catch (NumberFormatException e) {
                    errorWriter.println("Error in file '" + filePath + "' at line " + lineNumber + ": Error processing line - " + e.getMessage() + " on line: " + line);
                }
            }
        } catch (Exception e) {
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
        return id.matches("\\d{9}"); // Regex for exactly 9 digits
    }

    static boolean isValidCourseId(String courseId) {
        return courseId.matches("[A-Za-z]{2}\\d{3}"); // Regex for two letters followed by three digits
    }

}
