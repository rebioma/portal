/**
 * 
 */
package org.rebioma.server.services;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.client.OrderKey;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.User;
import org.rebioma.server.services.OccurrenceDbImpl.OccurrenceFilter;
import org.rebioma.server.services.QueryFilter.Operator;
import org.rebioma.server.util.ManagedSession;

/**
 * @author Mikajy
 *
 */
public class OccurrenceSearchDbPg implements IOccurrenceSearchDb, IDbServerStatus{
	
	private static final Logger log = Logger.getLogger(OccurrenceSearchDbPg.class);
	
	private RecordReviewDb recordReviewDb = DBFactory.getRecordReviewDb();
	
	@Override
	public List<Occurrence> find(OccurrenceQuery query,
			Set<OccurrenceFilter> filters, User user, int tryCount)
			throws Exception {
		log.debug("finding Occurrence instances by query.");
	    try {
	      Session session = ManagedSession.createNewSessionAndTransaction();
	      List<Occurrence> results = null;
	      Criteria criteria = session.createCriteria(Occurrence.class);
	      OccurrenceFilter userReviewFilter = null;
	      OccurrenceFilter myreviewPublicFilter = null;
	      ResultFilter resultFilter = query.getResultFilter();
	      for (OccurrenceFilter filter : filters) {
	        if (filter.column.equals(filter.getPropertyName("userReviewed"))) {
	          userReviewFilter = filter;
	          if (resultFilter != null) {
	            if (resultFilter == ResultFilter.PUBLIC) {
	              myreviewPublicFilter = new OccurrenceFilter("public = true");
	            } else if (resultFilter == ResultFilter.PRIVATE) {
	              myreviewPublicFilter = new OccurrenceFilter("public = false");
	            }
	            resultFilter = null;
	          }
	        }
	      }
	      if (myreviewPublicFilter != null) {
	        filters.add(myreviewPublicFilter);
	      }
	      filters.remove(userReviewFilter);
	      OccurrenceFilter idsFilter = null;
	      if (userReviewFilter != null) {
	        Boolean reviewed = null;
	        if (userReviewFilter.operator == Operator.EQUAL) {
	          reviewed = (Boolean) userReviewFilter.getValue();
	        }
	        List<Integer> occIds = recordReviewDb.getRecordReviewOccIds(user.getId(), reviewed);
	        System.out.println(occIds.size());
	        if (occIds.isEmpty()) {
	          occIds.add(0);
	        }
	        idsFilter = new OccurrenceFilter("id", Operator.IN, occIds);
	        filters.add(idsFilter);
	      }
	      //filtre sur les identifiants d'occurrence 
	      if(query.getOccurrenceIdsFilter() != null && !query.getOccurrenceIdsFilter().isEmpty()){
	    	  OccurrenceFilter occIdsFilter = new OccurrenceFilter("id", Operator.IN, query.getOccurrenceIdsFilter());
	    	  filters.add(occIdsFilter);
	      }
	      log.info("find filters: "
	          + OccurrenceDbImpl.addCreterionByFilters(criteria, user, filters, resultFilter, tryCount));
	      if (userReviewFilter != null) {
	        filters.remove(idsFilter);
	        filters.add(userReviewFilter);
	      }
	      if (myreviewPublicFilter != null) {
	        filters.remove(myreviewPublicFilter);
	      }
	      List<OrderKey> orderingMap = query.getOrderingMap();
	      log.info("order map = " + orderingMap);
	      if (query.isCountTotalResults()) {
	          criteria.setFirstResult(0);
	          criteria.setProjection(Projections.count("id"));
	          Integer count = (Integer) criteria.uniqueResult();
	          if (count != null) {
	            query.setCount(count);
	          }
	      } else {
	          query.setCount(-1);
	      }
	      // Sets the start, limit, and order by accepted species:
	      criteria.setFirstResult(query.getStart());
	      if (query.getLimit() != OccurrenceQuery.UNLIMITED) {
	        criteria.setMaxResults(query.getLimit());
	      }
	      criteria.setProjection(null);
	      /*for (OrderKey orderKey : orderingMap) {
	        String property = orderKey.getAttributeName();
	        String occAttribute = getOccurrencePropertyName(property);
	        if (orderKey.isAsc()) {
	          log.info("order by property " + occAttribute + " in ascending order");
	          criteria.addOrder(Order.asc(occAttribute));
	        } else {
	          log.info("order by property " + occAttribute + " in descending order");
	          criteria.addOrder(Order.desc(occAttribute));
	        }
	      }*/
	      criteria.addOrder(Order.asc("id"));
	      results = criteria.list();
	      
	      // filters.addAll(removedFilters);
	      log.debug("find by example successful, result size: " + results.size());
	      ManagedSession.commitTransaction(session);
	      return results;
	    } catch (RuntimeException re) {
	      log.error("find by example failed", re);
	      re.printStackTrace();
	      throw re;
	    } catch (Exception e) {
	      log.error("unexpected error: ", e);
	      e.printStackTrace();
	      throw e;
	    }
	}

	@Override
	public ServerStatus getDbServerStatus() {
		// La base de données Postgresql est toujours présente
		return ServerStatus.OK;
	}

}
