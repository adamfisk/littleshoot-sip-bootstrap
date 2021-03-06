package org.lastbamboo.common.sip.bootstrap;

import java.net.InetSocketAddress;
import java.net.URI;

import org.lastbamboo.common.offer.answer.OfferAnswerFactory;
import org.lastbamboo.common.sip.client.SipClientTracker;
import org.lastbamboo.common.sip.client.util.ProxyRegistrationListener;
import org.lastbamboo.common.sip.stack.IdleSipSessionListener;
import org.lastbamboo.common.sip.stack.message.SipMessageFactory;
import org.lastbamboo.common.sip.stack.transaction.client.SipTransactionTracker;
import org.lastbamboo.common.sip.stack.transport.SipTcpTransportLayer;
import org.lastbamboo.common.sip.stack.util.UriUtils;
import org.littleshoot.util.SessionSocketListener;
import org.littleshoot.util.SocketListener;

/**
 * An implementation of the proxy registrar factory interface.
 */
public final class ProxyRegistrarFactoryImpl implements ProxyRegistrarFactory {

    private final SipMessageFactory m_messageFactory;

    private final SipClientTracker m_sipClientTracker;

    private final UriUtils m_uriUtils;

    private final SipTransactionTracker m_transactionTracker;

    private final SipTcpTransportLayer m_transportLayer;

    private final OfferAnswerFactory m_offerAnswerFactory;

    private final IdleSipSessionListener m_idleSipSessionListener;

    private final SessionSocketListener callSocketListener;

    private final InetSocketAddress serverAddress;
    
    /**
     * Creates a new factory for creating classes for registering with 
     * registrars.
     * 
     * @param messageFactory The factory for creating SIP messages.
     * @param transportLayer The transport layer for actually writing messages.
     * @param transactionTracker The class for keeping track of SIP 
     * transactions.
     * @param clientTracker The class for keeping track of SIP clients.
     * @param uriUtils The class for manipulating SIP URIs.
     * @param offerAnswerFactory The class for creating classes capable of
     * processing offers and answers for an offer/answer protocol.
     * @param serverAddress Listener for incoming sockets.
     * @param idleSipSessionListener Listener for idle SIP sessions.
     */
    public ProxyRegistrarFactoryImpl(final SipMessageFactory messageFactory,
            final SipTcpTransportLayer transportLayer,
            final SipTransactionTracker transactionTracker,
            final SipClientTracker clientTracker, final UriUtils uriUtils,
            final OfferAnswerFactory offerAnswerFactory,
            final InetSocketAddress serverAddress,
            final SessionSocketListener callSocketListener,
            final IdleSipSessionListener idleSipSessionListener) {
        this.m_messageFactory = messageFactory;
        this.m_transportLayer = transportLayer;
        this.m_transactionTracker = transactionTracker;
        this.m_sipClientTracker = clientTracker;
        this.m_uriUtils = uriUtils;
        this.m_offerAnswerFactory = offerAnswerFactory;
        this.serverAddress = serverAddress;
        this.callSocketListener = callSocketListener;
        this.m_idleSipSessionListener = idleSipSessionListener;
    }

    /**
     * {@inheritDoc}
     */
    public ProxyRegistrar getRegistrar(final URI client, final URI proxy,
            final ProxyRegistrationListener listener) {
        return (new ProxyRegistrarImpl(this.m_uriUtils, client, proxy,
                listener, this.m_messageFactory, this.m_transportLayer,
                this.m_transactionTracker, this.m_offerAnswerFactory,
                this.serverAddress, this.callSocketListener, 
                this.m_sipClientTracker, this.m_idleSipSessionListener));
    }
}
