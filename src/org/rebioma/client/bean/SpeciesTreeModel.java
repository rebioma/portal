package org.rebioma.client.bean;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class SpeciesTreeModel extends BaseTreeModel {
        public static final String LABEL = "Label";
        public static final String LEVEL = "Level";
        public static final String PRIVATE_OCCURENCE = "PrivateOcc";
        public static final String PUBLIC_OCCURENCE = "PublicOcc";
        public static final String AUTHORITY_NAME = "AuthorityName";
        public static final String STATUS = "Status";
        public static final String SYNONYMISED_TAXA = "SynonymisedTaxa";
        public static final String SOURCE = "Source";
        public static final String VERNECULAR_NAME = "VernecularName";
        public static final String REVIEWER_NAME = "ReviewerName";
        public static final String OCCURENCE = "Occurence";
       

        public static final String KINGDOM = "Kingdom";
        public static final String PHYLUM = "Phylum";
        public static final String CLASS_ = "Class";
       
        public static final String SUBCLASS = "Subclass";
        public static final String ORDER = "Order";
        public static final String SUPERFAMILY = "Superfamily";
        public static final String FAMILY = "Family";
        public static final String GENUS = "Genus";
        public static final String ACCEPTEDSPECIES = "Acceptedspecies";
       
        public  String getSubclass() {
                return this.get(SUBCLASS);
        }
        public void setSubclass(String str){
                this.set(SUBCLASS, str);
        }
       
        public  String getOrder() {
                return this.get(ORDER);
        }
        public void setOrder(String str){
                this.set(ORDER, str);
        }
       
        public  String getSuperfamily() {
                return this.get(SUPERFAMILY);
        }
        public void setSuperfamily(String str){
                this.set(SUPERFAMILY, str);
        }
       
        public  String getFamily() {
                return this.get(FAMILY);
        }
        public void setFamily(String str){
                this.set(FAMILY, str);
        }
       
        public  String getGenus() {
                return this.get(GENUS);
        }
        public void setGenus(String str){
                this.set(GENUS, str);
        }
       
        public  String getAcceptedspecies() {
                return this.get(ACCEPTEDSPECIES);
        }
        public void setAcceptedspecies(String str){
                this.set(ACCEPTEDSPECIES, str);
        }
       
        public  String getClass_() {
                return this.get(CLASS_);
        }
        public void setClass_(String str){
                this.set(CLASS_, str);
        }
       
        public  String getKingdom() {
                return this.get(KINGDOM);
        }
        public void setKingdom(String str){
                this.set(KINGDOM, str);
        }
       
        public void setPhylum(String str){
                this.set(PHYLUM, str);
        }
        public  String getPhylum() {
                return this.get(PHYLUM);
        }
        /**
         *
         */
        private static final long serialVersionUID = 1L;
       
        public void setLabel(String label){
                this.set(LABEL, label);
        }
        public String getLabel(){
                return this.get(LABEL);
        }
        public void setLevel(String level){
                this.set(LEVEL, level);
        }
        public String getLevelQueryFilter(){
                //AcceptedKingdom, AcceptedPhylum, AcceptedClass, AcceptedOrder, AcceptedFamily, AcceptedGenus, AcceptedSpecies
                String level = getLevel();
                if("Kingdom".equalsIgnoreCase(level)){
                        return "AcceptedKingdom";
                }else if("Phylum".equalsIgnoreCase(level)){
                        return "AcceptedPhylum";
                }else if("class".equalsIgnoreCase(level) || "clazz".equalsIgnoreCase(level)){
                        return "AcceptedClass";
                }else if("Order".equalsIgnoreCase(level)){
                        return "AcceptedOrder";
                }else if("Family".equalsIgnoreCase(level)){
                        return "AcceptedFamily";
                }else if("Genus".equalsIgnoreCase(level)){
                        return "AcceptedGenus";
                }else if("Species".equalsIgnoreCase(level)){
                        return "AcceptedSpecies";
                }else{
                        return level;
                }
        }
        public String getLevel(){
                return this.get(LEVEL);
        }
        public void setNbPrivateOccurence(int nbPrivateOccurence){
                this.set(PRIVATE_OCCURENCE, nbPrivateOccurence);
        }
        public int getNbPrivateOccurence(){
                return this.get(PRIVATE_OCCURENCE);
        }
        public void setNbPublicOccurence(int nbPublicOccurence){
                this.set(PUBLIC_OCCURENCE, nbPublicOccurence);
        }
        public int getNbPublicOccurence(){
                return this.get(PUBLIC_OCCURENCE);
        }
        public void setAuthorityName(String authName){
                this.set(AUTHORITY_NAME, authName);
        }
        public String getAuthorityName(){
                String authName = this.get(AUTHORITY_NAME);
                return authName;
        }
        public void setStatus(String status){
                this.set(STATUS, status);
        }
        public String getStatus(){
                return this.get(STATUS);
        }
        public void setSynonymisedTaxa(String synTaxa){
                this.set(SYNONYMISED_TAXA, synTaxa);
        }
        public String getSynonymisedTaxa(){
                return this.get(SYNONYMISED_TAXA);
        }
        public void setSource(String source){
                this.set(SOURCE, source);
        }
        public String getSource(){
                return this.get(SOURCE);
        }
        public void setVernecularName(String vernecularName){
                this.set(VERNECULAR_NAME, vernecularName);
        }
        public String getVernecularName(){
                return this.get(VERNECULAR_NAME);
        }
        public void setReviewerName(String reviewerName){
                this.set(REVIEWER_NAME, reviewerName);
        }
        public String getReviewerName(){
                return this.get(REVIEWER_NAME);
        }
        public void setOccurence(Occurrence occ){
                this.set(OCCURENCE, occ);
        }
        public Occurrence getOccurence(){
                Occurrence occ = this.get(OCCURENCE);
                return occ;
        }
}
