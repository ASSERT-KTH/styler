@Entity
@Table(name = "sys_auth_role_admin_user")
public class SysAuthRoleAdminUser implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long roleId;
    private Long admId;

    public SysAuthRoleAdminUser() {
    }

    public SysAuthRoleAdminUser(Long roleId, Long admId) {
        this.roleId = roleId;
        this.admId = admId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getAdmId() {
        return admId;
    }

    public void setAdmId(Long admId) {
        this.admId = admId;
    }
}
