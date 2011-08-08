/*
 * Copyright (C) 2010 Benoit Guerout <bguerout at gmail dot com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.keemto.web;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;

public class ConnectionRepositoryConfigTest {

    private ConnectionRepositoryConfig connectionRepositoryConfig;

    @Mock
    private UsersConnectionRepository usersConnectionRepository;

    @Before
    public void prepare() throws Exception {
        initMocks(this);
        connectionRepositoryConfig = new ConnectionRepositoryConfig();
        connectionRepositoryConfig.usersConnectionRepository = usersConnectionRepository;
    }

    @Test
    public void shouldCreateAConnectionRepositoryWithPrincipal() {

        ConnectionRepository connectionRepository = mock(ConnectionRepository.class);
        when(usersConnectionRepository.createConnectionRepository("user")).thenReturn(connectionRepository);
        TestingAuthenticationToken principal = new TestingAuthenticationToken("user", null);

        ConnectionRepository repo = connectionRepositoryConfig.connectionRepository(principal);

        assertThat(repo, equalTo(connectionRepository));
        verify(usersConnectionRepository).createConnectionRepository("user");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionWhenPrincipalCannotBeRetrieved() {
        connectionRepositoryConfig.connectionRepository(null);
    }

}
