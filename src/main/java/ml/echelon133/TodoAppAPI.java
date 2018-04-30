package ml.echelon133;

import ml.echelon133.Model.DTO.APIMessage;
import ml.echelon133.Model.DTO.IAPIMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

@SpringBootApplication
@ComponentScan
public class TodoAppAPI {

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST)
    public IAPIMessage apiMessage() {
        return new APIMessage();
    }

    public static void main(String[] args) {
        SpringApplication.run(TodoAppAPI.class, args);
    }
}
