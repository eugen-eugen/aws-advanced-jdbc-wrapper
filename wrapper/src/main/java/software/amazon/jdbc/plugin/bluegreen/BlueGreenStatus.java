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

package software.amazon.jdbc.plugin.bluegreen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import software.amazon.jdbc.HostSpec;
import software.amazon.jdbc.plugin.bluegreen.routing.ConnectRouting;
import software.amazon.jdbc.plugin.bluegreen.routing.ExecuteRouting;
import software.amazon.jdbc.util.Pair;
import software.amazon.jdbc.util.StringUtils;

// It should be immutable
public class BlueGreenStatus {

  private static final Logger LOGGER = Logger.getLogger(BlueGreenStatus.class.getName());

  private final String bgdId;
  private final BlueGreenPhase currentPhase;
  private final List<ConnectRouting> unmodifiableConnectRouting;
  private final List<ExecuteRouting> unmodifiableExecuteRouting;

  // all known host names; host with no port
  private final Map<String, BlueGreenRole> roleByHost = new ConcurrentHashMap<>();

  private final Map<String, Pair<HostSpec, HostSpec>> correspondingNodes = new ConcurrentHashMap<>();


  public BlueGreenStatus(final String bgdId, final BlueGreenPhase phase) {
    this(bgdId, phase, new ArrayList<>(), new ArrayList<>(), new HashMap<>(), new HashMap<>());
  }

  public BlueGreenStatus(
      final String bgdId,
      final BlueGreenPhase phase,
      final List<ConnectRouting> connectRouting,
      final List<ExecuteRouting> executeRouting,
      final Map<String, BlueGreenRole> roleByHost,
      final Map<String, Pair<HostSpec, HostSpec>> correspondingNodes) {

    this.bgdId = bgdId;
    this.currentPhase = phase;
    this.unmodifiableConnectRouting = Collections.unmodifiableList(new ArrayList<>(connectRouting));
    this.unmodifiableExecuteRouting = Collections.unmodifiableList(new ArrayList<>(executeRouting));
    this.roleByHost.putAll(roleByHost);
    this.correspondingNodes.putAll(correspondingNodes);
  }

  public @NonNull BlueGreenPhase getCurrentPhase() {
    return this.currentPhase;
  }

  public List<ConnectRouting> getConnectRouting() {
    return this.unmodifiableConnectRouting;
  }

  public List<ExecuteRouting> getExecuteRouting() {
    return this.unmodifiableExecuteRouting;
  }

  public Map<String, BlueGreenRole> getRoleByHost() {
    return this.roleByHost;
  }

  public Map<String, Pair<HostSpec, HostSpec>> getCorrespondingNodes() {
    return this.correspondingNodes;
  }

  public BlueGreenRole getRole(HostSpec hostSpec) {
    return this.roleByHost.get(hostSpec.getHost().toLowerCase());
  }

  @Override
  public String toString() {
    String roleByHostMap = this.roleByHost.entrySet().stream()
        .map(x -> String.format("%s -> %s", x.getKey(), x.getValue()))
        .collect(Collectors.joining("\n   "));
    String connectRoutingStr = this.unmodifiableConnectRouting.stream().map(Object::toString)
        .collect(Collectors.joining("\n   "));
    String executeRoutingStr = this.unmodifiableExecuteRouting.stream().map(Object::toString)
        .collect(Collectors.joining("\n   "));

    return String.format("%s [\n"
            + " bgdId: '%s', \n"
            + " phase: %s, \n"
            + " Connect routing: \n"
            + "   %s \n"
            + " Execute routing: \n"
            + "   %s \n"
            + " roleByHost: \n"
            + "   %s \n"
            + "]",
        super.toString(),
        this.bgdId,
        this.currentPhase,
        StringUtils.isNullOrEmpty(connectRoutingStr) ? "-" : connectRoutingStr,
        StringUtils.isNullOrEmpty(executeRoutingStr) ? "-" : executeRoutingStr,
        StringUtils.isNullOrEmpty(roleByHostMap) ? "-" : roleByHostMap);
  }
}
