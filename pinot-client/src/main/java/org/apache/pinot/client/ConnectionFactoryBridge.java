/*
 *  Copyright 2023 The original authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.pinot.client;

import java.util.Properties;

public class ConnectionFactoryBridge {

  /**
   * @param properties
   * @param controllerUrl url host:port of the controller
   * @param transport pinot transport
   * @return A connection that connects to brokers as per the given controller
   */
  public static Connection fromController(Properties properties, String controllerUrl, PinotClientTransport transport) {
    try {
      return new Connection(
              properties,
              new ControllerBasedBrokerSelector(properties, controllerUrl),
              transport
      );
    } catch (Exception e) {
      throw new PinotClientException(e);
    }
  }

}
