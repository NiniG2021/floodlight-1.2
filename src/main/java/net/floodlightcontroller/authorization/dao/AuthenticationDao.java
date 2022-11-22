package net.floodlightcontroller.authorization.dao;

public interface AuthenticationDao {

    boolean verifyAuthentication(String ip, String mac);

}
