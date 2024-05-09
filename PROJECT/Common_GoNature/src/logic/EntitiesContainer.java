package logic;

/**
 * The EntitiesContainer class is a container for storing multiple entities and
 * an optional customer interface. It provides methods to set and retrieve these
 * entities and the customer interface.
 */
public class EntitiesContainer {
	private Object entity1;
	private Object entity2;
	private Object entity3;
	private ICustomer customerInterface;

	/**
	 * Constructs a new EntitiesContainer with three entities.
	 * 
	 * @param entity1 The first entity
	 * @param entity2 The second entity
	 * @param entity3 The third entity
	 */
	public EntitiesContainer(Object entity1, Object entity2, Object entity3) {
		this.entity1 = entity1;
		this.entity2 = entity2;
		this.entity3 = entity3;
	}

	/**
	 * Constructs a new EntitiesContainer with two entities.
	 * 
	 * @param entity1 The first entity
	 * @param entity2 The second entity
	 */
	public EntitiesContainer(Object entity1, Object entity2) {
		this.entity1 = entity1;
		this.entity2 = entity2;
	}

	/**
	 * Constructs a new EntitiesContainer with one entity.
	 * 
	 * @param entity1 The entity
	 */
	public EntitiesContainer(Object entity1) {
		this.entity1 = entity1;
	}

	/**
	 * Constructs a new EntitiesContainer with one entity and a customer interface.
	 * 
	 * @param entity1           The entity
	 * @param customerInterface The customer interface
	 */
	public EntitiesContainer(Object entity1, ICustomer customerInterface) {
		this.entity1 = entity1;
		this.setCustomerInterface(customerInterface);
	}

	/**
	 * Gets the first entity.
	 * 
	 * @return The first entity
	 */
	public Object getEntity1() {
		return entity1;
	}

	/**
	 * Sets the first entity.
	 * 
	 * @param entity1 The first entity to set
	 */
	public void setEntity1(Object entity1) {
		this.entity1 = entity1;
	}

	/**
	 * Gets the second entity.
	 * 
	 * @return The second entity
	 */
	public Object getEntity2() {
		return entity2;
	}

	/**
	 * Sets the second entity.
	 * 
	 * @param entity2 The second entity to set
	 */
	public void setEntity2(Object entity2) {
		this.entity2 = entity2;
	}

	/**
	 * Gets the third entity.
	 * 
	 * @return The third entity
	 */
	public Object getEntity3() {
		return entity3;
	}

	/**
	 * Sets the third entity.
	 * 
	 * @param entity3 The third entity to set
	 */
	public void setEntity3(Object entity3) {
		this.entity3 = entity3;
	}

	/**
	 * Gets the customer interface.
	 * 
	 * @return The customer interface
	 */
	public ICustomer getCustomerInterface() {
		return customerInterface;
	}

	/**
	 * Sets the customer interface.
	 * 
	 * @param customerInterface The customer interface to set
	 */
	public void setCustomerInterface(ICustomer customerInterface) {
		this.customerInterface = customerInterface;
	}

}
