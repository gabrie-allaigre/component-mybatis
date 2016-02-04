import com.synaptix.component.IComponent;

public interface IUser extends IComponent {

    @EqualsKey
    int getId();

    void setId(int id);

    String getLogin();

    void setLogin(String login);

}
