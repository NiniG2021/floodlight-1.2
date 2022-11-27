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

        if(ip.equals("1.0.0.3")){
            return "20180074";
        }
        return null;
    }

    @Override
    public String getResourceIdByIp(String ip) {

        if (ip.equals("10.0.0.3")){
            return "123456789";
        }

        if (ip.equals("10.0.0.1")){
            return "20182162";
        }
        return null;
    }

    @Override
    public boolean isThisUserAuthorizedForThisResource(String user, String resourceId) {

        if (user == null) {
            return false;
        }
        System.out.println("llegamos hasta aqui");
        System.out.println(user);
        return user.equals("20150505");
    }
}
