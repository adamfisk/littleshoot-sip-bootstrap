package org.lastbamboo.common.sip.bootstrap;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.sip.stack.util.UriUtils;
import org.lastbamboo.common.util.CandidateProvider;
import org.lastbamboo.shoot.bootstrap.server.api.BootstrapServer;

/**
 * The candidate provider that provides candidate registrars.
 */
public final class RegistrarCandidateProvider implements CandidateProvider<URI>
    {
    
    private static final Log LOG = 
        LogFactory.getLog(RegistrarCandidateProvider.class);
    
    /**
     * The bootstrap server to get SIP proxies with which to register.
     */
    //private final BootstrapServer m_bootstrapServer;
    
    /**
     * The default transport to use to connect to this host.
     */
    private static final String DEFAULT_TRANSPORT = "tcp";

    private static final int DEFAULT_PORT = 5060;

    private final UriUtils m_uriUtils;

    /**
     * Constructs a new registrar candidate provider.
     * 
     * @param uriUtils SIP URI utilities class.
     * @param bootstrapServer
     *      The bootstrap server to get SIP proxies with which to register.
     */
    public RegistrarCandidateProvider
            (final UriUtils uriUtils,
             final BootstrapServer bootstrapServer)
        {
        m_uriUtils = uriUtils;
        //this.m_bootstrapServer = bootstrapServer;
        }

    /**
     * {@inheritDoc}
     */
    public Collection<URI> getCandidates()
        {
        // TODO: We need to access the list of servers from S3.
        final InetAddress address;
        try
            {
            address = InetAddress.getByName(
                "ec2-67-202-6-199.z-1.compute-1.amazonaws.com");
            }
        catch (final UnknownHostException e)
            {
            LOG.error("Could not resolve address", e);
            return Collections.emptySet();
            }
        
        // The URI we are given is the public address (and SIP port) of
        // this host. To convert to the URI of the proxy we are running,
        // we replace the port with the proxy port.
        final String host = address.getHostAddress();

        final URI uri = m_uriUtils.getSipUri(host, DEFAULT_PORT, 
            DEFAULT_TRANSPORT);
        
        final Collection<URI> sipServerUris = new LinkedList<URI>();
        sipServerUris.add(uri);
        return sipServerUris;
        
        // NOTE: This was modified to not use the bootstrap server 
        // mechanism below for the following reasons:
        //
        // 1) The SIP server was experiencing "too many open files" issues,
        // and the top candidate for the cause was the hessian calls to update
        // the list of bootstrap servers.
        //
        // 2) The need for the bootstrap server mechanism was largely based
        // on peers acting as SIP servers.  Now that we use central SIP servers,
        // this isn't as important.

        /*
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
        */
        }

    public URI getCandidate()
        {
        return getCandidates().iterator().next();
        }
    }
