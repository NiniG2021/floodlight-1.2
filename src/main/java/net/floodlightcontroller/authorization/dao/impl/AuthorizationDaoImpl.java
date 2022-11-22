package net.floodlightcontroller.authorization.dao.impl;

import net.floodlightcontroller.authorization.dao.AuthorizationDao;

public class AuthorizationDaoImpl implements AuthorizationDao {

    @Override
    public String getUserByIp(String ip) {
        return null;
    }

    @Override
    public String getResourceIdByIp(String ip) {
        return null;
    }

    @Override
    public boolean isThisUserAuthorizedForThisResource(String user, String resourceId) {
        return false;
    }
}
