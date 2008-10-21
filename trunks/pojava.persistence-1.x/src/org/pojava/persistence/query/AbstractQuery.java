package org.pojava.persistence.query;


/**
 * An AbstractQuery represents the row selection (and not the column selection)
 * aspects of a search criteria. It is used by Dao methods to perform some
 * operation upon a definable set of rows, such as deleting, listing, or
 * processing.
 *
 * The primary goal of this approach is to abstract the persistence implementation
 * details away from your business layer, so your code behaves the same way when
 * you switch implementations, say from one database to another, or from a local
 * database to a web service or to an API for an Enterprise Service Bus.
 *
 * A typical SQL-only Query could just extend SqlQuery (which already extends
 * AbstractQuery).  A very basic query could look like the following: <code>
 * public class AnimalQuery extends SqlQuery {
 *   public AnimalQuery forNumberOfLegs(int legs) {
 *     String legStr=new Integer(legs).toString();
 *     super.sql.clear();
 *     super.sql.append("WHERE legs=" + legStr);
 *     return this;
 *   }
 * }
 * </code>
 * If you created your own custom provider, say, for an XML query using xpath,
 * your class may look like this (note that your business layer still defines
 * the number of legs the same way for either implementation): <code>
 * public class AnimalQuery extends SqlQuery implements XpathProvider {
 *   private String xpath=null;
 *   public AnimalQuery forNumberOfLegs(int legs) {
 *     String legStr=new Integer(legs).toString();
 *     super.sql.clear();
 *     super.sql.append("WHERE legs=" + legStr);
 *     this.xpath="//ANIMALS/ANIMAL[@legs='" + legStr + "']";
 *     return this;
 *   }
 *   public String generateXpath() {
 *     return xpath;
 *   }
 * }
 * </code>
 *
 * For either implementation, your business layer would typically make a
 * call similar to this: <code>
 * ...
 * AnimalQuery legQuery=new AnimalQuery().forNumberOfLegs(4);
 * List animals=AnimalDao.listByQuery(transaction, legQuery);
 * ...
 * </code>
 * 
 * @author John Pile
 * 
 */
public abstract class AbstractQuery {

	/**
	 * Maximum number of rows permitted before the driver cuts you off
	 * (0=unlimited).
	 */
	private int maxRows = 0;

	/**
	 * Default constructor
	 */
	public AbstractQuery() {
	}

	/**
	 * Show the maximum of rows returnable by the driver.
	 * @return maximum allowed rows in dataset.
	 */
	public int getMaxRows() {
		return maxRows;
	}

	/**
	 * Limit the number of rows returnable by the driver.
	 * @param maxRows
	 */
	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
	}

}
