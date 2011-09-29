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

package fr.keemto.core.fetcher;

import fr.keemto.core.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public class DefaultFetcherLocator implements FetcherLocator {

    private static final Logger log = LoggerFactory.getLogger(DefaultFetcherLocator.class);

    private final List<Fetcher> fetchers;

    public DefaultFetcherLocator(List<Fetcher> fetchers) {
        this.fetchers = fetchers;
        handleEmptyFetchersList();
    }

    private void handleEmptyFetchersList() {
        if (fetchers != null && fetchers.isEmpty()) {
            log.warn("Fetchers List is empty. This resolver will not be able to resolver fetchers.");
        }
    }

    @Override
    public List<Fetcher> getFetchersFor(User user) {
        List<Fetcher> userFetchers = new ArrayList<Fetcher>();
        for (Fetcher fetcher : fetchers) {
            if (fetcher.canFetch(user)) {
                userFetchers.add(fetcher);
            } else {
                log.debug("Fetcher " + fetcher.getProviderId()
                        + " is rejected because it cannot fetch events for user: " + user + ".");
            }
        }
        return userFetchers;
    }
}
