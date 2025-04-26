package DataModel;

import java.io.Serializable;

public abstract sealed class Task implements Serializable permits SimpleTask, ComplexTask {
    private static final long serialVersionUID = 1L;
    protected int idTask;
    protected String statusTask;

    public Task(int idTask){
        this.idTask = idTask;
        this.statusTask = "not finished";
    }

    public abstract int estimateDuration();

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public String getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(String statusTask) {
        this.statusTask = statusTask;
    }
}
