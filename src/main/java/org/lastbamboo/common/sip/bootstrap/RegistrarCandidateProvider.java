package org.lastbamboo.common.sip.bootstrap;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.sip.stack.util.UriUtils;
import org.lastbamboo.common.util.CandidateProvider;
import org.lastbamboo.common.util.UriOfString;
import org.lastbamboo.shoot.bootstrap.server.api.BootstrapServer;

/**
 * The candidate provider that provides candidate registrars.
 */
public final class RegistrarCandidateProvider implements CandidateProvider
    {
    
    private static final Log LOG = 
        LogFactory.getLog(RegistrarCandidateProvider.class);
    
    /**
     * The bootstrap server to get SIP proxies with which to register.
     */
    private final BootstrapServer m_bootstrapServer;

    /**
     * Constructs a new registrar candidate provider.
     *
     * @param bootstrapServer
     *      The bootstrap server to get SIP proxies with which to register.
     */
    public RegistrarCandidateProvider
            (final UriUtils uriUtils,
             final BootstrapServer bootstrapServer)
        {
        this.m_bootstrapServer = bootstrapServer;
        }

    /**
     * {@inheritDoc}
     */
    public Collection getCandidates
            ()
        {
        LOG.debug("Accessing candidates...");
        final Transformer uriOfString = new UriOfString ();

        // These URIs have the transport embedded in them.
        final Collection uriStrings =
                this.m_bootstrapServer.getSipProxies (6, "");

        if (uriStrings.isEmpty())
            {
            LOG.warn("No SIP proxies available!!");
            }
        // We now have a collection of Strings that are really URIs of SIP
        // proxies.  We convert these to actual URI objects.
        final Collection uris =
                CollectionUtils.collect (uriStrings, uriOfString);

        return (uris);
        }
    }
