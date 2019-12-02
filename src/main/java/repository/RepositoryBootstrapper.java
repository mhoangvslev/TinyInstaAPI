/*
 * Copyright 2019 minhhoangdang.
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
package repository;

import com.google.appengine.api.utils.SystemProperty;
import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import entity.User;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author minhhoangdang
 */
public class RepositoryBootstrapper implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
            ObjectifyService.init();
        } else {
            ObjectifyService.init(new ObjectifyFactory(
                    DatastoreOptions.newBuilder()
                            .setHost("http://localhost:8484")
                            .setProjectId("tinyinstagram")
                            .build()
                            .getService()
            ));
        }

        // Register all classes
        ObjectifyService.register(User.class);

        // Begin
        ObjectifyService.begin();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Auto-generated
    }

}
