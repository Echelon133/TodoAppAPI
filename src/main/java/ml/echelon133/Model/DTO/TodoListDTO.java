package ml.echelon133.Model.DTO;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class TodoListDTO {

    @NotNull
    @Length(min=4, max=60)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
