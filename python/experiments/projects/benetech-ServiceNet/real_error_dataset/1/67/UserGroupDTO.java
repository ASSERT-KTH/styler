package org.benetech.servicenet.service.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link org.benetech.servicenet.domain.UserGroup} entity.
 */
public class UserGroupDTO implements Serializable {

    private UUID id;

    private String name;


    private UUID siloId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getSiloId() {
        return siloId;
    }

    public void setSiloId(UUID siloId) {
        this.siloId = siloId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGroupDTO)) {
            return false;
        }

        return id != null && id.equals(((UserGroupDTO) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserGroupDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", siloId=" + getSiloId() +
            "}";
    }
}
