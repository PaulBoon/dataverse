package edu.harvard.iq.dataverse;

import edu.harvard.iq.dataverse.authorization.providers.builtin.BuiltinUserServiceBean;
import edu.harvard.iq.dataverse.PermissionServiceBean.StaticPermissionQuery;
import edu.harvard.iq.dataverse.actionlogging.ActionLogRecord;
import edu.harvard.iq.dataverse.actionlogging.ActionLogServiceBean;
import edu.harvard.iq.dataverse.authorization.users.GuestUser;
import edu.harvard.iq.dataverse.authorization.users.User;
import edu.harvard.iq.dataverse.util.SystemConfig;
import java.io.Serializable;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

/**
 *
 * @author gdurand
 */
@Named
@SessionScoped
public class DataverseSession implements Serializable{
    
    /* Note that on logout, variables must be cleared manually in DataverseHeaderFragment*/
    private User user;

    @EJB
    PermissionServiceBean permissionsService;

    @EJB
    BuiltinUserServiceBean usersSvc;
	
    @EJB 
    ActionLogServiceBean logSvc;

    @EJB
    SystemConfig systemConfig;

    private static final Logger logger = Logger.getLogger(DataverseSession.class.getCanonicalName());

    private boolean statusDismissed = false;
    
    public User getUser() {
        if ( user == null ) {
            user = GuestUser.get();
        }
 
        return user;
    }

    public void setUser(User aUser) {
        logSvc.log( 
                new ActionLogRecord(ActionLogRecord.ActionType.SessionManagement,(aUser==null) ? "logout" : "login")
                    .setUserIdentifier((aUser!=null) ? aUser.getIdentifier() : (user!=null ? user.getIdentifier() : "") ));
        
        this.user = aUser;
    }

    public boolean isStatusDismissed() {
        return statusDismissed;
    }
    
    public void setStatusDismissed(boolean status) {
        statusDismissed = status; //MAD: Set to true to enable code!
    }
    
    public StaticPermissionQuery on( Dataverse d ) {
            return permissionsService.userOn(user, d);
    }

    public void configureSessionTimeout() {
        HttpSession httpSession = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

        if (httpSession != null) {
            logger.info("jsession: "+httpSession.getId()+" setting the lifespan of the session to " + systemConfig.getLoginSessionTimeout() + " minutes");
            httpSession.setMaxInactiveInterval(systemConfig.getLoginSessionTimeout() * 60); // session timeout, in seconds
        }

    }
}
