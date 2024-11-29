package org.employee;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeReport {

    public static void main(String args[]) {
        try {
            List<Employee> employeeList = getEmployeeListFromTheFile("employees.csv");
            Map<Integer, Employee> employeeMap = employeeList.stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
            Map<Integer, List<Employee>> managerSubordinateMap = employeeList.stream()
                    .filter(e -> null != e.getManagerId()).collect(Collectors.groupingBy(e -> e.getManagerId()));

            // which managers earn less than they should, and by how much
            findManagersEarningLessThanExpected(employeeList , managerSubordinateMap);

            //which managers earn more than they should, and by how much
            findManagersEarningMoreThanExpected(employeeList , managerSubordinateMap);

            //which employees have a reporting line which is too long, and by how much
            findEmployeesWithLongReportingLines(employeeList , employeeMap);

        } catch (Exception e){
            System.out.println("while generating employee report encountered exception : "+ e.getMessage());
        }

    }

    private static List<Employee> getEmployeeListFromTheFile(String fileName) throws IOException {
        List<Employee> employees = new ArrayList<>();
        InputStream inputStream = (EmployeeReport.class).getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new FileNotFoundException("file not found: " + fileName);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            //skipHeader
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                int id = Integer.parseInt(columns[0]);
                String firstName = columns[1];
                String lastName = columns[2];
                double salary = Double.parseDouble(columns[3]);
                Integer managerId = columns.length > 4 && !columns[4].isEmpty() ? Integer.parseInt(columns[4]) : null;
                employees.add(new Employee(id, firstName, lastName, salary, managerId));
            }
        }

        return employees;

    }

    public static void findManagersEarningLessThanExpected(List<Employee> employeeList, Map<Integer, List<Employee>> managerSubordinateMap) {
        for (Employee manager : employeeList) {
            if (managerSubordinateMap.containsKey(manager.getId())) {
                List<Employee> subordinates = managerSubordinateMap.get(manager.getId());
                double avgSalary = subordinates.stream().mapToDouble(Employee::getSalary).average().orElse(0);
                double minSalary = avgSalary * 1.2;

                if (manager.getSalary() < minSalary) {
                    System.out.printf("Manager %s earns %.2f less than the minimum required salary %.2f%n",
                            manager.getFirstName(), minSalary - manager.getSalary(), minSalary);
                }
            }
        }
    }

    public static void findManagersEarningMoreThanExpected(List<Employee> employeeList, Map<Integer, List<Employee>> managerSubordinateMap) {
        for (Employee manager : employeeList) {
            if (managerSubordinateMap.containsKey(manager.getId())) {
                List<Employee> subordinates = managerSubordinateMap.get(manager.getId());
                double avgSalary = subordinates.stream().mapToDouble(Employee::getSalary).average().orElse(0);
                double maxSalary = avgSalary * 1.5;

                if (manager.getSalary() > maxSalary) {
                    System.out.printf("Manager %s earns %.2f more than the maximum allowed salary %.2f%n",
                            manager.getFirstName(), manager.getSalary() - maxSalary, maxSalary);
                }
            }
        }
    }

    public static void findEmployeesWithLongReportingLines(List<Employee> employeeList, Map<Integer, Employee> employeeMap) {
        for (Employee emp : employeeList) {
            int levels = 0;
            Integer managerId = emp.getManagerId();
            while (managerId != null) {
                levels++;
                managerId = employeeMap.get(managerId).getManagerId();
            }
            if (levels > 4) {
                System.out.printf("Employee %s has a reporting line too long by %d levels%n",
                        emp.getFirstName(), levels - 4);
            }
        }
    }
}
