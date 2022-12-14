package net.floodlightcontroller.authorization;

import net.floodlightcontroller.authorization.dao.AuthorizationDao;
import net.floodlightcontroller.authorization.dao.mock.AuthorizationDaoMock;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.IPv4;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;

import net.floodlightcontroller.core.IFloodlightProviderService;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;
import net.floodlightcontroller.packet.Ethernet;
import org.projectfloodlight.openflow.types.EthType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class Authorization implements IOFMessageListener, IFloodlightModule {
    protected IFloodlightProviderService floodlightProvider;
    protected Set<Long> macAddresses;
    protected static Logger logger;
    protected AuthorizationDao authorizationDao;

    @Override
    public String getName() {
        System.out.printf("entro a authorization");
        return "authorization";
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        return (type.equals(OFType.PACKET_IN) && (name.equals("forwarding")));
    }

    @Override
    public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
        // Se obtiene datos
        Ethernet eth =
                IFloodlightProviderService.bcStore.get(cntx,
                        IFloodlightProviderService.CONTEXT_PI_PAYLOAD);

        if (eth.getEtherType().equals(EthType.IPv4)) {
            /* We got an IPv4 packet; get the payload from Ethernet */
            IPv4 ipv4 = (IPv4) eth.getPayload();

            String user = authorizationDao.getUserByIp(ipv4.getSourceAddress().toString());
            String resourceId = authorizationDao.getResourceIdByIp(ipv4.getDestinationAddress().toString());

            logger.info(String.format("user: %s - resourceId: %s ", user, resourceId));

            boolean isAuthorized = authorizationDao.isThisUserAuthorizedForThisResource(user, resourceId);

            logger.info(String.format("ip_src: %s - ip_dst: %s - isAuthorized: %s ",
                    ipv4.getSourceAddress().toString(), ipv4.getDestinationAddress().toString(),
                    isAuthorized));

            if (!isAuthorized) {
                System.out.println(!isAuthorized);
                return Command.STOP;
            }

            // TODO: implement
            insertFlows();

        }

        return Command.CONTINUE;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        return null;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        return null;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
        macAddresses = new ConcurrentSkipListSet<Long>();
        logger = LoggerFactory.getLogger(Authorization.class);
        // Para unit testing:
        authorizationDao = new AuthorizationDaoMock();

        // Para implementacion:
        //authorizationDao = new AuthorizationDaoImpl();
        //authenticationDao = new AuthenticationDaoImpl();

    }

    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
    }


    // TODO:
    public void insertFlows() {

    }


}
