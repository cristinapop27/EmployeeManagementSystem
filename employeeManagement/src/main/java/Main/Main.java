package Main;
import BusinessLogic.*;
import DataAccess.PersistData;
import DataModel.*;
import Presentation.MainPage;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        PersistData persist = new PersistData();
        TaskManagement taskManagement = new TaskManagement(persist);
        Utility util = new Utility(taskManagement);

        Employee e1 = new Employee(1, "tudor");
        Employee e2 = new Employee(2, "tudor2");

        SimpleTask task1 = new SimpleTask(1, 1, 24);
        SimpleTask task2 = new SimpleTask(2, 1, 24);
        SimpleTask task3 = new SimpleTask(3, 1, 24);
        SimpleTask task4 = new SimpleTask(4, 1, 24);
        SimpleTask task5 = new SimpleTask(5, 1, 24);
        SimpleTask task6 = new SimpleTask(6, 1, 24);
        taskManagement.addUnassignedTask(task6);

        taskManagement.assignTaskToEmployee(e1, task1);
        taskManagement.assignTaskToEmployee(e1, task2);
        taskManagement.assignTaskToEmployee(e1, task3);
        taskManagement.assignTaskToEmployee(e2, task4);
        taskManagement.assignTaskToEmployee(e2, task5);

        taskManagement.modifyTaskStatus(e1, 1, "Completed");
        taskManagement.modifyTaskStatus(e1, 2, "Completed");
        taskManagement.modifyTaskStatus(e1, 3, "Completed");
        taskManagement.modifyTaskStatus(e2, 4, "Completed");
        taskManagement.modifyTaskStatus(e2, 5, "Completed");

        util.showEmployees();

        Map<String , Map<String, Integer>> workStatusMap = util.workStatus();
        System.out.println(workStatusMap);

        MainPage mainPage = new MainPage(taskManagement,util);
        mainPage.setVisible(true);

    }
}