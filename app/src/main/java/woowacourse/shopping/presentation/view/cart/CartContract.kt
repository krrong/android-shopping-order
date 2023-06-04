package woowacourse.shopping.presentation.view.cart

import woowacourse.shopping.presentation.model.CartModel

interface CartContract {
    interface View {
        fun setCartItemsView(carts: List<CartModel>)
        fun setChangedCartItemsView(carts: List<CartModel>)
        fun setEnableLeftButton(isEnabled: Boolean)
        fun setEnableRightButton(isEnabled: Boolean)
        fun setAllCartChecked(isChecked: Boolean)
        fun setPageCountView(page: Int)
        fun setTotalPriceView(totalPrice: Int)
        fun updateAllChecking(startPosition: Int, count: Int)
        fun setLayoutVisibility()
        fun moveToOrderView(cartItems: List<CartModel>)
        fun handleErrorView()
    }

    interface Presenter {
        fun initCartItems()
        fun loadCartItems()
        fun deleteCartItem(itemId: Long)
        fun calculatePreviousPage()
        fun calculateNextPage()
        fun calculateTotalPrice()
        fun order()
        fun updateProductCount(cartId: Long, count: Int)
        fun updateProductChecked(cartId: Long, isChecked: Boolean)
        fun updateCurrentPageAllProductChecked(isChecked: Boolean)
    }
}
