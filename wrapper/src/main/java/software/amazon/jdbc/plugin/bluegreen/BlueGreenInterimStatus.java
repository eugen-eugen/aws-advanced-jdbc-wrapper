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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.jdbc.HostSpec;
import software.amazon.jdbc.util.StringUtils;
import software.amazon.jdbc.util.Utils;

public class BlueGreenInterimStatus {
  public BlueGreenPhase blueGreenPhase;
  public String version;
  public int port;
  public List<HostSpec> startTopology;
  public List<HostSpec> currentTopology;
  public Map<String, Optional<String>> startIpAddressesByHostMap;
  public Map<String, Optional<String>> currentIpAddressesByHostMap;
  public Set<String> hostNames; // all known host names; just host, no port
  public boolean allStartTopologyIpChanged;
  public boolean allStartTopologyEndpointsRemoved;
  public boolean allTopologyChanged;

  public BlueGreenInterimStatus(
      final BlueGreenPhase blueGreenPhase,
      final String version,
      final int port,
      final List<HostSpec> startTopology,
      final List<HostSpec> currentTopology,
      final Map<String, Optional<String>> startIpAddressesByHostMap,
      final Map<String, Optional<String>> currentIpAddressesByHostMap,
      final Set<String> hostNames,
      boolean allStartTopologyIpChanged,
      boolean allStartTopologyEndpointsRemoved,
      boolean allTopologyChanged) {

    this.blueGreenPhase = blueGreenPhase;
    this.version = version;
    this.port = port;
    this.startTopology = startTopology;
    this.currentTopology = currentTopology;
    this.startIpAddressesByHostMap = startIpAddressesByHostMap;
    this.currentIpAddressesByHostMap = currentIpAddressesByHostMap;
    this.hostNames = hostNames;
    this.allStartTopologyIpChanged = allStartTopologyIpChanged;
    this.allStartTopologyEndpointsRemoved = allStartTopologyEndpointsRemoved;
    this.allTopologyChanged = allTopologyChanged;
  }

  @Override
  public String toString() {
    String currentIpMap = this.currentIpAddressesByHostMap.entrySet().stream()
        .map(x -> String.format("%s -> %s", x.getKey(), x.getValue()))
        .collect(Collectors.joining("\n   "));
    String startIpMap = this.startIpAddressesByHostMap.entrySet().stream()
        .map(x -> String.format("%s -> %s", x.getKey(), x.getValue()))
        .collect(Collectors.joining("\n   "));
    String allHostNamesStr = String.join("\n   ", this.hostNames);
    String startTopologyStr = Utils.logTopology(this.startTopology);
    String currentTopologyStr = Utils.logTopology(this.currentTopology);
    return String.format("%s [\n"
            + " phase %s, \n"
            + " version '%s', \n"
            + " port %d, \n"
            + " hostNames:\n"
            + "   %s \n"
            + " Start %s \n"
            + " start IP map:\n"
            + "   %s \n"
            + " Current %s \n"
            + " current IP map:\n"
            + "   %s \n"
            + " allStartTopologyIpChanged: %s \n"
            + " allStartTopologyEndpointsRemoved: %s \n"
            + " allTopologyChanged: %s \n"
            + "]",
        super.toString(),
        this.blueGreenPhase == null ? "<null>" : this.blueGreenPhase,
        this.version,
        this.port,
        StringUtils.isNullOrEmpty(allHostNamesStr) ? "-" : allHostNamesStr,
        StringUtils.isNullOrEmpty(startTopologyStr) ? "-" : startTopologyStr,
        StringUtils.isNullOrEmpty(startIpMap) ? "-" : startIpMap,
        StringUtils.isNullOrEmpty(currentTopologyStr) ? "-" : currentTopologyStr,
        StringUtils.isNullOrEmpty(currentIpMap) ? "-" : currentIpMap,
        this.allStartTopologyIpChanged,
        this.allStartTopologyEndpointsRemoved,
        this.allTopologyChanged);
  }

  public int getCustomHashCode() {

    int result = this.getValueHash(1,
        this.blueGreenPhase == null ? "" : this.blueGreenPhase.toString());
    result = this.getValueHash(result,
        this.version == null ? "" : this.version);
    result = this.getValueHash(result, String.valueOf(this.port));
    result = this.getValueHash(result, String.valueOf(this.allStartTopologyIpChanged));
    result = this.getValueHash(result, String.valueOf(this.allStartTopologyEndpointsRemoved));
    result = this.getValueHash(result, String.valueOf(this.allTopologyChanged));

    result = this.getValueHash(result,
        this.hostNames == null
            ? ""
            : this.hostNames.stream()
                .sorted()
                .collect(Collectors.joining(",")));
    result = this.getValueHash(result,
        this.startTopology == null
            ? ""
            : this.startTopology.stream()
                .map(x -> x.getHostAndPort() + x.getRole())
                .sorted(Comparator.comparing(x -> x))
                .collect(Collectors.joining(",")));
    result = this.getValueHash(result,
        this.currentTopology == null
            ? ""
            : this.currentTopology.stream()
                .map(x -> x.getHostAndPort() + x.getRole())
                .sorted(Comparator.comparing(x -> x))
                .collect(Collectors.joining(",")));
    result = this.getValueHash(result,
        this.startIpAddressesByHostMap == null
            ? ""
            : this.startIpAddressesByHostMap.entrySet().stream()
                .map(x -> x.getKey() + x.getValue())
                .sorted(Comparator.comparing(x -> x))
                .collect(Collectors.joining(",")));
    result = this.getValueHash(result,
        this.currentIpAddressesByHostMap == null
            ? ""
            : this.currentIpAddressesByHostMap.entrySet().stream()
                .map(x -> x.getKey() + x.getValue())
                .sorted(Comparator.comparing(x -> x))
                .collect(Collectors.joining(",")));
    return result;
  }

  protected int getValueHash(int currentHash, String val) {
    return currentHash * 31 + val.hashCode();
  }

}
