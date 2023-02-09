package com.exampleble.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.exampleble.R
import com.exampleble.repo.BaseView
import com.exampleble.ui.BaseActivity

import org.jetbrains.annotations.NotNull


abstract class BaseFragment : Fragment(),

    BaseView {
    /**
     * to get Fragment resource file
     */
    @LayoutRes
    abstract fun getInflateResource(): Int

    /**
     * to set fragment option menu
     */
    protected open fun hasOptionMenu(): Boolean = false

    /**
     * to display error message
     */
    abstract fun displayMessage(message: String)

    /**
     * to initialize variables
     */
    abstract fun initView()

    /**
     * to call API or bind adapter
     */
    abstract fun postInit()

    /**
     * to define all listener
     */
    abstract fun initObserver()
    abstract fun handleListener()
    abstract fun initProgressBar()
    abstract fun showLoadingIndicator(isShow: Boolean)
    var isInternetConnected: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initProgressBar()
    }

    private lateinit var binding: ViewDataBinding


    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getInflateResource(), container, false)
        setHasOptionsMenu(hasOptionMenu())
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
        handleListener()
        postInit()
        initObserver()
    }

    fun setTitle(@NotNull title: String) {
        (context as BaseActivity).title = title
    }

    @Suppress("UNCHECKED_CAST")
    @NonNull
    protected fun <T : ViewDataBinding> getBinding(): T {
        return binding as T
    }

    override fun onUnknownError(error: String?) {
        error?.let {
            displayMessage(error)
        }
    }

    override fun internalServer() {
        displayMessage(getString(R.string.text_error_internal_server))
    }

    override fun onTimeout() {
        displayMessage(getString(R.string.text_error_timeout))
    }

    override fun onNetworkError() {
        displayMessage(getString(R.string.text_error_network))
    }

    override fun onConnectionError() {
        displayMessage(getString(R.string.text_error_connection))
    }
}




