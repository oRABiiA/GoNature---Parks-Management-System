package utils;

/**
 * The EntranceDiscount class contains constants representing various discount
 * rates for park entrance fees.
 */
public class EntranceDiscount {

	/**
	 * Discount rate for preordered solo and family visits. Represents a 15%
	 * discount.
	 */
	public static final double SOLO_FAMILY_PREORDER_DISCOUNT = 0.85; // 15% on preordered solo visits

	/**
	 * Discount rate for occasional solo and family visits. Represents full price
	 * (no discount).
	 */
	public static final double SOLO__FAMILY_OCCASIONAL_DISCOUNT = 1.00; // full price

	/**
	 * Discount rate for preordered group visits. Represents a 25% discount (guide
	 * does not pay).
	 */
	public static final double GROUP_PREORDER_DISCOUNT = 0.75; // 25% on preordered groups (guide not pay)

	/**
	 * Discount rate for occasional group visits. Represents a 10% discount (guide
	 * pays).
	 */
	public static final double GROUP_OCCASIONAL_DISCOUNT = 0.90; // 10% on occasional groups (guide pay)

	/**
	 * Additional discount rate for prepaid group visits. Represents a 12% extra
	 * discount.
	 */
	public static final double ADDITIONAL_GROUP_DISCOUNT = 0.88; // 12% extra on prepaid
}
