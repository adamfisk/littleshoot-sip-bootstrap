package org.lastbamboo.common.sip.bootstrap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.http.client.HttpClientGetRequester;
import org.lastbamboo.common.http.client.ServiceUnavailableException;
import org.lastbamboo.common.json.JsonUtils;
import org.lastbamboo.common.sip.stack.util.UriUtils;
import org.lastbamboo.common.util.CandidateProvider;
import org.lastbamboo.common.util.ShootConstants;

/**
 * The candidate provider that provides candidate registrars.
 */
public final class RegistrarCandidateProvider implements CandidateProvider<URI>
    {
    
    private static final Log LOG = 
        LogFactory.getLog(RegistrarCandidateProvider.class);
    
    /**
     * The default transport to use to connect to this host.
     */
    private static final String DEFAULT_TRANSPORT = "tcp";

    private static final String API_URL = 
        ShootConstants.SERVER_URL+"/api/sipServer";

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
        LOG.debug("Accessing SIP servers...");
        final String data = getData();
        if (StringUtils.isBlank(data))
            {
            LOG.error("Bad data from server: " + data);
            return Collections.emptySet();
            }
        final Collection<URI> candidates = new LinkedList<URI>();
        final Collection<InetSocketAddress> addresses = 
            JsonUtils.getInetAddresses(data);
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
    
    private String getData()
        {
        final HttpClientGetRequester requester = new HttpClientGetRequester();
        final String data;
        try
            {
            // Note this will automatically decompress the body if necessary.
            data = requester.request(API_URL);
            }
        catch (final IOException e)
            {
            LOG.error("Could not access SIP server data from "+API_URL, e);
            return null;
            }
        catch (final ServiceUnavailableException e)
            {
            LOG.error("Could not access SIP server data from "+API_URL, e);
            return null;
            }
        return data;
        }
    }
