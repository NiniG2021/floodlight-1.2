package net.floodlightcontroller.intranetattack;
import java.util.*;

import net.floodlightcontroller.core.util.AppCookie;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.internal.DeviceManagerImpl;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.TCP;
import net.floodlightcontroller.restserver.IRestApiService;
import net.floodlightcontroller.virtualnetwork.HostResource;
import net.floodlightcontroller.virtualnetwork.IVirtualNetworkService;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.*;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import net.floodlightcontroller.core.IFloodlightProviderService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import net.floodlightcontroller.packet.Ethernet;
import org.sdnplatform.sync.internal.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Intranetattack implements IOFMessageListener, IFloodlightModule{

    protected static Logger log = LoggerFactory.getLogger(Intranetattack.class);

    private static final short APP_ID = 100;
    private static final long MILLIS_PER_SEC = 1000;

    static {
        AppCookie.registerApp(APP_ID, "Intranetattack");
    }

    // Our dependencies
    IFloodlightProviderService floodlightProviderService;
    IRestApiService restApiService;
    IDeviceService deviceService;

    // Our internal state

    protected Map<MacAddress, Integer> hostToSyn; // map of host MAC to syn flag counter
    protected Map<MacAddress, Integer> hostToSynAck; // map of host MAC to syn-ack flag counter
    protected Map<MacAddress, Long> hostToTimestamp; // map of host MAC to timestamp

    protected Map<IPv4Address, Host> hostsConsultados;
    protected Double thresholdTime;
    protected Integer thresholdCantPorts;
    protected ArrayList<MacAddress> hostBlocked;


    // IFloodlightModule

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IVirtualNetworkService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService>
    getServiceImpls() {
        Map<Class<? extends IFloodlightService>,
                IFloodlightService> m = new HashMap<Class<? extends IFloodlightService>, IFloodlightService>();
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l = new ArrayList<>();
        l.add(IFloodlightProviderService.class);
        l.add(IRestApiService.class);
        l.add(IDeviceService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context)
            throws FloodlightModuleException {
        floodlightProviderService = context.getServiceImpl(IFloodlightProviderService.class);
        restApiService = context.getServiceImpl(IRestApiService.class);
        deviceService = context.getServiceImpl(IDeviceService.class);

        //hostToSyn = new ConcurrentHashMap<MacAddress, String>();
        hostToSyn = new ConcurrentHashMap<MacAddress, Integer>();
        //hostToSynAck = new ConcurrentHashMap<MacAddress, String>();
        hostToSynAck = new ConcurrentHashMap<MacAddress, Integer>();

        //Definimos los parametros para el analisis,como la cantidad de puertos y el holdtime
        hostsConsultados = new ConcurrentHashMap<IPv4Address, Host>();
        thresholdTime = 3.0;
        thresholdCantPorts = 1600;
        hostBlocked = new ArrayList<>();

    }

    @Override
    public void startUp(FloodlightModuleContext context) {
        floodlightProviderService.addOFMessageListener(OFType.PACKET_IN, this);
    }

    // IOFMessageListener

    @Override
    public String getName() {
        return "anti port scanning";
    }

    @Override
    public boolean isCallbackOrderingPrereq(OFType type, String name) {
        // Link discovery should go before us so we don't block LLDPs
        return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(OFType type, String name) {
        // We need to go before forwarding
        return (type.equals(OFType.PACKET_IN) && name.equals("forwarding"));
    }

    @Override
    public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
        switch (msg.getType()) {
            case PACKET_IN:
                return processPacketIn(sw, (OFPacketIn) msg, cntx);
            default:
                break;
        }
        log.warn("Received unexpected message {}", msg);
        return Command.CONTINUE;
    }


    /**
     * Processes an OFPacketIn message and decides if the OFPacketIn should be dropped
     * or the processing should continue.
     *
     * @param sw   The switch the PacketIn came from.
     * @param msg  The OFPacketIn message from the switch.
     * @param cntx The FloodlightContext for this message.
     * @return Command.CONTINUE if processing should be continued, Command.STOP otherwise.
     *
     */


    //vemos que este metodo va a buscar a traves del la lista de host blocked va a consultar el MAC para posteriormente realizar la
    //funcion correspondiente
    protected Command processPacketIn(IOFSwitch sw, OFPacketIn msg, FloodlightContext cntx) {
        Ethernet eth = IFloodlightProviderService.bcStore.get(cntx,
                IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
        Command ret = Command.STOP;
        if (!hostBlocked.contains(eth.getSourceMACAddress())) {
            detectorIpSpoofing(eth);
        }
//        if (!hostBlocked.contains(eth.getSourceMACAddress())) {
//            portScanning(eth);
//        }
        if (!hostBlocked.contains(eth.getSourceMACAddress())) {
            ret = Command.CONTINUE;
        }

        if (log.isTraceEnabled())
            log.trace("Anti port scann entre {} y {}",
                    new Object[]{eth.getSourceMACAddress(), eth.getDestinationMACAddress()});

        return ret;


    }



    //realizamos codigo para Port scanning
   //----------

    protected void detectorIpSpoofing(Ethernet eth) {
        String ipDeviceVulnerado = "";
        String macDeviceVulnerado = "";
        //vemos que protocolo sigue,si es IPv4 y TCP analizando su carga util
        String macSource = eth.getSourceMACAddress().toString();
        if (eth.getEtherType().equals(EthType.IPv4)) {
            IPv4 ip = (IPv4) eth.getPayload();
            String ipSource = ip.getSourceAddress().toString();

            //Extraemos todos los dispositivos de la red
            Collection<? extends IDevice> devicesCollection = deviceService.getAllDevices();
            ArrayList<IDevice> devicesArrayList = new ArrayList<>(devicesCollection);
            boolean sameDevice = false;
            boolean ipFound = false;
            for (IDevice idDevice : devicesArrayList) {
                //Ontenemos la direcci??n ipv4 de estos dispositivos
                IPv4Address[] iPv4Addresses = idDevice.getIPv4Addresses();
                for (IPv4Address ipv4 : iPv4Addresses) {
                    if (ipv4.toString().equalsIgnoreCase(ipSource)) {
                        //Si existe una coincidencia => comparo la mac
                        ipFound = true;
                        ipDeviceVulnerado = ipv4.toString();
                        macDeviceVulnerado = idDevice.getMACAddressString();
                        if (idDevice.getMACAddressString().equalsIgnoreCase(macSource)) {
                            //Si la mac coincide, entonces se trata de un trafico normal, no hay ipSpoofing
                            sameDevice = true;
                        }
                    }
                }
            }

        }
    }


}
