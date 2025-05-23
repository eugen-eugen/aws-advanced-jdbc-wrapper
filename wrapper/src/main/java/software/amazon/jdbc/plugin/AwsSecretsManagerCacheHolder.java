/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package software.amazon.jdbc.plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import software.amazon.jdbc.plugin.AwsSecretsManagerConnectionPlugin.Secret;
import software.amazon.jdbc.util.Pair;

/**
 * The main plugin code AwsSecretsManagerConnectionPlugin depends on AWS SDK.
 * In order to avoid unnecessary dependencies, the plugin cache has been extracted into this
 * AwsSecretsManagerCacheHolder class. This cache holder class doesn't depend on AWS SDK and
 * can be safely cleared if needed.
 */
public class AwsSecretsManagerCacheHolder {
  static final Map<Pair<String /* secretId */, String /* region */>, Secret> secretsCache
      = new ConcurrentHashMap<>();

  public static void clearCache() {
    secretsCache.clear();
  }
}
