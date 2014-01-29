/**
 * 
 */
package org.rebioma.client;

import java.util.Set;

/**
 * @author Consultant
 *
 */
public interface ReviewHandler {
	
	void reviewAllRecords(final Boolean reviewed, String comment, boolean notified);
	
	void reviewRecords(Set<Integer> occurrenceIds,
		      final Boolean reviewed, String comment, boolean notified);

	void commentRecords(Set<Integer> occurrenceIds, String comment,
			boolean value);
}
