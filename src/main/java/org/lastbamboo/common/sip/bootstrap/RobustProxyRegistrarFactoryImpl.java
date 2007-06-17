package org.lastbamboo.common.sip.bootstrap;

import java.net.URI;

import org.lastbamboo.common.sip.client.util.ProxyRegistrationListener;
import org.lastbamboo.common.util.CandidateProvider;
import org.lastbamboo.common.util.ThreadUtils;

/**
 * An implementation of the robust proxy registrar factory implementation.
 */
public final class RobustProxyRegistrarFactoryImpl
        implements RobustProxyRegistrarFactory
    {
    /**
     * The candidate provider that provides candidate registrars for
     * registration.
     */
    private final CandidateProvider<URI> m_candidateProvider;

    /**
     * The registrar factory that provides registrars for single registrations.
     */
    private final ProxyRegistrarFactory m_registrarFactory;

    /**
     * Thread utilities.
     */
    private final ThreadUtils m_threadUtils;

    /**
     * Constructs a new robust proxy registrar factory.
     *
     * @param candidateProvider
     *      The candidate provider that provides candidate registrars for
     *      registration.
     * @param registrarFactory
     *      The registrar factory that provides registrars for single
     *      registrations.
     * @param threadUtils
     *      Thread utilities.
     */
    public RobustProxyRegistrarFactoryImpl
            (final CandidateProvider<URI> candidateProvider,
             final ProxyRegistrarFactory registrarFactory,
             final ThreadUtils threadUtils)
        {
        this.m_candidateProvider = candidateProvider;
        this.m_registrarFactory = registrarFactory;
        this.m_threadUtils = threadUtils;
        }

    /**
     * {@inheritDoc}
     */
    public RobustProxyRegistrar getRegistrar
            (final URI client,
             final ProxyRegistrationListener listener)
        {
        return (new RobustProxyRegistrarImpl (this.m_threadUtils, client, 
            this.m_candidateProvider, this.m_registrarFactory, listener));
        }
    }
