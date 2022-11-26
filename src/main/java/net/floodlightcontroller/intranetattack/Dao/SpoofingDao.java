package net.floodlightcontroller.intranetattack.Dao;

public interface SpoofingDao {

    String getUserIp(String ip);

    String getResourceIdByIp(String ip);

}
