package net.floodlightcontroller.authorization.dao.mock;

import net.floodlightcontroller.authorization.dao.AuthenticationDao;

public class AuthenticationDaoMock implements AuthenticationDao {
    @Override
    public boolean verifyAuthentication(String ip, String mac) {

        if (ip.equals("10.0.0.1") || ip.equals("10.0.0.2"))
            return true;

        return false;
    }
}
