package ml.echelon133.model;

import ml.echelon133.security.SecretGenerator;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String secret;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name="user_authorities",
            joinColumns = @JoinColumn(name="user_fk", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="authority_fk", referencedColumnName = "id")
    )
    private Set<Authority> authorities;

    @OneToMany(mappedBy="listOwner", cascade = CascadeType.ALL)
    private List<TodoList> todoLists;

    public User() {
        setTodoLists(new ArrayList<>());
        setAuthorities(new HashSet<>());

        SecretGenerator sgen = new SecretGenerator(16);
        String secret = sgen.generateSecret();
        setSecret(secret);
    }

    public User(String username, String password) {
        this();
        setUsername(username);
        setPassword(password);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public void addAuthority(Authority authority) {
        this.authorities.add(authority);
    }

    public List<TodoList> getTodoLists() {
        return todoLists;
    }

    public void setTodoLists(List<TodoList> todoLists) {
        for (TodoList list : todoLists) {
            list.setListOwner(this);
        }
        this.todoLists = todoLists;
    }

    public void addTodoList(TodoList todoList) {
        todoList.setListOwner(this);
        this.todoLists.add(todoList);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
