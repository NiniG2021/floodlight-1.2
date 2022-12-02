package net.floodlightcontroller.authentication;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.internal.IOFSwitchService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.util.AppCookie;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.topology.ITopologyService;
import org.projectfloodlight.openflow.protocol.OFFactories;
import org.projectfloodlight.openflow.protocol.OFFactory;
import org.projectfloodlight.openflow.protocol.OFVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationManager implements IFloodlightModule{

    private static final short APP_ID = 10;

    static {
        AppCookie.registerApp(APP_ID, "AuthenticationManager");
    }

    // Logger for module
    protected static Logger log = LoggerFactory.getLogger(AuthenticationManager.class);

    // Services to use by module
    protected IFloodlightProviderService floodlightProviderService;
    protected IDeviceService deviceService;
    protected ITopologyService topologyService;
    protected IOFSwitchService switchService;
    protected IRoutingService routingService;
    protected IRestApiService restApiService;
    protected IStaticFlowEntryPusherService sfp;

    private OFFactory factory = OFFactories.getFactory(OFVersion.OF_13);

    // IFloodlightModule
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
        return null;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        Map<Class<? extends IFloodlightService>, IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
        //l.add(IVirtualNetworkService.class);
        //return l;
        return null;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
        l.add(IDeviceService.class);
        l.add(ITopologyService.class);
        l.add(IOFSwitchService.class);
        l.add(IRoutingService.class);
        l.add(IRestApiService.class);
        l.add(IStaticFlowEntryPusherService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProviderService = context.getServiceImpl(IFloodlightProviderService.class);
        deviceService = context.getServiceImpl(IDeviceService.class);
        topologyService = context.getServiceImpl(ITopologyService.class);
        switchService = context.getServiceImpl(IOFSwitchService.class);
        routingService = context.getServiceImpl(IRoutingService.class);
        restApiService = context.getServiceImpl(IRestApiService.class);
        sfp = context.getServiceImpl(IStaticFlowEntryPusherService.class);

    }

    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {}
}
