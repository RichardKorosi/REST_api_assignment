package sk.stuba.fei.uim.vsa.pr2.user;

import lombok.Data;
import sk.stuba.fei.uim.vsa.pr2.auth.Permission;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Data
public class User implements Principal {

    private Long id;
    private String username;
    private String password;
    private List<Permission> permissions;

    public User(){
        permissions = new ArrayList<>();
    }

    @Override
    public String getName() {
        return username;
    }

    public void addPermission(Permission permission){
        this.permissions.add(permission);
    }
}
