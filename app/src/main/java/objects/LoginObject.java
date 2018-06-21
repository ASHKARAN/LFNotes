package objects;

public class LoginObject {

    private String state ;
    private int    id ;
    private String session ;
    private SettingsObject SettingsObject;
    private UserObject UserObject;



    public String getState() {
        return state ;
    }


    public SettingsObject getSettingsObject() {
        return SettingsObject;
    }

    public UserObject getUserObject() {
        return UserObject ;
    }


    public String getSession() {
        return session;
    }

    public int getId() {
        return id;
    }
}
