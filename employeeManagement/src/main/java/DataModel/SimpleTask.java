package DataModel;

public final class  SimpleTask extends Task {
    protected int startHour;
    protected int endHour;

    public SimpleTask(int idTask,int startHour, int endHour){
        super(idTask);
        this.startHour = startHour;
        this.endHour = endHour;
    }

    @Override
    public int estimateDuration(){
        return endHour - startHour;
    }

    @Override
    public String toString() {
        return "SimpleTask{" +
                "statusTask: " + statusTask+
                ", idTask: " + idTask +
                '}';
    }
}
