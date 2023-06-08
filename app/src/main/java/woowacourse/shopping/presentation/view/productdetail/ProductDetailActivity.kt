package woowacourse.shopping.presentation.view.productdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.cart.CartRepositoryImpl
import woowacourse.shopping.data.respository.cart.source.local.CartLocalDataSourceImpl
import woowacourse.shopping.data.respository.cart.source.remote.CartRemoteDataSourceImpl
import woowacourse.shopping.data.respository.product.ProductRepositoryImpl
import woowacourse.shopping.data.respository.product.source.remote.ProductRemoteDataSourceImpl
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.model.RecentProductModel
import woowacourse.shopping.presentation.view.orderlist.OrderListActivity
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_SERVER
import woowacourse.shopping.presentation.view.util.createRetrofit
import woowacourse.shopping.presentation.view.util.getParcelableCompat
import woowacourse.shopping.presentation.view.util.getSerializableCompat
import woowacourse.shopping.presentation.view.util.showToast

class ProductDetailActivity : AppCompatActivity(), ProductDetailContract.View {
    private lateinit var binding: ActivityProductDetailBinding

    private val productId: Long by lazy {
        intent.getLongExtra(KEY_PRODUCT_ID, -1)
    }

    private val recentProduct: RecentProductModel? by lazy {
        intent.getParcelableCompat(KEY_RECENT_PRODUCT)
    }

    private lateinit var server: Server

    private lateinit var presenter: ProductDetailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail)

        supportActionBar?.title = ACTION_BAR_TITLE

        server = intent.getSerializableCompat(KEY_SERVER_SERVER) ?: return finish()

        setPresenter()

        presenter.loadLastRecentProductInfo(recentProduct)
        setAddCart()
        setLastRecentProduct()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_product_detail_toolbar, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_close -> exitProductDetailView()
            R.id.action_order -> moveToOrderListView()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setPresenter() {
        val retrofit = createRetrofit(server)
        val productRemoteDataSource = ProductRemoteDataSourceImpl(retrofit)
        val cartLocalDataSource = CartLocalDataSourceImpl(this, server)
        val cartRemoteDataSource = CartRemoteDataSourceImpl(server)
        presenter = ProductDetailPresenter(
            this,
            productId = productId,
            productRepository = ProductRepositoryImpl(productRemoteDataSource),
            cartRepository = CartRepositoryImpl(cartLocalDataSource, cartRemoteDataSource),
        )
    }

    override fun setVisibleOfLastRecentProductInfoView(recentProduct: RecentProductModel) {
        binding.recentProduct = recentProduct

        binding.clLastProductInfo.visibility = View.VISIBLE
    }

    override fun setGoneOfLastRecentProductInfoView() {
        binding.clLastProductInfo.visibility = View.GONE
    }

    override fun setProductInfoView(cartModel: CartModel) {
        binding.product = cartModel
    }

    private fun setLastRecentProduct() {
        binding.clLastProductInfo.setOnClickListener {
            val intent = createIntent(this, recentProduct?.product?.id ?: -1, null, server)
            startActivity(intent)
            finish()
        }
    }

    private fun setAddCart() {
        binding.btProductDetailAddToCart.setOnClickListener {
            presenter.showCount()
        }
    }

    override fun showCountView(cartModel: CartModel) {
        CartInsertionDialog(this, cartModel) { count ->
            presenter.addCart(count)
        }
    }

    override fun addCartSuccessView() {
        binding.root.post {
            showToast(getString(R.string.toast_message_success_add_cart))
        }
    }

    override fun handleErrorView() {
        binding.root.post {
            showToast(getString(R.string.toast_message_system_error))
        }
    }

    private fun moveToOrderListView() {
        val intent = OrderListActivity.createIntent(this, server)
        startActivity(intent)
    }

    override fun exitProductDetailView() {
        finish()
    }

    companion object {
        private const val KEY_PRODUCT_ID = "KEY_PRODUCT_ID"
        private const val KEY_RECENT_PRODUCT = "KEY_RECENT_PRODUCT"
        private const val ACTION_BAR_TITLE = ""

        internal fun createIntent(
            context: Context,
            id: Long,
            recentProduct: RecentProductModel?,
            server: Server,
        ): Intent {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra(KEY_PRODUCT_ID, id)
            intent.putExtra(KEY_SERVER_SERVER, server)

            recentProduct?.let { intent.putExtra(KEY_RECENT_PRODUCT, it) }

            return intent
        }
    }
}
