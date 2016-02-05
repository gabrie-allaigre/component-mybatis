import com.synaptix.component.IComponent;

public interface IUser extends IComponent {

    @EqualsKey
    Long getId();

    void setId(Long id);

    String getLogin();

    void setLogin(String login);

}
