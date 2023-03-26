package api;

public class User {
    // создаем ключи  с полем типа String
    private String email;
    private String password;
    private String name;

    // создаем конструктор со всеми параметрами "Регистрация пользователя"
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public User(User user) {
        this.email = user.email;
        this.password = user.password;
        this.name = user.name;
    }

    // создаем конструктор со всеми параметрами "Логин пользователя"
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // конструктор без параметров
    public User() {
    }

    // геттер для поля email
    public String getEmail() {
        return email;
    }

    // сеттер для поля email
    public void setEmail(String email) {
        this.email = email;
    }

    // геттер для поля password
    public String getPassword() {
        return password;
    }

    // сеттер для поля password
    public void setPassword(String password) {
        this.password = password;
    }

    // геттер для поля name
    public String getName() {
        return name;
    }

    // сеттер для поля name
    public void setName(String name) {
        this.name = name;
    }

}




