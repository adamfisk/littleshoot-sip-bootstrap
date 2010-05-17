package org.lastbamboo.common.sip.bootstrap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.lastbamboo.common.sip.stack.util.UriUtils;
import org.lastbamboo.common.util.CandidateProvider;
import org.lastbamboo.common.util.SrvUtil;
import org.lastbamboo.common.util.SrvUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The candidate provider that provides candidate registrars.
 */
public final class RegistrarCandidateProvider implements CandidateProvider<URI>
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    /**
     * The default transport to use to connect to this host.
     */
    private static final String DEFAULT_TRANSPORT = "tcp";

    private final UriUtils m_uriUtils;

    /**
     * Constructs a new registrar candidate provider.
     * 
     * @param uriUtils SIP URI utilities class.
     */
    public RegistrarCandidateProvider(final UriUtils uriUtils)
        {
        m_uriUtils = uriUtils;
        }
        
    public Collection<URI> getCandidates()
        {
        m_log.debug("Accessing SIP servers...");
        final Collection<URI> candidates = new LinkedList<URI>();
        final SrvUtil srv = new SrvUtilImpl();
        
        final Collection<InetSocketAddress> addresses;
        try
            {
            addresses = srv.getAddresses("_sip._tcp2.littleshoot.org");
            }
        catch (final IOException e)
            {
            m_log.error("Could not locate addresses", e);
            return Collections.emptyList();
            }

        // addresses.add(new InetSocketAddress("75.101.156.158", 5061));
        for (final InetSocketAddress isa : addresses)
            {
            final InetAddress address = isa.getAddress();
            
            // The URI we are given is the public address (and SIP port) of
            // this host. To convert to the URI of the proxy we are running,
            // we replace the port with the proxy port.
            final String host = address.getHostAddress();
        
            final URI uri = m_uriUtils.getSipUri(host, isa.getPort(), 
                DEFAULT_TRANSPORT);
            candidates.add(uri);
            }
        
        return candidates;
        }
    
    public URI getCandidate()
        {
        final Collection<URI> candidates = getCandidates();
        if (candidates.isEmpty()) return null;
        return candidates.iterator().next();
        }

    }
