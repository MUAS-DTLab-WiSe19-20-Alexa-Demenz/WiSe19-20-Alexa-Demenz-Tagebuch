/*
     Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

     Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file
     except in compliance with the License. A copy of the License is located at

         http://aws.amazon.com/apache2.0/

     or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
     the specific language governing permissions and limitations under the License.
*/

package meintagebuch.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.SessionEndedRequest;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.requestType;

public class SessionEndedRequestHandler implements RequestHandler {

  /**
   * returns true if class can handle the HandlerInput -> else: false.
   *
   * @param input is the voice input as HandlerInput
   * @return bool value: true if input matches SessionEndedRequest
   */
  @Override
  public boolean canHandle(final HandlerInput input) {
    return input.matches(requestType(SessionEndedRequest.class));
  }

  /**
   * Closes the skill. If necessary, a cleanup logic must be added later.
   *
   * @param input voice input as HandlerInput
   * @return alexa's standard "Session Ended" response.
   */
  @Override
  public Optional<Response> handle(final HandlerInput input) {
    // any cleanup logic goes here
    return input.getResponseBuilder().withShouldEndSession(true).build();
  }
}
