/**
 * 
 */
package org.rebioma.server.elasticsearch.search;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.rebioma.client.OccurrenceQuery;
import org.rebioma.client.bean.Occurrence;
import org.rebioma.client.bean.OccurrenceReview;
import org.rebioma.client.bean.RecordReview;
import org.rebioma.client.bean.User;
import org.rebioma.client.services.OccurrenceService.OccurrenceServiceException;
import org.rebioma.server.services.OccurrenceDb;
import org.rebioma.server.upload.Traitement;

/**
 * @author Mikajy
 *
 */
public class OccurrenceDbEsImpl implements OccurrenceDb {

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#assignReviewer(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
	 */
	@Override
	public int assignReviewer(String userEmail, String taxoFieldName,
			String taxoFieldValue, boolean isMarine, boolean isTerrestrial)
			throws OccurrenceServiceException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#attachClean(org.hibernate.Session, org.rebioma.client.bean.Occurrence)
	 */
	@Override
	public void attachClean(Session session, Occurrence instance) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#attachDirty(org.rebioma.client.bean.Occurrence)
	 */
	@Override
	public void attachDirty(Occurrence instance) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#attachDirty(java.util.Set, org.rebioma.server.upload.Traitement, java.util.List, boolean, boolean)
	 */
	@Override
	public void attachDirty(Set<Occurrence> instances, Traitement traitement,
			List<RecordReview> rcdrv, boolean clearReview, boolean isSA) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#attachDirty(java.util.Set)
	 */
	@Override
	public void attachDirty(Set<Occurrence> instances) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#attachDirty(java.util.Set, boolean)
	 */
	@Override
	public void attachDirty(Set<Occurrence> instances, boolean resetReview) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#delete(org.rebioma.client.bean.Occurrence)
	 */
	@Override
	public void delete(Occurrence persistentInstance) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#delete(java.util.Set)
	 */
	@Override
	public void delete(Set<Occurrence> persistentInstance) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#detele(org.rebioma.client.OccurrenceQuery, org.rebioma.client.bean.User)
	 */
	@Override
	public int detele(OccurrenceQuery query, User user) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#findByAttributeValue(org.rebioma.server.services.OccurrenceDb.AttributeValue)
	 */
	@Override
	public List<Occurrence> findByAttributeValue(AttributeValue attributeValue) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#findByAttributeValues(java.util.Set)
	 */
	@Override
	public List<Occurrence> findByAttributeValues(
			Set<AttributeValue> attributeValues) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#findByExample(org.rebioma.client.bean.Occurrence)
	 */
	@Override
	public List<Occurrence> findByExample(Occurrence instance) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#findByExample(java.util.Set)
	 */
	@Override
	public List<Occurrence> findByExample(Set<Occurrence> instances) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#findById(java.lang.Integer)
	 */
	@Override
	public Occurrence findById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#findById(java.util.Set)
	 */
	@Override
	public List<Occurrence> findById(Set<Integer> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#findByOccurrenceQuery(org.rebioma.client.OccurrenceQuery, java.lang.Integer)
	 */
	@Override
	public List<Occurrence> findByOccurrenceQuery(OccurrenceQuery query,
			Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#findByOccurrenceQuery(org.rebioma.client.OccurrenceQuery, org.rebioma.client.bean.User)
	 */
	@Override
	public List<Occurrence> findByOccurrenceQuery(OccurrenceQuery query,
			User user) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#merge(org.rebioma.client.bean.Occurrence)
	 */
	@Override
	public Occurrence merge(Occurrence detachedInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#persist(org.rebioma.client.bean.Occurrence)
	 */
	@Override
	public void persist(Occurrence transientInstance) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#removeBadId(java.util.Set, org.rebioma.client.bean.User)
	 */
	@Override
	public String removeBadId(Set<Occurrence> instances, User logginUser) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#resetReviews()
	 */
	@Override
	public void resetReviews() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#update(org.rebioma.client.OccurrenceQuery, org.rebioma.client.bean.User)
	 */
	@Override
	public int update(OccurrenceQuery query, User user) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#validateOccurrences(java.util.Set, org.rebioma.client.bean.User, java.util.Map)
	 */
	@Override
	public String validateOccurrences(Set<Occurrence> instances,
			User logginUser, Map<User, Set<Occurrence>> userOccurrencesMap) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#checkForReviewedChanged(int)
	 */
	@Override
	public boolean checkForReviewedChanged(int occurrenceId) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#findOccurrenceIdsByQuery(org.rebioma.client.OccurrenceQuery, org.rebioma.client.bean.User)
	 */
	@Override
	public List<Integer> findOccurrenceIdsByQuery(OccurrenceQuery query,
			User user) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#getOccurrenceReviewsOf(int)
	 */
	@Override
	public List<OccurrenceReview> getOccurrenceReviewsOf(int occurrenceId) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#resetRecordReview(org.rebioma.client.bean.Occurrence, boolean)
	 */
	@Override
	public void resetRecordReview(Occurrence occurrence, boolean isStable) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#resetRecordReview(org.rebioma.client.bean.Occurrence, boolean, org.hibernate.Session)
	 */
	@Override
	public void resetRecordReview(Occurrence occurrence, boolean isStable,
			Session sess) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#updateStability(org.rebioma.client.bean.Occurrence, java.lang.Boolean, org.hibernate.Session)
	 */
	@Override
	public void updateStability(Occurrence o, Boolean stability, Session sess) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rebioma.server.services.OccurrenceDb#refresh()
	 */
	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

}
