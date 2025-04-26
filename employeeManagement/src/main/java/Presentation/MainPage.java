package Presentation;
import BusinessLogic.*;
import DataModel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.*;

public class MainPage extends JFrame{
    private TaskManagement manager;
    private Utility util;

    private DefaultListModel<String> listModel;
    private JList<String> employeeList;
    private JList<String> taskList;
    private JPanel rightPanel;
    private JPopupMenu popupMenuUnassigned;
    private JMenuItem assignTask;
    private JMenuItem deleteTaskB;
    private JMenuItem deleteTaskB1;
    private JPopupMenu popupMenuAssigned;
    private JMenuItem changeStatus;
    private JPopupMenu popupMenuEmployees;

    public MainPage(TaskManagement manager, Utility util) {
        this.manager = manager;
        this.util = util;

        //setting up the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,600);
        setTitle("Employee Management System");
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;

        JPanel leftPanel = new JPanel();
        c.gridx = 0;
        c.weightx= 0.33;
        leftPanel.setLayout(new GridLayout(8,1));
        add(leftPanel,c);

        rightPanel = new JPanel();
        rightPanel.setBackground(Color.lightGray);
        rightPanel.setLayout(new BorderLayout());
        c.gridx = 1;
        c.weightx= 0.67;
        add(rightPanel,c);

        setVisible(true);

        //show employees
        JButton showEmployee = new JButton("Show Employees");
        showEmployee.addActionListener(e->loadEmployees());
        leftPanel.add(showEmployee);

        //add employee
        JButton addEmployeeB = new JButton("Add Employee");
        addEmployeeB.addActionListener(e->addEmployee());
        leftPanel.add(addEmployeeB);

        //show all tasks
        JButton showAllTasks = new JButton("Show All Tasks");
        showAllTasks.addActionListener(e->showTasks());
        leftPanel.add(showAllTasks);

        //add task
        JButton addTask = new JButton("Add Task");
        addTask.addActionListener(e->addNewTask());
        leftPanel.add(addTask);

        //menu for unassigned tasks to assign them to an employee
        popupMenuUnassigned = new JPopupMenu();
        assignTask = new JMenuItem("Assign Task");
        deleteTaskB = new JMenuItem("Delete Task");
        assignTask.addActionListener(e->handleAssignTask());
        deleteTaskB.addActionListener(e->deleteTask());
        popupMenuUnassigned.add(assignTask);
        popupMenuUnassigned.add(deleteTaskB);

        //menu for assigned tasks to change status
        popupMenuAssigned = new JPopupMenu();
        changeStatus = new JMenuItem("Change Status");
        deleteTaskB1 = new JMenuItem("Delete Task");
        changeStatus.addActionListener(e->handleChangeOfStatus());
        deleteTaskB1.addActionListener(e->deleteTask());
        popupMenuAssigned.add(changeStatus);
        popupMenuAssigned.add(deleteTaskB1);

        //button for showing completed and uncompleted tasks
        JButton seeStatus = new JButton("See Status");
        seeStatus.addActionListener(e->showStatusOfTasks());
        leftPanel.add(seeStatus);

        //button to see all work hours
        JButton seeWorkHours = new JButton("See Work Hours");
        seeWorkHours.addActionListener(e->showEmployeeWorkDuration());
        leftPanel.add(seeWorkHours);
        //button to see employees with over 40 hours
        JButton seeEmployeesOver40 = new JButton("HardWorking Employees");
        seeEmployeesOver40.addActionListener(e->showHardworkingEmployes());
        leftPanel.add(seeEmployeesOver40);

    }

    public void deleteTask() {
        String selectedTask = taskList.getSelectedValue();

        Pattern pattern = Pattern.compile("(?:Unassigned: ID:|SimpleTask ID:|ComplexTask ID:)\\s*(\\d+)");
        Matcher matcher = pattern.matcher(selectedTask);

        int selectedTaskId = 0;
        if (matcher.find()) {
            selectedTaskId = Integer.parseInt(matcher.group(1));
        } else {
            return;
        }
        List<Task> unassignedTasks = manager.getUnassignedTasks();

        for (int i = 0; i < unassignedTasks.size(); i++) {
            if (unassignedTasks.get(i).getIdTask() == selectedTaskId) {
                //unassignedTasks.remove(i);
                manager.removeUnassignedTask(unassignedTasks.get(i));
                showTasks();
                return;
            }
        }

        for (Map.Entry<Employee, List<Task>> entry : manager.getMap().entrySet()) {
            Employee employee = entry.getKey();
            List<Task> tasks = entry.getValue();

            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getIdTask() == selectedTaskId) {
                    tasks.remove(i);
                    showTasks();
                    return;
                }
            }
        }

    }

    private void showEmployeeWorkDuration() {
        rightPanel.removeAll();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> workDurationList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(workDurationList);

        List<Employee> employees = manager.getEmployeeList();

        if (employees.isEmpty()) {
            listModel.addElement("No employees found.");
        } else {
            employees.forEach(emp -> {
                int workDuration = manager.calculateEmployeeWorkDuration(emp.getIdEmployee());
                listModel.addElement("Employee: " + emp.getName()+ " Work Duration: "+ workDuration + " hours");
            });
        }

        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void showHardworkingEmployes() {
        rightPanel.removeAll();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> employeeList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(employeeList);

        List<Employee> overworkedEmployees = util.showEmployees();

        if (overworkedEmployees.isEmpty()) {
            listModel.addElement("no employees");
        } else {
            overworkedEmployees.forEach(emp -> {
                int workHours = manager.calculateEmployeeWorkDuration(emp.getIdEmployee());
                listModel.addElement("Employee: " + emp.getName());
                listModel.addElement("    Work Hours: " + workHours);
            });

        }

        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void showStatusOfTasks() {
        rightPanel.removeAll();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> workStatusList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(workStatusList);

        Map<String, Map<String, Integer>> workStatusMap = util.workStatus();

        workStatusMap.forEach((employeeName, statusCounts) -> {
            int completed = statusCounts.getOrDefault("Completed", 0);
            int uncompleted = statusCounts.getOrDefault("Uncompleted", 0);

            listModel.addElement("Employee: " + employeeName);
            listModel.addElement("   - Completed: " + completed);
            listModel.addElement("   - Uncompleted: " + uncompleted);
        });


        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    public void loadEmployees() {
        rightPanel.removeAll();
        listModel = new DefaultListModel<>();
        employeeList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(employeeList);
        List<Employee> employees = manager.getEmployeeList();
        employees.forEach(employee -> listModel.addElement(employee.getName()));

        employeeList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {showTasksOfEmployee(e);}
            @Override
            public void mouseReleased(MouseEvent e) {showTasksOfEmployee(e);}
        });

        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    private void showTasksOfEmployee(MouseEvent e) {
        //in show employee when right clicking on an emp their task list will be shown
        JPopupMenu popupMenuTaskOfEmployee = new JPopupMenu();
        List<Task> tasksOfEmp;

        if(e.isPopupTrigger()) {
            int index = employeeList.locationToIndex(e.getPoint());
            if(index >=0){
                employeeList.setSelectedIndex(index);
                String selectedEmployeeName = employeeList.getSelectedValue();

                AtomicReference<Employee> selectedEmployee = new AtomicReference<>(null);

                manager.getMap().keySet().forEach(emp -> {
                    if (emp.getName().equals(selectedEmployeeName) && selectedEmployee.get() == null) {
                        selectedEmployee.set(emp);
                    }
                });

                Employee employee = selectedEmployee.get();

                if(employee != null){
                    tasksOfEmp = manager.getMap().get(employee);
                    tasksOfEmp.forEach(task -> {
                        String taskInfo = "Task Id: " + task.getIdTask() + " Duration: " + task.estimateDuration();
                        JMenuItem taskItem = new JMenuItem(taskInfo);
                        popupMenuTaskOfEmployee.add(taskItem);
                    });

                    popupMenuTaskOfEmployee.show(employeeList, e.getX(), e.getY());
                }
            }
        }
    }

    public void addEmployee() {
        String name = JOptionPane.showInputDialog(this, "Enter Employee Name", "Add Employee", JOptionPane.PLAIN_MESSAGE);
        if( name != null && !name.isEmpty() ) {
            Employee emp = new Employee(manager.getMap().size()+1, name);
            manager.addEmployee(emp);
            loadEmployees();
        }else {
            JOptionPane.showMessageDialog(this, "Enter Employee Name", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showTasks() {
        //shows all tasks
        //if assigned then can modify status
        //if unassigned then can assign to employee

        rightPanel.removeAll();
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);

        listModel.clear();
        JScrollPane scrollPane = new JScrollPane(taskList);
        List<String> allTasks = manager.getAllTasks();
        allTasks.forEach(task -> {
            if (!listModel.contains(task)) {
                listModel.addElement(task);
            }
        });

        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {showPopupMenuUnassigned(e);}
            @Override
            public void mouseReleased(MouseEvent e) {showPopupMenuUnassigned(e);}
        });

        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {showPopupMenuAssigned(e);}
            @Override
            public void mouseReleased(MouseEvent e) {showPopupMenuAssigned(e);}
        });

        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();

    }

    public void addNewTask() {
        String[] options = {"Simple", "Complex"};
        int choice = JOptionPane.showOptionDialog(this, "Select Task Type", "Add new task",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            //simple
            String startHour = JOptionPane.showInputDialog(this, "Enter Task Start Hour", JOptionPane.PLAIN_MESSAGE);
            if( startHour != null && !startHour.isEmpty() ) {
                    String endHour = JOptionPane.showInputDialog(this, "Enter Task End Hour", JOptionPane.PLAIN_MESSAGE);
                    if( endHour != null && !endHour.isEmpty() ) {
                        int start = Integer.parseInt(startHour);
                        int end = Integer.parseInt(endHour);
                        int newID = manager.getNextTaskId();
                        SimpleTask newSimpleTask = new SimpleTask(newID,start, end );
                        manager.addUnassignedTask(newSimpleTask);
                        showTasks();
                    }
            }else {
                JOptionPane.showMessageDialog(this, "Enter Employee Name", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (choice == 1) {
            //complex
            List<Task> unassignedTasks = manager.getUnassignedTasks();
            String[] tasksToChooseFrom = new String[unassignedTasks.size()];
            AtomicInteger index = new AtomicInteger(0);
            unassignedTasks.forEach(task ->
                    tasksToChooseFrom[index.getAndIncrement()] = "Task ID: " + task.getIdTask() + " Duration: " + task.estimateDuration()
            );

            JList<String> taskList = new JList<>(tasksToChooseFrom);
            taskList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            int result = JOptionPane.showConfirmDialog(this, new JScrollPane(taskList),
                    "Select subtasks(multiple choice)", JOptionPane.OK_CANCEL_OPTION);
            if(result == JOptionPane.OK_OPTION) {
                List<Task> selectedTasks = new ArrayList<>();
                int[] selectedIndices = taskList.getSelectedIndices();
                System.out.println(selectedIndices);
                for(int idx: selectedIndices){
                    selectedTasks.add(unassignedTasks.get(idx));
                }
                if(!selectedTasks.isEmpty()) {
                    int newID = manager.getNextTaskId();
                    ComplexTask newComplexTask = new ComplexTask(newID,new ArrayList<>( selectedTasks));
                    for(Task task : selectedTasks){
                        manager.removeUnassignedTask(task);
                    }
                    manager.addUnassignedTask(newComplexTask);
                    showTasks();
                }else {
                    JOptionPane.showMessageDialog(this, "Nothing selected", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
    }

    public void showPopupMenuUnassigned(MouseEvent e) {
        if(e.isPopupTrigger()) {
            int index = taskList.locationToIndex(e.getPoint());
            if(index >=0){
                taskList.setSelectedIndex(index);
                String selectedTask = taskList.getSelectedValue();

                Pattern pattern = Pattern.compile("Unassigned: ID:\\s*(\\d+)");
                Matcher matcher = pattern.matcher(selectedTask);
                int selectedTaskId = 0;

                if (matcher.find()) {
                    selectedTaskId = Integer.parseInt(matcher.group(1));
                }
                // selected id should be an unassigned task from that list
                List<Task> unassignedTasks = manager.getUnassignedTasks();
                int finalSelectedTaskId = selectedTaskId;
                AtomicBoolean isUnassigned = new AtomicBoolean(false);

                unassignedTasks.forEach(task -> {
                    if (task.getIdTask() == finalSelectedTaskId) {
                        isUnassigned.set(true);
                    }
                });

                if (isUnassigned.get()) {
                    popupMenuUnassigned.show(taskList, e.getX(), e.getY());
                }
            }
        }
    }

    public void showPopupMenuAssigned(MouseEvent e) {
        if(e.isPopupTrigger()) {
            int index = taskList.locationToIndex(e.getPoint());
            if(index >=0){
                taskList.setSelectedIndex(index);
                String selectedTask = taskList.getSelectedValue();

                Pattern pattern = Pattern.compile("(?:SimpleTask|ComplexTask) ID:\\s*(\\d+)");
                Matcher matcher = pattern.matcher(selectedTask);
                int selectedTaskId;

                if (matcher.find()) {
                    selectedTaskId = Integer.parseInt(matcher.group(1));
                } else {
                    selectedTaskId = 0;
                }

                List<Task> assignedTasks = manager.getAssignedTasks();
                AtomicBoolean isAssigned = new AtomicBoolean(false);

                assignedTasks.forEach(task -> {
                    if (task.getIdTask() == selectedTaskId) {
                        isAssigned.set(true);
                    }
                });

                if (isAssigned.get()) {
                    popupMenuAssigned.show(taskList, e.getX(),e.getY());
                }
            }
        }
    }

    public void handleAssignTask() {
        popupMenuEmployees = new JPopupMenu();
        List<Employee> employees = manager.getEmployeeList();

        String selectedTask = taskList.getSelectedValue();

        Pattern pattern = Pattern.compile("Unassigned: ID:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(selectedTask);
        int selectedTaskId;

        if (matcher.find()) {
            selectedTaskId = Integer.parseInt(matcher.group(1));
        } else {
            selectedTaskId = 0;
        }

        AtomicReference<Task> taskToAssignAtomic = new AtomicReference<>(null);

        manager.getUnassignedTasks().forEach(task -> {
            if (task.getIdTask() == selectedTaskId && taskToAssignAtomic.get() == null) {
                taskToAssignAtomic.set(task);
            }
        });

        Task taskToAssign = taskToAssignAtomic.get();

        employees.forEach(employee -> {
            JMenuItem empItem = new JMenuItem(employee.getName());
            empItem.addActionListener(e -> {
                manager.assignTaskToEmployee(employee, taskToAssign);
                showTasks();
            });
            popupMenuEmployees.add(empItem);
        });

        popupMenuEmployees.show(this, getMousePosition().x, getMousePosition().y);

    }

    public void handleChangeOfStatus(){
            //need to have task id and employee id
        String selectedTask = taskList.getSelectedValue();

        Pattern pattern = Pattern.compile("Employee:\\s*(\\d+).*?ID:\\s*(\\d+).*?Status:\\s*(\\w+)");
        Matcher matcher = pattern.matcher(selectedTask);

        int selectedTaskId = 0;
        int selectedTaskEmpId;
        String currentStatus = "";

        if (matcher.find()) {
            selectedTaskEmpId = Integer.parseInt(matcher.group(1));
            selectedTaskId = Integer.parseInt(matcher.group(2));
            currentStatus = matcher.group(3);
        } else {
            selectedTaskEmpId = 0;
        }

        AtomicReference<Employee> empAtomic = new AtomicReference<>(null);

        manager.getEmployeeList().forEach(employee -> {
            if (employee.getIdEmployee() == selectedTaskEmpId && empAtomic.get() == null) {
                empAtomic.set(employee);
            }
        });

        Employee emp = empAtomic.get();
        if(selectedTaskId >0 && emp != null) {
            String newStatus;
            if(currentStatus.equals("Completed"))
                newStatus = "not finished";
            else
                newStatus = "Completed";
            manager.modifyTaskStatus(emp, selectedTaskId, newStatus);
            showTasks();
        }
    }
}

