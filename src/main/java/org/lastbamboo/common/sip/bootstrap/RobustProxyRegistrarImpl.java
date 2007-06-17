package org.lastbamboo.common.sip.bootstrap;

import java.net.URI;

import org.lastbamboo.common.sip.client.util.ProxyRegistrationListener;
import org.lastbamboo.common.util.CandidateProvider;
import org.lastbamboo.common.util.ConnectionEstablisher;
import org.lastbamboo.common.util.ConnectionMaintainer;
import org.lastbamboo.common.util.ConnectionMaintainerImpl;
import org.lastbamboo.common.util.Optional;
import org.lastbamboo.common.util.ThreadUtils;

/**
 * A proxy registrar that attempts to be robust by maintaining registrations to
 * multiple SIP proxies.
 */
public class RobustProxyRegistrarImpl implements RobustProxyRegistrar
    {
    /**
     * The log for this class.
     */
    // Currently unused.
//    private static final Log LOG =
//            LogFactory.getLog (RobustProxyRegistrarImpl.class);

    /**
     * The connection maintainer used to maintain connections to multiple
     * registrars.
     */
    private final ConnectionMaintainer<URI> m_connectionMaintainer;

    /**
     * Constructs a new robust proxy registrar.
     *
     * @param threadUtils
     *      Thread utilities.
     * @param client
     *      The client to register.
     * @param candidateProvider
     *      The candidate provider that provides candidate registrars for
     *      registration.
     * @param registrarFactory
     *      The registrar factory that provides registrars for single
     *      registrations.
     * @param listener
     *      The listener to be notified of registration events.
     */
    public RobustProxyRegistrarImpl
            (final ThreadUtils threadUtils,
             final URI client,
             final CandidateProvider<URI> candidateProvider,
             final ProxyRegistrarFactory registrarFactory,
             final ProxyRegistrationListener listener)
        {
        final ConnectionEstablisher<URI,URI> establisher =
                new RegistrarConnectionEstablisher (client, registrarFactory,
                                                    listener);

        this.m_connectionMaintainer =
                new ConnectionMaintainerImpl<URI,URI> (threadUtils, establisher,
                                                       candidateProvider);
        }

    /**
     * {@inheritDoc}
     */
    public void register
            ()
        {
        this.m_connectionMaintainer.start ();
        }

    /**
     * {@inheritDoc}
     */
    public Optional<URI> mostRecentlyActive
            ()
        {
        return this.m_connectionMaintainer.getMostRecentlyActive ();
        }
    }
