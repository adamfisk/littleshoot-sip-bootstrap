package org.lastbamboo.common.sip.bootstrap;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.http.client.HttpClientGetRequester;
import org.lastbamboo.common.sip.stack.util.UriUtils;
import org.lastbamboo.common.util.CandidateProvider;

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
        "http://www.lastbamboo.org/lastbamboo-server-site/api/sipServer";

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

    /**
     * {@inheritDoc}
     */
    public Collection<URI> getCandidates()
        {
        final URI uri = getCandidate();
        if (uri == null)
            {
            return Collections.emptySet();
            }
        
        final Collection<URI> sipServerUris = new LinkedList<URI>();
        sipServerUris.add(uri);
        return sipServerUris;
        }

    public URI getCandidate()
        {
        final InetSocketAddress delegateAddress = getSocketAddress();
        if (delegateAddress == null)
            {
            LOG.error("No addresses!!");
            return null;
            }
        
        final InetAddress address = delegateAddress.getAddress();
        
        // The URI we are given is the public address (and SIP port) of
        // this host. To convert to the URI of the proxy we are running,
        // we replace the port with the proxy port.
        final String host = address.getHostAddress();
    
        final URI uri = m_uriUtils.getSipUri(host, delegateAddress.getPort(), 
            DEFAULT_TRANSPORT);
        return uri;
        }
        
    private static InetSocketAddress getSocketAddress()
        {
        final HttpClientGetRequester requester = 
            new HttpClientGetRequester();
        final String data;
        try
            {
            data = requester.request(API_URL);
            }
        catch (final IOException e)
            {
            LOG.error("Could not access SIP server data");
            return null;
            }
        if (StringUtils.isBlank(data) || !data.contains(":"))
            {
            LOG.error("Bad data from server: " + data);
            return null;
            }
        final String host = StringUtils.substringBefore(data, ":");
        final String portString = StringUtils.substringAfter(data, ":");
        if (!NumberUtils.isNumber(portString))
            {
            LOG.error("Bad port: "+portString);
            return null;
            }
        return new InetSocketAddress(host, Integer.parseInt(portString));
        }
    }
