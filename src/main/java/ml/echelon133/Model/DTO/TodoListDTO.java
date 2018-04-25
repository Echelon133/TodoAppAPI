package ml.echelon133.Model.DTO;

import org.hibernate.validator.constraints.Length;

public class TodoListDTO {

    @Length(min=4, max=60)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
