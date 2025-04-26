package DataModel;

import java.io.Serializable;
import java.util.Objects;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private int idEmployee;
    private String name;

    public Employee(int idEmployee, String name) {
        this.idEmployee = idEmployee;
        this.name = name;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "id " + idEmployee + " name " + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee employee = (Employee) obj;
        return idEmployee == employee.idEmployee;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idEmployee);
    }
}
