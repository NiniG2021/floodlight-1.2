package net.floodlightcontroller.authorization.dao.mock;

import net.floodlightcontroller.authorization.dao.AuthorizationDao;

public class AuthorizationDaoMock implements AuthorizationDao {
    @Override
    public String getUserByIp(String ip) {
        if (ip.equals("10.0.0.1")){
            return "20150505";
        }

        if (ip.equals("10.0.0.2")){
            return "20188888";
        }


        if (ip.equals("10.0.0.4")){
            return "201899998";
        }
        return null;
    }

    @Override
    public String getResourceIdByIp(String ip) {

        if (ip.equals("10.0.0.3")){
            return "123456789";
        }
        return null;
    }

    @Override
    public boolean isThisUserAuthorizedForThisResource(String user, String resourceId) {

        if (user == null) {
            return false;
        }

        return user.equals("20150505");
    }
}
