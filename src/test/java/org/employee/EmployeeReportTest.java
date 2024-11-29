package org.employee;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeReportTest {
    private EmployeeReport employeeReport;

    @BeforeEach
    public void setUp(){
        employeeReport = new EmployeeReport();
    }

    @Test
    void testFindManagersEarningLessThanExpected() {
        List<Employee> employees = Arrays.asList(
                new Employee(1, "ManagerA", "LastA", 50000, null),
                new Employee(2, "SubordinateA", "LastA", 40000, 1),
                new Employee(3, "SubordinateB", "LastB", 45000, 1)
        );

        Map<Integer, List<Employee>> managerSubordinateMap = Map.of(
                1, Arrays.asList(employees.get(1), employees.get(2))
        );

        String expectedOutput = "Manager ManagerA earns 1000.00 less than the minimum required salary 51000.00";
        assertConsoleOutput(expectedOutput, v -> employeeReport.findManagersEarningLessThanExpected(employees, managerSubordinateMap));
    }

    @Test
    void testfindManagersEarningMoreThanExpected() {
        List<Employee> employees = Arrays.asList(
                new Employee(1, "ManagerA", "LastA", 80000, null),
                new Employee(2, "SubordinateA", "LastA", 40000, 1),
                new Employee(3, "SubordinateB", "LastB", 45000, 1)
        );

        Map<Integer, List<Employee>> managerSubordinateMap = Map.of(
                1, Arrays.asList(employees.get(1), employees.get(2))
        );

        String expectedOutput = "Manager ManagerA earns 16250.00 more than the maximum allowed salary 63750.00";
        assertConsoleOutput(expectedOutput, v -> employeeReport.findManagersEarningMoreThanExpected(employees, managerSubordinateMap));
    }

    @Test
    void testfindEmployeesWithLongReportingLines() {
        List<Employee> employees = Arrays.asList(
                new Employee(1, "CEO", "LastA", 100000, null),
                new Employee(2, "ManagerA", "LastName1", 80000, 1),
                new Employee(3, "ManagerB", "LastName2", 70000, 2),
                new Employee(4, "ManagerC", "LastName3", 60000, 3),
                new Employee(5, "EmployeeD", "LastName4", 50000, 4),
                new Employee(6, "EmployeeE", "LastName5", 40000, 5)
        );

        Map<Integer, Employee> employeeMap = employees.stream().collect(Collectors.toMap(e -> e.getId(), e -> e));

        String expectedOutput = "Employee EmployeeE has a reporting line too long by 1 levels";
        assertConsoleOutput(expectedOutput, v -> employeeReport.findEmployeesWithLongReportingLines(employees, employeeMap));
    }

    // Utility method to capture console output
    private void assertConsoleOutput(String expectedOutput, Consumer<Void> functionToTest) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        try {
            functionToTest.accept(null);
            assertEquals(expectedOutput.trim(), outContent.toString().trim());
        } finally {
            System.setOut(originalOut);
        }
    }
}
