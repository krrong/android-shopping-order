package woowacourse.shopping.presentation.view.productlist

import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.mapper.toUIModel
import woowacourse.shopping.data.mapper.toUiModel
import woowacourse.shopping.data.model.ProductEntity
import woowacourse.shopping.data.respository.cart.CartRepository
import woowacourse.shopping.data.respository.product.ProductRepository
import woowacourse.shopping.data.respository.recentproduct.RecentProductRepository
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.model.RecentProductModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProductListPresenter(
    private val view: ProductContract.View,
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository,
    private val recentProductRepository: RecentProductRepository,
) : ProductContract.Presenter {
    private val products = mutableListOf<CartModel>()
    private val recentProducts = mutableListOf<RecentProductModel>()
    private var lastScroll = 0
    private var productsStartIndex = 0

    override fun initRecentProductItems() {
        val today = LocalDateTime.now().format(DateTimeFormatter.ofPattern(LOCAL_DATE_PATTERN))
        recentProductRepository.deleteNotTodayRecentProducts(today)
    }

    override fun initProductItems() {
        productRepository.loadDatas(::onFailure) { remoteEntities ->
            val allProducts = remoteEntities.map { productEntity -> productEntity.toUIModel() }
            products.addAll(allProducts)

            loadCartItems()
            loadRecentProductItems()
            view.setLayoutVisibility()
        }
    }

    private fun onFailure() {
        view.handleErrorView()
    }

    override fun loadCartItems() {
        cartRepository.loadAllCarts(::onFailure) { carts ->
            val newProducts = products.map { product ->
                product.copy(
                    count = carts.find { cart -> cart.productEntity.id == product.product.id }?.quantity
                        ?: 0,
                )
            }

            products.clear()
            products.addAll(newProducts)

            val allCount = newProducts.sumOf { it.count }

            view.setProductItemsView(products.subList(0, getSubToIndex()).toList())
            view.updateToolbarCartCountView(allCount)
            updateVisibilityCartCount(allCount)
        }
    }

    override fun updateProductItems(startIndex: Int) {
        productsStartIndex = startIndex
        view.setProductItemsView(products.subList(0, getSubToIndex()).toList())
    }

    private fun getSubToIndex(): Int {
        return if (products.size > productsStartIndex + DISPLAY_PRODUCT_COUNT) {
            productsStartIndex + DISPLAY_PRODUCT_COUNT
        } else {
            products.size
        }
    }

    override fun loadRecentProductItems() {
        recentProducts.clear()
        recentProducts.addAll(
            recentProductRepository.getRecentProducts(LOAD_RECENT_PRODUCT_COUNT)
                .filter { it.id != UNABLE_ID }
                .map {
                    RecentProductModel(
                        it.id,
                        products.find { product -> product.id == it.productId }?.product
                            ?: ProductEntity.errorData.toUIModel(),
                    )
                },
        )
        view.setRecentProductItemsView(recentProducts.toList())
    }

    override fun saveRecentProduct(productId: Long) {
        recentProductRepository.addCart(productId)
    }

    override fun actionOptionItem() {
        view.moveToCartView()
    }

    override fun getLastRecentProductItem(lastRecentIndex: Int): RecentProductModel {
        val lastRecentProducts = recentProductRepository.getRecentProducts(LAST_RECENT_COUNT).map {
            RecentProductModel(
                it.id,
                products.find { product -> product.product.id == it.productId }?.product
                    ?: ProductEntity.errorData.toUIModel(),
            )
        }

        return lastRecentProducts[lastRecentIndex]
    }

    override fun getRecentProductsLastScroll(): Int = lastScroll

    override fun updateRecentProductsLastScroll(lastScroll: Int) {
        this.lastScroll = lastScroll
    }

    override fun updateCount(productId: Long, count: Int) {
        val product = products.find { it.product.id == productId } ?: return

        if (product.count == 0) {
            cartRepository.addCartProduct(productId, ::onFailure) { cartId ->
                val newProduct = product.toDomain().updateCount(count).toUiModel()
                val cartProduct = newProduct.updateId(cartId)
                cartRepository.addLocalCart(cartProduct.id)

                val index = products.indexOf(product)
                products.removeAt(index)
                products.add(index, cartProduct)

                val allCount = products.sumOf { it.count }
                view.updateToolbarCartCountView(allCount)
                updateVisibilityCartCount(allCount)
            }
            return
        }

        cartRepository.loadAllCarts(::onFailure) { carts ->
            val cartProduct = carts.find { it.productEntity.id == productId } ?: return@loadAllCarts
            cartRepository.addLocalCart(cartProduct.id)
            val newCartProduct = cartProduct.copy(quantity = count)

            cartRepository.updateCartCount(newCartProduct, ::onFailure) {
                if (count == 0) {
                    cartRepository.deleteLocalCart(newCartProduct.id)
                }

                val newProduct = product.toDomain().updateCount(count).toUiModel()
                val index = products.indexOf(product)
                products.removeAt(index)
                products.add(index, newProduct)

                val allCount = products.sumOf { it.count }
                view.updateToolbarCartCountView(allCount)
                updateVisibilityCartCount(allCount)
            }
        }
    }

    private fun updateVisibilityCartCount(count: Int) {
        if (count == 0) {
            view.setGoneToolbarCartCountView()
            return
        }
        view.setVisibleToolbarCartCountView()
    }

    companion object {
        private const val LOCAL_DATE_PATTERN = "yyyy-MM-dd"
        private const val LOAD_RECENT_PRODUCT_COUNT = 10

        private const val LAST_RECENT_COUNT = 2

        private const val DISPLAY_PRODUCT_COUNT = 20
        private const val UNABLE_ID = -1L
    }
}
