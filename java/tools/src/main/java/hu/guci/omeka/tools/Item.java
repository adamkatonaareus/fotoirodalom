/**
 * 
 */
package hu.guci.omeka.tools;

/**
 * An omeka item.
 * @author adam.katona
 *
 */
public class Item 
{
	private int id;
	private int itemTypeId;
	private int collectionId;
	private boolean isPublic = true;
	private boolean isFeatured = false;
	private int ownerId = 1;
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the itemTypeId
	 */
	public int getItemTypeId() {
		return itemTypeId;
	}
	/**
	 * @param itemTypeId the itemTypeId to set
	 */
	public void setItemTypeId(int itemTypeId) {
		this.itemTypeId = itemTypeId;
	}
	/**
	 * @return the collectionId
	 */
	public int getCollectionId() {
		return collectionId;
	}
	/**
	 * @param collectionId the collectionId to set
	 */
	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}
	/**
	 * @return the isPublic
	 */
	public boolean isPublic() {
		return isPublic;
	}
	/**
	 * @param isPublic the isPublic to set
	 */
	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}
	/**
	 * @return the isFeatured
	 */
	public boolean isFeatured() {
		return isFeatured;
	}
	/**
	 * @param isFeatured the isFeatured to set
	 */
	public void setFeatured(boolean isFeatured) {
		this.isFeatured = isFeatured;
	}
	/**
	 * @return the owner_id
	 */
	public int getOwnerId() {
		return ownerId;
	}
	/**
	 * @param owner_id the owner_id to set
	 */
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
}
