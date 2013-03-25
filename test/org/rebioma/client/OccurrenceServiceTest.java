/*
 * Copyright 2008 University of California at Berkeley
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
 
package org.rebioma.client;

import org.rebioma.client.services.OccurrenceService;
import org.rebioma.client.services.OccurrenceServiceAsync;
import org.rebioma.client.services.OccurrenceService.OccurrenceServiceException;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

*//**
 * Unit testing of {@link OccurrenceService} and related classes.
 *//*
public class OccurrenceServiceTest extends GWTTestCase {

  *//**
   * Must refer to a valid module that sources this class.
   *//*
  @Override
  public String getModuleName() {
    return "org.rebioma.Portal";
  }

  *//**
   * Tests caching and the {@link DataSwitch} fetch method.
   *//*
  public void testDataSwitch() {
    double start = System.currentTimeMillis();
    run(getTestRunnable());
    System.out.println(System.currentTimeMillis() - start + " ms");
    start = System.currentTimeMillis();
    run(getTestRunnable());
    System.out.println(System.currentTimeMillis() - start + " ms");
  }

  *//**
   * Tests the Proxy fetch method.
   *//*
  public void testProxFetch() {
    run(new Runnable() {
      public void run() {
        final OccurrenceQuery query = new OccurrenceQuery(-1, -1);
        query.addBaseFilter("Genus like Myo").addBaseFilter("country = madagascar");

        String sid = "0CD8540443168E5EDC9E996235391ACF";
        OccurrenceService.Proxy.get().fetch(sid, query,
                new AsyncCallback<OccurrenceQuery>() {
                  public void onFailure(Throwable caught) {
                    try {
                      throw caught;
                    } catch (OccurrenceServiceException e) {
                      e.printStackTrace();
                      fail(e.toString());
                    } catch (Throwable e) {
                      fail(e.toString());
                    }
                  }

                  public void onSuccess(OccurrenceQuery result) {
                    System.out.println("foo");
                    assertNotNull(result);
                    System.out.println(result.getCount());
                    for (Occurrence occurrence : result.getResults()) {
                      System.out.println(occurrence);
                    }
                    finishTest();
                  }
                });
      }
    });
  }

  *//**
   * Tests the {@link OccurrenceService.Proxy} constructor.
   *//*
  public void testProxyConstructors() {
    OccurrenceServiceAsync service = OccurrenceService.Proxy.get();
    assertNotNull(service);
  }

  private Runnable getTestRunnable() {
    return new Runnable() {
      public void run() {
        DataSwitch ds = DataSwitch.get();
        final OccurrenceQuery query = new OccurrenceQuery(-1, -1);
        query.addBaseFilter("Genus like Myo").addBaseFilter("country = madagascar");
        String sid = "0CD8540443168E5EDC9E996235391ACF";
        ds.fetch(sid, query, new AsyncCallback<OccurrenceQuery>() {
          public void onFailure(Throwable caught) {
            try {
              throw caught;
            } catch (OccurrenceServiceException e) {
              e.printStackTrace();
              fail(e.toString());
            } catch (Throwable e) {
              fail(e.toString());
            }
          }

          public void onSuccess(OccurrenceQuery result) {
            System.out.println("foo");
            assertNotNull(result);
            System.out.println(result.getCount());
            for (Occurrence occurrence : result.getResults()) {
              System.out.println(occurrence);
            }
            finishTest();
          }
        });
      }
    };
  }

  *//**
   * Helper method that runs a {@link Runnable} task in a {@link Timer} delayed
   * sufficiently for an asynchronous test.
   * 
   * @param task the task to run
   *//*
  private void run(final Runnable task) {
    Timer timer = new Timer() {
      @Override
      public void run() {
        task.run();
      }
    };
    delayTestFinish(500000);
    timer.schedule(100);
  }
}
*/