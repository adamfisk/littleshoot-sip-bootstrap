package org.lastbamboo.common.sip.bootstrap;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.offer.answer.OfferAnswerTransactionListener;
import org.lastbamboo.common.p2p.P2PSignalingClient;
import org.lastbamboo.common.sip.client.util.ProxyRegistrationListener;
import org.lastbamboo.common.sip.stack.SipUriFactory;

/**
 * This class kicks off all SIP client services.
 */
public final class SipClientLauncher implements P2PSignalingClient
    {

    /**
     * The log for this class.
     */
    private static final Logger LOG = 
        LoggerFactory.getLogger (SipClientLauncher.class);

    /**
     * The factory used to create SIP URIs.
     */
    private final SipUriFactory m_sipUriFactory;

    /**
     * The object for maintaining a registration with a SIP proxy.
     */
    private final RobustProxyRegistrarFactory m_registrarFactory;

    /**
     * Launches a SIP client.
     *
     * @param registrarFactory The object for maintaining a registration with a 
     * SIP proxy.
     * @param sipUriFactory The factory for creating SIP URIs from user IDs.
     */
    public SipClientLauncher(final RobustProxyRegistrarFactory registrarFactory,
        final SipUriFactory sipUriFactory)
        {
        this.m_registrarFactory = registrarFactory;
        this.m_sipUriFactory = sipUriFactory;
        }

    /**
     * Registers a given user ID with SIP proxies so that other people can
     * connect to her.
     *
     * @param userId The identifier of the user to register.
     */
    public void register(final long userId)
        {
        LOG.debug("Registering...");
        // Set up the URI used as the 'From' for SIP messages.
        final URI sipUri = m_sipUriFactory.createSipUri (userId);
        register(sipUri);
        }

    /**
     * Registers a given user ID with SIP proxies so that other people can
     * connect to her.
     *
     * @param userId The identifier of the user to register.
     */
    public void register(final URI sipUri) 
        {
        // Register with the SIP network.
        final RobustProxyRegistrar registrar =
            m_registrarFactory.getRegistrar
                (sipUri, new NoOpRegistrationListener());

        registrar.register ();
        }

    /**
     * Registers a given user ID with SIP proxies so that other people can
     * connect to her.
     *
     * @param userId The identifier of the user to register.
     */
    public void register(final String id)
        {
        LOG.debug("Registering...");
        // Set up the URI used as the 'From' for SIP messages.
        final URI sipUri = m_sipUriFactory.createSipUri (id);
        register(sipUri);
        }
    
    private static final class NoOpRegistrationListener 
        implements ProxyRegistrationListener
        {

        public void reRegistered(URI client, URI proxy)
            {
            LOG.debug("Got re-registered");
            }

        public void registered(URI client, URI proxy)
            {
            LOG.debug("Got registered");
            }

        public void registrationFailed(URI client, URI proxy)
            {
            LOG.debug("Got registration failed.");
            }

        public void unregistered(URI client, URI proxy)
            {
            LOG.debug("Got unregistered");
            }
        
        }

    public void offer(final URI sipUri, final byte[] offer,
        final OfferAnswerTransactionListener transactionListener) 
        {
        LOG.error("Offer not supported");
        throw new UnsupportedOperationException("Offer not supported");
        }

    public void login(final String user, final String password) 
        {
        LOG.error("Offer not supported");
        throw new UnsupportedOperationException("Offer not supported");
        }
    }
