package org.lastbamboo.common.sip.bootstrap;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;

import org.lastbamboo.common.offer.answer.IceMediaStreamDesc;
import org.lastbamboo.common.offer.answer.NoAnswerException;
import org.lastbamboo.common.offer.answer.OfferAnswerFactory;
import org.lastbamboo.common.offer.answer.OfferAnswerTransactionListener;
import org.lastbamboo.common.p2p.DefaultTcpUdpSocket;
import org.lastbamboo.common.p2p.P2PClient;
import org.lastbamboo.common.p2p.P2PConnectionListener;
import org.lastbamboo.common.p2p.TcpUdpSocket;
import org.lastbamboo.common.sip.client.SipClient;
import org.lastbamboo.common.sip.client.SipClientTracker;
import org.lastbamboo.common.sip.client.util.ProxyRegistrationListener;
import org.lastbamboo.common.sip.stack.SipUriFactory;
import org.littleshoot.util.KeyStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class kicks off all SIP client services.
 */
public final class SipClientLauncher implements P2PClient {

    /**
     * The log for this class.
     */
    private static final Logger LOG = 
        LoggerFactory.getLogger (SipClientLauncher.class);

    /**
     * The object for maintaining a registration with a SIP proxy.
     */
    private final RobustProxyRegistrarFactory m_registrarFactory;

    private final SipClientTracker m_sipClientTracker;

    private final OfferAnswerFactory m_offerAnswerFactory;

    private final int m_relayWaitTime;

    private volatile boolean loggedIn;

    /**
     * Launches a SIP client.
     * 
     * @param sipClientTracker Keeps track of SIP clients.
     * @param registrarFactory The object for maintaining a registration with a 
     * SIP proxy.
     * @param offerAnswerFactory Factory for creating offers and answers.
     * @param relayWaitTime The time to wait before using a relay.
     */
    public SipClientLauncher(final SipClientTracker sipClientTracker,
            final RobustProxyRegistrarFactory registrarFactory,
            final OfferAnswerFactory offerAnswerFactory, 
            final int relayWaitTime) {
        this.m_sipClientTracker = sipClientTracker;
        this.m_registrarFactory = registrarFactory;
        this.m_offerAnswerFactory = offerAnswerFactory;
        this.m_relayWaitTime = relayWaitTime;
    }
    
    public String login(final String user, final String password) {
        LOG.debug("Registering...");
        // Set up the URI used as the 'From' for SIP messages.
        final URI sipUri = SipUriFactory.createSipUri(user);
        register(sipUri);
        this.loggedIn = true;
        return sipUri.toASCIIString();
    }

    public String login(final String user, final String password,
        final String id) throws IOException {
        return login(user, password);
    }

    /**
     * Registers a given user ID with SIP proxies so that other people can
     * connect to her.
     *
     * @param userId The identifier of the user to register.
     */
    private void register(final URI sipUri) {
        if (loggedIn) {
            LOG.info("Already logged in -- not logging in again.");
            return;
        }
        // Register with the SIP network.
        final ProxyRegistrar registrar = m_registrarFactory.getRegistrar(
                sipUri, new NoOpRegistrationListener());

        registrar.register();
    }

    private static final class NoOpRegistrationListener implements
            ProxyRegistrationListener {

        public void reRegistered(URI client, URI proxy) {
            LOG.debug("Got re-registered");
        }

        public void registered(URI client, URI proxy) {
            LOG.debug("Got registered");
        }

        public void registrationFailed(URI client, URI proxy) {
            LOG.debug("Got registration failed.");
        }

        public void unregistered(URI client, URI proxy) {
            LOG.debug("Got unregistered");
        }
    }

    public void offer(URI uri, byte[] offer,
            OfferAnswerTransactionListener transactionListener,
            KeyStorage keyStore) throws IOException {
        LOG.error("Offer not supported");
        throw new UnsupportedOperationException("Offer not supported");
    }
    
    public Socket newSocket(final URI sipUri) throws IOException,
            NoAnswerException {
        LOG.trace("Creating SIP socket for URI: {}", sipUri);
        final SipClient client = this.m_sipClientTracker.getSipClient();
        if (client == null) {
            LOG.warn("No available SIP clients!!");
            throw new IOException("No available connections to SIP proxies!!");
        }

        final IceMediaStreamDesc stream = IceMediaStreamDesc.newReliable();
        final TcpUdpSocket tcpUdpSocket = new DefaultTcpUdpSocket(client,
                this.m_offerAnswerFactory, this.m_relayWaitTime, stream);

        return tcpUdpSocket.newSocket(sipUri);
    }

    public Socket newUnreliableSocket(final URI sipUri) throws IOException,
            NoAnswerException {
        LOG.trace("Creating SIP socket for URI: {}", sipUri);
        final SipClient client = this.m_sipClientTracker.getSipClient();
        if (client == null) {
            LOG.warn("No available SIP clients!!");
            throw new IOException("No available connections to SIP proxies!!");
        }

        final IceMediaStreamDesc desc =
            new IceMediaStreamDesc(true, true, "application", "udp", 1, true, 
                    true);
            //IceMediaStreamDesc.newUnreliableUdpStream();
        final TcpUdpSocket tcpUdpSocket = new DefaultTcpUdpSocket(client,
                this.m_offerAnswerFactory, this.m_relayWaitTime, desc);

        return tcpUdpSocket.newSocket(sipUri);
    }

    public Socket newRawSocket(final URI uri) throws IOException, 
        NoAnswerException {
        return newSocket(uri);
    }

    public Socket newRawUnreliableSocket(final URI uri) throws IOException,
        NoAnswerException {
        return newUnreliableSocket(uri);
    }

    @Override
    public void logout() {
        // TODO Does nothing for now -- we need to implement this!!
    }

    @Override
    public void addConnectionListener(P2PConnectionListener listener) {
        // TODO Not supported for now.
    }

}
