/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.rosvoxcontinuos;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

/**
 * A simple {@link Publisher} {@link NodeMain}.
 * 
 * @author damonkohler@google.com (Damon Kohler)
 */
public class Talker extends AbstractNodeMain {
    private String topic_name;
    private Publisher<std_msgs.String> publisher;

    public Talker() {
        topic_name = "chatter";
    }

    public Talker(String topic) {
        topic_name = topic;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava_tutorial_pubsub/talker");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        publisher =
                connectedNode.newPublisher("chatter", std_msgs.String._TYPE);
        // This CancellableLoop will be canceled automatically when the node shuts
        // down.
    connectedNode.executeCancellableLoop(new CancellableLoop() {
      private int i;
      String speak;

      @Override
      protected void setup() {

        speak = null;
      }

      @Override
      protected void loop() throws InterruptedException {
        std_msgs.String str = publisher.newMessage();
          speak = MainActivity.getSaidaVoz();
          i = 0;
          if(speak!=null){

                  str.setData(speak);
                  publisher.publish(str);
                  Thread.sleep(1000);

              MainActivity.setSaidaVoz(null);
              speak=null;

       }

      }

    });
    }
}

