/*
 * Copyright 2014-2016 michael-simons.eu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ac.simons.biking2.bikingpictures;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * @author Michael J. Simons, 2014-02-18
 */
@Component
@ConditionalOnExpression(value = "environment['biking2.dailyfratze-access-token'] != null && !environment['biking2.dailyfratze-access-token'].isEmpty()")
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DailyFratzeProvider {

    private final String accessToken;
    private final String imageUrlFormat;

    @Autowired
    DailyFratzeProvider(@Value("${biking2.dailyfratze-access-token}") final String accessToken) {
        this(accessToken, "https://dailyfratze.de/api/images/%s/%d.jpg");
    }

    public URLConnection getRSSConnection(final String url) {
        URLConnection rv = null;
        try {
            rv = new URL(Optional.ofNullable(url).orElse("https://dailyfratze.de/michael/tags/Theme/Radtour?format=rss&dir=d")).openConnection();
        } catch (IOException ex) {
            log.error("Failed to open URL connection to DailyFratze RSS endpoint", ex);
        }
        return rv;
    }

    public URLConnection getImageConnection(final Integer id) {
        URLConnection rv = null;
        try {
            rv = new URL(String.format(imageUrlFormat, "s", id)).openConnection();
            rv.setRequestProperty("Authorization", String.format("Bearer %s", accessToken));
        } catch (IOException ex) {
            log.error("Failed to open secure connection to DailyFratze image api", ex);
        }
        return rv;
    }
}
