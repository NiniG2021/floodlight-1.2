package net.floodlightcontroller.authorization.dao;

public interface AuthorizationDao {

    String getUserByIp(String ip);

    String getResourceIdByIp(String ip);

    // user: codigo, resourceId: id del recurso
    boolean isThisUserAuthorizedForThisResource(String user, String resourceId);

}
