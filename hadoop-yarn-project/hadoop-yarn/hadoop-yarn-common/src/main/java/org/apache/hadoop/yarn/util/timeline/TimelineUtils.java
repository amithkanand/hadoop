/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.yarn.util.timeline;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.classification.InterfaceAudience.Public;
import org.apache.hadoop.classification.InterfaceStability.Evolving;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.webapp.YarnJacksonJaxbJsonProvider;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * The helper class for the timeline module.
 * 
 */
@Public
@Evolving
public class TimelineUtils {

  public static final String FLOW_NAME_TAG_PREFIX = "TIMELINE_FLOW_NAME_TAG";
  public static final String FLOW_VERSION_TAG_PREFIX = "TIMELINE_FLOW_VERSION_TAG";
  public static final String FLOW_RUN_ID_TAG_PREFIX = "TIMELINE_FLOW_RUN_ID_TAG";

  private static ObjectMapper mapper;

  static {
    mapper = new ObjectMapper();
    YarnJacksonJaxbJsonProvider.configObjectMapper(mapper);
  }

  /**
   * Serialize a POJO object into a JSON string not in a pretty format
   * 
   * @param o
   *          an object to serialize
   * @return a JSON string
   * @throws IOException
   * @throws JsonMappingException
   * @throws JsonGenerationException
   */
  public static String dumpTimelineRecordtoJSON(Object o)
      throws JsonGenerationException, JsonMappingException, IOException {
    return dumpTimelineRecordtoJSON(o, false);
  }

  /**
   * Serialize a POJO object into a JSON string
   * 
   * @param o
   *          an object to serialize
   * @param pretty
   *          whether in a pretty format or not
   * @return a JSON string
   * @throws IOException
   * @throws JsonMappingException
   * @throws JsonGenerationException
   */
  public static String dumpTimelineRecordtoJSON(Object o, boolean pretty)
      throws JsonGenerationException, JsonMappingException, IOException {
    if (pretty) {
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    } else {
      return mapper.writeValueAsString(o);
    }
  }

  public static InetSocketAddress getTimelineTokenServiceAddress(
      Configuration conf) {
    InetSocketAddress timelineServiceAddr = null;
    if (YarnConfiguration.useHttps(conf)) {
      timelineServiceAddr = conf.getSocketAddr(
          YarnConfiguration.TIMELINE_SERVICE_WEBAPP_HTTPS_ADDRESS,
          YarnConfiguration.DEFAULT_TIMELINE_SERVICE_WEBAPP_HTTPS_ADDRESS,
          YarnConfiguration.DEFAULT_TIMELINE_SERVICE_WEBAPP_HTTPS_PORT);
    } else {
      timelineServiceAddr = conf.getSocketAddr(
          YarnConfiguration.TIMELINE_SERVICE_WEBAPP_ADDRESS,
          YarnConfiguration.DEFAULT_TIMELINE_SERVICE_WEBAPP_ADDRESS,
          YarnConfiguration.DEFAULT_TIMELINE_SERVICE_WEBAPP_PORT);
    }
    return timelineServiceAddr;
  }

  public static Text buildTimelineTokenService(Configuration conf) {
    InetSocketAddress timelineServiceAddr =
        getTimelineTokenServiceAddress(conf);
    return SecurityUtil.buildTokenService(timelineServiceAddr);
  }

  public static String generateDefaultFlowIdBasedOnAppId(ApplicationId appId) {
    return "flow_" + appId.getClusterTimestamp() + "_" + appId.getId();
  }

  /**
   * Generate flow name tag
   *
   * @param flowName flow name that identifies a distinct flow application which
   *                 can be run repeatedly over time
   * @return
   */
  public static String generateFlowNameTag(String flowName) {
    return FLOW_NAME_TAG_PREFIX + ":" + flowName;
  }

  /**
   * Generate flow version tag
   *
   * @param flowVersion flow version that keeps track of the changes made to the
   *                    flow
   * @return
   */
  public static String generateFlowVersionTag(String flowVersion) {
    return FLOW_VERSION_TAG_PREFIX + ":" + flowVersion;
  }

  /**
   * Generate flow run ID tag
   *
   * @param flowRunId flow run ID that identifies one instance (or specific
   *                  execution) of that flow
   * @return
   */
  public static String generateFlowRunIdTag(long flowRunId) {
    return FLOW_RUN_ID_TAG_PREFIX + ":" + flowRunId;
  }
}
