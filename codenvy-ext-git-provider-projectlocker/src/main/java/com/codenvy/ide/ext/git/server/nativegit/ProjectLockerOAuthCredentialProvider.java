/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.git.server.nativegit;

import com.codenvy.api.auth.shared.dto.OAuthToken;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.shared.GitUser;
import com.codenvy.security.oauth.OAuthAuthenticationException;
import com.codenvy.security.oauth.ProjectLockerOAuthAuthenticator;
import com.codenvy.security.oauth.shared.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Credentials provider for ProjectLocker
 *
 * @author Max Shaposhnik
 */
@Singleton
public class ProjectLockerOAuthCredentialProvider implements CredentialsProvider {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectLockerOAuthCredentialProvider.class);

    private static String OAUTH_PROVIDER_NAME = "projectlocker";
    public final Pattern PROJECTLOCKER_2_URL_PATTERN;

    private final ProjectLockerOAuthAuthenticator authAuthenticator;

    @Inject
    public ProjectLockerOAuthCredentialProvider(ProjectLockerOAuthAuthenticator authAuthenticator,
                                                @Named("oauth.projectlocker.git.pattern") String gitPattern) {
        this.authAuthenticator = authAuthenticator;
        this.PROJECTLOCKER_2_URL_PATTERN = Pattern.compile(gitPattern);
    }

    @Override
    public UserCredential getUserCredential() throws GitException {
        try {
            OAuthToken token = authAuthenticator.getToken(EnvironmentContext.getCurrent().getUser().getId());
            if (token != null) {
                return new UserCredential(token.getToken(), token.getToken(), OAUTH_PROVIDER_NAME);
            }
        } catch (IOException e) {
            LOG.warn(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public GitUser getUser() throws GitException {
        try {
            OAuthToken token = authAuthenticator.getToken(EnvironmentContext.getCurrent().getUser().getId());
            if (token != null) {
                User user = authAuthenticator.getUser(token);
                if (user != null) {
                    return DtoFactory.getInstance().createDto(GitUser.class)
                                     .withEmail(user.getEmail())
                                     .withName(user.getName());
                }

            }
        } catch (IOException | OAuthAuthenticationException e) {
            LOG.warn(e.getLocalizedMessage());
        }
        return null;
    }

    @Override
    public String getId() {
        return OAUTH_PROVIDER_NAME;
    }

    @Override
    public boolean canProvideCredentials(String url) {
        return PROJECTLOCKER_2_URL_PATTERN.matcher(url).matches();
    }
}
