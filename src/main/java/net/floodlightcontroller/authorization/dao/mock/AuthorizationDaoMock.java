package net.floodlightcontroller.authorization.dao.mock;

import net.floodlightcontroller.authorization.dao.AuthorizationDao;

public class AuthorizationDaoMock implements AuthorizationDao {
    @Override
    public String getUserByIp(String ip) {
        if (ip.equals("10.0.0.1"))
            return "201505050";

        if (ip.equals("10.0.0.2"))
            return "201888888";

        return null;
    }

    @Override
    public String getResourceIdByIp(String ip) {

        if (ip.equals("10.0.0.3"))
            return "123456789";

        return null;
    }

    @Override
    public boolean isThisUserAuthorizedForThisResource(String user, String resourceId) {

        if (user.equals("201505050"))
            return true;

        if (user.equals("201888888"))
            return false;

        return false;
    }
}
