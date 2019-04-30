package com.duzi.swipecalendar.content

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.duzi.swipecalendar.R
import kotlinx.android.synthetic.main.fragment_content.*

class ContentFragment: Fragment() {

    private var title: String? = null
    private var content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(TITLE)
            content = it.getString(CONTENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_content, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title?.run {
            tvTitle.text = this
        }

        content?.run {
            tvContent.text = this
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(title: String?, content: String?) =
            ContentFragment().apply {
                arguments = Bundle().apply {
                    putString(TITLE, title)
                    putString(CONTENT, content)
                }
            }

        const val TITLE = "TITLE"
        const val CONTENT = "CONTENT"
    }
}