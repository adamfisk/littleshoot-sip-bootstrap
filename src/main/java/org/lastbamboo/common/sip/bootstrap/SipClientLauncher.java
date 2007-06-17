package org.lastbamboo.common.sip.bootstrap;

import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.sip.client.util.ProxyRegistrationListener;
import org.lastbamboo.common.sip.stack.SipUriFactory;

/**
 * This class kicks off all SIP client services.
 */
public final class SipClientLauncher
    {

    /**
     * The log for this class.
     */
    private static final Log LOG = LogFactory.getLog (SipClientLauncher.class);

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
        final URI client = m_sipUriFactory.createSipUri (userId);

        // Register with the SIP network.
        final RobustProxyRegistrar registrar =
                m_registrarFactory.getRegistrar
                    (client, new NoOpRegistrationListener());

        registrar.register ();
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
    }
