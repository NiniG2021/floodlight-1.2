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

        if (ip.equals("192.168.200.202")){
            return "20180000";
        }

        if (ip.equals("192.168.200.204")){
            return "20180001";
        }
        if (ip.equals("192.168.200.201")){
            return "20180002";
        }
        if (ip.equals("192.168.200.200")){
            return "15122022";
        }

        if (ip.equals("10.0.0.4")){
            return "20189999";
        }



        if(ip.equals("10.0.0.3")){
            System.out.println("mi codigue");
            return "20180074";
        }

        return null;
    }

    @Override
    public String getResourceIdByIp(String ip) {

        if (ip.equals("10.0.0.3")){
            return "12345678";
        }

        if (ip.equals("10.0.0.1")){
            System.out.println("mi codigue x2");
            return "20182162";
        }
        if (ip.equals("10.0.0.2")){
            System.out.println("mi codigue x2");
            return "20210726";
        }

        if(ip.equals("10.0.0.4")){
            return  "20210727";
        }


        return null;
    }

    @Override
    public boolean isThisUserAuthorizedForThisResource(String user, String resourceId) {

        if (user == null) {
            return false;
        }

        if(user.equals("20150505")&&resourceId.equals("20210726")){
            return true;
        }
        if(user.equals("20150505")&&resourceId.equals("20210727")){
            return true;
        }


        if(user.equals("20150505")&&resourceId.equals("12345678")){
            return true;
        }

        if(user.equals("20180074")&&resourceId.equals("20182162")){
            return true;
        }
        if(user.equals("20188888")&&resourceId.equals("20182162")){
            return true;
        }

        if(user.equals("20189999")&&resourceId.equals("20182162")){
            return true;
        }

        if(user.equals("20180074")&&resourceId.equals("20210727")){
            return true;
        }

        if(user.equals("20189999")&&resourceId.equals("12345678")){
            return true;
        }

       /* if(user.equals("20188888")){
            return true;
        }

        */
        if(user.equals("20180000")){
            return true;
        }
        if(user.equals("20180001")){
            return true;
        }
        if(user.equals("20180002")){
            return true;
        }
        if(user.equals("15122022")){
            return true;
        }

        return false;
    }
}
