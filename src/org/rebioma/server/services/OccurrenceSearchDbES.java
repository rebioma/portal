/**
 * 
 */
package org.rebioma.server.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.OccurrenceQuery.ResultFilter;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.User;
import org.rebioma.server.elasticsearch.search.OccurrenceMapping;
import org.rebioma.server.elasticsearch.search.OccurrenceSearch;
import org.rebioma.server.services.OccurrenceDbImpl.OccurrenceFilter;
import org.rebioma.server.services.QueryFilter.Operator;
import org.rebioma.server.util.StringUtil;

/**
 * @author Mikajy
 *
 */
public class OccurrenceSearchDbES implements IOccurrenceSearchDb{
	
	private static final Logger log = Logger.getLogger(OccurrenceSearchDbES.class);
	private RecordReviewDb recordReviewDb = DBFactory.getRecordReviewDb();
	

	private SearchResponse _findByOccurrenceFilters(
			Set<OccurrenceFilter> filters, User user,
			ResultFilter resultFilter, int from, int size) throws Exception {
		log.debug("finding Occurrence instances by filters.");
		try {
			// Session session =
			// ManagedSession.createNewSessionAndTransaction();
			List<Occurrence> results = null;
			// Criteria criteria = session.createCriteria(Occurrence.class);
			OccurrenceFilter userReviewFilter = null;
			OccurrenceFilter myreviewPublicFilter = null;
			for (OccurrenceFilter filter : filters) {
				if (filter.column
						.equals(filter.getPropertyName("userReviewed"))) {
					userReviewFilter = filter;
					if (resultFilter != null) {
						if (resultFilter == ResultFilter.PUBLIC) {
							myreviewPublicFilter = new OccurrenceFilter(
									"public = true");
						} else if (resultFilter == ResultFilter.PRIVATE) {
							myreviewPublicFilter = new OccurrenceFilter(
									"public = false");
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
				List<Integer> occIds = recordReviewDb.getRecordReviewOccIds(
						user.getId(), reviewed);
				System.out.println(occIds.size());
				if (occIds.isEmpty()) {
					occIds.add(0);
				}
				idsFilter = new OccurrenceFilter("id", Operator.IN, occIds);
				filters.add(idsFilter);
			}

			// log.info("find filters: "
			// + addCreterionByFilters(criteria, user, filters, resultFilter,
			// tryCount));
			QueryBuilder queryBuilder = getEsQueriesAndFilters(user, filters,
					resultFilter);
			SearchResponse searchResponse = OccurrenceSearch.getInstance()
					.doSearch(queryBuilder, from, size);
			return searchResponse;
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
	
	private QueryBuilder getEsQueriesAndFilters(User user,
			Set<OccurrenceDbImpl.OccurrenceFilter> searchFilters,
			ResultFilter resultFilter) {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolean isMyOccurrence = false;
		String ownerField = OccurrenceDbImpl.getOccurrencePropertyName("ownerEmail");
		List<FilterBuilder> andFilters = new ArrayList<FilterBuilder>();
		List<FilterBuilder> orFilters = new ArrayList<FilterBuilder>();
		FilterBuilder filterBuilder = null;
		for (OccurrenceFilter filter : searchFilters) {
			if ((filter.getOperator() != Operator.IS_EMPTY && filter
					.getOperator() != Operator.IS_NOT_EMPTY)
					&& filter.value instanceof String
					&& ((String) filter.value).equals("")) {
				continue;
			}
			if (filter.column.equalsIgnoreCase(ownerField)) {
				isMyOccurrence = true;
			}
			if (filter.column.equalsIgnoreCase(filter
					.getPropertyName("globalsearchtext"))) {
				String globalSearchText = filter.getValue().toString();
				if (globalSearchText != null
						&& globalSearchText.trim().length() > 0) {
					MultiMatchQueryBuilder query1 = QueryBuilders
							.multiMatchQuery(globalSearchText)
							.field("biologic_identity.ngram", 10)
							.field("biologic_classification.ngram", 5)
							.field("biologic_autre_nom.ngram", 3)
							.field("localisation.ngram").field("owneremail", 5)
							.type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
					MultiMatchQueryBuilder query2 = QueryBuilders
							.multiMatchQuery(globalSearchText)
							.field("biologic_identity.edge_ngram", 10)
							.field("biologic_classification.edge_ngram", 5)
							.field("biologic_autre_nom.edge_ngram", 3)
							.field("localisation.edge_ngram").field("owneremail", 5)
							.type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
					MultiMatchQueryBuilder query3 = QueryBuilders
							.multiMatchQuery(globalSearchText)
							.field("biologic_identity", 10)
							.field("biologic_classification", 5)
							.field("biologic_autre_nom", 3)
							.field("localisation").field("owneremail", 5)
							.type(MultiMatchQueryBuilder.Type.BEST_FIELDS);
					boolQueryBuilder.must(query1).should(
							QueryBuilders.boolQuery().should(query2)
									.should(query3)).minimumShouldMatch("80%");
				}
			} else if (filter.column.equalsIgnoreCase(filter
					.getPropertyName("quickSearch"))) {
				String quickSearchValue = filter.getValue().toString();
				/*
				 * le field identity l'ensemble des fields acceptedspecies,
				 * verbatimspecies, scientificname
				 */
				MultiMatchQueryBuilder query = QueryBuilders
						.multiMatchQuery(quickSearchValue)
						.field("identity", 10).field("identity.edge_ngram", 3)
						.field("identity.ngram")
						.type(MultiMatchQueryBuilder.Type.MOST_FIELDS);
				boolQueryBuilder.must(query);
			} else {
				Operator op = filter.getOperator();
				String field = filter.column.toLowerCase();
				switch (op) {
				case CONTAIN:
				case NOT_CONTAIN:
					String ngramField = field + ".ngram";
					MatchQueryBuilder containquery = QueryBuilders.matchQuery(
							ngramField, (String) filter.getValue());
					if (op.equals(Operator.CONTAIN)) {
						if (filter.isDisjunction()) {
							boolQueryBuilder.should(containquery);
						} else {
							boolQueryBuilder.must(containquery);
						}
					} else if (op.equals(Operator.NOT_CONTAIN)) {
						if (filter.isDisjunction()) {
							// TODO find ES should_not equivalent
							boolQueryBuilder.should(boolQueryBuilder
									.mustNot(containquery));
						} else {
							boolQueryBuilder.mustNot(containquery);
						}
					}
					break;
				case NOT_START_WITH:
				case START_WITH:
					String edgeNgramField = field + ".edge_ngram";
					MatchQueryBuilder startWithquery = QueryBuilders
							.matchQuery(edgeNgramField,
									(String) filter.getValue());
					if (op.equals(Operator.START_WITH)) {
						if (filter.isDisjunction()) {
							boolQueryBuilder.should(startWithquery);
						} else {
							boolQueryBuilder.must(startWithquery);
						}
					} else if (op.equals(Operator.NOT_START_WITH)) {
						if (filter.isDisjunction()) {
							boolQueryBuilder.should(QueryBuilders.boolQuery()
									.mustNot(startWithquery));
						} else {
							boolQueryBuilder.mustNot(startWithquery);
						}
					}
					break;
				case EQUAL:
				case NOT_EQUAL:
					FilterBuilder eqFilter = null;
					if (filter.column.equals(filter.getPropertyName("sex"))) {
						// TODO - Mikajy
					} else if (filter.column.equals(filter
							.getPropertyName("BasisOfRecord"))) {
						// TODO - Mikajy
					} else {
						eqFilter = FilterBuilders.termFilter(
								filter.column.toLowerCase(), filter.getValue());
					}
					filterBuilder = op.equals(Operator.EQUAL) ? eqFilter
							: FilterBuilders.notFilter(eqFilter);
					break;
				case GREATER:
					filterBuilder = FilterBuilders.rangeFilter(field).gt(
							filter.getValue());
					break;
				case GREATER_EQUAL:
					filterBuilder = FilterBuilders.rangeFilter(field).gte(
							filter.getValue());
					break;
				case LESS:
					filterBuilder = FilterBuilders.rangeFilter(field).lt(
							filter.getValue());
					break;
				case LESS_EQUAL:
					filterBuilder = FilterBuilders.rangeFilter(field).lte(
							filter.getValue());
					break;
				case IN:
				case NOT_IN:
					Object[] values = null;
					if (filter.getValue() instanceof Collection<?>) {
						values = ((Collection<?>) filter.getValue())
								.toArray(new Object[0]);
					} else if (StringUtil.isType(Occurrence.class,
							filter.column, Integer.class)) {
						values = filter.getIntegerValues();
					} else {
						values = filter.getCollectionValues();
					}
					FilterBuilder inFilter = FilterBuilders.inFilter(field,
							values);
					filterBuilder = op.equals(Operator.IN) ? inFilter
							: FilterBuilders.notFilter(inFilter);
					break;
				case IS_EMPTY:
					filterBuilder = FilterBuilders.missingFilter(filter.column).nullValue(true).existence(true);
					break;
				case IS_NOT_EMPTY:
					filterBuilder = FilterBuilders.notFilter(FilterBuilders.missingFilter(filter.column).nullValue(true).existence(true));
					break;
				}
				if (filterBuilder != null) {
					if (filter.isDisjunction()) {
						orFilters.add(filterBuilder);
					} else {
						andFilters.add(filterBuilder);
					}
				}
			}
		}
		QueryBuilder queryBuilder;

		FilterBuilder publicFilter = getPublicFilter(user, resultFilter,
				isMyOccurrence);
		if (publicFilter != null) {
			andFilters.add(publicFilter);
		}
		if (!orFilters.isEmpty()) {
			FilterBuilder filter = FilterBuilders.orFilter(orFilters
					.toArray(new FilterBuilder[0]));
			andFilters.add(filter);
		}

		if (!andFilters.isEmpty() && boolQueryBuilder.hasClauses()) {
			queryBuilder = QueryBuilders.filteredQuery(boolQueryBuilder,
					FilterBuilders.andFilter(andFilters
							.toArray(new FilterBuilder[0])));
		} else if (!andFilters.isEmpty()) {
			queryBuilder = QueryBuilders.filteredQuery(QueryBuilders
					.matchAllQuery(), FilterBuilders.andFilter(andFilters
					.toArray(new FilterBuilder[0])));
		} else {
			queryBuilder = boolQueryBuilder;
		}
		return queryBuilder;
	}
	
	private FilterBuilder getPublicFilter(User user, ResultFilter resultFilter,
			boolean isMyOccurrence) {
		if (resultFilter == null) {
			return null;
		}
		boolean sAdmin = false;
		if (user != null)
			sAdmin = new RoleDbImpl().isSAdmin(user.getId());
		String publicCol = OccurrenceDbImpl.getOccurrencePropertyName("public").toLowerCase();
		String ownerCol = OccurrenceDbImpl.getOccurrencePropertyName("ownerEmail").toLowerCase();
		FilterBuilder filterBuilder = null;
		switch (resultFilter) {
		case PUBLIC:
			filterBuilder = FilterBuilders.termFilter(publicCol, true);
			break;
		case PRIVATE:
			if (sAdmin) {
				filterBuilder = FilterBuilders.termFilter(publicCol, false);
			} else {
				filterBuilder = FilterBuilders.andFilter(
						FilterBuilders.termFilter(publicCol, false),
						FilterBuilders.termFilter(ownerCol, user.getEmail()));
			}
			break;
		case BOTH:
			if (sAdmin) {
				filterBuilder = FilterBuilders.orFilter(
						FilterBuilders.termFilter(publicCol, true),
						FilterBuilders.termFilter(publicCol, false));
			} else {
				FilterBuilder publicFilter = FilterBuilders.termFilter(
						publicCol, true);
				FilterBuilder privateFilter = FilterBuilders.andFilter(
						FilterBuilders.termFilter(publicCol, false),
						FilterBuilders.termFilter(ownerCol, user == null ? null
								: user.getEmail()));

				if (isMyOccurrence) {
					filterBuilder = FilterBuilders.orFilter(
							publicFilter,
							privateFilter,
							FilterBuilders.termFilter("shareduserscsv",
									user.getEmail()));
				} else {
					filterBuilder = FilterBuilders.orFilter(publicFilter,
							privateFilter);
				}
			}
			break;
		}

		// if (!isMyOccurrence && userId != null) {
		// criteria.add(Restrictions.ilike("sharedUsersCSV", " " + userId +
		// " "));
		// queryFilters.add("sharedUsersCSV like '% " + userId + " ");
		//
		// }
		return filterBuilder;
	}
	
	

	@Override
	public List<Occurrence> find(OccurrenceQuery query,
			Set<OccurrenceFilter> filters, User user, int tryCount)
			throws Exception {
		log.debug("finding Occurrence instances by query.");
		// filtre sur les identifiants d'occurrence
		if (query.getOccurrenceIdsFilter() != null
				&& !query.getOccurrenceIdsFilter().isEmpty()) {
			OccurrenceFilter occIdsFilter = new OccurrenceFilter("id",
					Operator.IN, query.getOccurrenceIdsFilter());
			filters.add(occIdsFilter);
		}

		int from = query.getStart() < 0 ? 0 : query.getStart();
		int size = 10;
		if (query.getLimit() > 0) {
			size = query.getLimit();
		}
		ResultFilter resultFilter = query.getResultFilter();
		SearchResponse searchResponse = _findByOccurrenceFilters(filters, user,
				resultFilter, from, size);
		SearchHits searchHits = searchResponse.getHits();
		query.setCount((int) searchHits.getTotalHits());
		List<Occurrence> results = new ArrayList<Occurrence>();
		for (SearchHit hit : searchHits.getHits()) {
			Occurrence o = OccurrenceMapping.asOccurrence(hit.getSource());
			results.add(o);
		}
		// if (userReviewFilter != null) {
		// filters.remove(idsFilter);
		// filters.add(userReviewFilter);
		// }
		// if (myreviewPublicFilter != null) {
		// filters.remove(myreviewPublicFilter);
		// }
		// List<OrderKey> orderingMap = query.getOrderingMap();
		// log.info("order map = " + orderingMap);
		// if (query.isCountTotalResults()) {
		// criteria.setFirstResult(0);
		// criteria.setProjection(Projections.count("id"));
		// Integer count = (Integer) criteria.uniqueResult();
		// if (count != null) {
		// query.setCount(count);
		// }
		// } else {
		// query.setCount(-1);
		// }
		// Sets the start, limit, and order by accepted species:
		// criteria.setFirstResult(query.getStart());
		// if (query.getLimit() != OccurrenceQuery.UNLIMITED) {
		// criteria.setMaxResults(query.getLimit());
		// }
		// criteria.setProjection(null);
		/*
		 * for (OrderKey orderKey : orderingMap) { String property =
		 * orderKey.getAttributeName(); String occAttribute =
		 * getOccurrencePropertyName(property); if (orderKey.isAsc()) {
		 * log.info("order by property " + occAttribute +
		 * " in ascending order"); criteria.addOrder(Order.asc(occAttribute)); }
		 * else { log.info("order by property " + occAttribute +
		 * " in descending order"); criteria.addOrder(Order.desc(occAttribute));
		 * } }
		 */
		// criteria.addOrder(Order.asc("id"));
		// results = criteria.list();

		// filters.addAll(removedFilters);
		log.debug("find by example successful, result size: " + results.size());
		// ManagedSession.commitTransaction(session);
		return results;
	}

}
