package DataModel;

import java.io.Serializable;
import java.util.List;

public final class ComplexTask extends Task implements Serializable {
    private static final long serialVersionUID = 1L;
    protected List<Task> tasks;
    public ComplexTask(int idTask, List<Task> tasks) {
        super(idTask);
        this.tasks = tasks;
    };

    @Override
    public int estimateDuration() {
        int time=0;
        for(Task task: tasks) {
            time+=task.estimateDuration();
        }
        return time;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task newTask){
        tasks.add(newTask);
    }

    public void deleteTask(Task delTask){
        tasks.remove(delTask);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getIdTask());
        sb.append(" Tasks:");

        for (Task task : tasks) {
            sb.append(task.toString());
        }

        sb.append(" Estimated Duration: ").append(estimateDuration());
        return sb.toString();
    }

}
