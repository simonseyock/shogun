package de.terrestris.shoguncore.model.security.permission;

import de.terrestris.shoguncore.model.User;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "userclasspermissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserClassPermission extends ClassPermission {

    @ManyToOne(optional = false)
    private User user;

}