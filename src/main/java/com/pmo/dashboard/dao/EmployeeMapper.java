package com.pmo.dashboard.dao;

import java.util.List;
import java.util.Map;

import com.pmo.dashboard.entity.Employee;
import com.pmo.dashboard.entity.EmployeePageCondition;

public interface EmployeeMapper
{
    int addEmployee(Employee employee);
    Employee queryEmployeeById(String employeeId);
    int updateEmployee(Employee employee);
    List<Employee> queryEmployeeList(EmployeePageCondition employeePageCondition);
    List<Employee> queryEmployeeByCsSubDeptId(Map<String, Object> params);
}
